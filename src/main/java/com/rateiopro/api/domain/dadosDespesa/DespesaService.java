package com.rateiopro.api.domain.dadosDespesa;

import com.rateiopro.api.domain.dadosGrupo.Grupo;
import com.rateiopro.api.domain.dadosUsuarioGrupo.PerfilGrupo;
import com.rateiopro.api.domain.dadosUsuarioGrupo.UsuarioGrupoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class DespesaService {

    @Autowired
    private UsuarioGrupoRepository usuarioGrupoRepository;

    public Grupo pegarGrupoPertencenteAoUsuario(Long usuarioId, Long grupoId){
        var usuarioGrupo = usuarioGrupoRepository.findByUsuarioIdAndGrupoId(usuarioId,grupoId)
                .orElseThrow(() -> new EntityNotFoundException("Você não faz parte desse grupo"));

        if (!usuarioGrupo.getGrupo().isAtivo()) {
            throw new EntityNotFoundException("Este grupo está inativo.");
        }

        return usuarioGrupo.getGrupo();
    }

    public void checarSeUsuarioPodeAtualizarEDeletar(Long usuarioId, Long grupoId,Despesa despesa){
        var usuarioGrupo = usuarioGrupoRepository.findByUsuarioIdAndGrupoId(usuarioId,grupoId)
                .orElseThrow(() -> new EntityNotFoundException("Você não faz parte desse grupo"));

        if (usuarioGrupo.getPerfil().equals(PerfilGrupo.MEMBRO) && !Objects.equals(despesa.getPagoPorUsuario().getId(), usuarioId)){
            throw new RuntimeException("Apenas o dono do grupo ou quem criou a despesa pode atualizá-la ou deletá-la!");
        }
    }

    public void reativarDespesaParaDono(Long usuarioId, Long grupoId,Despesa despesa){
        var usuarioGrupo = usuarioGrupoRepository.findByUsuarioIdAndGrupoId(usuarioId,grupoId)
                .orElseThrow(() -> new EntityNotFoundException("Você não faz parte desse grupo"));

        if (usuarioGrupo.getPerfil().equals(PerfilGrupo.MEMBRO) && !Objects.equals(despesa.getPagoPorUsuario().getId(), usuarioId)){
            throw new RuntimeException("Apenas o dono do grupo ou quem criou a despesa pode reativar");
        }

        despesa.reativar();
    }
}
