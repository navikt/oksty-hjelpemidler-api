package no.navn.oebs.hjelpemiddel.api.exception;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturnNotFoundResponseForMissingResource() {
        MockHttpServletRequest request = request("GET", "/bruker/123/status", " corr-1 ");

        var response = handler.handleResourceNotFound(new ResourceNotFoundException("Fant ikke"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Not Found");
        assertThat(response.getBody().get("message")).isEqualTo("Fant ikke");
        assertThat(response.getBody().get("status")).isEqualTo(404);
        assertThat(response.getBody().get("correlationId")).isEqualTo("corr-1");
    }

    @Test
    void shouldReturnInternalServerErrorForUnhandledException() {
        MockHttpServletRequest request = request("POST", "/bruker/oppslag", "cid-2");

        var response = handler.handleGenericException(new RuntimeException("boom"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().get("status")).isEqualTo(500);
        assertThat(response.getBody()).doesNotContainKey("message");
    }

    @Test
    void shouldReturnBadRequestForTypeMismatch() {
        MockHttpServletRequest request = request("GET", "/bruker/abc/status", "cid-3");
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "abc",
                Integer.class,
                "brukernummer",
                null,
                new IllegalArgumentException("invalid"));

        var response = handler.handleTypeMismatch(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Invalid argument provided for parameter: brukernummer");
        assertThat(response.getBody().get("status")).isEqualTo(400);
        assertThat(response.getBody().get("correlationId")).isEqualTo("cid-3");
    }

    @Test
    void shouldReturnBadRequestForValidationError() {
        MockHttpServletRequest request = request("POST", "/bruker/oppslag", "cid-4");
        MethodParameter methodParameter = methodParameterForValidationError();
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                methodParameter,
                new BeanPropertyBindingResult(new Object(), "request"));

        var response = handler.handleTypeMismatch(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Invalid argument provided");
        assertThat(response.getBody().get("status")).isEqualTo(400);
        assertThat(response.getBody().get("correlationId")).isEqualTo("cid-4");
        assertThat(response.getBody()).doesNotContainKey("message");
    }

    private MockHttpServletRequest request(String method, String uri, String correlationId) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, uri);
        request.addHeader("x-correlation-id", correlationId);
        return request;
    }

    private MethodParameter methodParameterForValidationError() {
        try {
            Method method = GlobalExceptionHandlerTest.class.getDeclaredMethod("validationTarget", Object.class);
            return new MethodParameter(method, 0);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unused")
    private void validationTarget(Object request) {
        // only used to create MethodParameter for MethodArgumentNotValidException in tests
    }

}
