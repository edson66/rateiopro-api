package com.rateiopro.api.domain.dadosGrupo;

import com.rateiopro.api.domain.dadosUsuario.Usuario;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GrupoRepository extends JpaRepository<Grupo,Long> {


    @Query("SELECT ug.grupo FROM UsuarioGrupo ug WHERE ug.usuario = :usuario AND ug.grupo.ativo=true")
    Page<Grupo> findGruposAtivosByUsuario(@Param("usuario") Usuario usuario,Pageable pageable);

    Grupo findByCodigoConviteAndAtivoTrue(@NotBlank String s);

    @Query("SELECT ug.grupo FROM UsuarioGrupo ug WHERE ug.usuario = :usuario AND ug.grupo.ativo = true AND ug.grupo.id = :id")
    Optional<Grupo> findByIdAndUsuarioAndAtivoTrue(@Param("id") Long id,@Param("usuario") Usuario usuario);

    Optional<Grupo> findByIdAndAtivoFalse(Long grupoId);
}
