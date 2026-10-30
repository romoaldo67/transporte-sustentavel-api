package com.transporte.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> naoEncontrado(RecursoNaoEncontradoException e) {
        return resposta(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({RegraNegocioException.class, IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<Map<String, Object>> requisicaoInvalida(Exception e) {
        String mensagem = e instanceof HttpMessageNotReadableException
                ? "Corpo da requisição inválido ou mal formatado."
                : e.getMessage();
        return resposta(HttpStatus.BAD_REQUEST, mensagem);
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<Map<String, Object>> conflito(ConflitoException e) {
        return resposta(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> violacaoIntegridade(SQLIntegrityConstraintViolationException e) {
        String msg = e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate")
                ? "Já existe um registro com esses dados (e-mail ou CPF duplicado)."
                : "Operação não permitida: o registro possui vínculos com outros dados.";
        return resposta(HttpStatus.CONFLICT, msg);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Map<String, Object>> erroBanco(SQLException e) {
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao acessar o banco de dados.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> erroGenerico(Exception e) {
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor.");
    }

    private ResponseEntity<Map<String, Object>> resposta(HttpStatus status, String mensagem) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", Instant.now().toString());
        corpo.put("status", status.value());
        corpo.put("erro", status.getReasonPhrase());
        corpo.put("mensagem", mensagem);
        return ResponseEntity.status(status).body(corpo);
    }
}
