package br.edu.fiap.marketplace.controller;

import br.edu.fiap.marketplace.dto.CarrinhoRequest;
import br.edu.fiap.marketplace.dto.CarrinhoResponse;
import br.edu.fiap.marketplace.dto.ConfirmacaoPagamentoResponse;
import br.edu.fiap.marketplace.service.CarrinhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/** TODO criar as rotas protegidas de criação, consulta e alteração do carrinho. */
@RestController
@RequestMapping("/api/carrinhos")
@Tag(name = "Carrinhos")
@SecurityRequirement(name = "bearerAuth")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    public CarrinhoController(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

    @PostMapping
    @Operation(
            summary = "Cria um novo carrinho",
            description = "Cria um novo carrinho e exibe as informações na resposta")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Confirmação de criação do carrinho",
                    content = @Content(schema = @Schema(implementation = CarrinhoResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente ou inválido")
    })
    public ResponseEntity<CarrinhoResponse> criar(@Valid @RequestBody CarrinhoRequest request) {
        CarrinhoResponse response = carrinhoService.criar(request);

        return ResponseEntity.created(URI.create("/api/carrinhos/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Consultar carrinho por ID",
            description = "Retorna os detalhes de um carrinho pelo ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Carrinho encontrado",
                    content = @Content(schema = @Schema(implementation = CarrinhoResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Carrinho não encontrado"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente ou inválido")
    })
    public ResponseEntity<CarrinhoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(carrinhoService.buscar(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(
            summary = "Obter carrinhos de um usuário",
            description = "Retorna os carrinhos do usuário pelo ID do usuário'")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Carrinhos do usuário encontrados",
                    content = @Content(schema = @Schema(implementation = CarrinhoResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Carrinhos não encontrados"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente ou inválido")
    })
    public ResponseEntity<List<CarrinhoResponse>> buscaCarrinhoUsuarioId(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carrinhoService.buscarCarrinhosUsuario(usuarioId));
    }

    /**
     * Cancela um carrinho aberto.
     * 
     * @param id ID do carrinho a ser cancelado
     * @return 204 No Content se cancelado com sucesso
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar carrinho", 
               description = "Cancela um carrinho aberto. Apenas carrinhos com status ABERTO podem ser cancelados.")
    @ApiResponse(responseCode = "204", description = "Carrinho cancelado com sucesso", content = @Content())
    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content())
    @ApiResponse(responseCode = "404", description = "Carrinho não encontrado", content = @Content())
    @ApiResponse(responseCode = "409", description = "Carrinho não está aberto", content = @Content())
    public ResponseEntity<Void> cancelarCarrinho(@PathVariable Long id) {
        carrinhoService.cancelarCarrinho(id);
        return ResponseEntity.noContent().build();
    }
}
