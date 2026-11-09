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
import org.openapitools.model.Bruker;
import org.openapitools.model.Brukerpass;

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
    void shouldReturnAdresserWhenFnrExists() {
        BrukerAdresseEntity entity = new BrukerAdresseEntity();
        Adresser expectedAdresser = new Adresser();
        List<BrukerAdresseEntity> entities = List.of(entity);

        when(brukerAdresseJpaRepository.getBrukerAdresseByFnr("fnr")).thenReturn(entities);
        when(brukerMapper.toAdresser(entities)).thenReturn(expectedAdresser);

        Adresser actual = brukerService.getAdresserByFnr("fnr");

        assertThat(actual).isEqualTo(expectedAdresser);
        verify(brukerAdresseJpaRepository).getBrukerAdresseByFnr("fnr");
        verify(brukerMapper).toAdresser(entities);
    }

    @Test
    void shouldThrowWhenAdresserNotFound() {
        when(brukerAdresseJpaRepository.getBrukerAdresseByFnr("fnr")).thenReturn(List.of());

        assertThatThrownBy(() -> brukerService.getAdresserByFnr("fnr"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No addresses found for the given brukernummer");
    }

    @Test
    void shouldReturnBrukerWhenFnrExists() {
        BrukerInfoRespons respons = new BrukerInfoRespons("123", "Aktiv", "fnr", "A", null);
        Bruker expectedBruker = new Bruker().brukernummer("123").aktiv(true);

        when(brukerinfoProcedure.executeBrukerinfoProcedure("fnr")).thenReturn(respons);
        when(brukerMapper.toBruker(respons)).thenReturn(expectedBruker);

        Bruker actual = brukerService.getBrukerByFnr("fnr");

        assertThat(actual).isEqualTo(expectedBruker);
        verify(brukerinfoProcedure).executeBrukerinfoProcedure("fnr");
        verify(brukerMapper).toBruker(respons);
    }

    @Test
    void shouldThrowWhenBrukerNotFoundForFnr() {
        when(brukerinfoProcedure.executeBrukerinfoProcedure("fnr")).thenReturn(null);

        assertThatThrownBy(() -> brukerService.getBrukerByFnr("fnr"))
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

        when(brukerpassProcedure.executeBrukerpassProcedure("fnr")).thenReturn(respons);
        when(brukerMapper.toBrukerpass(respons)).thenReturn(mapped);

        List<Brukerpass> actual = brukerService.getBrukerpassByFnr("fnr");

        assertThat(actual).containsExactly(mapped);
        verify(brukerpassProcedure).executeBrukerpassProcedure("fnr");
        verify(brukerMapper).toBrukerpass(respons);
    }

    @Test
    void shouldThrowWhenBrukerpassResponseIsNull() {
        when(brukerpassProcedure.executeBrukerpassProcedure("fnr")).thenReturn(null);

        assertThatThrownBy(() -> brukerService.getBrukerpassByFnr("fnr"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No brukerpass found for the given brukernummer");
    }
}
