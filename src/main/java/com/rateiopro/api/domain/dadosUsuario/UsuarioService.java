package com.rateiopro.api.domain.dadosUsuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private PasswordEncoder encoder;

    public String codificar(String senha){
        return encoder.encode(senha);
    }
}
