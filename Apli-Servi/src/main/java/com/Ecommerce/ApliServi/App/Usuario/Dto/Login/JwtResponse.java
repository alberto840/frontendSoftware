package com.Ecommerce.ApliServi.App.Usuario.Dto.Login;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class JwtResponse {
    private String username;
    private String accessToken;
    private String refreshToken;

}