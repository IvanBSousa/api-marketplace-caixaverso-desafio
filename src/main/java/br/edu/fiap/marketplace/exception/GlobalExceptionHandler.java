package br.edu.fiap.marketplace.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Tratamento global de exceções com mapeamento para erros de domínio (404, 409, 401).
 * Nunca expõe stack trace ou detalhes internos ao cliente.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ApiErrorResponse> tratarRecursoNaoEncontrado(
            RecursoNaoEncontradoException erro,
            HttpServletRequest request) {
        logger.warn("RecursoNaoEncontradoException: {}", erro.getMessage());
        return resposta(
                HttpStatus.NOT_FOUND,
                erro.getMessage(),
                request,
                Map.of());
    }

    @ExceptionHandler(ConflitoNegocioException.class)
    public ResponseEntity<ApiErrorResponse> tratarConflitoNegocio(
            ConflitoNegocioException erro,
            HttpServletRequest request) {
        logger.warn("ConflitoNegocioException: {}", erro.getMessage());
        return resposta(
                HttpStatus.CONFLICT,
                erro.getMessage(),
                request,
                Map.of());
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ApiErrorResponse> tratarRegraNegocio(
            RegraNegocioException erro,
            HttpServletRequest request) {
        logger.warn("RegraNegocioException: {}", erro.getMessage());
        return resposta(
                HttpStatus.CONFLICT,
                erro.getMessage(),
                request,
                Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> tratarValidacao(
            MethodArgumentNotValidException erro,
            HttpServletRequest request) {
        Map<String, String> campos = new LinkedHashMap<>();
        erro.getBindingResult().getFieldErrors().forEach(fieldError ->
                campos.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage()));
        logger.warn("Erro de validação: {}", campos);
        return resposta(
                HttpStatus.BAD_REQUEST,
                "Existem campos inválidos na requisição.",
                request,
                campos);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiErrorResponse> tratarEntradaInvalida(
            Exception erro,
            HttpServletRequest request) {
        logger.warn("Erro de entrada inválida: {}", erro.getMessage());
        return resposta(
                HttpStatus.BAD_REQUEST,
                "A requisição contém um valor ou formato inválido.",
                request,
                Map.of());
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ApiErrorResponse> tratarRecursoNaoEncontrado(
            RecursoNaoEncontradoException erro,
            HttpServletRequest request) {
        return resposta(
                HttpStatus.NOT_FOUND,
                erro.getMessage(),
                request,
                Map.of());
    }

    @ExceptionHandler(ConflitoDeNegocioException.class)
    public ResponseEntity<ApiErrorResponse> tratarConflitoNegocio(
            ConflitoDeNegocioException erro,
            HttpServletRequest request) {
        return resposta(
                HttpStatus.CONFLICT,
                erro.getMessage(),
                request,
                Map.of());
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ApiErrorResponse> tratarOperacaoNaoSuportada(
            UnsupportedOperationException erro,
            HttpServletRequest request) {
        return resposta(
                HttpStatus.CONFLICT,
                erro.getMessage(),
                request,
                Map.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> tratarInesperado(
            Exception erro,
            HttpServletRequest request) {
        logger.error("=== ERRO NÃO TRATADO ===", erro);
        logger.error("Tipo: {}", erro.getClass().getName());
        logger.error("Mensagem: {}", erro.getMessage());
        logger.error("Caminho: {}", request.getRequestURI());
        logger.error("Método: {}", request.getMethod());
        
        return resposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno não esperado.",
                request,
                Map.of());
    }

    private ResponseEntity<ApiErrorResponse> resposta(
            HttpStatus status,
            String mensagem,
            HttpServletRequest request,
            Map<String, String> campos) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                mensagem,
                request.getRequestURI(),
                campos);
        return ResponseEntity.status(status).body(response);
    }
}
