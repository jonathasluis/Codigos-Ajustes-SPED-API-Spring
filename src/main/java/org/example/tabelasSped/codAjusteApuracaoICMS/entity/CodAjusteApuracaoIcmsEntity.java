package org.example.tabelasSped.codAjusteApuracaoICMS.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table (name = "codigos_ajustes_apuracao")
public class CodAjusteApuracaoIcmsEntity {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "cod_aj_apur", length = 50)
    private String codigoAjuste;

    @Column (name = "descricao", length = 1000)
    private String descricao;

    @Column (name = "uf", length = 2)
    private String uf;

    @Column (name = "data_inicio", length = 8)
    private String dataInicio;

    @Column (name = "data_fim", length = 8)
    private String dataFim;

    @CreationTimestamp
    @Column (name = "data_insert", nullable = false, updatable = false)
    private Instant dataInsert;

    @UpdateTimestamp
    @Column (name = "data_update")
    private Instant dataUpdate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoAjuste() {
        return codigoAjuste;
    }

    public void setCodigoAjuste(String codigoAjuste) {
        this.codigoAjuste = codigoAjuste;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getDataFim() {
        return dataFim;
    }

    public void setDataFim(String dataFim) {
        this.dataFim = dataFim;
    }

    public Instant getDataInsert() {
        return dataInsert;
    }

    public void setDataInsert(Instant dataInsert) {
        this.dataInsert = dataInsert;
    }

    public Instant getDataUpdate() {
        return dataUpdate;
    }

    public void setDataUpdate(Instant dataUpdate) {
        this.dataUpdate = dataUpdate;
    }
}