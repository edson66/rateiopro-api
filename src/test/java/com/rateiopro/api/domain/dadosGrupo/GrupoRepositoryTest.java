package com.rateiopro.api.domain.dadosGrupo;

import com.rateiopro.api.domain.dadosUsuario.Usuario;
import com.rateiopro.api.domain.dadosUsuarioGrupo.PerfilGrupo;
import com.rateiopro.api.domain.dadosUsuarioGrupo.UsuarioGrupo;
import com.rateiopro.api.domain.dadosUsuarioGrupo.UsuarioGrupoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GrupoRepositoryTest {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioGrupoRepository usuarioGrupoRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void findGruposAtivosByUsuario() {
        var donoDosGrupos = criarUsuario();

        var naoEDonoDosGrupos = criarSegundoUsuario();

        var grupo1 = criarGrupoValido("Grupo teste 1");

        var grupo2 = criarGrupoValido("grupo teste 2");

        var grupoInvalido = criarGrupoValido("Grupo outra pessoa");

        var usuarioGrupo1 = criarUsuarioGrupoValido(donoDosGrupos,grupo1);

        var usuarioGrupo2 = criarUsuarioGrupoValido(donoDosGrupos,grupo2);

        var usuarioGrupo3 = criarUsuarioGrupoValido(naoEDonoDosGrupos,grupoInvalido);

        em.persistAndFlush(donoDosGrupos);
        em.persistAndFlush(naoEDonoDosGrupos);
        em.persistAndFlush(grupo1);
        em.persistAndFlush(grupo2);
        em.persistAndFlush(grupoInvalido);
        em.persistAndFlush(usuarioGrupo1);
        em.persistAndFlush(usuarioGrupo2);
        em.persistAndFlush(usuarioGrupo3);


        Page<Grupo> gruposBuscados = grupoRepository.findGruposAtivosByUsuario(donoDosGrupos, Pageable.ofSize(10));

        assertNotNull(gruposBuscados);
        assertEquals(2,gruposBuscados.getTotalElements());

        assertTrue(gruposBuscados.getContent().contains(grupo1));
        assertTrue(gruposBuscados.getContent().contains(grupo2));
        assertFalse(gruposBuscados.getContent().contains(grupoInvalido));
    }

    @Test
    void findByIdAndUsuarioAndAtivoTrue() {

        var donoDoGrupo = criarUsuario();

        var grupoTeste = criarGrupoValido("Teste");

        var usuarioGrupo = criarUsuarioGrupoValido(donoDoGrupo,grupoTeste);

        em.persistAndFlush(donoDoGrupo);
        em.persistAndFlush(grupoTeste);
        em.persistAndFlush(usuarioGrupo);

        Optional<Grupo> grupo = grupoRepository.findByIdAndUsuarioAndAtivoTrue(grupoTeste.getId(), donoDoGrupo);
        if (grupo.isEmpty()){
            throw new RuntimeException("grupo não encontrado");
        }

        assertNotNull(grupo);

        assertEquals("Teste",grupo.get().getNome());
    }


    private Usuario criarUsuario(){
        var usuario = new Usuario();
        usuario.setNome("Teste");
        usuario.setEmail("email@email.com");
        usuario.setSenha("senhaTeste");

        return usuario;
    }

    private Usuario criarSegundoUsuario(){
        var usuario = new Usuario();
        usuario.setNome("Teste2");
        usuario.setEmail("teste@email.com");
        usuario.setSenha("senhaTeste");

        return usuario;
    }

    private Grupo criarGrupoValido(String nome){
        var grupo = new Grupo();
        grupo.setNome(nome);
        grupo.setDescricao("Teste");
        grupo.setCodigoConvite(nome);

        return grupo;
    }

    private UsuarioGrupo criarUsuarioGrupoValido(Usuario usuario,Grupo grupo){
        var usuarioGrupo = new UsuarioGrupo();
        usuarioGrupo.setGrupo(grupo);
        usuarioGrupo.setUsuario(usuario);
        usuarioGrupo.setPerfil(PerfilGrupo.MEMBRO);

        return usuarioGrupo;
    }
}