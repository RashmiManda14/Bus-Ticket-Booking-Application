package com.demo.ticket.exception;



import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Builder
public class ErrorResponse {

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private final OffsetDateTime timestamp;

	private final int status;
	private final String error;
	private final String message;
	private final String path;
	private final List<String> details;
}
