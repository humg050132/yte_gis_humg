package org.example.yte_gis_humg.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public record LoiResponse(int status, String message, Instant thoiGian) {}

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<LoiResponse> xuLyResponseStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return ResponseEntity.status(status)
                .body(new LoiResponse(status.value(), ex.getReason(), Instant.now()));
    }

    @ExceptionHandler({MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<LoiResponse> xuLyThamSoSai(Exception ex) {
        return ResponseEntity.badRequest()
                .body(new LoiResponse(400, "Tham số không hợp lệ: " + ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<LoiResponse> xuLyDatabase(DataAccessException ex) {
        log.error("Lỗi truy vấn database", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new LoiResponse(500, "Lỗi hệ thống, vui lòng thử lại sau", Instant.now()));
    }
}