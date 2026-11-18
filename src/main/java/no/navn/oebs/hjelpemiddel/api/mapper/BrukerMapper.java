package no.navn.oebs.hjelpemiddel.api.mapper;

import no.navn.oebs.hjelpemiddel.api.dto.BrukerInfoRespons;
import no.navn.oebs.hjelpemiddel.api.dto.BrukerpassRespons;
import no.navn.oebs.hjelpemiddel.api.repository.entity.BrukerAdresseEntity;
import org.openapitools.model.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
public class BrukerMapper {

    public Adresser toAdresser(List<BrukerAdresseEntity> adresserEntity) {
        if (adresserEntity == null || adresserEntity.isEmpty()) {
            return null;
        }
        Adresser adresser = new Adresser();

        //All bostedsadresser are identical, therefor only add first
        adresser.setBostedsadresse(toBostedsadresse(adresserEntity.getFirst()));

        //Only add distinct leveringsadresser, they can be duplicated
        adresserEntity.forEach(adresseEntity ->
                {
                    Adresse leveringsAdresse = toLeveringsadresse(adresseEntity);
                    if (!alreadyPresentLeveringadresse(adresser, leveringsAdresse)) {
                        adresser.addLeveringsadresserItem(leveringsAdresse);
                    }
                }
        );
        return adresser;
    }

    private boolean alreadyPresentLeveringadresse(Adresser adresser, Adresse leveringsadresse) {
        for (Adresse adresse : adresser.getLeveringsadresser()){
            if (Objects.equals(adresse, leveringsadresse)){
                return true;
            }
        }
        return false;
    }

    public Adresse toValidBostedsadresse(BrukerAdresseEntity adresseEntity) {
        String aktivAdressStatus = "A";
        if (    aktivAdressStatus.equals(adresseEntity.getStatusBrukernr())
                && aktivAdressStatus.equals(adresseEntity.getStatusFnr())
                && aktivAdressStatus.equals(adresseEntity.getStatusAdrPSted())
                && aktivAdressStatus.equals(adresseEntity.getStatusAdrCSted())
                && aktivAdressStatus.equals(adresseEntity.getStatusAdrBruk())
        ) {
            return toBostedsadresse(adresseEntity);
        }
        return null;
    }

    public Bruker toBruker(BrukerInfoRespons brukerInfoRespons) {
        return new Bruker()
                .aktiv(brukerInfoRespons.brukerStatus().equals("Aktiv"))
                .brukernummer(brukerInfoRespons.brukerNr());
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
