package com.rateiopro.api.controllers;

import com.rateiopro.api.domain.dadosUsuario.DadosAtualizacaoUsuario;
import com.rateiopro.api.domain.dadosUsuario.DadosDetalharUsuario;
import com.rateiopro.api.domain.dadosUsuario.Usuario;
import com.rateiopro.api.domain.dadosUsuario.UsuarioService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity exibirDados(@AuthenticationPrincipal Usuario usuario){
        return ResponseEntity.ok(new DadosDetalharUsuario(usuario));
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizarDados(@RequestBody @Valid DadosAtualizacaoUsuario dados,
                                         @AuthenticationPrincipal Usuario usuario){
        if (dados.senha() != null){
            var senhaCodificada = usuarioService.codificar(dados.senha());
            usuario.atualizarDados(dados,senhaCodificada);
        }
        usuario.atualizarDados(dados, dados.senha());

        return ResponseEntity.ok(new DadosAtualizacaoUsuario(usuario));
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity desativarUsuario(@AuthenticationPrincipal Usuario usuario){
        usuario.desativar();

        return ResponseEntity.noContent().build();
    }
}
