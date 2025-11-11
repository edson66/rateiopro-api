package com.rateiopro.api.domain.dadosUsuario;

import jakarta.validation.constraints.NotBlank;

public record DadosDetalharUsuario(
        String email,
        String nome
) {
    public DadosDetalharUsuario(Usuario usuario) {
        this(usuario.getEmail(), usuario.getNome());
    }
}
