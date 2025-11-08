package com.rateiopro.api.domain.dadosDespesa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosCompletosDespesa(
        Long id,
        String descricao,
        BigDecimal valor,
        LocalDate data,
        String pagador,
        String nomeGrupo
) {
    public DadosCompletosDespesa(Despesa despesa) {
        this(despesa.getId(), despesa.getDescricao(), despesa.getValor(),despesa.getData(),
                despesa.getPagoPorUsuario().getNome(),despesa.getGrupo().getNome());
    }
}
