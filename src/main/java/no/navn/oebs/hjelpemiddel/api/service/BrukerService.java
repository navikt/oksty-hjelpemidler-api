package no.navn.oebs.hjelpemiddel.api.service;

import lombok.AllArgsConstructor;
import no.navn.oebs.hjelpemiddel.api.dto.BrukerInfoRespons;
import no.navn.oebs.hjelpemiddel.api.dto.BrukerpassRespons;
import no.navn.oebs.hjelpemiddel.api.exception.ResourceNotFoundException;
import no.navn.oebs.hjelpemiddel.api.mapper.BrukerMapper;
import no.navn.oebs.hjelpemiddel.api.repository.BrukerAdresseJpaRepository;
import no.navn.oebs.hjelpemiddel.api.repository.BrukerinfoProcedure;
import no.navn.oebs.hjelpemiddel.api.repository.BrukerpassProcedure;
import no.navn.oebs.hjelpemiddel.api.repository.entity.BrukerAdresseEntity;
import org.openapitools.model.Adresser;
import org.openapitools.model.Bruker;
import org.openapitools.model.Brukerpass;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BrukerService {

    private final BrukerAdresseJpaRepository brukerAdresseJpaRepository;
    private final BrukerMapper brukerMapper;
    private final BrukerpassProcedure brukerpassProcedure;
    private final BrukerinfoProcedure brukerinfoProcedure;

    public Adresser getAdresserByFnr(String fnr) {
        List<BrukerAdresseEntity> brukerAdresseEntityList = brukerAdresseJpaRepository.getBrukerAdresseByFnr(fnr);
        if (brukerAdresseEntityList.isEmpty()) {
            throw new ResourceNotFoundException("No addresses found for the given brukernummer");
        }
        return brukerMapper.toAdresser(brukerAdresseEntityList);
    }

    public Bruker getBrukerByFnr(String fnr) {
        BrukerInfoRespons response = brukerinfoProcedure.executeBrukerinfoProcedure(fnr);
        if (response == null) {
            throw new ResourceNotFoundException("No brukernummer found for the given fødselsnummer");
        }
        return brukerMapper.toBruker(response);
    }

    public String getFnrByBrukernummer(String brukernummer) {
        List<String> fnrList = brukerAdresseJpaRepository.getFnrByBrukernummer(brukernummer);
        return !fnrList.isEmpty() ? fnrList.getFirst() : null;
    }

    public List<Brukerpass> getBrukerpassByFnr(String fnr) {
        BrukerpassRespons respons = brukerpassProcedure.executeBrukerpassProcedure(fnr);
        if (respons == null) {
            throw new ResourceNotFoundException("No brukerpass found for the given brukernummer");
        }
        return List.of(brukerMapper.toBrukerpass(respons));
    }

}
