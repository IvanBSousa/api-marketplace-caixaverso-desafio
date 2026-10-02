package br.edu.fiap.marketplace.repository;

import br.edu.fiap.marketplace.entity.ConfirmacaoPagamento;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** TODO adicionar consultas por carrinho e pelo identificador externo do pagamento. */
public interface ConfirmacaoPagamentoRepository
        extends JpaRepository<ConfirmacaoPagamento, Long> {

    /**
     * Busca uma confirmação de pagamento pelo ID do carrinho.
     * Garante unicidade pois a tabela tem UNIQUE constraint.
     */
    Optional<ConfirmacaoPagamento> findByCarrinhoId(Long carrinhoId);

    /**
     * Busca uma confirmação de pagamento pelo identificador externo do pagamento.
     * Garante unicidade pois a tabela tem UNIQUE constraint.
     */
    Optional<ConfirmacaoPagamento> findByIdPagamento(String idPagamento);
}
