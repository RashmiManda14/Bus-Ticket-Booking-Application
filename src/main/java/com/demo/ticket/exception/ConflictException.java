package com.demo.ticket.exception;



import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
 
@ResponseStatus(HttpStatus.NOT_FOUND)
 
public class ConflictException extends RuntimeException {
    public ConflictException(String message) { super(message); }
}
 
