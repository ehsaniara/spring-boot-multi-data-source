package com.ehsaniara.multidatasource.handler;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Jay Ehsaniara, Dec 30 2019
 */

@Slf4j
@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public ExceptionResponse handleResourceNotFound(final ResourceNotFoundException exception,
                                                    final HttpServletRequest request) {
        log.error(exception.getMessage());

        ExceptionResponse error = new ExceptionResponse();
        error.setMessage(exception.getMessage());
        error.setUrl(request.getRequestURI());
        error.setStatus(HttpStatus.NOT_FOUND.value());

        return error;
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionResponse handleIllegalStateException(final IllegalStateException exception,
                                                         final HttpServletRequest request) {
        log.error(exception.getMessage());

        ExceptionResponse error = new ExceptionResponse();
        error.setMessage(exception.getMessage());
        error.setUrl(request.getRequestURI());
        error.setStatus(HttpStatus.BAD_REQUEST.value());

        return error;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionResponse handleIllegalArgumentException(final IllegalArgumentException exception,
                                                            final HttpServletRequest request) {
        log.error(exception.getMessage());

        ExceptionResponse error = new ExceptionResponse();
        error.setMessage(exception.getMessage());
        error.setUrl(request.getRequestURI());
        error.setStatus(HttpStatus.BAD_REQUEST.value());

        return error;
    }

    @ExceptionHandler(SQLGrammarException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ExceptionResponse handleSQLGrammarException(final SQLGrammarException exception,
                                                       final HttpServletRequest request) {
        log.error(exception.getMessage());

        ExceptionResponse error = new ExceptionResponse();
        error.setMessage("SQL Exception");
        error.setUrl(request.getRequestURI());
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

        return error;
    }

}
