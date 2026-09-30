package no.navn.oebs.hjelpemiddel.api.repository;

import no.navn.oebs.hjelpemiddel.api.repository.entity.BrukerDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdresseJpaRepository extends JpaRepository<BrukerDB, Long> {


    @Query(value = """
            SELECT bruker_nummer
            FROM APPS.XXRTV_DIGIHOT_OEBS_ADR_FNR_V
            WHERE fnr = :fnr
             """, nativeQuery = true)
    List<String> getBrukerNrByFnr(@Param("fnr") String fnr);

    @Query(value = """
            SELECT fnr
            FROM APPS.XXRTV_DIGIHOT_OEBS_ADR_FNR_V
            WHERE bruker_nummer = :bruker_nummer
             """, nativeQuery = true)
    List<String> getFnrByBrukerNr(@Param("bruker_nummer") String bruker_nummer);

    @Query(value = """
            SELECT *
            FROM APPS.XXRTV_DIGIHOT_OEBS_ADR_FNR_V
            WHERE bruker_nummer = :bruker_nummer
             """, nativeQuery = true)
    List<BrukerDB> getBrukerByBrukerNr(@Param("bruker_nummer") String brukerNr);

}
