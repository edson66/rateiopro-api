package com.rateiopro.api.domain.dadosGrupo;

import com.rateiopro.api.domain.dadosUsuario.Usuario;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrupoRepository extends JpaRepository<Grupo,Long> {
    Page<Grupo> findByUsuario(Usuario usuario, Pageable pageable);

    Grupo findByIdAndUsuarioAndAtivoTrue(Long id, Usuario usuario);

    Grupo findByCodigoConvite(@NotBlank String s);
}
