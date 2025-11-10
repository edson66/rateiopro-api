package com.rateiopro.api.domain.dadosGrupo;

import com.rateiopro.api.domain.dadosUsuario.Usuario;
import com.rateiopro.api.domain.dadosUsuarioGrupo.PerfilGrupo;
import com.rateiopro.api.domain.dadosUsuarioGrupo.UsuarioGrupoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GrupoService {

    @Autowired
    private GrupoRepository repository;

    @Autowired
    private UsuarioGrupoRepository usuarioGrupoRepository;

    public Grupo bucarGrupoAtivoPorId(Usuario usuario,Long id){
        return repository.findByIdAndUsuarioAndAtivoTrue(id,usuario)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado ou inacessível."));
    }

    public Grupo buscarGrupoParaDono(Long usuarioId,Long grupoId){

        var usuarioGrupo = usuarioGrupoRepository.findByUsuarioIdAndGrupoId(usuarioId,grupoId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

        if (usuarioGrupo.getPerfil().equals(PerfilGrupo.MEMBRO)){
            throw new RuntimeException("Apenas o dono do grupo pode alterar os dados");
        }

        if (!usuarioGrupo.getUsuario().isAtivo() || !usuarioGrupo.getGrupo().isAtivo()){
            throw new RuntimeException("Grupo ou Usuário inativo(s).");
        }

        return usuarioGrupo.getGrupo();
    }

    public void reativarGrupoParaDono(Long usuarioId,Long grupoId){

        var usuarioGrupo = usuarioGrupoRepository.findByUsuarioIdAndGrupoId(usuarioId,grupoId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

        if (usuarioGrupo.getPerfil().equals(PerfilGrupo.MEMBRO)){
            throw new RuntimeException("Apenas o dono do grupo pode reativar o grupo");
        }

        Grupo grupo = repository.findByIdAndAtivoFalse(grupoId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado ou já está ativo"));

        grupo.reativar();
    }
}
