package no.navn.oebs.hjelpemiddel.api.controller;

import lombok.AllArgsConstructor;
import no.nav.security.token.support.core.api.Protected;
import org.openapitools.api.BrukerApi;
import org.openapitools.model.Bruker;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;


@Controller
@AllArgsConstructor
public class BrukerController implements BrukerApi {

    //private final BrukerService brukerService;
                                         
    @Override
    @Protected
    public ResponseEntity<Bruker> getBruker(String fnr) {
//        Bruker bruker = brukerService.getBrukerByFnr(fnr);
//        if (bruker != null) {
//            return ResponseEntity.ok(bruker);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
        return ResponseEntity.ok(new Bruker());
    }

    @Override
    @Protected
    public ResponseEntity<String> getBrukernummer(String fnr) {
//        String brukerNr = brukerService.getBrukerNrByFnr(fnr);
//        return ResponseEntity.ok(brukerNr);
        return ResponseEntity.ok("123456789");
    }

    @Override
    @Protected
    public ResponseEntity<String> getFodselsnummer(String brukerNr) {
//        String fodselsnummer = brukerService.getFnrByBrukerNr(brukerNr);
//        return ResponseEntity.ok(fodselsnummer);
        return ResponseEntity.ok("01010112345");
    }


}
