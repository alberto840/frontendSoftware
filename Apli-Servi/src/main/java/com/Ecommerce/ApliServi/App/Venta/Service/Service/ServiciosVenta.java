package com.Ecommerce.ApliServi.App.Venta.Service.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Ecommerce.ApliServi.App.Producto.Entity.ProductoEntity;
import com.Ecommerce.ApliServi.App.Producto.Repository.ProductoRepository;
import com.Ecommerce.ApliServi.App.Usuario.Entity.UserEntity;
import com.Ecommerce.ApliServi.App.Usuario.Repository.UserRepository;
import com.Ecommerce.ApliServi.App.Venta.Dto.ventaDto;
import com.Ecommerce.ApliServi.App.Venta.Entity.VentaEntity;
import com.Ecommerce.ApliServi.App.Venta.Repository.ventaRepository;
import com.Ecommerce.ApliServi.App.Venta.Service.Interface.VentaInterface;

@Service
public class ServiciosVenta implements VentaInterface {
    private final ventaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final UserRepository userRepository;

    @Autowired
    public ServiciosVenta(ventaRepository ventaRepository, ProductoRepository productoRepository,
            UserRepository userRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ventaDto createVenta(ventaDto ventaDto) {
        // Verificar si existen el producto y el usuario
        ProductoEntity productoEntity = productoRepository.findById(ventaDto.getProductoId())
                .orElseThrow(
                        () -> new RuntimeException("El producto con ID " + ventaDto.getProductoId() + " no existe."));

        UserEntity usuarioEntity = userRepository.findById(ventaDto.getUsuarioId())
                .orElseThrow(
                        () -> new RuntimeException("El usuario con ID " + ventaDto.getUsuarioId() + " no existe."));

        // Mapear la venta DTO a la entidad
        VentaEntity ventaEntity = mapToEntity(ventaDto, productoEntity, usuarioEntity);
        // Guardar la venta
        return mapToDto(ventaRepository.save(ventaEntity));
    }

    @Override
    public ventaDto getVentaById(int id) {
        VentaEntity ventaEntity = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));
        return mapToDto(ventaEntity);
    }

    @Override
    public List<ventaDto> getAllVentas() {
        return ventaRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Método para mapear de DTO a entidad
    private VentaEntity mapToEntity(ventaDto ventaDto, ProductoEntity productoEntity, UserEntity usuarioEntity) {
        VentaEntity ventaEntity = new VentaEntity();
        ventaEntity.setCantidad(ventaDto.getCantidad());
        ventaEntity.setFechaVenta(ventaDto.getFechaVenta());
        ventaEntity.setPrecio(ventaDto.getPrecio());
        ventaEntity.setProducto(productoEntity);
        ventaEntity.setUsuario(usuarioEntity);
        return ventaEntity;
    }

    // Método para mapear de entidad a DTO
    private ventaDto mapToDto(VentaEntity ventaEntity) {
        ventaDto ventaDto = new ventaDto();
        ventaDto.setId(ventaEntity.getIdVenta());
        ventaDto.setCantidad(ventaEntity.getCantidad());
        ventaDto.setFechaVenta(ventaEntity.getFechaVenta());
        ventaDto.setPrecio(ventaEntity.getPrecio());
        ventaDto.setProductoId(ventaEntity.getProducto().getId()); // Asumiendo que ProductoEntity tiene un campo 'id'
        ventaDto.setUsuarioId(ventaEntity.getUsuario().getIdUsuario()); // Asumiendo que UserEntity tiene un campo 'id'
        return ventaDto;
    }

}
