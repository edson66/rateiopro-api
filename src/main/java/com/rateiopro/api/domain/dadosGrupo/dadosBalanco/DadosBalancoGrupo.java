package com.rateiopro.api.domain.dadosGrupo.dadosBalanco;

import java.math.BigDecimal;
import java.util.List;

public record DadosBalancoGrupo(
        Long grupoId,
        String nomeGrupo,
        BigDecimal totalDespesas,
        BigDecimal valorPorPessoa,
        List<DadosBalancoIndividual> balancoDosMembros
) {
}
