package com.Ecommerce.ApliServi.App.Usuario.Api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.Ecommerce.ApliServi.App.Usuario.Dto.Login.JwtResponse;
import com.Ecommerce.ApliServi.App.Usuario.Dto.Login.LoginRequest;
import com.Ecommerce.ApliServi.App.Usuario.Dto.Respuesta.Respuesta;
import com.Ecommerce.ApliServi.Util.JwtUtils;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

@RestController
@RequestMapping("/api/auth")
public class LoginApi {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<Respuesta> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = jwtUtils.createAccessToken(authentication);
            String refreshToken = jwtUtils.createRefreshToken(authentication); // Asumiendo que tienes un método para
                                                                               // crear el refresh token

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            JwtResponse jwtResponse = new JwtResponse(username, accessToken, refreshToken);

            return ResponseEntity.ok(new Respuesta("SUCCESS", jwtResponse));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Respuesta("ERROR", "Credenciales inválidas"));
        }
    }

    @GetMapping("/result")
    public ResponseEntity<Respuesta> getTokenValues(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String tokenHeader) {
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String jwtToken = tokenHeader.substring(7);
            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);

            // Obtener los valores del token
            Map<String, Claim> tokenClaims = jwtUtils.returnClaimsFromToken(decodedJWT);

            return ResponseEntity.ok(new Respuesta("SUCCESS", tokenClaims));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Respuesta("ERROR", "Token de autorización no proporcionado o inválido"));
        }
    }

}
