package br.edu.fiap.marketplace.controller;

import br.edu.fiap.marketplace.dto.UsuarioRequest;
import br.edu.fiap.marketplace.dto.UsuarioResponse;
import br.edu.fiap.marketplace.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/** TODO criar as rotas públicas de cadastro e protegidas de consulta. */
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar usuário com senha protegida por BCrypt")
    public ResponseEntity<UsuarioResponse> cadastrar(
            @Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse response = usuarioService.cadastrar(request);
        return ResponseEntity
                .created(URI.create("/api/usuarios/" + response.id()))
                .body(response);
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar usuários sem retornar senhas")
    public ResponseEntity<List<UsuarioResponse>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Buscar usuário por ID sem retornar a senha")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PatchMapping  ("/{id}/alaterar-senha")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Alterar senha protegida por BCrypt")
    public ResponseEntity<UsuarioResponse> alterarSenha(
            @Valid @RequestBody UsuarioRequest request, @RequestParam Long id) {
        UsuarioResponse response = usuarioService.alteraSenha(request, id);
        return ResponseEntity.ok(response);
    }
}
