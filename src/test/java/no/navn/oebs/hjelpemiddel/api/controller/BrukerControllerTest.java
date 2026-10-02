package no.navn.oebs.hjelpemiddel.api.controller;

import no.nav.security.token.support.spring.test.EnableMockOAuth2Server;
import no.navn.oebs.hjelpemiddel.api.service.BrukerService;
import org.junit.jupiter.api.Test;
import org.openapitools.model.Adresser;
import org.openapitools.model.BrukernummerOppslagRequest;
import org.openapitools.model.Brukerpass;
import org.openapitools.model.Status;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@EnableMockOAuth2Server
class BrukerControllerTest {

    private final StubBrukerService brukerService = new StubBrukerService();
    private final BrukerController controller = new BrukerController(brukerService);

    @Test
    void shouldReturnStatusFromService() {
        brukerService.status = new Status().aktiv(true);

        var response = controller.getBrukerStatus(UUID.randomUUID(), "123");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(brukerService.status);
    }

    @Test
    void shouldReturnBrukernummerFromService() {
        BrukernummerOppslagRequest request = new BrukernummerOppslagRequest("12345678910");
        brukerService.brukernummer = "555";

        var response = controller.getBrukernummer(UUID.randomUUID(), request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getBrukernummer()).isEqualTo("555");
    }

    @Test
    void shouldReturnBrukerpassFromService() {
        Brukerpass pass = new Brukerpass().kontraktnr("K-1");
        brukerService.brukerpass = List.of(pass);

        var response = controller.getBrukerPass(UUID.randomUUID(), "123");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsExactly(pass);
    }

    @Test
    void shouldReturnAdresserFromService() {
        brukerService.adresser = new Adresser();

        var response = controller.getAdresser(UUID.randomUUID(), "123");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(brukerService.adresser);
    }

    private static class StubBrukerService extends BrukerService {
        private Status status;
        private String brukernummer;
        private List<Brukerpass> brukerpass;
        private Adresser adresser;

        private StubBrukerService() {
            super(null, null, null);
        }

        @Override
        public Status getStatusByBrukernummer(String brukernummer) {
            return status;
        }

        @Override
        public String getBrukernummerByFnr(String fnr) {
            return brukernummer;
        }

        @Override
        public List<Brukerpass> getBrukerpassByBrukernummer(String brukernummer) {
            return brukerpass;
        }

        @Override
        public Adresser getAdresserByBrukernummer(String brukernummer) {
            return adresser;
        }
    }
}
