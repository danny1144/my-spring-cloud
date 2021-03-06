package com.siemens.ldap.config;

import com.icitic.ldap.exception.LDAPException;
import com.siemens.ldap.util.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 异常处理
 * @author z00403vj
 */
@RestControllerAdvice
@Slf4j
public class ExceptionResove {

    @ExceptionHandler(LDAPException.class)
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseMessage<String> ldapExceptionHandle(HttpServletRequest request, Exception e) {

        String url = request.getRequestURL().toString();
        String msg = "LDAPException handler: url " + url + "\nerror message:" + e.getMessage() + "\ncause by:"
                + e.getCause();        log.error(msg.replaceAll("[\r\n]", ""));
        return ResponseMessage.error(e.getMessage());
    }
    @ExceptionHandler(Exception.class)
    public ResponseMessage<String> exceptionHandle(HttpServletRequest request, Exception e) {

        String url = request.getRequestURL().toString();
        String msg = "Global exception handler: url " + url + "\nerror message:" + e.getMessage() + "\ncause by:"
                + e.getCause();
        log.error(msg.replaceAll("[\r\n]", ""));
        return ResponseMessage.error(e.getMessage());
    }
}
