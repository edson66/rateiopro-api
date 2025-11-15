package com.rateiopro.api.controllers;


import com.rateiopro.api.domain.dadosGrupo.*;
import com.rateiopro.api.domain.dadosUsuario.Usuario;
import com.rateiopro.api.domain.dadosUsuario.UsuarioService;
import com.rateiopro.api.domain.dadosUsuarioGrupo.PerfilGrupo;
import com.rateiopro.api.domain.dadosUsuarioGrupo.UsuarioGrupo;
import com.rateiopro.api.domain.dadosUsuarioGrupo.UsuarioGrupoRepository;
import jakarta.persistence.EntityNotFoundException;
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
        var gruposDaPessoa = grupoRepository.findGruposAtivosByUsuario(usuario,pageable)
                .map(DadosCompletosGrupo::new);

        return ResponseEntity.ok(gruposDaPessoa);
    }

    @GetMapping("/{id}")
    public ResponseEntity listarGrupoPorId(@AuthenticationPrincipal Usuario usuario,@PathVariable Long id){

        var grupo = grupoService.bucarGrupoAtivoPorId(usuario,id);

        return ResponseEntity.ok(new DadosCompletosGrupo(grupo));

    }

    @PutMapping("/{idGrupo}")
    @Transactional
    public ResponseEntity atualizarGrupo(@RequestBody DadosAtualizacaoGrupo dados,@PathVariable Long idGrupo,
                                         @AuthenticationPrincipal Usuario usuario){
        var grupo = grupoService.buscarGrupoParaDono(usuario.getId(),idGrupo);

        grupo.atualizarInformacoes(dados);

        return ResponseEntity.ok(new DadosAtualizacaoGrupo(grupo));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity deletarGrupo(@PathVariable Long id,@AuthenticationPrincipal Usuario usuario){
        var grupo = grupoService.buscarGrupoParaDono(usuario.getId(),id);

        grupo.excluir();

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/entrar")
    @Transactional
    public ResponseEntity entrarNoGrupo(@RequestBody @Valid DadosEntrarNoGrupo dados,
                                     @AuthenticationPrincipal Usuario usuario){

        var grupo = grupoRepository.findByCodigoConviteAndAtivoTrue(dados.codigoConvite());

        if (grupo == null || !grupo.isAtivo()){
            throw new EntityNotFoundException("Código de convite inválido ou grupo inativo.");
        }

        if (usuarioGrupoRepository.existsByUsuarioAndGrupo(usuario,grupo)){
            throw new RuntimeException("Você já faz parte deste grupo.");
        }

        var usuarioGrupo = new UsuarioGrupo(usuario,grupo, PerfilGrupo.MEMBRO);
        usuarioGrupoRepository.save(usuarioGrupo);


        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/sair")
    @Transactional
    public ResponseEntity sairDoGrupo(@PathVariable Long id,@AuthenticationPrincipal Usuario usuario){

        var usuarioGrupo = usuarioGrupoRepository.findByUsuarioIdAndGrupoId(usuario.getId(), id)
                .orElseThrow(() -> new EntityNotFoundException("O Usuário não pertence ao grupo requisitado"));

        if (usuarioGrupo.getPerfil() == PerfilGrupo.DONO){
            throw new RuntimeException("O dono não pode sair do grupo,apenas excluir");
        }
        usuarioGrupoRepository.delete(usuarioGrupo);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{idGrupo}/reativar")
    @Transactional
    public ResponseEntity reativarGrupo(@PathVariable Long idGrupo,@AuthenticationPrincipal Usuario usuario){
        grupoService.reativarGrupoParaDono(usuario.getId(), idGrupo);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/balanco")
    public ResponseEntity calcularBalanco(@PathVariable Long id,
                                          @AuthenticationPrincipal Usuario usuario){

        var grupo = grupoService.bucarGrupoAtivoPorId(usuario,id);

        var balanco = grupoService.calcularBalanco(grupo);

        return ResponseEntity.ok(balanco);
    }
    
}
