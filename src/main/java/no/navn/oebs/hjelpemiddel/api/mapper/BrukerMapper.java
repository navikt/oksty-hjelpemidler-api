package no.navn.oebs.hjelpemiddel.api.mapper;

import no.navn.oebs.hjelpemiddel.api.repository.entity.BrukerDB;
import no.navn.oebs.hjelpemiddel.api.repository.entity.KontraktDB;
import org.openapitools.model.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class BrukerMapper {

    public Adresser mapDbAdreserToAdresser(List<BrukerDB> brukerDBList) {
        if (brukerDBList == null || brukerDBList.isEmpty()) {
            return null;
        }
        Adresser adresser = new Adresser();
        for (BrukerDB brukerDB : brukerDBList) {
            adresser.getAdresser().add(getPersonAdresse(brukerDB));
        }
        brukerDBList.forEach(brukerDB -> adresser.getAdresser().add(getPersonAdresse(brukerDB)));
        brukerDBList.stream().filter( it -> it.getPrimaerAdr().equals("Y")).findFirst().ifPresent(primAdr ->  adresser.setPrimaeradresse(getPersonAdresse(primAdr)));
        return adresser;
    }

    public Status mapBrukerDBToStatus(BrukerDB brukerDB) {
        return new Status().aktiv(getStatus(brukerDB)); //todo: Skal aktiv være en eller to parametre?
    }

    public Personadresse getPersonAdresse(BrukerDB brukerDB) {
        return new Personadresse()
                .bostedsadresse(getAdresse(brukerDB))
                .leveringsadresse(getLeveringsAdresse(brukerDB));
    }

    public Adresse getAdresse(BrukerDB brukerDB) {
        Adresse adresse = new Adresse();
        adresse.setGateadresse(brukerDB.getAdresse()); //Hva slags adresse er dette?
        adresse.setPostnummer(brukerDB.getPostNr());
        adresse.by(brukerDB.getBy());
        adresse.kommune(brukerDB.getKommune());
        return adresse;
    }

    public Adresse getLeveringsAdresse(BrukerDB brukerDB) {
        Adresse adresse = new Adresse();
        adresse.setGateadresse(brukerDB.getLeveringsAdresse());
        adresse.setPostnummer(brukerDB.getLeveringsPostNr());
        adresse.by(brukerDB.getLeveringsBy());
        adresse.kommune(brukerDB.getLeveringsKommune());
        adresse.bydel(brukerDB.getLeveringsBydel());
        return adresse;
    }

    public boolean getStatus(BrukerDB brukerDB) {
        String activeStatus = "A"; // "A"  from OeBS indicates active status
        return brukerDB.getStatusBrukernr().equals(activeStatus)
                && brukerDB.getStatusFnr().equals(activeStatus);
    }

    public Brukerpass getBrukerPass(KontraktDB kontraktDB){
        return new Brukerpass()
                .kontraktnr(kontraktDB.getKontraktNr())
                .startdato(mapStringToLocalDate(kontraktDB.getStartDate()))
                .sluttdato(mapStringToLocalDate(kontraktDB.getEndDate()));
    }


    public LocalDate mapStringToLocalDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateString, formatter).toLocalDate();
    }
}
