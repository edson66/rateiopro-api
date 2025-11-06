package com.rateiopro.api.security;

import com.rateiopro.api.domain.dadosUsuario.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FiltroValidacaoToken extends OncePerRequestFilter {


    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var tokenJWT = pegarToken(request);

        if (tokenJWT != null) {
            var email = tokenService.validarToken(tokenJWT);
            var usuario = usuarioRepository.findByEmail(email);

            var autenticacao = new UsernamePasswordAuthenticationToken(usuario,null,usuario.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(autenticacao);
        }



        doFilter(request,response,filterChain);
    }

    private String pegarToken(HttpServletRequest request){
        var token = request.getHeader("Authorization");

        if (token != null) {
            return token.replace("Bearer ","");
        }

        return null;
    }
}
