package com.rateiopro.api.domain.dadosGrupo;

import com.rateiopro.api.domain.dadosUsuario.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GrupoService {

    @Autowired
    private GrupoRepository repository;

    public Grupo bucarGrupoPorId(Usuario usuario,Long id){
        return repository.findByIdAndUsuarioAndAtivoTrue(id,usuario);
    }
}
