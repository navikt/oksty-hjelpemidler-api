package no.navn.oebs.hjelpemiddel.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record BrukerpassRespons(

        //todo: Skal man ta med parametere som ikke er i bruk?
        @JsonProperty("fnr") String fnr,
        @JsonProperty("brukerNummer") String brukerNr,
        @JsonProperty("kontrakt_nummer") String kontraktNr,
        @JsonProperty("sjekk_navn") String checkName,
        @JsonProperty("start_date") LocalDateTime startDate,
        @JsonProperty("end_date") LocalDateTime endDate,
        @JsonProperty("status") String status
) { }
