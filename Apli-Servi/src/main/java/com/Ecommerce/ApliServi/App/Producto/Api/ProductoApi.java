package com.Ecommerce.ApliServi.App.Producto.Api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Ecommerce.ApliServi.App.Producto.Dto.Basico.ProductoDto;
import com.Ecommerce.ApliServi.App.Producto.Dto.Crear.CrearProductoDto;
import com.Ecommerce.ApliServi.App.Producto.Dto.Respuesta.PageableQuery;
import com.Ecommerce.ApliServi.App.Producto.Dto.Respuesta.Respuesta;
import com.Ecommerce.ApliServi.App.Producto.Service.Service.ProductoService;

@RestController
@RequestMapping("/api/producto")
public class ProductoApi {
    private final ProductoService productoService;

    @Autowired
    public ProductoApi(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping("/crear")
    public ResponseEntity<Respuesta> createProductoDto(@ModelAttribute CrearProductoDto productoAuxDto) {
        try {
            ProductoDto productoDto = productoService.createProductoDto(productoAuxDto);
            return ResponseEntity.ok(new Respuesta("SUCCESS", productoDto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Respuesta("ERROR", e.getMessage()));
        }
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Respuesta> getProductoById(@PathVariable int id) {
        try {
            ProductoDto productoDto = productoService.getProductoById(id);
            return ResponseEntity.ok(new Respuesta("SUCCESS", productoDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Respuesta("ERROR", "Producto no encontrado con ID: " + id));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Respuesta> getAllProductos() {
        try {
            List<ProductoDto> productos = productoService.getAllProducto();
            return ResponseEntity.ok(new Respuesta("SUCCESS", productos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Respuesta("ERROR", "Error al obtener todos los productos: " + e.getMessage()));
        }
    }

    @GetMapping("/all-paginated")
    public ResponseEntity<Respuesta> getAllProductos(PageableQuery pageableQuery) {
        try {
            List<ProductoDto> productos = productoService.getAllProductoS(pageableQuery);
            return ResponseEntity.ok(new Respuesta("SUCCESS", productos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Respuesta("ERROR", "Error al obtener todos los productos paginados: " + e.getMessage()));
        }
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<Respuesta> updateProducto(@PathVariable int id,
            @ModelAttribute CrearProductoDto productoAuxDto) {
        try {
            ProductoDto updatedProducto = productoService.updateProducto(id, productoAuxDto);
            return ResponseEntity.ok(new Respuesta("SUCCESS", updatedProducto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Respuesta("ERROR", "Error al actualizar el producto: " + e.getMessage()));
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Respuesta> deleteProducto(@PathVariable int id) {
        try {
            productoService.deleteProducto(id);
            return ResponseEntity.ok(new Respuesta("SUCCESS", "Producto eliminado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Respuesta("ERROR", "Error al eliminar el producto: " + e.getMessage()));
        }
    }
}
