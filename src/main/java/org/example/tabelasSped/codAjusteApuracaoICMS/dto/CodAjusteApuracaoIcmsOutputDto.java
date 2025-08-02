package org.example.tabelasSped.codAjusteApuracaoICMS.dto;

import java.time.LocalDate;

public record CodAjusteApuracaoIcmsOutputDto(
        Long id,
        String codigoAjuste,
        String descricao,
        String uf,
        String dataInicio,
        String dataFim
) {

}
