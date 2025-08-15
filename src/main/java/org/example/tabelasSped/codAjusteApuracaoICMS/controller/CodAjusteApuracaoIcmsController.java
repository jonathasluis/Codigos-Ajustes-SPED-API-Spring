package org.example.tabelasSped.codAjusteApuracaoICMS.controller;

import org.example.tabelasSped.codAjusteApuracaoICMS.dto.CodAjusteApuracaoIcmsInputDto;
import org.example.tabelasSped.codAjusteApuracaoICMS.dto.CodAjusteApuracaoIcmsOutputDto;
import org.example.tabelasSped.codAjusteApuracaoICMS.service.CodAjusteApuracaoIcmsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.nio.charset.UnsupportedCharsetException;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping ("/codigos-ajustes-apuracao")
public class CodAjusteApuracaoIcmsController {

    private final CodAjusteApuracaoIcmsService codAjusteApuracaoIcmsService;

    public CodAjusteApuracaoIcmsController(CodAjusteApuracaoIcmsService codAjusteApuracaoIcmsService) {
        this.codAjusteApuracaoIcmsService = codAjusteApuracaoIcmsService;
    }

    @GetMapping
    public ResponseEntity<List<CodAjusteApuracaoIcmsOutputDto>> pesquisarCodigosAjustes(
            @RequestParam (name = "codigo", required = false) List<String> codigos,
            @RequestParam (required = false) String uf,
            @RequestParam (required = false) @DateTimeFormat (pattern = "ddMMyyyy") String dataInicio
    ) {
        List<CodAjusteApuracaoIcmsOutputDto> dtos = codAjusteApuracaoIcmsService.pesquisarCodigos(codigos, uf, dataInicio);
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
    public ResponseEntity<?> gerarScriptsZip(
            @RequestParam (name = "codigo", required = false) List<String> codigos,            @RequestParam (required = false) String uf,
            @RequestParam (required = false) @DateTimeFormat (pattern = "ddMMyyyy") String dataInicio,
            @RequestHeader (name = "Accept-Charset", required = false, defaultValue = "UTF-8") String encodingHeader
    ) {
        Charset charset;
        try {
            String mainEncoding = encodingHeader.split(",")[0].split(";")[0].trim();
            charset = Charset.forName(mainEncoding);
            System.out.println(mainEncoding);
        } catch (UnsupportedCharsetException e) {
            return ResponseEntity.badRequest().body("Encoding '" + encodingHeader + "' não é suportado.");
        }
        try {
            Map<String, String> arquivosSql = codAjusteApuracaoIcmsService.getSqlFiles(codigos, uf, dataInicio);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                for (Map.Entry<String, String> entry : arquivosSql.entrySet()) {
                    ZipEntry zipEntry = new ZipEntry(entry.getKey());
                    zos.putNextEntry(zipEntry);
                    zos.write(entry.getValue().getBytes(charset));
                    zos.closeEntry();
                }
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"scripts_sql_ajustes.zip\"");
            return ResponseEntity.ok()
                                 .headers(headers)
                                 .contentType(MediaType.valueOf("application/zip"))
                                 .body(baos.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Falha ao gerar o arquivo ZIP: " + e.getMessage());

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
