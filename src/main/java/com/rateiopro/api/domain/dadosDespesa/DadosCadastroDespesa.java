package com.rateiopro.api.domain.dadosDespesa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosCadastroDespesa(
        @NotBlank
        String descricao,
        @NotNull
        BigDecimal valor,
        @NotNull
        LocalDate data
) {
    public DadosCadastroDespesa(Despesa despesaEnviada) {
        this(despesaEnviada.getDescricao(), despesaEnviada.getValor(),despesaEnviada.getData());
    }
}
