package com.Ecommerce.ApliServi.App.Usuario_Complemento.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Ecommerce.ApliServi.App.Usuario_Complemento.Entity.CommentEntity;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {

    List<CommentEntity> findByUser_IdUsuario(int userId);

}
