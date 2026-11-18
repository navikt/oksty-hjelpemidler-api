package no.navn.oebs.hjelpemiddel.api.mapper;

import no.navn.oebs.hjelpemiddel.api.dto.BrukerInfoRespons;
import no.navn.oebs.hjelpemiddel.api.dto.BrukerpassRespons;
import no.navn.oebs.hjelpemiddel.api.repository.entity.BrukerAdresseEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openapitools.model.Adresse;
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
    void shouldMapFirstResidentialAddressAndAllDistinctDeliveryAddresses() {
        BrukerAdresseEntity first = createAdresseEntity(
                "N", "Storgata 1", "0123", "Oslo", "0301",
                "Leveringsveien 2", "0456", "Oslo", "0301", "Sentrum");
        BrukerAdresseEntity second = createAdresseEntity(
                "Y", "Parkveien 9", "6789", "Bergen", "4601",
                "Bryggeveien 4", "5432", "Bergen", "4601", "Nordnes");

        Adresser result = brukerMapper.toAdresser(List.of(first, second));

        assertThat(result).isNotNull();
        assertThat(result.getBostedsadresse()).isNotNull();
        assertThat(result.getLeveringsadresser()).hasSize(2);
        assertThat(result.getBostedsadresse().getGateadresse()).isEqualTo("Storgata 1");
        assertThat(result.getBostedsadresse().getPostnummer()).isEqualTo("0123");
        assertThat(result.getBostedsadresse().getBy()).isEqualTo("Oslo");
        assertThat(result.getBostedsadresse().getKommune()).isEqualTo("0301");
        assertThat(result.getLeveringsadresser().get(0).getGateadresse()).isEqualTo("Leveringsveien 2");
        assertThat(result.getLeveringsadresser().get(0).getPostnummer()).isEqualTo("0456");
        assertThat(result.getLeveringsadresser().get(0).getBy()).isEqualTo("Oslo");
        assertThat(result.getLeveringsadresser().get(0).getKommune()).isEqualTo("0301");
        assertThat(result.getLeveringsadresser().get(0).getBydel()).isEqualTo("Sentrum");
        assertThat(result.getLeveringsadresser().get(1).getGateadresse()).isEqualTo("Bryggeveien 4");
        assertThat(result.getLeveringsadresser().get(1).getPostnummer()).isEqualTo("5432");
        assertThat(result.getLeveringsadresser().get(1).getBy()).isEqualTo("Bergen");
        assertThat(result.getLeveringsadresser().get(1).getKommune()).isEqualTo("4601");
        assertThat(result.getLeveringsadresser().get(1).getBydel()).isEqualTo("Nordnes");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "Sentrum")
    void shouldRemoveDuplicateDeliveryAddresses(String district) {
        BrukerAdresseEntity first = createAdresseEntity(
                "Y", "Storgata 1", "0123", "Oslo", "0301",
                "Leveringsveien 2", "0456", "Oslo", "0301", district);
        BrukerAdresseEntity duplicate = createAdresseEntity(
                "N", "Parkveien 9", "6789", "Bergen", "4601",
                "Leveringsveien 2", "0456", "Oslo", "0301", district);

        Adresser result = brukerMapper.toAdresser(List.of(first, duplicate));

        assertThat(result.getLeveringsadresser()).containsExactly(
                new Adresse().gateadresse("Leveringsveien 2").postnummer("0456")
                        .by("Oslo").kommune("0301").bydel(district));
    }

    @ParameterizedTest
    @CsvSource({
            "leveringsAdresse, Leveringsveien 3",
            "leveringsPostNr, 0457",
            "leveringsBy, Bergen",
            "leveringsKommune, 4601",
            "leveringsBydel,"
    })
    void shouldKeepDeliveryAddressesThatDifferInOneField(String field, String value) {
        BrukerAdresseEntity first = createAdresseEntity(
                "Y", "Storgata 1", "0123", "Oslo", "0301",
                "Leveringsveien 2", "0456", "Oslo", "0301", "Sentrum");
        BrukerAdresseEntity second = createAdresseEntity(
                "N", "Storgata 1", "0123", "Oslo", "0301",
                "Leveringsveien 2", "0456", "Oslo", "0301", "Sentrum");
        ReflectionTestUtils.setField(second, field, value);

        Adresser result = brukerMapper.toAdresser(List.of(first, second));

        assertThat(result.getLeveringsadresser()).hasSize(2);
        assertThat(result.getLeveringsadresser().getFirst()).isNotEqualTo(
                result.getLeveringsadresser().getLast());
    }

    @Test
    void shouldMapValidPrimaryBostedsadresse() {
        BrukerAdresseEntity entity = createAdresseEntity(
                "Y", "Storgata 1", "0123", "Oslo", "0301",
                "Leveringsveien 2", "0456", "Oslo", "0301", "Sentrum");

        Adresse result = brukerMapper.toValidBostedsadresse(entity);

        assertThat(result.getGateadresse()).isEqualTo("Storgata 1");
        assertThat(result.getPostnummer()).isEqualTo("0123");
        assertThat(result.getBy()).isEqualTo("Oslo");
        assertThat(result.getKommune()).isEqualTo("0301");
    }

    @ParameterizedTest
    @CsvSource({
            "primaerAdr, N",
            "statusBrukernr, I",
            "statusFnr, I",
            "statusAdrPSted, I",
            "statusAdrCSted, I",
            "statusAdrBruk, I"
    })
    void shouldRejectBostedsadresseWhenNotPrimaryOrStatusInactive(String field, String value) {
        BrukerAdresseEntity entity = createAdresseEntity(
                "Y", "Storgata 1", "0123", "Oslo", "0301",
                "Leveringsveien 2", "0456", "Oslo", "0301", "Sentrum");
        ReflectionTestUtils.setField(entity, field, value);

        assertThat(brukerMapper.toValidBostedsadresse(entity)).isNull();
    }

    @Test
    void shouldReturnNullWhenBostedsadresseEntityIsNull() {
        assertThat(brukerMapper.toValidBostedsadresse(null)).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "statusBrukernr", "statusFnr", "statusAdrPSted", "statusAdrCSted", "statusAdrBruk"
    })
    void shouldRejectBostedsadresseWhenStatusIsNull(String field) {
        BrukerAdresseEntity entity = createAdresseEntity(
                "Y", "Storgata 1", "0123", "Oslo", "0301",
                "Leveringsveien 2", "0456", "Oslo", "0301", "Sentrum");
        ReflectionTestUtils.setField(entity, field, null);

        assertThat(brukerMapper.toValidBostedsadresse(entity)).isNull();
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

    private BrukerAdresseEntity createAdresseEntity(
            String primary,
            String address,
            String postNr,
            String city,
            String municipality,
            String deliveryAddress,
            String deliveryPostNr,
            String deliveryCity,
            String deliveryMunicipality,
            String district) {
        BrukerAdresseEntity entity = new BrukerAdresseEntity();
        ReflectionTestUtils.setField(entity, "adresse", address);
        ReflectionTestUtils.setField(entity, "postNr", postNr);
        ReflectionTestUtils.setField(entity, "by", city);
        ReflectionTestUtils.setField(entity, "kommune", municipality);
        ReflectionTestUtils.setField(entity, "leveringsAdresse", deliveryAddress);
        ReflectionTestUtils.setField(entity, "leveringsPostNr", deliveryPostNr);
        ReflectionTestUtils.setField(entity, "leveringsBy", deliveryCity);
        ReflectionTestUtils.setField(entity, "leveringsKommune", deliveryMunicipality);
        ReflectionTestUtils.setField(entity, "leveringsBydel", district);
        ReflectionTestUtils.setField(entity, "primaerAdr", primary);
        ReflectionTestUtils.setField(entity, "statusBrukernr", "A");
        ReflectionTestUtils.setField(entity, "statusFnr", "A");
        ReflectionTestUtils.setField(entity, "statusAdrPSted", "A");
        ReflectionTestUtils.setField(entity, "statusAdrCSted", "A");
        ReflectionTestUtils.setField(entity, "statusAdrBruk", "A");
        return entity;
    }
}
