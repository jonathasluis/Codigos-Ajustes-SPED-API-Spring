package org.example.tabelasSped.codAjusteApuracaoICMS.util;

import org.example.tabelasSped.codAjusteApuracaoICMS.dto.CodAjusteApuracaoIcmsOutputDto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SaamIntegration {

    private static final int TAMANHO_LOTE = 1000;

    private static String getInsertSqlStatmentSAAM(List<String> unionSelects) {
        return "INSERT INTO codigos_ajustes_apuracao (cod_aj_apur, descricao, uf, data_inicio, data_fim) \n" +
                "SELECT b.* \n" +
                "FROM codigos_ajustes_apuracao a \n" +
                "RIGHT JOIN (\n" +
                String.join(" UNION \n", unionSelects) +
                ") b ON trim(a.cod_aj_apur) = trim(b.cod_aj_apur) AND trim(a.data_inicio) = trim(b.data_inicio)\n" +
                "WHERE a.cod_aj_apur IS NULL;\n";
    }

    private static String getSelectStatment(CodAjusteApuracaoIcmsOutputDto dto) {
        return String.format("SELECT '%s'::text as cod_aj_apur, " +
                        "'%s'::text as descricao, " +
                        "'%s'::text as uf, " +
                        "'%s'::text as data_inicio, " +
                        "'%s'::text as data_fim ",
                dto.codigoAjuste(),
                dto.descricao(),
                dto.uf(),
                dto.dataInicio(),
                dto.dataFim());
    }

    public static Map<String, String> getMapSqlFiles(List<CodAjusteApuracaoIcmsOutputDto> dtos) {
        int fileCounter = 1;
        Map<String, String> sqlFiles = new LinkedHashMap<>();
        List<String> statmentsListSelect = new ArrayList<>();
        for (int i = 0; i < dtos.size(); i++) {
            CodAjusteApuracaoIcmsOutputDto dto = dtos.get(i);
            statmentsListSelect.add(getSelectStatment(dto));
            if ((i + 1) % TAMANHO_LOTE == 0 || i == dtos.size() - 1) {
                sqlFiles.put("script_ajustes_" + fileCounter + ".sql", getInsertSqlStatmentSAAM(statmentsListSelect));
                statmentsListSelect.clear();
                fileCounter++;
            }
        }
        return sqlFiles;
    }

}
