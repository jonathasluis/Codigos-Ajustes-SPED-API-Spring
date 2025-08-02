package org.example.tabelasSped.codAjusteApuracaoICMS.service;

import org.example.tabelasSped.codAjusteApuracaoICMS.dto.CodAjusteApuracaoIcmsInputDto;
import org.example.tabelasSped.codAjusteApuracaoICMS.dto.CodAjusteApuracaoIcmsOutputDto;
import org.example.tabelasSped.codAjusteApuracaoICMS.entity.CodAjusteApuracaoIcmsEntity;
import org.example.tabelasSped.codAjusteApuracaoICMS.repository.CodAjusteApuracaoIcmsRepository;
import org.example.tabelasSped.codAjusteApuracaoICMS.util.SaamIntegration;
import org.example.tabelasSped.codAjusteApuracaoICMS.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CodAjusteApuracaoIcmsService {

    @Autowired
    private CodAjusteApuracaoIcmsRepository codAjusteApuracaoIcmsRepository;

    public List<CodAjusteApuracaoIcmsOutputDto> pesquisarCodigos(String codigoAjuste, String uf, String dataInicio) {
        Example<CodAjusteApuracaoIcmsEntity> example = getExample(codigoAjuste, uf, dataInicio);
        return codAjusteApuracaoIcmsRepository.findAll(example).stream()
                                              .map(Util::convertEntiTyToOutputDto)
                                              .toList();
    }

    public List<CodAjusteApuracaoIcmsOutputDto> obterCodigosAjustesAtualizados(Instant data) {
        return codAjusteApuracaoIcmsRepository.findByDataInsertAfterOrDataUpdateAfter(data, data)
                                              .stream()
                                              .map(Util::convertEntiTyToOutputDto)
                                              .toList();
    }

    @Transactional
    public Map<String, Integer> sincronizarCodigosEmLote(List<CodAjusteApuracaoIcmsInputDto> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return Collections.emptyMap();
        }
        List<CodAjusteApuracaoIcmsEntity> entidadesExistentes = codAjusteApuracaoIcmsRepository.findAll();
        Map<ChaveEntidade, CodAjusteApuracaoIcmsEntity> mapaEntidadesExistentes = entidadesExistentes.stream()
                                                                                                     .collect(Collectors.toMap(
                                                                                                             entidade -> new ChaveEntidade(entidade.getCodigoAjuste(), entidade.getDataInicio()),
                                                                                                             entidade -> entidade
                                                                                                     ));
        List<CodAjusteApuracaoIcmsEntity> entidadesParaCriar = new ArrayList<>();
        List<CodAjusteApuracaoIcmsEntity> entidadesParaAtualizar = new ArrayList<>();
        for (CodAjusteApuracaoIcmsInputDto dto : dtos) {
            ChaveEntidade chave = new ChaveEntidade(dto.codigoAjuste(), dto.dataInicio());
            CodAjusteApuracaoIcmsEntity entidadeExistente = mapaEntidadesExistentes.get(chave);
            if (entidadeExistente != null) {
                if (Util.entityIsChange(dto, entidadeExistente)) {
                    Util.convertInputDtoToEntity(dto, entidadeExistente);
                    entidadesParaAtualizar.add(entidadeExistente);
                }
            } else {
                entidadesParaCriar.add(Util.convertInputDtoToEntity(dto));
            }
        }
        if (!entidadesParaCriar.isEmpty()) {
            codAjusteApuracaoIcmsRepository.saveAll(entidadesParaCriar);
        }
        if (!entidadesParaAtualizar.isEmpty()) {
            codAjusteApuracaoIcmsRepository.saveAll(entidadesParaAtualizar);
        }
        Map<String, Integer> resultado = new HashMap<>();
        resultado.put("registrosCriados", entidadesParaCriar.size());
        resultado.put("registrosAtualizados", entidadesParaAtualizar.size());
        return resultado;
    }

    private Example<CodAjusteApuracaoIcmsEntity> getExample(String codigoAjuste, String uf, String dataInicio) {
        CodAjusteApuracaoIcmsEntity probe = new CodAjusteApuracaoIcmsEntity();
        probe.setCodigoAjuste(codigoAjuste);
        probe.setUf(uf);
        probe.setDataInicio(dataInicio);
        ExampleMatcher matcher = ExampleMatcher.matching()
                                               .withIgnoreCase()
                                               .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                                               .withIgnoreNullValues();
        return Example.of(probe, matcher);
    }

    public Map<String, String> getSqlFiles(String codigoAjuste, String uf, String dataInicio) {
        List<CodAjusteApuracaoIcmsOutputDto> dtos = pesquisarCodigos(codigoAjuste, uf, dataInicio);
        return SaamIntegration.getMapSqlFiles(dtos);
    }

    private record ChaveEntidade(String codigoAjuste, String dataInicio) {
    }
}
