package org.example.tabelasSped.codAjusteApuracaoICMS.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record CodAjusteApuracaoIcmsInputDto(
        @JsonProperty ("cod_aj_apur") // Mapeia o nome do campo no JSON
        String codigoAjuste,

        @JsonProperty ("descricao")
        String descricao,

        @JsonProperty ("uf")
        String uf,

        @JsonProperty ("data_inicio")
        @JsonFormat (pattern = "ddMMyyyy") // Converte a string ddMMyyyy para LocalDate
        String dataInicio,

        @JsonProperty ("data_fim")
        @JsonFormat (pattern = "ddMMyyyy")
        String dataFim
) {
}
