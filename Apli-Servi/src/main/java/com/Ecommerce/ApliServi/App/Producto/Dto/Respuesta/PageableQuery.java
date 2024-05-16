package com.Ecommerce.ApliServi.App.Producto.Dto.Respuesta;

public interface PageableQuery {
    Integer Pagina();

    Integer ElementosPorPagina();

    String OrdenadoPor();

    String EnOrden();
}
