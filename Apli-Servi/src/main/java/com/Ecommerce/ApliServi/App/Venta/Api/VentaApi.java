package com.Ecommerce.ApliServi.App.Venta.Api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Ecommerce.ApliServi.App.Venta.Dto.ventaDto;
import com.Ecommerce.ApliServi.App.Venta.Dto.Respuesta.Respuesta;
import com.Ecommerce.ApliServi.App.Venta.Service.Service.ServiciosVenta;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/venta")
public class VentaApi {
    private final ServiciosVenta serviciosVenta;

    @Autowired
    public VentaApi(ServiciosVenta serviciosVenta) {
        this.serviciosVenta = serviciosVenta;
    }

    @PostMapping("/crear")
    public ResponseEntity<Respuesta> createVenta(@RequestBody ventaDto ventaDto) {
        try {
            ventaDto createdVenta = serviciosVenta.createVenta(ventaDto);
            return ResponseEntity.ok(new Respuesta("SUCCESS", createdVenta));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Respuesta("ERROR", e.getMessage()));
        }
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Respuesta> getVentaById(@PathVariable int id) {
        try {
            ventaDto ventaDto = serviciosVenta.getVentaById(id);
            return ResponseEntity.ok(new Respuesta("SUCCESS", ventaDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Respuesta("ERROR", "Venta no encontrada por id: " + id));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Respuesta> getAllVentas() {
        try {
            List<ventaDto> ventas = serviciosVenta.getAllVentas();
            return ResponseEntity.ok(new Respuesta("SUCCESS", ventas));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Respuesta("ERROR", e.getMessage()));
        }
    }

}
