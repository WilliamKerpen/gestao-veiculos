package br.com.sistema_automotivo.gestao_veiculos.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice // @RestControllerAdvice -> mecanismo do Spring que permite centralizar o tratamento de exceções dos Controllers.
public class GlobalExceptionHandler {

    // Recurso não encontrado

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> tratarNaoEncontrado(
            RecursoNaoEncontradoException exception) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("erro", exception.getMessage()));
    }

    // Regra de negócio violada

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Map<String, String>> tratarRegraNegocio(
            RegraNegocioException exception) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("erro", exception.getMessage()));
    }

    // Erros de validação

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> tratarValidacao(
            MethodArgumentNotValidException exception) {

        String mensagem = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Dados inválidos");

        return ResponseEntity
                .badRequest()
                .body(Map.of("erro", mensagem));
    }

    // Restrições do banco de dados

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> tratarIntegridade(
            DataIntegrityViolationException exception) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "erro",
                        "Violação de uma restrição do banco de dados"
                ));
    }
}