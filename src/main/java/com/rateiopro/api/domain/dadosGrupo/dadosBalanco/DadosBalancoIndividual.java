package com.rateiopro.api.domain.dadosGrupo.dadosBalanco;

import java.math.BigDecimal;

public record DadosBalancoIndividual(
        Long usuarioId,
        String nomeUsuario,
        BigDecimal totalAPagar,
        BigDecimal totalPago,
        BigDecimal balanco
) {
}
