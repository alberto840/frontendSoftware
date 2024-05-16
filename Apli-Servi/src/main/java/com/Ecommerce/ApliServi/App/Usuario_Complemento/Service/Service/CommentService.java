package com.Ecommerce.ApliServi.App.Usuario_Complemento.Service.Service;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.Ecommerce.ApliServi.App.Producto.Entity.ProductoEntity;
import com.Ecommerce.ApliServi.App.Producto.Repository.ProductoRepository;
import com.Ecommerce.ApliServi.App.Usuario.Entity.UserEntity;
import com.Ecommerce.ApliServi.App.Usuario.Repository.UserRepository;
import com.Ecommerce.ApliServi.App.Usuario_Complemento.Dto.CommentDto;
import com.Ecommerce.ApliServi.App.Usuario_Complemento.Dto.Respuesta.PageableQuery;
import com.Ecommerce.ApliServi.App.Usuario_Complemento.Entity.CommentEntity;
import com.Ecommerce.ApliServi.App.Usuario_Complemento.Repository.CommentRepository;
import com.Ecommerce.ApliServi.App.Usuario_Complemento.Service.Interface.CommentInterface;

@Service
public class CommentService implements CommentInterface {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ProductoRepository productoRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, UserRepository userRepository,
            ProductoRepository productoRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public CommentDto save(CommentDto dto) {
        CommentEntity entity = convertToEntity(dto);
        CommentEntity savedEntity = commentRepository.save(entity);
        return convertToDto(savedEntity);
    }

    @Override
    public CommentDto findById(int id) {
        CommentEntity entity = commentRepository.findById(id).orElse(null);
        return convertToDto(entity);
    }

    @Override
    public List<CommentDto> findAll() {
        List<CommentEntity> entities = commentRepository.findAll();
        return entities.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto update(int id, CommentDto dto) {
        CommentEntity existingEntity = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado con ID: " + id));

        // Actualiza los campos del comentario existente con los valores del DTO
        existingEntity.setComment(dto.getComentario());
        existingEntity.setCommentDate(dto.getFechaComentario());
        existingEntity.setRating(dto.getPuntuacion());

        // Si es necesario, actualiza el usuario asociado al comentario
        if (dto.getId_user() != 0) {
            UserEntity userEntity = userRepository.findById(dto.getId_user())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getId_user()));
            existingEntity.setUser(userEntity);
        }

        // Si es necesario, actualiza el producto asociado al comentario
        if (dto.getId_producto() != 0) {
            ProductoEntity productoEntity = productoRepository.findById(dto.getId_producto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + dto.getId_producto()));
            existingEntity.setProduct(productoEntity);
        }

        // Guarda la entidad actualizada en la base de datos
        CommentEntity updatedEntity = commentRepository.save(existingEntity);

        // Convierte y devuelve la entidad actualizada como DTO
        return convertToDto(updatedEntity);
    }

    @Override
    public void deleteById(int id) {
        commentRepository.deleteById(id);
    }

    @Override
    public List<CommentDto> findCommentDtos(PageableQuery pageableQuery) {
        Sort sort = Sort.by(Sort.Direction.fromString(pageableQuery.EnOrden()),
                pageableQuery.OrdenadoPor());
        PageRequest pageRequest = PageRequest.of(pageableQuery.Pagina(), pageableQuery.ElementosPorPagina(),
                sort);

        Page<CommentEntity> commentPage = commentRepository.findAll(pageRequest);
        return commentPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> finCommentDtos(int userId) {
        List<CommentEntity> commentEntities = commentRepository.findByUser_IdUsuario(userId);
        return commentEntities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CommentEntity convertToEntity(CommentDto dto) {
        CommentEntity entity = new CommentEntity();
        entity.setCommentId(dto.getId());
        entity.setComment(dto.getComentario());
        entity.setCommentDate(dto.getFechaComentario());
        entity.setRating(dto.getPuntuacion());

        if (dto.getId_user() != 0) {
            UserEntity userEntity = userRepository.findById(dto.getId_user())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getId_user()));
            entity.setUser(userEntity);
        }

        if (dto.getId_producto() != 0) {
            ProductoEntity productoEntity = productoRepository.findById(dto.getId_producto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + dto.getId_producto()));
            entity.setProduct(productoEntity);
        }

        return entity;
    }

    public CommentDto convertToDto(CommentEntity entity) {
        CommentDto dto = new CommentDto();
        dto.setId(entity.getCommentId());
        dto.setComentario(entity.getComment());
        dto.setFechaComentario(entity.getCommentDate());
        dto.setPuntuacion(entity.getRating());
        if (entity.getUser() != null) {
            dto.setId_user(entity.getUser().getIdUsuario());
        }
        if (entity.getProduct() != null) {
            dto.setId_producto(entity.getProduct().getId());
        }
        return dto;
    }

}
