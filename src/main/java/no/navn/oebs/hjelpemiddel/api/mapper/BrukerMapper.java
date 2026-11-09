package no.navn.oebs.hjelpemiddel.api.mapper;

import no.navn.oebs.hjelpemiddel.api.dto.BrukerInfoRespons;
import no.navn.oebs.hjelpemiddel.api.dto.BrukerpassRespons;
import no.navn.oebs.hjelpemiddel.api.repository.entity.BrukerAdresseEntity;
import org.openapitools.model.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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

    public Bruker toBruker(BrukerInfoRespons brukerInfoRespons) {
        return new Bruker()
                .aktiv(brukerInfoRespons.brukerStatus().equals("Aktiv"))
                .brukernummer(brukerInfoRespons.brukerNr());
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

    public Brukerpass toBrukerpass(BrukerpassRespons brukerpassRespons){
        LocalDateTime endDate = brukerpassRespons.endDate();
        LocalDateTime startDate = brukerpassRespons.startDate();
        return new Brukerpass().
                kontraktnr(brukerpassRespons.kontraktNr())
                .startdato(startDate == null ? null : startDate.toLocalDate())
                .sluttdato(endDate == null ? null : endDate.toLocalDate());
    }

}
