package com.rateiopro.api.domain.dadosGrupo;

import com.rateiopro.api.domain.dadosDespesa.DespesaRepository;
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
}