package com.Ecommerce.ApliServi.App.Venta.Service.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Ecommerce.ApliServi.App.Producto.Entity.ProductoEntity;
import com.Ecommerce.ApliServi.App.Producto.Repository.ProductoRepository;
import com.Ecommerce.ApliServi.App.Venta.Dto.ShoppingCartDto;
import com.Ecommerce.ApliServi.App.Venta.Entity.PurchaseRecordEntity;
import com.Ecommerce.ApliServi.App.Venta.Entity.ShoppingCartEntity;
import com.Ecommerce.ApliServi.App.Venta.Repository.PurchaseRecordRepository;
import com.Ecommerce.ApliServi.App.Venta.Repository.ShoppingCartRepository;
import com.Ecommerce.ApliServi.App.Venta.Service.Interface.ShoppingCarInterface;

@Service
class ShoppingCarServicio implements ShoppingCarInterface {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductoRepository productoRepository;
    private final PurchaseRecordRepository purchaseRecordRepository;

    @Autowired
    public ShoppingCarServicio(ShoppingCartRepository shoppingCartRepository, ProductoRepository productoRepository,
            PurchaseRecordRepository purchaseRecordRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.productoRepository = productoRepository;
        this.purchaseRecordRepository = purchaseRecordRepository;
    }

    @Override
    public ShoppingCartDto createShoppingCart(ShoppingCartDto shoppingCartDto) {
        ShoppingCartEntity shoppingCartEntity = mapToEntity(shoppingCartDto);
        ShoppingCartEntity savedEntity = shoppingCartRepository.save(shoppingCartEntity);
        return mapToDto(savedEntity);
    }

    @Override
    public ShoppingCartDto getShoppingCartById(int id) {
        ShoppingCartEntity shoppingCartEntity = shoppingCartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shopping Cart not found with ID: " + id));
        return mapToDto(shoppingCartEntity);
    }

    @Override
    public List<ShoppingCartDto> getAllShoppingCarts() {
        List<ShoppingCartEntity> shoppingCartEntities = shoppingCartRepository.findAll();
        return shoppingCartEntities.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteShoppingCartById(int id) {
        ShoppingCartEntity shoppingCartEntity = shoppingCartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shopping Cart not found with ID: " + id));
        shoppingCartRepository.delete(shoppingCartEntity);
    }

    private ShoppingCartEntity mapToEntity(ShoppingCartDto shoppingCartDto) {
        ShoppingCartEntity shoppingCartEntity = new ShoppingCartEntity();
        shoppingCartEntity.setId(shoppingCartDto.getId());
        shoppingCartEntity.setQuantity(shoppingCartDto.getQuantity());

        // Recuperar el producto por su ID
        ProductoEntity productoEntity = productoRepository.findById(shoppingCartDto.getProductId())
                .orElseThrow(
                        () -> new RuntimeException("Producto no encontrado con ID: " + shoppingCartDto.getProductId()));
        shoppingCartEntity.setProducto(productoEntity);

        // Recuperar el registro de compra por su ID
        PurchaseRecordEntity purchaseRecordEntity = purchaseRecordRepository
                .findById(shoppingCartDto.getPurchaseRecordId())
                .orElseThrow(() -> new RuntimeException(
                        "Registro de compra no encontrado con ID: " + shoppingCartDto.getPurchaseRecordId()));
        shoppingCartEntity.setPurchaseRecordId(purchaseRecordEntity);

        return shoppingCartEntity;
    }

    private ShoppingCartDto mapToDto(ShoppingCartEntity shoppingCartEntity) {
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(shoppingCartEntity.getId());
        shoppingCartDto.setQuantity(shoppingCartEntity.getQuantity());
        shoppingCartDto.setProductId(shoppingCartEntity.getProducto().getId());
        shoppingCartDto.setPurchaseRecordId(shoppingCartEntity.getPurchaseRecordId().getId());
        return shoppingCartDto;
    }

}
