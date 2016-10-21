package gr.abiss.calipso.tiers.controller;


import gr.abiss.calipso.tiers.dto.ErrorInfo;
import gr.abiss.calipso.web.spring.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorInfo handleBadRequestException(HttpServletRequest request, Exception e) {
        BadRequestException ex = (BadRequestException) e;
        ErrorInfo.Builder builder = new ErrorInfo.Builder()
                .message(ex.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .errors(ex.getErrors())
                .throwable(ex);
        return builder.build();
    }
}