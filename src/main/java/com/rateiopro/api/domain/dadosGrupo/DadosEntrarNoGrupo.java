package com.rateiopro.api.domain.dadosGrupo;

import jakarta.validation.constraints.NotBlank;

public record DadosEntrarNoGrupo(
        @NotBlank
        String codigoConvite
) {
}
