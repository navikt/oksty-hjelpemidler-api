package no.navn.oebs.hjelpemiddel.api.controller;

import lombok.AllArgsConstructor;
import no.nav.security.token.support.core.api.Protected;
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

    @Protected
    @Override
    public ResponseEntity<Bruker> getBruker(UUID xCorrelationId, FnrRequestBody request) {
        Bruker bruker = brukerService.getBrukerByFnr(request.getFnr());
        return ResponseEntity.ok(bruker);
    }

    @Protected
    @Override
    public ResponseEntity<List<Brukerpass>> getBrukerPass(UUID xCorrelationId, FnrRequestBody request) {
        List<Brukerpass> brukerpass = brukerService.getBrukerpassByFnr(request.getFnr());
        return ResponseEntity.ok(brukerpass);
    }

    @Protected
    @Override
    public ResponseEntity<Adresser> getAdresser(UUID xCorrelationId, FnrRequestBody request){
        return ResponseEntity.ok(brukerService.getAdresserByFnr(request.getFnr()));
    }
}
