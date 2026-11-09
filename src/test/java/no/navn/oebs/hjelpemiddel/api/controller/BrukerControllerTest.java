package no.navn.oebs.hjelpemiddel.api.controller;

import no.nav.security.token.support.spring.test.EnableMockOAuth2Server;
import no.navn.oebs.hjelpemiddel.api.service.BrukerService;
import org.junit.jupiter.api.Test;
import org.openapitools.model.Adresser;
import org.openapitools.model.Bruker;
import org.openapitools.model.Brukerpass;
import org.openapitools.model.FnrRequestBody;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@EnableMockOAuth2Server
class BrukerControllerTest {

    private final StubBrukerService brukerService = new StubBrukerService();
    private final BrukerController controller = new BrukerController(brukerService);

    @Test
    void shouldReturnBrukerFromService() {
        FnrRequestBody request = new FnrRequestBody("12345678910");
        brukerService.bruker = new Bruker();

        var response = controller.getBruker(UUID.randomUUID(), request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(brukerService.bruker);
        assertThat(brukerService.fnr).isEqualTo(request.getFnr());
    }

    @Test
    void shouldReturnBrukerpassFromService() {
        FnrRequestBody request = new FnrRequestBody("12345678910");
        Brukerpass pass = new Brukerpass().kontraktnr("K-1");
        brukerService.brukerpass = List.of(pass);

        var response = controller.getBrukerPass(UUID.randomUUID(), request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsExactly(pass);
        assertThat(brukerService.fnr).isEqualTo(request.getFnr());
    }

    @Test
    void shouldReturnAdresserFromService() {
        FnrRequestBody request = new FnrRequestBody("12345678910");
        brukerService.adresser = new Adresser();

        var response = controller.getAdresser(UUID.randomUUID(), request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(brukerService.adresser);
        assertThat(brukerService.fnr).isEqualTo(request.getFnr());
    }

    private static class StubBrukerService extends BrukerService {
        private String fnr;
        private Bruker bruker;
        private List<Brukerpass> brukerpass;
        private Adresser adresser;

        private StubBrukerService() {
            super(null, null, null, null);
        }

        @Override
        public Bruker getBrukerByFnr(String fnr) {
            this.fnr = fnr;
            return bruker;
        }

        @Override
        public List<Brukerpass> getBrukerpassByFnr(String fnr) {
            this.fnr = fnr;
            return brukerpass;
        }

        @Override
        public Adresser getAdresserByFnr(String fnr) {
            this.fnr = fnr;
            return adresser;
        }
    }
}
