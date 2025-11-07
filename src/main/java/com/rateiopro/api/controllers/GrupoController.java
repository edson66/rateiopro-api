package com.rateiopro.api.controllers;


import com.rateiopro.api.domain.dadosGrupo.DadosCadastroGrupo;
import com.rateiopro.api.domain.dadosGrupo.Grupo;
import com.rateiopro.api.domain.dadosGrupo.GrupoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/grupos")
public class GrupoController {

    @Autowired
    private GrupoRepository repository;

    public ResponseEntity criarGrupo(@RequestBody @Valid DadosCadastroGrupo dados){
        var grupo = new Grupo(dados.nome(),dados.descricao());
        repository.save(grupo);
    }
}
