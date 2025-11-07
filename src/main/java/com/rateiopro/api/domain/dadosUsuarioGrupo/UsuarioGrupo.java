package com.rateiopro.api.domain.dadosUsuarioGrupo;

import com.rateiopro.api.domain.dadosGrupo.Grupo;
import com.rateiopro.api.domain.dadosUsuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "usuarios_grupo")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
public class UsuarioGrupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;
    @Enumerated(EnumType.STRING)
    private PerfilGrupo perfil;

    public UsuarioGrupo(Usuario usuario, Grupo grupo, PerfilGrupo perfilGrupo) {
        this.usuario = usuario;
        this.grupo = grupo;
        this.perfil = perfilGrupo;
    }
}
