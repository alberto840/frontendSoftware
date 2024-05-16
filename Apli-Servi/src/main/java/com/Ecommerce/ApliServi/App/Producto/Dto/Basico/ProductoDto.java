package com.Ecommerce.ApliServi.App.Producto.Dto.Basico;

import java.math.BigDecimal;
import java.util.List;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
public class ProductoDto {
    private int id;
    private String nombre;
    private String descripcion;
    private String imageUrl;
    private Integer stock;
    private BigDecimal precio;
    private int marca;
    private List<Integer> categorias;
    private List<Integer> usuarios;
    private List<Integer> descuento;
}
