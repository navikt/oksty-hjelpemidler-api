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
        return !brukerDBList.isEmpty() ? brukerMapper.mapBrukerDBToStatus(brukerDBList.getFirst()) : null;
    }

    public Adresser getAdresseByBrukerNr(String brukernummer) {
        List<BrukerDB> brukerDBList = adresseJpaRepository.getBrukerByBrukerNr(brukernummer);
        return !brukerDBList.isEmpty() ? brukerMapper.mapDbAdreserToAdresser(brukerDBList) : null;
    }

    public String getBrukerNrByFnr(String fnr) {
        List<String> brukerNr = adresseJpaRepository.getBrukerNrByFnr(fnr);
        return !brukerNr.isEmpty() ? brukerNr.getFirst() : null;
    }

    public String getFnrByBrukerNr(String brukerNr) {
        List<String> fnr = adresseJpaRepository.getFnrByBrukerNr(brukerNr);
        return !fnr.isEmpty() ? fnr.getFirst() : null;
    }

    public List<Brukerpass> getBrukerpassByBrukernummer(String brukernummer) {
        String fnr = getFnrByBrukerNr(brukernummer);
        if (fnr == null) {
            return null;
        }
        List<KontraktDB> kontraktDB = kontraktJpaRepository.getBrukerpassByFnr(fnr);
        return kontraktDB != null ? kontraktDB.stream().map(brukerMapper::getBrukerPass).toList() : null;
    }

}
