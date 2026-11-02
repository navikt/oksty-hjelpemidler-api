package no.navn.oebs.hjelpemiddel.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BrukerInfoRespons(
        @JsonProperty("brukerNummer") String brukerNr,
        @JsonProperty("brukerStatus") String brukerStatus,
        @JsonProperty("fnr") String fnr,
        @JsonProperty("fnrStatus") String fnrStatus,
        @JsonProperty("morsDato") String deathDate
) { }
