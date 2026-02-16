package com.demo.ticket.exception;


//
//

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

// ========= Common error body =========
	@Getter
	@Builder
	static class ErrorResponse {
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
		private OffsetDateTime timestamp;
		private int status;
		private String error;
		private String message;
		private String path;
		private List<String> details;
	}

	private ResponseEntity<Object> build(HttpStatus status, String message, String path, List<String> details) {
		ErrorResponse body = ErrorResponse.builder().timestamp(OffsetDateTime.now()).status(status.value())
				.error(status.getReasonPhrase()).message(message).path(path).details(details).build();
		return ResponseEntity.status(status).body(body);
	}

// ========= 404: Not Found (your custom) =========
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Object> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
		return build(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI(), null);
	}

// ========= 409: Conflict (duplicates / business rule) =========
	@ExceptionHandler(DuplicatePaymentException.class)
	public ResponseEntity<Object> handleDuplicate(DuplicatePaymentException ex, HttpServletRequest req) {
		return build(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI(), null);
	}

// ========= 400: Validation on @RequestBody (Bean Validation) =========
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpServletRequest req) {
		List<String> details = ex.getBindingResult().getFieldErrors().stream()
				.map(err -> err.getField() + ": " + err.getDefaultMessage()).collect(Collectors.toList());
		return build(HttpStatus.BAD_REQUEST, "Validation failed", req.getRequestURI(), details);
	}

// ========= 400: Validation on path/query params =========
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
		List<String> details = ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage)
				.collect(Collectors.toList());
		return build(HttpStatus.BAD_REQUEST, "Constraint violation", req.getRequestURI(), details);
	}

// ========= 400: Malformed JSON / unreadable body =========
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Object> handleMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
		return build(HttpStatus.BAD_REQUEST, "Malformed JSON request", req.getRequestURI(), null);
	}

// ========= 400: Type mismatch in path/query (e.g., string where integer expected) =========
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
		String msg = "Parameter '" + ex.getName() + "' with value '" + ex.getValue()
				+ "' could not be converted to type "
				+ (ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "required type");
		return build(HttpStatus.BAD_REQUEST, msg, req.getRequestURI(), null);
	}

// ========= 405: HTTP method not allowed =========
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<Object> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpServletRequest req) {
		return build(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), req.getRequestURI(), null);
	}

// ========= 404: No handler (wrong endpoint path) =========
	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<Object> handleNoHandlerFound(NoHandlerFoundException ex, HttpServletRequest req) {
		return build(HttpStatus.NOT_FOUND, "Endpoint not found", ex.getRequestURL(), List.of(ex.getMessage()));
	}

// ========= 400: Data integrity / FK / UK from DB layer =========
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Object> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
		return build(HttpStatus.BAD_REQUEST, "Data integrity violation", req.getRequestURI(), null);
	}

// ========= Respect ResponseStatusException from services (e.g., 409 Conflict) =========
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Object> handleResponseStatus(ResponseStatusException ex, HttpServletRequest req) {
		HttpStatus status = (ex.getStatusCode() instanceof HttpStatus s) ? s : HttpStatus.BAD_REQUEST;
		String message = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
		return build(status, message, req.getRequestURI(), null);
	}

// ========= 500: Fallback for any unhandled exception =========
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleAll(Exception ex, HttpServletRequest req) {
		log.error("Unexpected error", ex);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", req.getRequestURI(), null);
	}

}
