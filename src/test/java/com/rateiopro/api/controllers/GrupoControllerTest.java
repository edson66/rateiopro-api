package com.rateiopro.api.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rateiopro.api.domain.dadosGrupo.*;
import com.rateiopro.api.domain.dadosUsuario.DadosAtualizacaoUsuario;
import com.rateiopro.api.domain.dadosUsuario.Usuario;
import com.rateiopro.api.domain.dadosUsuario.UsuarioRepository;
import com.rateiopro.api.domain.dadosUsuarioGrupo.UsuarioGrupo;
import com.rateiopro.api.domain.dadosUsuarioGrupo.UsuarioGrupoRepository;
import com.rateiopro.api.security.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(GrupoController.class)
@WithMockUser
class GrupoControllerTest {

    @MockitoBean
    private GrupoRepository grupoRepository;

    @MockitoBean
    private UsuarioGrupoRepository usuarioGrupoRepository;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private GrupoService grupoService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("deve criar um grupo e devolver 201 CREATED")
    void criarGrupoCenario1() throws Exception {
        var grupoASerCriado = new Grupo();
        grupoASerCriado.setId(1L);
        grupoASerCriado.setNome("teste");
        grupoASerCriado.setDescricao("grupo para testes");

        DadosCadastroGrupo dadosGrupo = new DadosCadastroGrupo(grupoASerCriado);

        String jsonRequest = objectMapper.writeValueAsString(dadosGrupo);

        var request = post("/grupos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .with(csrf());
        var response = mvc.perform(request);

        response.andExpect(status().isCreated());

        response.andExpect(header().string("Location","http://localhost/grupos/"));
        response.andExpect(jsonPath("$.nome").value("teste"));
        response.andExpect(jsonPath("$.descricao").value("grupo para testes"));

        verify(grupoRepository,times(1)).save(any(Grupo.class));
    }


    @Test
    void listarGrupoPorId() throws Exception {
        var grupoASerCriado = new Grupo();
        grupoASerCriado.setId(1L);
        grupoASerCriado.setNome("teste");
        grupoASerCriado.setDescricao("grupo para testes");

        when(grupoService.bucarGrupoAtivoPorId(nullable(Usuario.class),eq(1L))).thenReturn(grupoASerCriado);

        var request = get("/grupos/1");
        var response = mvc.perform(request);

        response.andExpect(status().isOk());

        response.andExpect(jsonPath("$.id").value(1L));
        response.andExpect(jsonPath("$.nome").value("teste"));
        response.andExpect(jsonPath("$.descricao").value("grupo para testes"));
    }

    @Test
    void atualizarGrupo() throws Exception {
        var grupoCriado = new Grupo();
        grupoCriado.setId(1L);
        grupoCriado.setNome("teste");
        grupoCriado.setDescricao("grupo para testes");

        var grupoAtualizado = new Grupo();
        grupoCriado.setId(1L);
        grupoCriado.setNome("teste");
        grupoCriado.setDescricao("grupo para testes atualizado");

        var usuarioMockado = new Usuario();
        usuarioMockado.setId(1L);

        var token = new UsernamePasswordAuthenticationToken(usuarioMockado,
                null,usuarioMockado.getAuthorities());

        DadosAtualizacaoGrupo atualizacaoGrupo = new DadosAtualizacaoGrupo(grupoAtualizado);

        String jsonRequest = objectMapper.writeValueAsString(atualizacaoGrupo);

        when(grupoService.buscarGrupoParaDono(eq(1L),eq(1L))).thenReturn(grupoCriado);

        var request = put("/grupos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .with(csrf())
                .with(authentication(token));
        var response = mvc.perform(request);

        response.andExpect(status().isOk());

        response.andExpect(jsonPath("$.nome").value("teste"));
        response.andExpect(jsonPath("$.descricao").value("grupo para testes atualizado"));
    }

    @Test
    void deletarGrupo() throws Exception {
        Usuario usuarioMockado = new Usuario();
        usuarioMockado.setId(1L);

        var token = new UsernamePasswordAuthenticationToken(usuarioMockado,
                null,usuarioMockado.getAuthorities());

        var grupoASerCriado = new Grupo();
        grupoASerCriado.setId(1L);
        grupoASerCriado.setNome("teste");
        grupoASerCriado.setDescricao("grupo para testes");

        when(grupoService.buscarGrupoParaDono(eq(1L),eq(1L))).thenReturn(grupoASerCriado);

        var request = delete("/grupos/1")
                .with(csrf())
                .with(authentication(token));
        var response = mvc.perform(request);

        response.andExpect(status().isNoContent());


    }

    @Test
    void entrarNoGrupo() throws Exception {
        var grupoASerCriado = new Grupo();
        grupoASerCriado.setId(1L);
        grupoASerCriado.setNome("teste");
        grupoASerCriado.setDescricao("grupo para testes");

        var dados = new DadosEntrarNoGrupo("TESTE");

        var jsonRequest = objectMapper.writeValueAsString(dados);

        when(grupoRepository.findByCodigoConviteAndAtivoTrue(dados.codigoConvite())).thenReturn(grupoASerCriado);

        var request = post("/grupos/entrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .with(csrf());
        var response = mvc.perform(request);

        response.andExpect(status().isOk());
        verify(usuarioGrupoRepository,times(1)).save(any(UsuarioGrupo.class));
    }

    @Test
    void reativarGrupo() throws Exception {
        var usuarioMock = new Usuario();
        usuarioMock.setId(1L);

        var token = new UsernamePasswordAuthenticationToken(usuarioMock,
                null,usuarioMock.getAuthorities());

        Long idParaReativar = 2L;

        doNothing().when(grupoService).reativarGrupoParaDono(eq(usuarioMock.getId()),eq(idParaReativar));

        var request = put("/grupos/2/reativar")
                .with(csrf())
                .with(authentication(token));
        var response = mvc.perform(request);

        response.andExpect(status().isOk());
        verify(grupoService,times(1)).reativarGrupoParaDono(
                eq(usuarioMock.getId()),
                eq(idParaReativar)
        );
    }
}