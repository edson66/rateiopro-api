package com.rateiopro.api.domain.dadosDespesa;

import com.rateiopro.api.domain.dadosGrupo.Grupo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


public interface DespesaRepository extends JpaRepository<Despesa,Long> {
    Page<Despesa> findAllByGrupoAndAtivoTrue(Grupo grupo, Pageable pageable);

    Optional<Despesa> findByIdAndAtivoTrue(Long id);

    Optional<Despesa> findByIdAndAtivoFalse(Long idDespesa);
}
