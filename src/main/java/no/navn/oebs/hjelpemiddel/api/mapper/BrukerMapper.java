package no.navn.oebs.hjelpemiddel.api.mapper;

import no.navn.oebs.hjelpemiddel.api.repository.entity.BrukerAdresseEntity;
import no.navn.oebs.hjelpemiddel.api.repository.entity.BrukerPassEntity;
import org.openapitools.model.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class BrukerMapper {

    public Adresser toAdresser(List<BrukerAdresseEntity> adresserEntity) {
        if (adresserEntity == null || adresserEntity.isEmpty()) {
            return null;
        }
        Adresser adresser = new Adresser();
        //todo: Hvordan skal det håndteres hvis det finnes flere adresser med primæradresse? Skal primæradressen inkluderes i listen over adresser?
        adresserEntity.forEach(adresseEntity ->
                {
                    Personadresse personadresse= toPersonadresse(adresseEntity);
                    if ("Y".equals(adresseEntity.getPrimaerAdr())) {
                        adresser.setPrimaeradresse(personadresse);
                    } else {
                        adresser.addAdresserItem(personadresse);
                    }
                }
        );
        return adresser;
    }

    public Status toStatus(BrukerAdresseEntity adresse) {
        //todo: Skal aktiv være en eller to parametre?
        return new Status().aktiv(isActive(adresse));
    }

    public Personadresse toPersonadresse(BrukerAdresseEntity adresse) {
        return new Personadresse()
                .bostedsadresse(toBostedsadresse(adresse))
                .leveringsadresse(toLeveringsadresse(adresse));
    }

    public Adresse toBostedsadresse(BrukerAdresseEntity adresse) {
        return new Adresse()
                .gateadresse(adresse.getAdresse())
                .postnummer(adresse.getPostNr())
                .by(adresse.getBy())
                .kommune(adresse.getKommune());
    }

    public Adresse toLeveringsadresse(BrukerAdresseEntity adresse) {
        return new Adresse()
                .gateadresse(adresse.getLeveringsAdresse())
                .postnummer(adresse.getLeveringsPostNr())
                .by(adresse.getLeveringsBy())
                .kommune(adresse.getLeveringsKommune())
                .bydel(adresse.getLeveringsBydel());
    }

    public boolean isActive(BrukerAdresseEntity brukerAdresse) {
        String activeStatus = "A"; // "A"  from OeBS indicates active status
        return brukerAdresse.getStatusBrukernr().equals(activeStatus)
                && brukerAdresse.getStatusFnr().equals(activeStatus);
    }

    public Brukerpass toBrukerpass(BrukerPassEntity brukerPass){
        return new Brukerpass()
                .kontraktnr(brukerPass.getKontraktNr())
                .startdato(toLocalDate(brukerPass.getStartDate()))
                .sluttdato(toLocalDate(brukerPass.getEndDate()));
    }


    public LocalDate toLocalDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateString, formatter).toLocalDate();
    }
}
