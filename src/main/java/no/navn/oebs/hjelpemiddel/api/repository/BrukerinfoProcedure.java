package no.navn.oebs.hjelpemiddel.api.repository;

import lombok.extern.slf4j.Slf4j;
import no.navn.oebs.hjelpemiddel.api.dto.BrukerInfoRespons;
import no.navn.oebs.hjelpemiddel.api.exception.InvalidOebsResponseException;
import no.navn.oebs.hjelpemiddel.api.mapper.BrukerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Map;

@Slf4j
@Repository
public class BrukerinfoProcedure {

    //xxrtv_hz_digihot_brukerinfo_pkg.get_bruker_info( p_fodsels_nummer    in  varchar2
    //                                                   , Brukerinfojson      out clob

    private static final String SCHEMA = "apps";
    private static final String PACKAGE = "xxrtv_hz_digihot_brukerinfo_pkg";
    private static final String PROCEDURE = "get_bruker_info";

    private static final String IN_PARAM_FNR = "p_fodsels_nummer";

    private static final String OUT_PARAM_MESSAGE = "Brukerinfojson";

    private final SimpleJdbcCall brukerInfoCall;
    private final ObjectMapper objectMapper;

    @Autowired
    public BrukerinfoProcedure(DataSource dataSource, ObjectMapper objectMapper, BrukerMapper brukerMapper) {
        this.brukerInfoCall = new SimpleJdbcCall(dataSource)
                .withSchemaName(SCHEMA)
                .withCatalogName(PACKAGE)
                .withProcedureName(PROCEDURE)
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter(IN_PARAM_FNR, Types.VARCHAR),
                        new SqlOutParameter(OUT_PARAM_MESSAGE, Types.VARCHAR)
                );
        this.objectMapper = objectMapper;
    }

    public BrukerInfoRespons executeBrukerinfoProcedure(String fnr) {
        Map<String, Object> result = getBrukerinfo(fnr);
        String outMessage = (String) result.get(OUT_PARAM_MESSAGE);

        try {
            return parseBrukerinfoResponse(outMessage);

            //todo: Evaluation of the response. Is there some values and formatting that is not allowed?

        } catch (InvalidOebsResponseException e) {
            log.error("Validation failed for brukerinfo with korrelation id {} brukernummer {}",
                    fnr, e.getMessage()); //todo: Denne loggen må endres slik at det ikke logges fnr
            throw e;
        }

    }

    public Map<String, Object> getBrukerinfo(String fnr) {
        MapSqlParameterSource inputParams = new MapSqlParameterSource()
                .addValue(IN_PARAM_FNR, fnr);
        return brukerInfoCall.execute(inputParams);
    }

    private BrukerInfoRespons parseBrukerinfoResponse(String outMessage) {
        //todo: Tom reponse trenger ikke være feil, det kan være
        if (outMessage == null || outMessage.isEmpty()) {
            return null;
            //throw new InvalidOebsResponseException("OeBS did return an empty response to validation procedure processing kontostreng.");
        }
        try {
            return objectMapper.readValue(outMessage, new TypeReference<BrukerInfoRespons>() {});

        } catch (JacksonException e) {
            throw new InvalidOebsResponseException(
                    "Parsing of OeBS response to validationResponse failed. Response: " + outMessage, e);
        }
    }
}
