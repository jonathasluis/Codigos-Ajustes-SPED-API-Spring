package org.example.tabelasSped.codAjusteApuracaoICMS.util;

import org.example.tabelasSped.codAjusteApuracaoICMS.dto.CodAjusteApuracaoIcmsInputDto;
import org.example.tabelasSped.codAjusteApuracaoICMS.dto.CodAjusteApuracaoIcmsOutputDto;
import org.example.tabelasSped.codAjusteApuracaoICMS.entity.CodAjusteApuracaoIcmsEntity;

public class Util {

    public static CodAjusteApuracaoIcmsOutputDto convertEntiTyToOutputDto(CodAjusteApuracaoIcmsEntity entidade) {
        return new CodAjusteApuracaoIcmsOutputDto(
                entidade.getId(),
                entidade.getCodigoAjuste(),
                entidade.getDescricao(),
                entidade.getUf(),
                entidade.getDataInicio(),
                entidade.getDataFim()
        );
    }

    public static CodAjusteApuracaoIcmsEntity convertInputDtoToEntity(CodAjusteApuracaoIcmsInputDto dto) {
        CodAjusteApuracaoIcmsEntity entity = new CodAjusteApuracaoIcmsEntity();
        entity.setCodigoAjuste(dto.codigoAjuste());
        entity.setDescricao(dto.descricao());
        entity.setUf(dto.uf());
        entity.setDataInicio(dto.dataInicio());
        entity.setDataFim(dto.dataFim());
        return entity;
    }

    public static void convertInputDtoToEntity(CodAjusteApuracaoIcmsInputDto dto, CodAjusteApuracaoIcmsEntity entity) {
        entity.setDescricao(dto.descricao());
        entity.setUf(dto.uf());
        entity.setDataFim(dto.dataFim());
    }

    public static boolean entityIsChange(CodAjusteApuracaoIcmsInputDto dto, CodAjusteApuracaoIcmsEntity entity) {
        return !entity.getDataFim().equals(dto.dataFim()) || !entity.getDescricao().equals(dto.descricao());

    }
}
