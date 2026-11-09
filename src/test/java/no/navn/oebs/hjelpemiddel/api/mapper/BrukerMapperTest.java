package no.navn.oebs.hjelpemiddel.api.mapper;

import no.navn.oebs.hjelpemiddel.api.dto.BrukerInfoRespons;
import no.navn.oebs.hjelpemiddel.api.dto.BrukerpassRespons;
import no.navn.oebs.hjelpemiddel.api.repository.entity.BrukerAdresseEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openapitools.model.Adresser;
import org.openapitools.model.Bruker;
import org.openapitools.model.Brukerpass;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BrukerMapperTest {

    private final BrukerMapper brukerMapper = new BrukerMapper();

    @Test
    void shouldReturnNullWhenAdresserListIsNull() {
        assertThat(brukerMapper.toAdresser(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenAdresserListIsEmpty() {
        assertThat(brukerMapper.toAdresser(List.of())).isNull();
    }

    @Test
    void shouldMapPrimaryAndSecondaryAddresses() {
        BrukerAdresseEntity primary = createAdresseEntity("Y");
        BrukerAdresseEntity secondary = createAdresseEntity("N");

        Adresser result = brukerMapper.toAdresser(List.of(primary, secondary));

        assertThat(result).isNotNull();
        assertThat(result.getPrimaeradresse()).isNotNull();
        assertThat(result.getAdresser()).hasSize(1);
        assertThat(result.getPrimaeradresse().getBostedsadresse().getGateadresse()).isEqualTo("Storgata 1");
        assertThat(result.getAdresser().getFirst().getBostedsadresse().getGateadresse()).isEqualTo("Storgata 1");
    }

    @ParameterizedTest
    @CsvSource({
            "Aktiv, A, true",
            "Aktiv, I, true",
            "Inaktiv, A, false",
            "Inaktiv, I, false"
    })
    void shouldMapBrukerFromBrukerinfo(String brukerstatus, String fnrstatus, boolean expectedActive) {
        BrukerInfoRespons respons = new BrukerInfoRespons("123", brukerstatus, "fnr", fnrstatus, null);

        Bruker bruker = brukerMapper.toBruker(respons);

        assertThat(bruker.getBrukernummer()).isEqualTo("123");
        assertThat(bruker.getAktiv()).isEqualTo(expectedActive);
    }

    @Test
    void shouldMapBrukerpassFieldsAndDates() {
        BrukerpassRespons respons = new BrukerpassRespons(
                "fnr", "123", "K-123", "Kontroll",
                LocalDateTime.of(2026, 1, 15, 11, 0),
                LocalDateTime.of(2026, 12, 31, 23, 59, 59),
                "Aktiv");

        Brukerpass brukerpass = brukerMapper.toBrukerpass(respons);

        assertThat(brukerpass.getKontraktnr()).isEqualTo("K-123");
        assertThat(brukerpass.getStartdato()).isEqualTo(LocalDate.of(2026, 1, 15));
        assertThat(brukerpass.getSluttdato()).isEqualTo(LocalDate.of(2026, 12, 31));
    }

    @Test
    void shouldMapBrukerpassWhenStartDateIsNull() {
        BrukerpassRespons respons = new BrukerpassRespons(
                "fnr", "123", "K-123", "Kontroll",
                null, LocalDateTime.of(2026, 12, 31, 23, 59, 59), "Aktiv");

        Brukerpass brukerpass = brukerMapper.toBrukerpass(respons);

        assertThat(brukerpass.getStartdato()).isNull();
        assertThat(brukerpass.getSluttdato()).isEqualTo(LocalDate.of(2026, 12, 31));
    }

    @Test
    void shouldMapBrukerpassWhenEndDateIsNull() {
        BrukerpassRespons respons = new BrukerpassRespons(
                "fnr", "123", "K-123", "Kontroll",
                LocalDateTime.of(2026, 1, 15, 11, 0), null, "Aktiv");

        Brukerpass brukerpass = brukerMapper.toBrukerpass(respons);

        assertThat(brukerpass.getStartdato()).isEqualTo(LocalDate.of(2026, 1, 15));
        assertThat(brukerpass.getSluttdato()).isNull();
    }

    private BrukerAdresseEntity createAdresseEntity(String primary) {
        BrukerAdresseEntity entity = new BrukerAdresseEntity();
        ReflectionTestUtils.setField(entity, "adresse", "Storgata 1");
        ReflectionTestUtils.setField(entity, "postNr", "0123");
        ReflectionTestUtils.setField(entity, "by", "Oslo");
        ReflectionTestUtils.setField(entity, "kommune", "0301");
        ReflectionTestUtils.setField(entity, "leveringsAdresse", "Leveringsveien 2");
        ReflectionTestUtils.setField(entity, "leveringsPostNr", "0456");
        ReflectionTestUtils.setField(entity, "leveringsBy", "Oslo");
        ReflectionTestUtils.setField(entity, "leveringsKommune", "0301");
        ReflectionTestUtils.setField(entity, "leveringsBydel", "Sentrum");
        ReflectionTestUtils.setField(entity, "primaerAdr", primary);
        return entity;
    }
}
