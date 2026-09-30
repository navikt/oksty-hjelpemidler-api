package no.navn.oebs.hjelpemiddel.api.controller;

import lombok.AllArgsConstructor;
import no.nav.security.token.support.core.api.Unprotected;
import no.navn.oebs.hjelpemiddel.api.service.BrukerService;
import org.openapitools.api.BrukerApi;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;


@Controller
@AllArgsConstructor
public class BrukerController implements BrukerApi {

    private final BrukerService brukerService;
                                         
    @Unprotected
    @Override
    public ResponseEntity<Status> getBrukerStatus(String brukerNr) {
        Status bruker = brukerService.getBrukerByBrukerNr(brukerNr);
        if (bruker != null) {
            return ResponseEntity.ok(bruker);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Unprotected
    @Override
    public ResponseEntity<BrukernummerOppslagResponse> getBrukernummer(BrukernummerOppslagRequest request) {
        String brukerNr = brukerService.getBrukerNrByFnr(request.getFnr());
        BrukernummerOppslagResponse response = new BrukernummerOppslagResponse();
        response.setBrukernummer(brukerNr);
        return ResponseEntity.ok(response);
    }

    @Unprotected
    @Override
    public ResponseEntity<Brukerpass> getBrukerPass(String brukernummer) {
        //todo: Legge inn handtering av hvis fnr ikke finnes i kontraktDB
        Brukerpass brukerpass = brukerService.getBrukerpassByBrukernummer(brukernummer);
        if (brukerpass == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brukerpass);
    }

    @Unprotected
    @Override
    public ResponseEntity<Adresser> getAdresser(String brukernummer){
        Adresser adresser = brukerService.getAdresseByBrukerNr(brukernummer);
        return ResponseEntity.ok(adresser);
    }
}
