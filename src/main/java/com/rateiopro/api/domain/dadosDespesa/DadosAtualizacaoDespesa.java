package com.rateiopro.api.domain.dadosDespesa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosAtualizacaoDespesa(
        String descricao,
        BigDecimal valor,
        LocalDate data
) {
    public DadosAtualizacaoDespesa(Despesa despesa) {
        this(despesa.getDescricao(), despesa.getValor(),despesa.getData());
    }
}
