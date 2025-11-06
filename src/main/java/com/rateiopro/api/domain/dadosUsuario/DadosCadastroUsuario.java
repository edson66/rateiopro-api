package com.rateiopro.api.domain.dadosUsuario;

import jakarta.validation.constraints.NotBlank;

public record DadosCadastroUsuario(
        @NotBlank
        String email,
        @NotBlank
        String nome,
        @NotBlank
        String senha
) {
}
