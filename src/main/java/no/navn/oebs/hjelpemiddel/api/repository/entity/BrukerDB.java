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
@Table(name = "xxrtv_digihot_oebs_adr_fnr_v", schema = "APPS")
public class BrukerDB {

    // Henter data fra denne tabellen for å kunne mappe bruker_nummer til fodselsnummer

    @Id
    @Column(name = "adr_bruk_id")
    private String id;

    @Column(name= "bruker_nummer")
    private String brukerNr;

    @Column(name = "fnr")
    private String fodselNr;

    @Column(name="bosteds_addresse")
    private String adresse;

    @Column(name="bosteds_postnummer")
    private String postNr;

    @Column(name="bosteds_by")
    private String by;

    @Column(name="bosteds_kommune")
    private String kommune;

    @Column(name="leverings_addresse")
    private String leveringsAdresse;

    @Column(name="leverings_postnummer")
    private String leveringsPostNr;

    @Column(name="leverings_by")
    private String leveringsBy;

    @Column(name="leverings_kommune")
    private String leveringsKommune;

    @Column(name="bydel")
    private String leveringsBydel;

    @Column(name="primaer_adr") //todo: Hva brukes denne til: Faktura adresse. Kan være noe annet. Skal være full adresse slik at det kan fakuterers mot
    private String primaerAdr;

    //Status på bruker nummer og fodselsnummer, de er to ulike entiteter. De bør ha samme status, og hvis de ikke har det så er det noe feil
    //Kan være en status på bruker
    @Column(name="status_brukernr") //todo: Hva brukes denne til.
    private String statusBrukernr;

    @Column(name="status_fnr") //todo: Hva brukes denne til. Fnr går gjennom hele verien
    private String statusFnr;

}
