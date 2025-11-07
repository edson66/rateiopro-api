package com.rateiopro.api.domain.dadosGrupo;

import com.rateiopro.api.domain.dadosDespesa.Despesa;
import com.rateiopro.api.domain.dadosUsuarioGrupo.UsuarioGrupo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "grupos")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String descricao;
    @Column(name = "codigo_convite")
    private String codigoConvite;
    private boolean ativo = true;

    @OneToMany(mappedBy = "grupo")
    private List<UsuarioGrupo> membrosDoGrupo;
    @OneToMany(mappedBy = "grupo")
    private List<Despesa> despesasDoGrupo;

    @CreationTimestamp
    @Column(name = "created_at",nullable = false,updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at",nullable = false)
    private LocalDateTime updatedAt;

    public Grupo(@NotBlank String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public void atualizarInformacoes(DadosAtualizacaoGrupo dados) {
        if (dados.nome() != null) {
            this.nome = dados.nome();
        }

        if (dados.descricao() != null) {
            this.descricao = dados.descricao();
        }
    }

    public void excluir() {
        this.ativo = false;
    }
}
