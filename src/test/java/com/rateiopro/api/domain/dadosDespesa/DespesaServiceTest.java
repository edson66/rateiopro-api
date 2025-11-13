package com.rateiopro.api.domain.dadosDespesa;

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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DespesaServiceTest {

    @Mock
    private UsuarioGrupoRepository usuarioGrupoRepository;

    @InjectMocks
    private DespesaService despesaService;

    @Test
    @DisplayName("Deve passar sem erros,DONO")
    void checarSeUsuarioPodeAtualizarEDeletarcaso1() {

        Long usuarioId = 1L;
        Long grupoId = 2L;

        var despesaMock = new Despesa();

        var usuarioGrupoMock = new UsuarioGrupo();
        usuarioGrupoMock.setPerfil(PerfilGrupo.DONO);

        when(usuarioGrupoRepository.findByUsuarioIdAndGrupoId(usuarioId,grupoId)).thenReturn(Optional.of(usuarioGrupoMock));

        assertDoesNotThrow(() -> {
            despesaService.checarSeUsuarioPodeAtualizarEDeletar(usuarioId,grupoId,despesaMock);
        });
    }

    @Test
    @DisplayName("Nao é dono do grupo,mas é dono da despesa,deve passar sem excessões")
    void checarSeUsuarioPodeAtualizarEDeletarcaso2() {

        Long usuarioId = 1L;
        Long grupoId = 2L;

        var pagoPor = new Usuario();
        pagoPor.setId(usuarioId);

        var despesaMock = new Despesa();
        despesaMock.setPagoPorUsuario(pagoPor);

        var usuarioGrupoMock = new UsuarioGrupo();
        usuarioGrupoMock.setPerfil(PerfilGrupo.MEMBRO);

        when(usuarioGrupoRepository.findByUsuarioIdAndGrupoId(usuarioId,grupoId)).thenReturn(Optional.of(usuarioGrupoMock));

        assertDoesNotThrow(() -> {
            despesaService.checarSeUsuarioPodeAtualizarEDeletar(usuarioId,grupoId,despesaMock);
        });
    }

    @Test
    @DisplayName("deve retornar excessão pois não é dono nem da despesa nem do grupo")
    void checarSeUsuarioPodeAtualizarEDeletarcaso3() {

        Long usuarioId = 1L;
        Long grupoId = 2L;

        var pagoPor = new Usuario();
        pagoPor.setId(usuarioId+1);

        var despesaMock = new Despesa();
        despesaMock.setPagoPorUsuario(pagoPor);

        var usuarioGrupoMock = new UsuarioGrupo();
        usuarioGrupoMock.setPerfil(PerfilGrupo.MEMBRO);

        when(usuarioGrupoRepository.findByUsuarioIdAndGrupoId(usuarioId,grupoId)).thenReturn(Optional.of(usuarioGrupoMock));

        var ex = assertThrows(RuntimeException.class,() -> {
            despesaService.checarSeUsuarioPodeAtualizarEDeletar(usuarioId,grupoId,despesaMock);
        });

        assertEquals("Apenas o dono do grupo ou quem criou a despesa pode atualizá-la ou deletá-la!",
                ex.getMessage());
    }
}


