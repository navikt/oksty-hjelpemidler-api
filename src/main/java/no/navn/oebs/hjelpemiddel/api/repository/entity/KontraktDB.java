package no.navn.oebs.hjelpemiddel.api.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
@Table(name = "xxrtv_digihot_oebs_brukerp_v", schema = "APPS")
public class KontraktDB {

    // Det finnes flere type kontrakter, men dette viewet inneholder bare brukerpass

    //Måte å registere en avtale. Eksemepel kontakte leverandør for å hente ut det
    //Kan ha flere kontrakter. Brukerpass er en type kontrakt, men nå er det den eneste
    //Det er bare brukerpass - har noe med saksbehandlingen å gjøre. Sluttdato har ikke så mye å si.


    @Id
    @Column(name = "kontrakt_id")
    private String id;

    @Column(name = "fnr")
    private String fodselNr;

    @Column(name="kontrakt_nummer")
    private String kontraktNr;

    @Column(name="start_date")
    private String startDate;

    @Column(name="end_date")
    private String endDate;

}
