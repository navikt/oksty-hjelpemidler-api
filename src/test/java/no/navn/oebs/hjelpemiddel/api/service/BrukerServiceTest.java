package no.navn.oebs.hjelpemiddel.api.service;

import no.navn.oebs.hjelpemiddel.api.exception.ResourceNotFoundException;
import no.navn.oebs.hjelpemiddel.api.mapper.BrukerMapper;
import no.navn.oebs.hjelpemiddel.api.repository.BrukerAdresseJpaRepository;
import no.navn.oebs.hjelpemiddel.api.repository.BrukerPassJpaRepository;
import no.navn.oebs.hjelpemiddel.api.repository.entity.BrukerAdresseEntity;
import no.navn.oebs.hjelpemiddel.api.repository.entity.BrukerPassEntity;
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
    private BrukerPassJpaRepository brukerPassJpaRepository;

    @Mock
    private BrukerMapper brukerMapper;

    @InjectMocks
    private BrukerService brukerService;

    @Test
    void shouldReturnStatusWhenBrukernummerExists() {
        BrukerAdresseEntity entity = new BrukerAdresseEntity();
        Status expectedStatus = new Status().aktiv(true);

        when(brukerAdresseJpaRepository.getBrukerAdresseByBrukernummer("123")).thenReturn(List.of(entity));
        when(brukerMapper.toStatus(entity)).thenReturn(expectedStatus);

        Status actual = brukerService.getStatusByBrukernummer("123");

        assertThat(actual).isEqualTo(expectedStatus);
    }

    @Test
    void shouldThrowWhenStatusNotFound() {
        when(brukerAdresseJpaRepository.getBrukerAdresseByBrukernummer("123")).thenReturn(List.of());

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
        when(brukerAdresseJpaRepository.getBrukernummerByFnr("fnr")).thenReturn(List.of("123"));

        String actual = brukerService.getBrukernummerByFnr("fnr");

        assertThat(actual).isEqualTo("123");
    }

    @Test
    void shouldThrowWhenBrukernummerNotFoundForFnr() {
        when(brukerAdresseJpaRepository.getBrukernummerByFnr("fnr")).thenReturn(List.of());

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
        BrukerPassEntity entity = new BrukerPassEntity();
        Brukerpass mapped = new Brukerpass();

        when(brukerAdresseJpaRepository.getFnrByBrukernummer("123")).thenReturn(List.of("fnr"));
        when(brukerPassJpaRepository.getBrukerpassByFnr("fnr")).thenReturn(List.of(entity));
        when(brukerMapper.toBrukerpass(entity)).thenReturn(mapped);

        List<Brukerpass> actual = brukerService.getBrukerpassByBrukernummer("123");

        assertThat(actual).containsExactly(mapped);
        verify(brukerMapper).toBrukerpass(entity);
    }

    @Test
    void shouldThrowWhenFnrMissingForBrukerpassLookup() {
        when(brukerAdresseJpaRepository.getFnrByBrukernummer("123")).thenReturn(List.of());

        assertThatThrownBy(() -> brukerService.getBrukerpassByBrukernummer("123"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No brukerpass found for the given brukernummer");
    }

    @Test
    void shouldThrowWhenBrukerpassListIsNull() {
        when(brukerAdresseJpaRepository.getFnrByBrukernummer("123")).thenReturn(List.of("fnr"));
        when(brukerPassJpaRepository.getBrukerpassByFnr("fnr")).thenReturn(null);

        assertThatThrownBy(() -> brukerService.getBrukerpassByBrukernummer("123"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No brukerpass found for the given brukernummer");
    }

    @Test
    void shouldThrowWhenBrukerpassListIsEmpty() {
        when(brukerAdresseJpaRepository.getFnrByBrukernummer("123")).thenReturn(List.of("fnr"));
        when(brukerPassJpaRepository.getBrukerpassByFnr("fnr")).thenReturn(List.of());

        assertThatThrownBy(() -> brukerService.getBrukerpassByBrukernummer("123"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No brukerpass found for the given brukernummer");
    }
}
