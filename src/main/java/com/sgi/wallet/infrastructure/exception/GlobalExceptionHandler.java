package com.sgi.wallet.infrastructure.exception;

import com.sgi.wallet.infrastructure.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * GlobalExceptionHandler maneja las excepciones personalizadas en la aplicación.
 * Utiliza @ControllerAdvice para interceptar las excepciones lanzadas y generar respuestas de error adecuadas.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja la excepción personalizada y devuelve una respuesta con el código de estado y el mensaje de error.
     *
     * @param ex Excepción personalizada.
     * @return Mono que encapsula la respuesta de error.
     */


    /**
     * Crea un objeto ErrorResponse a partir de la excepción personalizada.
     *
     * @param ex Excepción personalizada.
     * @return Un objeto ErrorResponse con los detalles del error.
     */

}