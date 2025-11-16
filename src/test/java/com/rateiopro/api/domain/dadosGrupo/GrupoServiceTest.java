package com.rateiopro.api.domain.dadosGrupo;

import com.rateiopro.api.domain.dadosDespesa.Despesa;
import com.rateiopro.api.domain.dadosDespesa.DespesaRepository;
import com.rateiopro.api.domain.dadosGrupo.dadosBalanco.DadosBalancoIndividual;
import com.rateiopro.api.domain.dadosUsuario.Usuario;
import com.rateiopro.api.domain.dadosUsuarioGrupo.PerfilGrupo;
import com.rateiopro.api.domain.dadosUsuarioGrupo.UsuarioGrupo;
import com.rateiopro.api.domain.dadosUsuarioGrupo.UsuarioGrupoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrupoServiceTest {

    @Mock
    private GrupoRepository repository;

    @Mock
    private UsuarioGrupoRepository usuarioGrupoRepository;

    @Mock
    private DespesaRepository despesaRepository;

    @InjectMocks
    private GrupoService grupoService;

    @Test
    @DisplayName("deve reativar grupo corretamente")
    void reativarGrupoParaDono() {

        Long grupoId = 1L;
        Long usuarioId = 5L;

        var usuarioGrupoMock = new UsuarioGrupo();
        usuarioGrupoMock.setPerfil(PerfilGrupo.DONO);

        var grupoMock = new Grupo();
        grupoMock.setAtivo(false);

        when(usuarioGrupoRepository.findByUsuarioIdAndGrupoId(usuarioId,grupoId)).thenReturn(Optional.of(usuarioGrupoMock));

        when(repository.findByIdAndAtivoFalse(grupoId)).thenReturn(Optional.of(grupoMock));

        grupoService.reativarGrupoParaDono(usuarioId,grupoId);

        assertTrue(grupoMock.isAtivo());
    }

    @Test
    @DisplayName("deve dar erro pois membro nao pode reativar um grupo")
    void reativarGrupoParaDonocaso2() {

        Long grupoId = 1L;
        Long usuarioId = 5L;

        var usuarioGrupoMock = new UsuarioGrupo();
        usuarioGrupoMock.setPerfil(PerfilGrupo.MEMBRO);

        when(usuarioGrupoRepository.findByUsuarioIdAndGrupoId(usuarioId,grupoId)).thenReturn(Optional.of(usuarioGrupoMock));

        var excessao = assertThrows(RuntimeException.class,() -> {
            grupoService.reativarGrupoParaDono(usuarioId,grupoId);
        });

        assertEquals("Apenas o dono do grupo pode reativar o grupo",excessao.getMessage());

        verify(repository,never()).findByIdAndAtivoFalse(anyLong());

    }

    @Test
    @DisplayName("deve calcular o balanço corretamente")
    void calcularBalancoCaso1(){

        var grupo = new Grupo();
        grupo.setId(4L);
        grupo.setNome("Grupo para teste");

        var usuario1Mock = new Usuario();
        usuario1Mock.setId(1L);
        usuario1Mock.setNome("Mock 1");
        var usuario2Mock = new Usuario();
        usuario2Mock.setId(2L);
        usuario1Mock.setNome("Mock 2");

        var membro1DoGrupo = new UsuarioGrupo();
        membro1DoGrupo.setUsuario(usuario1Mock);

        var membro2DoGrupo = new UsuarioGrupo();
        membro2DoGrupo.setUsuario(usuario2Mock);

        List<UsuarioGrupo> membros = new ArrayList<>();
        membros.add(membro1DoGrupo);
        membros.add(membro2DoGrupo);

        var despesa1 = new Despesa();
        despesa1.setGrupo(grupo);
        despesa1.setValor(new BigDecimal("200.00"));
        despesa1.setPagoPorUsuario(usuario1Mock);

        var despesa2 = new Despesa();
        despesa2.setGrupo(grupo);
        despesa2.setValor(new BigDecimal("400.00"));
        despesa2.setPagoPorUsuario(usuario2Mock);

        List<Despesa> despesas = new ArrayList<>();
        despesas.add(despesa1);
        despesas.add(despesa2);

        when(usuarioGrupoRepository.findByGrupo(any(Grupo.class))).thenReturn(membros);
        when(despesaRepository.findByGrupoAndAtivoTrue(any(Grupo.class))).thenReturn(despesas);


        var resultado = grupoService.calcularBalanco(grupo);

        assertNotNull(resultado);
        assertEquals(0,resultado.totalDespesas().compareTo(new BigDecimal("600.00")));
        assertEquals(0,resultado.valorPorPessoa().compareTo(new BigDecimal("300.00")));
        assertEquals(2,resultado.balancoDosMembros().size());

        DadosBalancoIndividual balancoUsuario1 = resultado.balancoDosMembros().getFirst();
        assertEquals(0,balancoUsuario1.balanco().compareTo(new BigDecimal("-100.00")));

        DadosBalancoIndividual balacoUsuario2 = resultado.balancoDosMembros().get(1);
        assertEquals(0,balacoUsuario2.balanco().compareTo(new BigDecimal("100.00")));

    }

    @Test
    @DisplayName("deve calcular o balanço Para despesas zeradas")
    void calcularBalancoCaso2(){

        var grupo = new Grupo();
        grupo.setId(4L);
        grupo.setNome("Grupo para teste");

        var usuario1Mock = new Usuario();
        usuario1Mock.setId(1L);
        usuario1Mock.setNome("Mock 1");
        var usuario2Mock = new Usuario();
        usuario2Mock.setId(2L);
        usuario1Mock.setNome("Mock 2");

        var membro1DoGrupo = new UsuarioGrupo();
        membro1DoGrupo.setUsuario(usuario1Mock);

        var membro2DoGrupo = new UsuarioGrupo();
        membro2DoGrupo.setUsuario(usuario2Mock);

        List<UsuarioGrupo> membros = new ArrayList<>();
        membros.add(membro1DoGrupo);
        membros.add(membro2DoGrupo);

        List<Despesa> despesas = new ArrayList<>();


        when(usuarioGrupoRepository.findByGrupo(any(Grupo.class))).thenReturn(membros);
        when(despesaRepository.findByGrupoAndAtivoTrue(any(Grupo.class))).thenReturn(despesas);


        var resultado = grupoService.calcularBalanco(grupo);

        assertNotNull(resultado);
        assertEquals(0,resultado.totalDespesas().compareTo(new BigDecimal("0")));
        assertEquals(0,resultado.valorPorPessoa().compareTo(new BigDecimal("0")));
        assertEquals(2,resultado.balancoDosMembros().size());

        DadosBalancoIndividual balancoUsuario1 = resultado.balancoDosMembros().getFirst();
        assertEquals(0,balancoUsuario1.balanco().compareTo(new BigDecimal("0")));

        DadosBalancoIndividual balacoUsuario2 = resultado.balancoDosMembros().get(1);
        assertEquals(0,balacoUsuario2.balanco().compareTo(new BigDecimal("0")));

    }
}