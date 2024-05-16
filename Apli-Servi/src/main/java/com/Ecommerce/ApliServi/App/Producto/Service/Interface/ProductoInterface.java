package com.Ecommerce.ApliServi.App.Producto.Service.Interface;

import java.util.List;

import com.Ecommerce.ApliServi.App.Producto.Dto.Basico.ProductoDto;
import com.Ecommerce.ApliServi.App.Producto.Dto.Crear.CrearProductoDto;
import com.Ecommerce.ApliServi.App.Producto.Dto.Respuesta.PageableQuery;

public interface ProductoInterface {
    ProductoDto createProductoDto(CrearProductoDto productoAuxDto);

    ProductoDto getProductoById(int id);

    List<ProductoDto> getAllProducto();

    List<ProductoDto> getAllProductoS(PageableQuery pageableQuery);

    ProductoDto updateProducto(int id, CrearProductoDto productoAuxDto);

    void deleteProducto(int id);
}