package com.Ecommerce.ApliServi.App.Usuario.Entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Us_user")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private int idUsuario;
    @Column(name = "email", unique = false, nullable = false, length = 250)
    private String email;
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "username", unique = false, nullable = false, length = 50)
    private String username;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private PersonaEntity idpersona;
    @Column(name = "account_non_expired")
    private boolean accountNonExpired;

    @Column(name = "account_non_locked")
    private boolean accountNonLocked;

    @Column(name = "credentials_non_expired")
    private boolean credentialsNonExpired;

    @Column(name = "roles")
    private boolean enabled;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "User_Roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<RolesEntity> roles;
}
