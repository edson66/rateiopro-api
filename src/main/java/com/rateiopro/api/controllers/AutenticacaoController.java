package com.rateiopro.api.controllers;

import com.rateiopro.api.domain.dadosUsuario.*;
import com.rateiopro.api.security.TokenDto;
import com.rateiopro.api.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RoleRepository roleRepository;


    @PostMapping("/cadastrar")
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroUsuario dados){
        var senhaCodificada = usuarioService.codificar(dados.senha());

        Role roleUser = roleRepository.findByNome(AppRole.ROLE_USER);
        var usuario = new Usuario(dados.email(), dados.nome(), senhaCodificada,roleUser);

        usuarioRepository.save(usuario);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    @Transactional
    public ResponseEntity login(@RequestBody @Valid DadosLoginUsuario dados){
        var usuario = usuarioRepository.findByEmail(dados.email());
        if (!usuario.isAtivo()){
            throw new RuntimeException("Usuário inativo.");
        }
        var usuarioAutenticado = manager.authenticate(new UsernamePasswordAuthenticationToken(dados.email(),dados.senha()));
        var tokenJWT = tokenService.gerarToken(usuarioAutenticado.getName());

        return ResponseEntity.ok((new TokenDto(tokenJWT)));
    }
}
