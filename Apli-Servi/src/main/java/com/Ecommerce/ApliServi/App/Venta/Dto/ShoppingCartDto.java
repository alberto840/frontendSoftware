package com.Ecommerce.ApliServi.App.Venta.Dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ShoppingCartDto {
    private int id;
    private int quantity;
    private int productId;
    private int purchaseRecordId;
}
