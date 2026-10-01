package no.navn.oebs.hjelpemiddel.api.controller;

import lombok.AllArgsConstructor;
import no.nav.security.token.support.core.api.Unprotected;
import no.navn.oebs.hjelpemiddel.api.service.BrukerService;
import org.openapitools.api.BrukerApi;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;


@Controller
@AllArgsConstructor
public class BrukerController implements BrukerApi {

    private final BrukerService brukerService;
                                         
    @Unprotected
    @Override
    public ResponseEntity<Status> getBrukerStatus(UUID xCorrelationId, String brukerNr) {
        return ResponseEntity.ok(brukerService.getBrukerByBrukerNr(brukerNr));
    }

    @Unprotected
    @Override
    public ResponseEntity<BrukernummerOppslagResponse> getBrukernummer(UUID xCorrelationId, BrukernummerOppslagRequest request) {
        String brukerNr = brukerService.getBrukerNrByFnr(request.getFnr());
        return ResponseEntity.ok(new BrukernummerOppslagResponse().brukernummer(brukerNr));
    }

    @Unprotected
    @Override
    public ResponseEntity<List<Brukerpass>> getBrukerPass(UUID xCorrelationId, String brukernummer) {
        List<Brukerpass> brukerpass = brukerService.getBrukerpassByBrukernummer(brukernummer);
        //todo: Legge til info om brukerpass eller fnr ikke funnet, hvis brukernummer ikke gjøres tilgjengelig i viewet
        //todo: Skal det returners 200 eller 404 hvis det ikke finnes brukerpass for brukernummeret? Hvis 200, skal det returneres en tom liste eller null?
        return ResponseEntity.ok(brukerpass);
    }

    @Unprotected
    @Override
    public ResponseEntity<Adresser> getAdresser(UUID xCorrelationId, String brukernummer){
        return ResponseEntity.ok(brukerService.getAdresseByBrukerNr(brukernummer));
    }
}
