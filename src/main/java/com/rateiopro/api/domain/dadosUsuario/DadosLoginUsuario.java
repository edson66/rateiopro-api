package com.rateiopro.api.domain.dadosUsuario;

import jakarta.validation.constraints.NotBlank;

public record DadosLoginUsuario(
        @NotBlank
        String email,
        @NotBlank
        String senha
) {
}
