package com.Ecommerce.ApliServi.App.Venta.Api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Ecommerce.ApliServi.App.Venta.Dto.ShoppingCartDto;
import com.Ecommerce.ApliServi.App.Venta.Dto.Respuesta.Respuesta;
import com.Ecommerce.ApliServi.App.Venta.Service.Interface.ShoppingCarInterface;
import java.util.List;

@RestController
@RequestMapping("/api/shopping-cart")
public class ShoppingCarApi {
    private final ShoppingCarInterface shoppingCarInterface;

    public ShoppingCarApi(ShoppingCarInterface shoppingCarInterface) {
        this.shoppingCarInterface = shoppingCarInterface;
    }

    @PostMapping("/create")
    public ResponseEntity<Respuesta> createShoppingCart(@RequestBody ShoppingCartDto shoppingCartDto) {
        try {
            ShoppingCartDto createdShoppingCart = shoppingCarInterface.createShoppingCart(shoppingCartDto);
            return ResponseEntity.ok(new Respuesta("SUCCESS", createdShoppingCart));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Respuesta("ERROR", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Respuesta> getShoppingCartById(@PathVariable int id) {
        try {
            ShoppingCartDto shoppingCartDto = shoppingCarInterface.getShoppingCartById(id);
            return ResponseEntity.ok(new Respuesta("SUCCESS", shoppingCartDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Respuesta("ERROR", "Shopping Cart not found for id: " + id));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Respuesta> getAllShoppingCarts() {
        try {
            List<ShoppingCartDto> shoppingCarts = shoppingCarInterface.getAllShoppingCarts();
            return ResponseEntity.ok(new Respuesta("SUCCESS", shoppingCarts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Respuesta("ERROR", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Respuesta> deleteShoppingCartById(@PathVariable int id) {
        try {
            shoppingCarInterface.deleteShoppingCartById(id);
            return ResponseEntity.ok(new Respuesta("SUCCESS", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Respuesta("ERROR", e.getMessage()));
        }
    }
}
