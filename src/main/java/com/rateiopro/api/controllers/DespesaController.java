package com.rateiopro.api.controllers;

import com.rateiopro.api.domain.dadosDespesa.*;
import com.rateiopro.api.domain.dadosGrupo.GrupoRepository;
import com.rateiopro.api.domain.dadosUsuario.Usuario;
import com.rateiopro.api.domain.dadosUsuario.UsuarioRepository;
import com.rateiopro.api.domain.dadosUsuarioGrupo.UsuarioGrupoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @Autowired
    private DespesaService despesaService;

    @PostMapping("/grupos/{idGrupo}/despesas")
    @Transactional
    public ResponseEntity criarDespesa(@RequestBody @Valid DadosCadastroDespesa dados,
                                       @AuthenticationPrincipal Usuario usuario,
                                       @PathVariable Long idGrupo){

        var grupo = despesaService.pegarGrupoPertencenteAoUsuario(usuario.getId(),idGrupo);

        var despesa = new Despesa(dados,grupo,usuario);
        despesaRepository.save(despesa);

        return ResponseEntity.ok().body(new DadosCompletosDespesa(despesa));
    }

    @GetMapping("/grupos/{idGrupo}/despesas")
    public ResponseEntity<Page<DadosCompletosDespesa>> listarDespesas(@PathVariable Long idGrupo,
                                                                      @AuthenticationPrincipal Usuario usuario,
                                                                      @PageableDefault(sort = {"id"}) Pageable pageable){

        var grupo = despesaService.pegarGrupoPertencenteAoUsuario(usuario.getId(), idGrupo);

        var despesasDoGrupo = despesaRepository.findAllByGrupoAndAtivoTrue(grupo,pageable).map(DadosCompletosDespesa::new);

        return ResponseEntity.ok(despesasDoGrupo);
    }

    @PutMapping("/despesas/{id}")
    @Transactional
    public ResponseEntity atualizarDespesa(@RequestBody DadosAtualizacaoDespesa dados,
                                           @AuthenticationPrincipal Usuario usuario,
                                           @PathVariable Long id){
        var despesa = despesaRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Despesa não encontrada"));

        var grupo = despesa.getGrupo();

        despesaService.checarSeUsuarioPodeAtualizarEDeletar(usuario.getId(), grupo.getId(),despesa);

        despesa.atualizarInformacoes(dados);

        return ResponseEntity.ok(new DadosAtualizacaoDespesa(despesa));
    }

    @DeleteMapping("/despesas/{id}")
    @Transactional
    public ResponseEntity deletarTransacao(@PathVariable Long id,
                                           @AuthenticationPrincipal Usuario usuario){
        var despesa = despesaRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Despesa não encontrada"));

        var grupo = despesa.getGrupo();

        despesaService.checarSeUsuarioPodeAtualizarEDeletar(usuario.getId(), grupo.getId(), despesa);

        despesa.desativar();

        return ResponseEntity.noContent().build();
    }

    @PutMapping("despesas/{idDespesa}/reativar")
    @Transactional
    public ResponseEntity reativarDespesa(@PathVariable Long idDespesa,@AuthenticationPrincipal Usuario usuario){

        var despesa = despesaRepository.findByIdAndAtivoFalse(idDespesa)
                .orElseThrow(() -> new EntityNotFoundException("Despesa não encontrada"));


        var grupo = despesa.getGrupo();

        despesaService.reativarDespesaParaDono(usuario.getId(), grupo.getId(), despesa);

        return ResponseEntity.ok().build();
    }
}
