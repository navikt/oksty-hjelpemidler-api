package no.navn.oebs.hjelpemiddel.api.service;

import lombok.AllArgsConstructor;
import no.navn.oebs.hjelpemiddel.api.exception.ResourceNotFoundException;
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
        if (brukerDBList.isEmpty()) {
            throw new ResourceNotFoundException("No status found for the given brukernummer");
        }
        return brukerMapper.mapBrukerDBToStatus(brukerDBList.getFirst());
    }

    public Adresser getAdresseByBrukerNr(String brukernummer) {
        List<BrukerDB> brukerDBList = adresseJpaRepository.getBrukerByBrukerNr(brukernummer);
        if (brukerDBList.isEmpty()) {
            throw new ResourceNotFoundException("No addresses found for the given brukernummer");
        }
        return brukerMapper.mapDbAdreserToAdresser(brukerDBList);
    }

    public String getBrukerNrByFnr(String fnr) {
        List<String> brukerNr = adresseJpaRepository.getBrukerNrByFnr(fnr);
        if (brukerNr.isEmpty()) {
            throw new ResourceNotFoundException("No brukernummer found for the given fødselsnummer");
        }
        return brukerNr.getFirst();
    }

    public String getFnrByBrukerNr(String brukerNr) {
        List<String> fnr = adresseJpaRepository.getFnrByBrukerNr(brukerNr);
        return !fnr.isEmpty() ? fnr.getFirst() : null;
    }

    public List<Brukerpass> getBrukerpassByBrukernummer(String brukernummer) {
        String fnr = getFnrByBrukerNr(brukernummer);
        if (fnr == null) {
            throw new ResourceNotFoundException("No brukerpass found for the given brukernummer");
        }
        List<KontraktDB> kontraktDB = kontraktJpaRepository.getBrukerpassByFnr(fnr);
        if (kontraktDB == null || kontraktDB.isEmpty()) {
            throw new ResourceNotFoundException("No brukerpass found for the given brukernummer");
        }
        return kontraktDB.stream().map(brukerMapper::getBrukerPass).toList();
    }

}
