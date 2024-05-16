package com.Ecommerce.ApliServi.App.Producto.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Ecommerce.ApliServi.App.Producto.Entity.ProductoEntity;

@Repository
public interface ProductoRepository extends JpaRepository<ProductoEntity, Integer> {

}
