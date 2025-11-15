package com.rateiopro.api.domain.dadosGrupo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosCompletosGrupo(
        @NotNull
        Long id,
        @NotBlank
        String nome,
        String descricao,
        String codigoConvite
) {
    public DadosCompletosGrupo(Grupo grupo) {
        this(grupo.getId(), grupo.getNome(), grupo.getDescricao(), grupo.getCodigoConvite());
    }
}
