package br.edu.fiap.marketplace.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import br.edu.fiap.marketplace.service.ConfirmacaoPagamentoService;


/** TODO criar as rotas protegidas de confirmação e consulta do pagamento. */
@RestController
@RequestMapping("/api/pagamentos")
@Tag(name = "Pagamentos")
@SecurityRequirement(name = "bearerAuth")
public class ConfirmacaoPagamentoController {
    private final ConfirmacaoPagamentoService pagamentoService;

    public ConfirmacaoPagamentoController(
            ConfirmacaoPagamentoService pagamentoService) {

        this.pagamentoService = pagamentoService;
    }

    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<Void> aprovar(@PathVariable Long id) {
        pagamentoService.aprovar(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/recusar")
    public ResponseEntity<Void> recusar(
            @PathVariable Long id) {

        pagamentoService.recusar(id);

        return ResponseEntity.ok().build();
    }




    }


