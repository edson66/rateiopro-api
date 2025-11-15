package com.rateiopro.api.domain.dadosGrupo;

import com.rateiopro.api.domain.dadosDespesa.Despesa;
import com.rateiopro.api.domain.dadosDespesa.DespesaRepository;
import com.rateiopro.api.domain.dadosGrupo.dadosBalanco.DadosBalancoGrupo;
import com.rateiopro.api.domain.dadosGrupo.dadosBalanco.DadosBalancoIndividual;
import com.rateiopro.api.domain.dadosUsuario.Usuario;
import com.rateiopro.api.domain.dadosUsuarioGrupo.PerfilGrupo;
import com.rateiopro.api.domain.dadosUsuarioGrupo.UsuarioGrupo;
import com.rateiopro.api.domain.dadosUsuarioGrupo.UsuarioGrupoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GrupoService {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioGrupoRepository usuarioGrupoRepository;

    @Autowired
    private DespesaRepository despesaRepository;

    public Grupo bucarGrupoAtivoPorId(Usuario usuario,Long id){
        return grupoRepository.findByIdAndUsuarioAndAtivoTrue(id,usuario)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado ou inacessível."));
    }

    public Grupo buscarGrupoParaDono(Long usuarioId,Long grupoId){

        var usuarioGrupo = usuarioGrupoRepository.findByUsuarioIdAndGrupoId(usuarioId,grupoId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

        if (usuarioGrupo.getPerfil().equals(PerfilGrupo.MEMBRO)){
            throw new RuntimeException("Apenas o dono do grupo pode alterar os dados");
        }

        if (!usuarioGrupo.getUsuario().isAtivo() || !usuarioGrupo.getGrupo().isAtivo()){
            throw new RuntimeException("Grupo ou Usuário inativo(s).");
        }

        return usuarioGrupo.getGrupo();
    }

    public void reativarGrupoParaDono(Long usuarioId,Long grupoId){

        var usuarioGrupo = usuarioGrupoRepository.findByUsuarioIdAndGrupoId(usuarioId,grupoId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

        if (usuarioGrupo.getPerfil().equals(PerfilGrupo.MEMBRO)){
            throw new RuntimeException("Apenas o dono do grupo pode reativar o grupo");
        }

        Grupo grupo = grupoRepository.findByIdAndAtivoFalse(grupoId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado ou já está ativo"));

        grupo.reativar();
    }

    public DadosBalancoGrupo calcularBalanco(Grupo grupo) {

        var membros = usuarioGrupoRepository.findByGrupo(grupo)
                .stream().filter(ug -> ug.getUsuario().isAtivo()).toList();

        List<Despesa> despesas = despesaRepository.findByGrupoAndAtivoTrue(grupo);

        var totalMembros = membros.size();
        if (totalMembros == 0){
            return new DadosBalancoGrupo(grupo.getId(), grupo.getNome(), BigDecimal.ZERO,
                    BigDecimal.ZERO,new ArrayList<>());
        }

        var totalDespesas = despesas.stream().map(Despesa::getValor)
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        var valorPorPessoa = totalDespesas.divide(
                new BigDecimal(totalMembros),2, RoundingMode.HALF_UP
        );

        Map<Long,BigDecimal> valorPagoPorUsuario = new HashMap<>();
        for (Despesa despesa: despesas){
            Long id = despesa.getPagoPorUsuario().getId();
            BigDecimal valor = despesa.getValor();

            BigDecimal totalAtual = valorPagoPorUsuario.getOrDefault(id,BigDecimal.ZERO);
            valorPagoPorUsuario.put(id,totalAtual.add(valor));
        }


        List<DadosBalancoIndividual> balancoUsuarios = new ArrayList<>();

        for (UsuarioGrupo ug: membros){
            var usuario = ug.getUsuario();
            var usuarioId = usuario.getId();

            var totalPago = valorPagoPorUsuario.getOrDefault(usuarioId,BigDecimal.ZERO);

            var balanco = totalPago.subtract(valorPorPessoa);

            balancoUsuarios.add(new DadosBalancoIndividual(
                    usuarioId,
                    usuario.getNome(),
                    valorPorPessoa,
                    totalPago,
                    balanco
            ));
        }


        return new DadosBalancoGrupo(
                grupo.getId(),
                grupo.getNome(),
                totalDespesas,
                valorPorPessoa,
                balancoUsuarios
        );
    }
}
