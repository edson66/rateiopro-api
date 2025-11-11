package com.rateiopro.api.domain.dadosGrupo;

import jakarta.validation.constraints.NotBlank;

public record DadosCadastroGrupo(
        @NotBlank
        String nome,
        String descricao
) {
    public DadosCadastroGrupo(Grupo grupoASerCriado) {
        this(grupoASerCriado.getNome(), grupoASerCriado.getDescricao());
    }
}
