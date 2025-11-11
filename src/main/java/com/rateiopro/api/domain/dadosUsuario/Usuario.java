package com.rateiopro.api.domain.dadosUsuario;

import com.rateiopro.api.domain.dadosDespesa.Despesa;
import com.rateiopro.api.domain.dadosUsuarioGrupo.UsuarioGrupo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
@Setter
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private boolean ativo = true;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "usuario_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;

    @OneToMany(mappedBy = "usuario")
    private List<UsuarioGrupo> gruposQueParticipa;
    @OneToMany(mappedBy = "pagoPorUsuario")
    private List<Despesa> despesasPagas;
    @CreationTimestamp
    @Column(name = "created_at",nullable = false,updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at",nullable = false)
    private LocalDateTime updatedAt;

    public Usuario(@NotBlank String email, @NotBlank String nome, String senhaCodificada,Role role) {
        this.email = email;
        this.nome = nome;
        this.senha = senhaCodificada;
        this.roles = new ArrayList<>();
        this.roles.add(role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void atualizarDados(DadosAtualizacaoUsuario dados,String senha) {
        if (dados.email() != null){
            this.email = dados.email();
        }
        if (dados.nome() != null){
            this.nome = dados.nome();
        }
        if (dados.senha() != null){
            this.senha = senha;
        }
    }

    public void desativar() {
        this.ativo = false;
    }

    public void ativar(){
        this.ativo = true;
    }
}
