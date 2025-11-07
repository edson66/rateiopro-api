package com.rateiopro.api.controllers;


import com.rateiopro.api.domain.dadosGrupo.*;
import com.rateiopro.api.domain.dadosUsuario.Usuario;
import com.rateiopro.api.domain.dadosUsuario.UsuarioService;
import com.rateiopro.api.domain.dadosUsuarioGrupo.PerfilGrupo;
import com.rateiopro.api.domain.dadosUsuarioGrupo.UsuarioGrupo;
import com.rateiopro.api.domain.dadosUsuarioGrupo.UsuarioGrupoRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


@RestController
@RequestMapping("/grupos")
public class GrupoController {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioGrupoRepository usuarioGrupoRepository;

    @Autowired
    private GrupoService grupoService;

    @PostMapping
    @Transactional
    public ResponseEntity criarGrupo(@RequestBody @Valid DadosCadastroGrupo dados,
                                     UriComponentsBuilder builder,
                                     @AuthenticationPrincipal Usuario usuario){
        var grupo = new Grupo(dados.nome(),dados.descricao());
        grupoRepository.save(grupo);

        var usuarioGrupo = new UsuarioGrupo(usuario,grupo, PerfilGrupo.DONO);
        usuarioGrupoRepository.save(usuarioGrupo);

        var uri = builder.path("/grupos/{id}").buildAndExpand(grupo.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosCompletosGrupo(grupo));
    }


    @GetMapping("/meus")
    public ResponseEntity<Page<DadosCompletosGrupo>> listarGrupos(@PageableDefault(size = 20,sort = {"id"})Pageable pageable,
                                                                  @AuthenticationPrincipal Usuario usuario){
        var gruposDaPessoa = grupoRepository.findByUsuario(usuario,pageable).map(DadosCompletosGrupo::new);

        return ResponseEntity.ok(gruposDaPessoa);

    }

    @GetMapping("/{id}")
    public ResponseEntity listarGrupos(@AuthenticationPrincipal Usuario usuario,@PathVariable Long id){



        //perguntar sobre o tratamento desse metodo




        var grupo = grupoService.bucarGrupoPorId(usuario,id);

        return ResponseEntity.ok(new DadosCompletosGrupo(grupo));

    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity atualizarGrupo(@RequestBody DadosAtualizacaoGrupo dados,@PathVariable Long id,
                                         @AuthenticationPrincipal Usuario usuario){
        var grupo = grupoService.bucarGrupoPorId(usuario,id);

        grupo.atualizarInformacoes(dados);

        return ResponseEntity.ok(new DadosAtualizacaoGrupo(grupo));
    }

    @DeleteMapping("/id")
    @Transactional
    public ResponseEntity deletarGrupo(@PathVariable Long id,@AuthenticationPrincipal Usuario usuario){
        var grupo = grupoService.bucarGrupoPorId(usuario,id);

        grupo.excluir();

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/entrar")
    @Transactional
    public ResponseEntity criarGrupo(@RequestBody @Valid DadosEntrarNoGrupo dados,
                                     @AuthenticationPrincipal Usuario usuario){

        var grupo = grupoRepository.findByCodigoConvite(dados.codigoConvite());

        var usuarioGrupo = new UsuarioGrupo(usuario,grupo, PerfilGrupo.MEMBRO);
        usuarioGrupoRepository.save(usuarioGrupo);




        //perguntar se esse response entity ta ok




        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/id/sair")
    @Transactional
    public ResponseEntity sairDoGrupo(@PathVariable Long id,@AuthenticationPrincipal Usuario usuario){
        var grupo = grupoService.bucarGrupoPorId(usuario,id);

        var usuarioGrupo = usuarioGrupoRepository.getReferenceById(id);

        usuarioGrupoRepository.delete(usuarioGrupo);

        //perguntar sobre esse Response Entity também
        return ResponseEntity.noContent().build();
    }
    
}
