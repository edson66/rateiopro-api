package com.rateiopro.api.controllers;

import com.rateiopro.api.domain.dadosDespesa.DadosCadastroDespesa;
import com.rateiopro.api.domain.dadosDespesa.DadosCompletosDespesa;
import com.rateiopro.api.domain.dadosDespesa.Despesa;
import com.rateiopro.api.domain.dadosDespesa.DespesaRepository;
import com.rateiopro.api.domain.dadosGrupo.GrupoRepository;
import com.rateiopro.api.domain.dadosUsuario.Usuario;
import com.rateiopro.api.domain.dadosUsuario.UsuarioRepository;
import com.rateiopro.api.domain.dadosUsuarioGrupo.UsuarioGrupoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping
public class DespesaController {


    @Autowired
    private DespesaRepository despesaRepository;

    @Autowired
    private UsuarioGrupoRepository usuarioGrupoRepository;


    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/grupos/{idGrupo}/despesas")
    @Transactional
    public ResponseEntity criarDespesa(@RequestBody @Valid DadosCadastroDespesa dados,
                                       @AuthenticationPrincipal Usuario usuario,
                                       UriComponentsBuilder builder,
                                       @PathVariable Long idGrupo){


        //perguntar sobre como e saberei a questão do id dos grupos pelo front



        var usuarioGrupo = usuarioGrupoRepository.findByUsuarioIdAndGrupoId(usuario.getId(),idGrupo)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

        var grupo = usuarioGrupo.getGrupo();

        var despesa = new Despesa(dados,grupo,usuario);
        despesaRepository.save(despesa);

        var uri = builder.path("/grupos/{idGrupo}/despesas").buildAndExpand(grupo.getId()).toUri();

        return ResponseEntity.created(uri).body(new DadosCompletosDespesa(despesa));
    }

}
