package com.rateiopro.api.controllers;

import com.rateiopro.api.domain.dadosUsuario.UsuarioRepository;
import com.rateiopro.api.domain.dadosUsuario.dadosOAuth.OAuthService;
import com.rateiopro.api.security.TokenDto;
import com.rateiopro.api.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/login")
public class AuthExternasController {

    @Autowired
    private OAuthService service;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    @GetMapping("/github")
    public ResponseEntity<Void> redirecionarParaGithub(){
        var url = service.gerarUrl();

        var header = new HttpHeaders();
        header.setLocation(URI.create(url));

        return new ResponseEntity<>(header, HttpStatus.FOUND);
    }

    @GetMapping("/github/autorizado")
    public ResponseEntity obterDadosEAutenticar(@RequestParam String code){
        var email = service.buscarEmail(code);

        var usuario = usuarioRepository.findByEmailAndAtivoTrue(email);

        if (usuario == null){
            throw new RuntimeException("Não existe uma conta atrelada à este email!");
        }

        var authentication = new UsernamePasswordAuthenticationToken(usuario,null,usuario.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);


        var tokenJWT = tokenService.gerarToken(email);

        return ResponseEntity.ok(new TokenDto(tokenJWT));
    }
}
