package no.navn.oebs.hjelpemiddel.api.repository;

import no.navn.oebs.hjelpemiddel.api.repository.entity.KontraktDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KontraktJpaRepository extends JpaRepository<KontraktDB, Long> {

    @Query(value = """
            SELECT *
            FROM apps.xxrtv_digihot_oebs_brukerp_v
            WHERE fnr = :fnr
             """, nativeQuery = true)
    List<KontraktDB> getBrukerpassByFnr(@Param("fnr") String fnr);

}
