package com.rateiopro.api.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${senha.secreta}")
    private String senhaSecreta;

    public String gerarToken(String email){
        try {
            Algorithm algorithm = Algorithm.HMAC256(senhaSecreta);
            return JWT.create()
                    .withIssuer("rateioPro")
                    .withSubject(email)
                    .withExpiresAt(LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00")))
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar o token JWT");
        }
    }

    public String validarToken(String token){

        try {
            Algorithm algorithm = Algorithm.HMAC256(senhaSecreta);
            return JWT.require(algorithm)
                    .withIssuer("rateioPro")
                    .build()
                    .verify(token)
                    .getSubject()
                    ;

        } catch (JWTVerificationException exception){
            throw new RuntimeException("Token JWT inválido!");
        }
    }
}
