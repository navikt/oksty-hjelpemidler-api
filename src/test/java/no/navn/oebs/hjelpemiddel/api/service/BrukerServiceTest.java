package no.navn.oebs.hjelpemiddel.api.service;

import no.navn.oebs.hjelpemiddel.api.dto.BrukerInfoRespons;
import no.navn.oebs.hjelpemiddel.api.dto.BrukerpassRespons;
import no.navn.oebs.hjelpemiddel.api.exception.ResourceNotFoundException;
import no.navn.oebs.hjelpemiddel.api.mapper.BrukerMapper;
import no.navn.oebs.hjelpemiddel.api.repository.BrukerAdresseJpaRepository;
import no.navn.oebs.hjelpemiddel.api.repository.BrukerinfoProcedure;
import no.navn.oebs.hjelpemiddel.api.repository.BrukerpassProcedure;
import no.navn.oebs.hjelpemiddel.api.repository.entity.BrukerAdresseEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.Adresser;
import org.openapitools.model.Brukerpass;
import org.openapitools.model.Status;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrukerServiceTest {

    @Mock
    private BrukerAdresseJpaRepository brukerAdresseJpaRepository;

    @Mock
    private BrukerpassProcedure brukerpassProcedure;

    @Mock
    private BrukerinfoProcedure brukerinfoProcedure;

    @Mock
    private BrukerMapper brukerMapper;

    @InjectMocks
    private BrukerService brukerService;

    @Test
    void shouldReturnStatusWhenBrukernummerExists() {
        BrukerInfoRespons respons = new BrukerInfoRespons("123", "Aktiv", "fnr", "A", null);
        Status expectedStatus = new Status().aktiv(true);

        when(brukerAdresseJpaRepository.getFnrByBrukernummer("123")).thenReturn(List.of("fnr"));
        when(brukerinfoProcedure.executeBrukerinfoProcedure("fnr")).thenReturn(respons);
        when(brukerMapper.toStatus(respons)).thenReturn(expectedStatus);

        Status actual = brukerService.getStatusByBrukernummer("123");

        assertThat(actual).isEqualTo(expectedStatus);
        verify(brukerinfoProcedure).executeBrukerinfoProcedure("fnr");
        verify(brukerMapper).toStatus(respons);
    }

    @Test
    void shouldThrowWhenFnrMissingForStatusLookup() {
        when(brukerAdresseJpaRepository.getFnrByBrukernummer("123")).thenReturn(List.of());

        assertThatThrownBy(() -> brukerService.getStatusByBrukernummer("123"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No status found for the given brukernummer");
    }

    @Test
    void shouldThrowWhenBrukerinfoNotFoundForStatusLookup() {
        when(brukerAdresseJpaRepository.getFnrByBrukernummer("123")).thenReturn(List.of("fnr"));
        when(brukerinfoProcedure.executeBrukerinfoProcedure("fnr")).thenReturn(null);

        assertThatThrownBy(() -> brukerService.getStatusByBrukernummer("123"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No status found for the given brukernummer");
    }

    @Test
    void shouldReturnAdresserWhenBrukernummerExists() {
        BrukerAdresseEntity entity = new BrukerAdresseEntity();
        Adresser expectedAdresser = new Adresser();
        List<BrukerAdresseEntity> entities = List.of(entity);

        when(brukerAdresseJpaRepository.getBrukerAdresseByBrukernummer("123")).thenReturn(entities);
        when(brukerMapper.toAdresser(entities)).thenReturn(expectedAdresser);

        Adresser actual = brukerService.getAdresserByBrukernummer("123");

        assertThat(actual).isEqualTo(expectedAdresser);
    }

    @Test
    void shouldThrowWhenAdresserNotFound() {
        when(brukerAdresseJpaRepository.getBrukerAdresseByBrukernummer("123")).thenReturn(List.of());

        assertThatThrownBy(() -> brukerService.getAdresserByBrukernummer("123"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No addresses found for the given brukernummer");
    }

    @Test
    void shouldReturnBrukernummerWhenFnrExists() {
        BrukerInfoRespons respons = new BrukerInfoRespons("123", "Aktiv", "fnr", "A", null);
        when(brukerinfoProcedure.executeBrukerinfoProcedure("fnr")).thenReturn(respons);

        String actual = brukerService.getBrukernummerByFnr("fnr");

        assertThat(actual).isEqualTo("123");
        verify(brukerinfoProcedure).executeBrukerinfoProcedure("fnr");
    }

    @Test
    void shouldThrowWhenBrukernummerNotFoundForFnr() {
        when(brukerinfoProcedure.executeBrukerinfoProcedure("fnr")).thenReturn(null);

        assertThatThrownBy(() -> brukerService.getBrukernummerByFnr("fnr"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No brukernummer found for the given fødselsnummer");
    }

    @Test
    void shouldReturnFnrWhenBrukernummerExists() {
        when(brukerAdresseJpaRepository.getFnrByBrukernummer("123")).thenReturn(List.of("fnr"));

        String actual = brukerService.getFnrByBrukernummer("123");

        assertThat(actual).isEqualTo("fnr");
    }

    @Test
    void shouldReturnNullWhenFnrNotFound() {
        when(brukerAdresseJpaRepository.getFnrByBrukernummer("123")).thenReturn(List.of());

        String actual = brukerService.getFnrByBrukernummer("123");

        assertThat(actual).isNull();
    }

    @Test
    void shouldReturnBrukerpassWhenFound() {
        BrukerpassRespons respons = new BrukerpassRespons(
                "fnr", "123", "K-123", "Kontroll", null, null, "Aktiv");
        Brukerpass mapped = new Brukerpass();

        when(brukerAdresseJpaRepository.getFnrByBrukernummer("123")).thenReturn(List.of("fnr"));
        when(brukerpassProcedure.executeBrukerpassProcedure("fnr")).thenReturn(respons);
        when(brukerMapper.toBrukerpass(respons)).thenReturn(mapped);

        List<Brukerpass> actual = brukerService.getBrukerpassByBrukernummer("123");

        assertThat(actual).containsExactly(mapped);
        verify(brukerpassProcedure).executeBrukerpassProcedure("fnr");
        verify(brukerMapper).toBrukerpass(respons);
    }

    @Test
    void shouldThrowWhenFnrMissingForBrukerpassLookup() {
        when(brukerAdresseJpaRepository.getFnrByBrukernummer("123")).thenReturn(List.of());

        assertThatThrownBy(() -> brukerService.getBrukerpassByBrukernummer("123"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No brukerpass found for the given brukernummer");
    }

    @Test
    void shouldThrowWhenBrukerpassResponseIsNull() {
        when(brukerAdresseJpaRepository.getFnrByBrukernummer("123")).thenReturn(List.of("fnr"));
        when(brukerpassProcedure.executeBrukerpassProcedure("fnr")).thenReturn(null);

        assertThatThrownBy(() -> brukerService.getBrukerpassByBrukernummer("123"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No brukerpass found for the given brukernummer");
    }
}
