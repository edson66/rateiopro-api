package com.rateiopro.api.domain.dadosUsuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DadosAtualizacaoUsuario(
        @Email
        String email,
        String nome,
        String senha
) {
    public DadosAtualizacaoUsuario(Usuario usuario) {
        this(usuario.getEmail(), usuario.getNome(), "suaSenha");
    }
}
