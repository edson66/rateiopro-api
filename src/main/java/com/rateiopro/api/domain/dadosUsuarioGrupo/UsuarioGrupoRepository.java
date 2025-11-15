package com.rateiopro.api.domain.dadosUsuarioGrupo;


import com.rateiopro.api.domain.dadosGrupo.Grupo;
import com.rateiopro.api.domain.dadosUsuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioGrupoRepository extends JpaRepository<UsuarioGrupo,Long> {
    Optional<UsuarioGrupo> findByUsuarioIdAndGrupoId(Long id, Long idGrupo);

    boolean existsByUsuarioAndGrupo(Usuario usuario, Grupo grupo);

    List<UsuarioGrupo> findByGrupo(Grupo grupo);
}
