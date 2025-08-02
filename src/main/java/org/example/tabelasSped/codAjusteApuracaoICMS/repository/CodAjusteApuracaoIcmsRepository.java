package org.example.tabelasSped.codAjusteApuracaoICMS.repository;

import org.example.tabelasSped.codAjusteApuracaoICMS.entity.CodAjusteApuracaoIcmsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CodAjusteApuracaoIcmsRepository extends JpaRepository<CodAjusteApuracaoIcmsEntity, Long>, QueryByExampleExecutor<CodAjusteApuracaoIcmsEntity> {

    List<CodAjusteApuracaoIcmsEntity> findByDataInsertAfterOrDataUpdateAfter(Instant dataInsert, Instant dataUpdate);

    Optional<CodAjusteApuracaoIcmsEntity> findByCodigoAjusteAndDataInicio(String codigoAjuste, String dataInicio);

}
