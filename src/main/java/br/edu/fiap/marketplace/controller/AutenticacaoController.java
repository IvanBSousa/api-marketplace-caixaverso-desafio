package br.edu.fiap.marketplace.controller;

import br.edu.fiap.marketplace.dto.LoginRequest;
import br.edu.fiap.marketplace.dto.TokenResponse;
import br.edu.fiap.marketplace.exception.ApiErrorResponse;
import br.edu.fiap.marketplace.service.AutenticacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** TODO criar POST /api/auth/login usando LoginRequest e TokenResponse. */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação")
public class AutenticacaoController {
    private final AutenticacaoService autenticacaoService;

    public AutenticacaoController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Autenticar e receber um JWT",
            description = "Localiza o usuário pelo e-mail, compara a senha com o hash BCrypt "
                    + "e devolve um JWT HS256. A senha e o hash não entram no token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credenciais válidas; JWT emitido"),
            @ApiResponse(
                    responseCode = "400",
                    description = "JSON ou campos inválidos",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "E-mail, senha ou estado do usuário inválido (Sem autorização)",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(autenticacaoService.login(request));
    }
}
