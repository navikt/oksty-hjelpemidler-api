package no.navn.oebs.hjelpemiddel.api.service;

import lombok.AllArgsConstructor;
import no.navn.oebs.hjelpemiddel.api.exception.ResourceNotFoundException;
import no.navn.oebs.hjelpemiddel.api.mapper.BrukerMapper;
import no.navn.oebs.hjelpemiddel.api.repository.BrukerAdresseJpaRepository;
import no.navn.oebs.hjelpemiddel.api.repository.BrukerPassJpaRepository;
import no.navn.oebs.hjelpemiddel.api.repository.entity.BrukerAdresseEntity;
import no.navn.oebs.hjelpemiddel.api.repository.entity.BrukerPassEntity;
import org.openapitools.model.Adresser;
import org.openapitools.model.Status;
import org.openapitools.model.Brukerpass;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BrukerService {

    private final BrukerAdresseJpaRepository brukerAdresseJpaRepository;
    private final BrukerPassJpaRepository brukerPassJpaRepository;
    private final BrukerMapper brukerMapper;

    public Status getStatusByBrukernummer(String brukernummer) {
        List<BrukerAdresseEntity> brukerAdresseEntityList = brukerAdresseJpaRepository.getBrukerAdresseByBrukernummer(brukernummer);
        if (brukerAdresseEntityList.isEmpty()) {
            throw new ResourceNotFoundException("No status found for the given brukernummer");
        }
        return brukerMapper.toStatus(brukerAdresseEntityList.getFirst());
    }

    public Adresser getAdresserByBrukernummer(String brukernummer) {
        List<BrukerAdresseEntity> brukerAdresseEntityList = brukerAdresseJpaRepository.getBrukerAdresseByBrukernummer(brukernummer);
        if (brukerAdresseEntityList.isEmpty()) {
            throw new ResourceNotFoundException("No addresses found for the given brukernummer");
        }
        return brukerMapper.toAdresser(brukerAdresseEntityList);
    }

    public String getBrukernummerByFnr(String fnr) {
        List<String> brukernummerList = brukerAdresseJpaRepository.getBrukernummerByFnr(fnr);
        if (brukernummerList.isEmpty()) {
            throw new ResourceNotFoundException("No brukernummer found for the given fødselsnummer");
        }
        return brukernummerList.getFirst();
    }

    public String getFnrByBrukernummer(String brukernummer) {
        List<String> fnrList = brukerAdresseJpaRepository.getFnrByBrukernummer(brukernummer);
        return !fnrList.isEmpty() ? fnrList.getFirst() : null;
    }

    public List<Brukerpass> getBrukerpassByBrukernummer(String brukernummer) {
        String fnr = getFnrByBrukernummer(brukernummer);
        if (fnr == null) {
            throw new ResourceNotFoundException("No brukerpass found for the given brukernummer");
        }
        List<BrukerPassEntity> brukerPassEntityList = brukerPassJpaRepository.getBrukerpassByFnr(fnr);
        if (brukerPassEntityList == null || brukerPassEntityList.isEmpty()) {
            throw new ResourceNotFoundException("No brukerpass found for the given brukernummer");
        }
        return brukerPassEntityList.stream().map(brukerMapper::toBrukerpass).toList();
    }

}
