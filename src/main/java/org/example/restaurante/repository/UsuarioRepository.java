package org.example.restaurante.repository;

import org.example.restaurante.model.Usuario;
import org.example.restaurante.model.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsuario(String usuario);

    @Query("select count(u) from Usuario u where u.tipo_usuario = :tipo and u.ativo = true")
    long contarAtivosPorTipo(@Param("tipo") TipoUsuario tipo);
}
