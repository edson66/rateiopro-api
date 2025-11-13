package com.rateiopro.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rateiopro.api.domain.dadosDespesa.*;
import com.rateiopro.api.domain.dadosGrupo.Grupo;
import com.rateiopro.api.domain.dadosUsuario.Usuario;
import com.rateiopro.api.domain.dadosUsuario.UsuarioRepository;
import com.rateiopro.api.domain.dadosUsuarioGrupo.UsuarioGrupoRepository;
import com.rateiopro.api.security.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(DespesaController.class)
@WithMockUser
class DespesaControllerTest {

    @MockitoBean
    private DespesaRepository despesaRepository;

    @MockitoBean
    private UsuarioGrupoRepository usuarioGrupoRepository;

    @MockitoBean
    private DespesaService despesaService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private TokenService tokenService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @DisplayName("Criar despesa com sucesso")
    void criarDespesacaso1() throws Exception {

        var usuarioMock = new Usuario();
        usuarioMock.setId(3L);

        var token = new UsernamePasswordAuthenticationToken(usuarioMock,
                null,usuarioMock.getAuthorities());

        var grupoMock = new Grupo();
        grupoMock.setId(2L);

        var despesaEnviada = new Despesa();
        despesaEnviada.setId(1L);
        despesaEnviada.setData(LocalDate.of(2025,10,10));
        despesaEnviada.setValor(BigDecimal.valueOf(100));
        despesaEnviada.setDescricao("Despesa para teste");

        var dtoDespesa = new DadosCadastroDespesa(despesaEnviada);

        var jsonRequest = mapper.writeValueAsString(dtoDespesa);

        when(despesaService.pegarGrupoPertencenteAoUsuario(eq(usuarioMock.getId()),eq(grupoMock.getId())))
                .thenReturn(grupoMock);

        when(despesaRepository.save(any(Despesa.class))).thenAnswer(invocationOnMock -> {
            Despesa despesa = invocationOnMock.getArgument(0);
            despesa.setId(1L);
            return despesa;
        });

        var request = post("/grupos/2/despesas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.authentication(token));
        var response = mvc.perform(request);

        response.andExpect(status().isOk());
        response.andExpect(jsonPath("$.id").value(1L));
        response.andExpect(jsonPath("$.valor").value(new BigDecimal(100)));
        response.andExpect(jsonPath("$.data").value("2025-10-10"));
        response.andExpect(jsonPath("$.descricao").value("Despesa para teste"));
    }

    @Test
    void atualizarDespesa() throws Exception {

        var usuarioMock = new Usuario();
        usuarioMock.setId(3L);

        var token = new UsernamePasswordAuthenticationToken(usuarioMock,
                null,usuarioMock.getAuthorities());

        var grupoMock = new Grupo();
        grupoMock.setId(2L);

        var despesaEnviada = new Despesa();
        despesaEnviada.setValor(BigDecimal.valueOf(1000));
        despesaEnviada.setDescricao("Despesa para teste atualizada");

        var dtoDespesa = new DadosAtualizacaoDespesa(despesaEnviada);

        var jsonRequest = mapper.writeValueAsString(dtoDespesa);

        var despesaExistente = new Despesa();
        despesaExistente.setId(1L);
        despesaExistente.setData(LocalDate.of(2025,10,10));
        despesaExistente.setValor(BigDecimal.valueOf(100));
        despesaExistente.setDescricao("Despesa para teste");
        despesaExistente.setGrupo(grupoMock);


        when(despesaRepository.findByIdAndAtivoTrue(eq(1L))).thenReturn(Optional.of(despesaExistente));

        doNothing().when(despesaService).checarSeUsuarioPodeAtualizarEDeletar(eq(3L),eq(2L),eq(despesaExistente));

        var request = put("/despesas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.authentication(token));
        var response = mvc.perform(request);

        response.andExpect(status().isOk());
        response.andExpect(jsonPath("$.valor").value(BigDecimal.valueOf(1000)));
        response.andExpect(jsonPath("$.descricao").value("Despesa para teste atualizada"));
        response.andExpect(jsonPath("$.data").value("2025-10-10"));
    }


}