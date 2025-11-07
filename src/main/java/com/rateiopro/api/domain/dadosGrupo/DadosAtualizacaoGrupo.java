package com.rateiopro.api.domain.dadosGrupo;

import jakarta.validation.constraints.NotBlank;

public record DadosAtualizacaoGrupo(
        String nome,
        String descricao
) {
    public DadosAtualizacaoGrupo(Grupo grupo) {
        this(grupo.getNome(), grupo.getDescricao());
    }
}
