package no.navn.oebs.hjelpemiddel.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import no.nav.security.token.support.core.exceptions.JwtTokenInvalidClaimException;
import no.nav.security.token.support.spring.validation.interceptor.JwtTokenUnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String ERROR = "error";
    private static final String MESSAGE = "message";
    private static final String STATUS = "status";
    private static final String TIMESTAMP = "timestamp";
    private static final String CORRELATION_ID_FIELD = "correlationId";
    private static final String CORRELATION_ID_HEADER = "x-correlation-id";
    private static final Pattern ISSUER_PATTERN = Pattern.compile("issuer \\[([^,\\]]+)");

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleJwtTokenUnauthorizedException(
            JwtTokenUnauthorizedException ex,
            HttpServletRequest request) {

        String correlationId = sanitizeForLog(getCorrelationId(request));
        String issuer = extractIssuer(ex.getMessage());
        String message = ex.getCause() == null ? null : sanitizeForLog(ex.getCause().getMessage());

        Map<String, Object> response = buildErrorResponse("Unauthorized", message, 401, request);
        String requestUri = getUriWithoutPathParameters(request);

        if (ex.getCause() instanceof JwtTokenInvalidClaimException) {
            LOGGER.warn("Auth rejected: status=403 correlationId={} path={} issuer={} reason={}",
                    correlationId,
                    requestUri,
                    issuer,
                    message);
            response.put(STATUS, 403);
            return new ResponseEntity<>(response, org.springframework.http.HttpStatus.FORBIDDEN);
        }

        LOGGER.warn("Auth rejected: status=401 correlationId={} path={} issuer={} reason={}",
                correlationId,
                requestUri,
                issuer,
                message);
        return new ResponseEntity<>(response, org.springframework.http.HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        String correlationId = sanitizeForLog(getCorrelationId(request));
        String requestUri = getUriWithoutPathParameters(request);
        String sanitizedMessage = (ex == null || ex.getMessage() == null) ? null : sanitizeForLog(ex.getMessage());

        LOGGER.error(
                "500 response due to An unexpected error: correlationId={} path={} method={} exceptionMessage={}",
                correlationId,
                requestUri,
                request.getMethod(),
                sanitizedMessage,
                ex);
        Map<String, Object> response = buildErrorResponse("An unexpected error occurred", null, 500, request);
        return new ResponseEntity<>(response, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        String correlationId = sanitizeForLog(getCorrelationId(request));
        String requestUri = getUriWithoutPathParameters(request);
        String propertyName = (ex == null) ? null : sanitizeForLog(ex.getPropertyName());
        String sanitizedMessage = (ex == null ) ? null : sanitizeForLog(ex.getMessage());


        LOGGER.warn(
                "400 response due to type mismatch: correlationId={} path={} parameter={} reason={}",
                correlationId,
                requestUri,
                propertyName,
                sanitizedMessage);
        Map<String, Object> response = buildErrorResponse("Invalid argument provided for parameter: " + propertyName, sanitizedMessage, 400, request);
        return new ResponseEntity<>(response, org.springframework.http.HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String correlationId = sanitizeForLog(getCorrelationId(request));
        String requestUri = getUriWithoutPathParameters(request);
        String sanitizedMessage = (ex == null ) ? null : sanitizeForLog(ex.getMessage());

        LOGGER.warn(
                "400 response due to invalid argument: correlationId={} path={} reason={}",
                correlationId,
                requestUri,
                sanitizedMessage);
        Map<String, Object> response = buildErrorResponse("Invalid argument provided" ,null, 400, request);
        return new ResponseEntity<>(response, org.springframework.http.HttpStatus.BAD_REQUEST);
    }

    private Map<String, Object> buildErrorResponse(String error, String message, int status, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, error);
        if (message != null) {
            response.put(MESSAGE, message);
        }
        response.put(STATUS, status);
        response.put(TIMESTAMP, LocalDateTime.now());
        addContextFields(response, request);
        return response;
    }

    private void addContextFields(Map<String, Object> response, HttpServletRequest request) {
        response.put(CORRELATION_ID_FIELD, getCorrelationId(request));
    }

    private String getCorrelationId(HttpServletRequest request) {
        return normalize(request.getHeader(CORRELATION_ID_HEADER));
    }

    private String extractIssuer(String message) {
        if (message == null) {
            return "unknown";
        }
        Matcher matcher = ISSUER_PATTERN.matcher(message);
        return matcher.find() ? matcher.group(1) : "unknown";
    }

    private String sanitizeForLog(String value) {
        if (value == null) {
            return null;
        }
        StringBuilder sanitized = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            sanitized.append(Character.isISOControl(ch) ? ' ' : ch);
        }
        return sanitized.toString();
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String sanitized = sanitizeForLog(value);
        String trimmed = sanitized.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String getUriWithoutPathParameters(HttpServletRequest request) {
        if (request == null || request.getRequestURI() == null) {
            return null;
        }
        String requestUri = request.getRequestURI();
        return requestUri.replaceAll("/\\d+", "/{brukernummer}"); // Replace numeric path parameters with {id}
    }

}
