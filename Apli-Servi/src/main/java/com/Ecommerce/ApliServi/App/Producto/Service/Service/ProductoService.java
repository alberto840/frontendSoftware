package com.Ecommerce.ApliServi.App.Producto.Service.Service;

import java.io.*;
import io.minio.*;
import io.minio.http.Method;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.Ecommerce.ApliServi.App.Producto.Dto.Basico.ProductoDto;
import com.Ecommerce.ApliServi.App.Producto.Dto.Crear.CrearProductoDto;
import com.Ecommerce.ApliServi.App.Producto.Entity.CategoriaEntity;
import com.Ecommerce.ApliServi.App.Producto.Entity.DescuentoEntity;
import com.Ecommerce.ApliServi.App.Producto.Entity.MarcaEntity;
import com.Ecommerce.ApliServi.App.Producto.Entity.ProductoEntity;
import com.Ecommerce.ApliServi.App.Producto.Repository.CategoriaRepository;
import com.Ecommerce.ApliServi.App.Producto.Repository.DescuentoRepository;
import com.Ecommerce.ApliServi.App.Producto.Repository.MarcaRepository;
import com.Ecommerce.ApliServi.App.Producto.Repository.ProductoRepository;
import com.Ecommerce.ApliServi.App.Producto.Service.Interface.ProductoInterface;
import com.Ecommerce.ApliServi.App.Usuario.Entity.UserEntity;
import com.Ecommerce.ApliServi.App.Usuario.Repository.UserRepository;
import com.Ecommerce.ApliServi.App.Producto.Dto.Respuesta.PageableQuery;

@Service
public class ProductoService implements ProductoInterface {
    @Autowired
    ProductoRepository productoRepository;
    @Autowired
    CategoriaRepository categoriaRepository;
    @Autowired
    UserRepository usuarioRepository;
    @Autowired
    MarcaRepository marcaRepository;
    @Autowired
    DescuentoRepository descuentoRepository;
    @Autowired
    private MinioClient minioClient;

    private static final String BUCKET_NAME = "1-producto";

    public ProductoDto createProductoDto(CrearProductoDto productoAuxDto) {
        // Mapear el DTO de producto a la entidad de producto
        ProductoEntity productoEntity = mapToEntity(productoAuxDto);
        // Verificar que todas las propiedades requeridas de la entidad estén presentes
        if (productoEntity.getNombre() == null || productoEntity.getNombre().isEmpty()) {
            throw new RuntimeException("El nombre del producto no puede ser nulo o vacío.");
        }
        // Guardar la entidad de producto en la base de datos
        productoEntity = productoRepository.save(productoEntity);
        // Mapear la entidad de producto a DTO de producto y devolverla
        return mapToDto(productoEntity);

    }

    // Método para obtener un producto por su ID
    public ProductoDto getProductoById(int id) {
        ProductoEntity productoEntity = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        return mapToDto(productoEntity);
    }

    // Método para obtener todos los productos
    public List<ProductoDto> getAllProducto() {
        List<ProductoEntity> productos = productoRepository.findAll();
        return productos.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    // Método para actualizar un producto existente
    public ProductoDto updateProducto(int id, CrearProductoDto productoAuxDto) {
        ProductoEntity existingProducto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        // Actualizar los campos del producto con los datos del DTO
        existingProducto.setNombre(productoAuxDto.getNombre());
        existingProducto.setDescripcion(productoAuxDto.getDescripcion());
        existingProducto.setStock(productoAuxDto.getStock());
        existingProducto.setPrecio(productoAuxDto.getPrecio());

        // Si se proporciona una nueva imagen, eliminar la imagen anterior y guardar la
        // nueva
        if (productoAuxDto.getImageUrl() != null) {
            try {
                // Obtener nombre de la imagen
                String nombreImagen = productoAuxDto.getImageUrl().getOriginalFilename();
                // Obtener flujo de entrada de la imagen
                InputStream inputStream = productoAuxDto.getImageUrl().getInputStream();
                // Guardar la nueva imagen en Minio
                saveImage(inputStream, nombreImagen);
                // Eliminar la imagen anterior
                deleteImage(existingProducto.getImageUrl());
                String urlimg = getUrlImagen(nombreImagen);
                // Actualizar la URL de la imagen en la entidad de producto
                existingProducto.setImageUrl(urlimg);
            } catch (IOException e) {
                throw new RuntimeException("Error al procesar la imagen: " + e.getMessage());
            }
        }

        // Guardar los cambios en la base de datos
        productoRepository.save(existingProducto);

        // Mapear la entidad de producto actualizada a DTO y devolverla
        return mapToDto(existingProducto);
    }

    // Método para eliminar un producto por su ID
    public void deleteProducto(int id) {
        ProductoEntity productoEntity = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        // Eliminar la imagen asociada al producto
        deleteImage(productoEntity.getImageUrl());

        // Eliminar el producto de la base de datos
        productoRepository.delete(productoEntity);
    }

    private ProductoDto mapToDto(ProductoEntity entity) {
        ProductoDto dto = new ProductoDto();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());
        dto.setImageUrl(entity.getImageUrl());
        dto.setStock(entity.getStock());
        dto.setPrecio(entity.getPrecio());
        dto.setMarca(entity.getMarca().getId());
        // Mapear las categorías y usuarios
        dto.setCategorias(entity.getCategorias().stream().map(CategoriaEntity::getId).collect(Collectors.toList()));
        dto.setUsuarios(entity.getUsuarios().stream().map(UserEntity::getIdUsuario).collect(Collectors.toList()));
        dto.setDescuento(entity.getDiscounts().stream().map(DescuentoEntity::getId).collect(Collectors.toList()));
        return dto;
    }

    private ProductoEntity mapToEntity(CrearProductoDto dto) {
        try {
            ProductoEntity entity = new ProductoEntity();
            entity.setNombre(dto.getNombre());
            entity.setDescripcion(dto.getDescripcion());
            entity.setStock(dto.getStock());
            entity.setPrecio(dto.getPrecio());
            if (dto.getImageUrl() != null && !dto.getImageUrl().isEmpty()) {
                String nombreImagen = dto.getImageUrl().getOriginalFilename();
                InputStream inputStream = dto.getImageUrl().getInputStream();
                saveImage(inputStream, nombreImagen);
                String urlImagen = getUrlImagen(nombreImagen);
                entity.setImageUrl(urlImagen);
            } else {
                throw new RuntimeException("La imagen es nula o vacía.");
            }

            // Asignar la marca si se proporciona
            if (dto.getMarca() != 0) {
                MarcaEntity marcaEntity = marcaRepository.findById(dto.getMarca())
                        .orElseThrow(() -> new RuntimeException("Marca no encontrada con ID: " + dto.getMarca()));
                entity.setMarca(marcaEntity);
            }
            // Asignar las categorías si se proporcionan
            if (dto.getCategorias() != null && !dto.getCategorias().isEmpty()) {
                List<CategoriaEntity> categorias = dto.getCategorias().stream()
                        .map(categoryId -> categoriaRepository.findById(categoryId)
                                .orElseThrow(
                                        () -> new RuntimeException("Categoría no encontrada con ID: " + categoryId)))
                        .collect(Collectors.toList());
                entity.setCategorias(categorias);
            }
            // Asignar los usuarios si se proporcionan
            if (dto.getUsuarios() != null && !dto.getUsuarios().isEmpty()) {
                List<UserEntity> usuarios = dto.getUsuarios().stream()
                        .map(userId -> usuarioRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId)))
                        .collect(Collectors.toList());
                entity.setUsuarios(usuarios);
            }
            // Asignar los descuentos si se proporcionan
            if (dto.getDescuento() != null && !dto.getDescuento().isEmpty()) {
                List<DescuentoEntity> descuentos = dto.getDescuento().stream()
                        .map(descuentoId -> descuentoRepository.findById(descuentoId)
                                .orElseThrow(
                                        () -> new RuntimeException("Descuento no encontrado con ID: " + descuentoId)))
                        .collect(Collectors.toList());
                entity.setDiscounts(descuentos);
            }
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error al crear el producto: " + e.getMessage());
        }

    }

    // Método auxiliar para guardar la imagen en Minio
    private void saveImage(InputStream inputStream, String objectName) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(objectName)
                    .stream(inputStream, inputStream.available(), -1)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar la imagen de la marca: " + e.getMessage());
        }
    }

    // Método auxiliar para eliminar la imagen de Minio
    private void deleteImage(String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la imagen de la marca: " + e.getMessage());
        }
    }

    // Método auxiliar para obtener la URL de la imagen desde Minio
    private String getUrlImagen(String objectName) {
        try {
            // Construir y devolver la URL de la imagen
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(BUCKET_NAME)
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener la URL de la imagen: " + e.getMessage());
        }
    }

    @Override
    public List<ProductoDto> getAllProductoS(PageableQuery pageableQuery) {
        Sort sort = Sort.by(Sort.Direction.fromString(pageableQuery.EnOrden()),
                pageableQuery.OrdenadoPor());
        PageRequest pageable = PageRequest.of(pageableQuery.Pagina(),
                pageableQuery.ElementosPorPagina(), sort);
        Page<ProductoEntity> productPage = productoRepository.findAll(pageable);
        return productPage.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

}
