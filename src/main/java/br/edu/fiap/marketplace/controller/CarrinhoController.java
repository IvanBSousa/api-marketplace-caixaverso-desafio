package br.edu.fiap.marketplace.controller;

import br.edu.fiap.marketplace.service.CarrinhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
