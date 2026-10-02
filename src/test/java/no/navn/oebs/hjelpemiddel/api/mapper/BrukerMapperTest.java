package no.navn.oebs.hjelpemiddel.api.mapper;

import no.navn.oebs.hjelpemiddel.api.repository.entity.BrukerAdresseEntity;
import no.navn.oebs.hjelpemiddel.api.repository.entity.BrukerPassEntity;
import org.junit.jupiter.api.Test;
import org.openapitools.model.Adresser;
import org.openapitools.model.Brukerpass;
import org.openapitools.model.Status;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
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
        BrukerAdresseEntity primary = createAdresseEntity("Y", "A", "A");
        BrukerAdresseEntity secondary = createAdresseEntity("N", "A", "A");

        Adresser result = brukerMapper.toAdresser(List.of(primary, secondary));

        assertThat(result).isNotNull();
        assertThat(result.getPrimaeradresse()).isNotNull();
        assertThat(result.getAdresser()).hasSize(1);
        assertThat(result.getPrimaeradresse().getBostedsadresse().getGateadresse()).isEqualTo("Storgata 1");
        assertThat(result.getAdresser().getFirst().getBostedsadresse().getGateadresse()).isEqualTo("Storgata 1");
    }

    @Test
    void shouldMapActiveStatusWhenBothFieldsAreA() {
        BrukerAdresseEntity entity = createAdresseEntity("N", "A", "A");

        Status status = brukerMapper.toStatus(entity);

        assertThat(status.getAktiv()).isTrue();
    }

    @Test
    void shouldMapInactiveStatusWhenAnyFieldIsNotA() {
        BrukerAdresseEntity entity = createAdresseEntity("N", "I", "A");

        Status status = brukerMapper.toStatus(entity);

        assertThat(status.getAktiv()).isFalse();
    }

    @Test
    void shouldMapBrukerpassFieldsAndDates() {
        BrukerPassEntity passEntity = new BrukerPassEntity();
        ReflectionTestUtils.setField(passEntity, "kontraktNr", "K-123");
        ReflectionTestUtils.setField(passEntity, "startDate", "2026-01-15 11:00:00");
        ReflectionTestUtils.setField(passEntity, "endDate", "2026-12-31 23:59:59");

        Brukerpass brukerpass = brukerMapper.toBrukerpass(passEntity);

        assertThat(brukerpass.getKontraktnr()).isEqualTo("K-123");
        assertThat(brukerpass.getStartdato()).isEqualTo(LocalDate.of(2026, 1, 15));
        assertThat(brukerpass.getSluttdato()).isEqualTo(LocalDate.of(2026, 12, 31));
    }

    @Test
    void shouldReturnNullDateWhenDateStringIsNullOrEmpty() {
        assertThat(brukerMapper.toLocalDate(null)).isNull();
        assertThat(brukerMapper.toLocalDate("")).isNull();
    }

    @Test
    void shouldParseLocalDateFromTimestamp() {
        LocalDate date = brukerMapper.toLocalDate("2025-09-10 08:30:00");

        assertThat(date).isEqualTo(LocalDate.of(2025, 9, 10));
    }

    private BrukerAdresseEntity createAdresseEntity(String primary, String statusBrukernr, String statusFnr) {
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
        ReflectionTestUtils.setField(entity, "statusBrukernr", statusBrukernr);
        ReflectionTestUtils.setField(entity, "statusFnr", statusFnr);
        return entity;
    }
}
