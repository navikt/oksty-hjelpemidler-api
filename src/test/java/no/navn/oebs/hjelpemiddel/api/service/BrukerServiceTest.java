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
import org.openapitools.model.Adresse;
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

        when(brukerAdresseJpaRepository.getBrukerAdresserByFnr("fnr")).thenReturn(entities);
        when(brukerMapper.toAdresser(entities)).thenReturn(expectedAdresser);

        Adresser actual = brukerService.getAdresserByFnr("fnr");

        assertThat(actual).isEqualTo(expectedAdresser);
        verify(brukerAdresseJpaRepository).getBrukerAdresserByFnr("fnr");
        verify(brukerMapper).toAdresser(entities);
    }

    @Test
    void shouldThrowWhenAdresserNotFound() {
        when(brukerAdresseJpaRepository.getBrukerAdresserByFnr("fnr")).thenReturn(List.of());

        assertThatThrownBy(() -> brukerService.getAdresserByFnr("fnr"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No addresses found for the given brukernummer");
    }

    @Test
    void shouldReturnUniqueValidBostedsadresseAndFilterInvalidAddresses() {
        BrukerAdresseEntity validEntity = new BrukerAdresseEntity();
        BrukerAdresseEntity invalidEntity = new BrukerAdresseEntity();
        List<BrukerAdresseEntity> entities = List.of(validEntity, invalidEntity);
        Adresse expectedAdresse = new Adresse().gateadresse("Storgata 1");

        when(brukerAdresseJpaRepository.getBostedsadresseByFnr("fnr")).thenReturn(entities);
        when(brukerMapper.toValidBostedsadresse(validEntity)).thenReturn(expectedAdresse);
        when(brukerMapper.toValidBostedsadresse(invalidEntity)).thenReturn(null);

        Adresse actual = brukerService.getBostedsadresseByFnr("fnr");

        assertThat(actual).isSameAs(expectedAdresse);
        verify(brukerAdresseJpaRepository).getBostedsadresseByFnr("fnr");
        verify(brukerMapper).toValidBostedsadresse(validEntity);
        verify(brukerMapper).toValidBostedsadresse(invalidEntity);
    }

    @Test
    void shouldThrowWhenBostedsadresseNotFound() {
        when(brukerAdresseJpaRepository.getBostedsadresseByFnr("fnr")).thenReturn(List.of());

        assertThatThrownBy(() -> brukerService.getBostedsadresseByFnr("fnr"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No residential address found for the given brukernummer");
    }

    @Test
    void shouldThrowWhenNoValidBostedsadresseIsFound() {
        BrukerAdresseEntity entity = new BrukerAdresseEntity();
        when(brukerAdresseJpaRepository.getBostedsadresseByFnr("fnr")).thenReturn(List.of(entity));
        when(brukerMapper.toValidBostedsadresse(entity)).thenReturn(null);

        assertThatThrownBy(() -> brukerService.getBostedsadresseByFnr("fnr"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No valid residential address found for the given brukernummer");
    }

    @Test
    void shouldThrowWhenMultipleValidBostedsadresserAreFound() {
        BrukerAdresseEntity firstEntity = new BrukerAdresseEntity();
        BrukerAdresseEntity secondEntity = new BrukerAdresseEntity();
        when(brukerAdresseJpaRepository.getBostedsadresseByFnr("fnr"))
                .thenReturn(List.of(firstEntity, secondEntity));
        when(brukerMapper.toValidBostedsadresse(firstEntity)).thenReturn(new Adresse());
        when(brukerMapper.toValidBostedsadresse(secondEntity)).thenReturn(new Adresse());

        assertThatThrownBy(() -> brukerService.getBostedsadresseByFnr("fnr"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Multiple valid residential addresses found for the given brukernummer");
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
