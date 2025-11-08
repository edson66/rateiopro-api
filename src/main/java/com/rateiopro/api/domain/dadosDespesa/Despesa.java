package com.rateiopro.api.domain.dadosDespesa;

import com.rateiopro.api.domain.dadosGrupo.Grupo;
import com.rateiopro.api.domain.dadosUsuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table(name = "despesas")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
@Setter
public class Despesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descricao;
    private BigDecimal valor;
    private LocalDate data;
    @ManyToOne
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;
    @ManyToOne
    @JoinColumn(name = "pago_por_usuario_id")
    private Usuario pagoPorUsuario;
    private boolean ativo = true;
    @CreationTimestamp
    @Column(name = "created_at",nullable = false,updatable = false)
    private LocalDateTime createdAt;

    public Despesa(@Valid DadosCadastroDespesa dados, Grupo grupo, Usuario usuario) {
        this.descricao = dados.descricao();
        this.valor = dados.valor();
        this.data = dados.data();
        this.grupo = grupo;
        this.pagoPorUsuario = usuario;
    }
}
