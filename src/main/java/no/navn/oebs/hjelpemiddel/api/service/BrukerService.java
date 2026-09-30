package no.navn.oebs.hjelpemiddel.api.service;

import lombok.AllArgsConstructor;
import no.navn.oebs.hjelpemiddel.api.mapper.BrukerMapper;
import no.navn.oebs.hjelpemiddel.api.repository.AdresseJpaRepository;
import no.navn.oebs.hjelpemiddel.api.repository.KontraktJpaRepository;
import no.navn.oebs.hjelpemiddel.api.repository.entity.BrukerDB;
import no.navn.oebs.hjelpemiddel.api.repository.entity.KontraktDB;
import org.openapitools.model.Adresser;
import org.openapitools.model.Status;
import org.openapitools.model.Brukerpass;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BrukerService {

    private final AdresseJpaRepository adresseJpaRepository;
    private final KontraktJpaRepository kontraktJpaRepository;
    private final BrukerMapper brukerMapper;

    public Status getBrukerByBrukerNr(String brukerNr) {
        List<BrukerDB> brukerDBList = adresseJpaRepository.getBrukerByBrukerNr(brukerNr);
        if (!brukerDBList.isEmpty()) {
            return brukerMapper.mapBrukerDBToStatus(brukerDBList.getFirst());
        } else {
            return null;
        }
    }


    public Adresser getAdresseByBrukerNr(String brukernummer) {
        List<BrukerDB> brukerDBList = adresseJpaRepository.getBrukerByBrukerNr(brukernummer);
        if (!brukerDBList.isEmpty()) {
            return brukerMapper.mapDbAdreserToAdresser(brukerDBList);
        } else {
            return null;
        }
    }

    public String getBrukerNrByFnr(String fnr) {
        List<String> brukerNr = adresseJpaRepository.getBrukerNrByFnr(fnr);
        return brukerNr.getFirst();
    }

    public String getFnrByBrukerNr(String brukerNr) {
        List<String> fnr = adresseJpaRepository.getFnrByBrukerNr(brukerNr);
        return fnr.getFirst();
    }

    public Brukerpass getBrukerpassByBrukernummer(String brukernummer) {
        String fnr = getFnrByBrukerNr(brukernummer);
        KontraktDB kontraktDB = kontraktJpaRepository.getBrukerpassByFnr(fnr);
        if (kontraktDB == null) {
            return null;
        }
        return brukerMapper.getBrukerPass(kontraktDB);
    }



}
