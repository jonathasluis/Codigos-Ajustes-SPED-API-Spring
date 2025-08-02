package org.example.tabelasSped.codAjusteApuracaoICMS.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.example.tabelasSped.codAjusteApuracaoICMS.dto.CodAjusteApuracaoIcmsInputDto;
import org.example.tabelasSped.codAjusteApuracaoICMS.dto.CodAjusteApuracaoIcmsOutputDto;
import org.example.tabelasSped.codAjusteApuracaoICMS.service.CodAjusteApuracaoIcmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping ("/codigos-ajustes-apuracao")
public class CodAjusteApuracaoIcmsController {

    @Autowired
    private CodAjusteApuracaoIcmsService codAjusteApuracaoIcmsService;

    @GetMapping
    public ResponseEntity<List<CodAjusteApuracaoIcmsOutputDto>> pesquisarCodigosAjustes(
            @RequestParam (required = false) String codigo,
            @RequestParam (required = false) String uf,
            @RequestParam (required = false) @DateTimeFormat (pattern = "ddMMyyyy") String dataInicio
    ) {
        List<CodAjusteApuracaoIcmsOutputDto> dtos = codAjusteApuracaoIcmsService.pesquisarCodigos(codigo, uf, dataInicio);
        if (dtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dtos);
    }

    @GetMapping ("/atualizados/{data}")
    public ResponseEntity<List<CodAjusteApuracaoIcmsOutputDto>> listarCodigosAjustesApuracaoAtualizados(
            @PathVariable @DateTimeFormat (pattern = "ddMMyyyy") LocalDate data
    ) {
        var dataInstant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();
        List<CodAjusteApuracaoIcmsOutputDto> dtos = codAjusteApuracaoIcmsService.obterCodigosAjustesAtualizados(dataInstant);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping ("/scripts/gerar-zip")
    public void gerarScriptsZip(HttpServletResponse response,
                                @RequestParam (required = false) String codigo,
                                @RequestParam (required = false) String uf,
                                @RequestParam (required = false) @DateTimeFormat (pattern = "ddMMyyyy") String dataInicio
    ) throws IOException {
        // 1. Define os headers da resposta para indicar um download de arquivo ZIP
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"scripts_sql_ajustes.zip\"");
        Map<String, String> arquivosSql = codAjusteApuracaoIcmsService.getSqlFiles(codigo, uf, dataInicio);
        // 3. Cria o arquivo ZIP em memória e o escreve diretamente na resposta HTTP
        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
            for (Map.Entry<String, String> entry : arquivosSql.entrySet()) {
                ZipEntry zipEntry = new ZipEntry(entry.getKey());
                zipOut.putNextEntry(zipEntry);
                zipOut.write(entry.getValue().getBytes());
                zipOut.closeEntry();
            }
        }
    }

    @PostMapping ("/sincronizar")
    public ResponseEntity<Map<String, Integer>> sincronizar(
            @RequestBody List<CodAjusteApuracaoIcmsInputDto> dtosDeEntrada
    ) {
        if (dtosDeEntrada == null || dtosDeEntrada.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Map<String, Integer> resultado = codAjusteApuracaoIcmsService.sincronizarCodigosEmLote(dtosDeEntrada);
        return ResponseEntity.ok(resultado);
    }

}
