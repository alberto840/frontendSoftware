package com.Ecommerce.ApliServi.App.Venta.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Ecommerce.ApliServi.App.Venta.Entity.ShoppingCartEntity;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCartEntity, Integer> {

}
