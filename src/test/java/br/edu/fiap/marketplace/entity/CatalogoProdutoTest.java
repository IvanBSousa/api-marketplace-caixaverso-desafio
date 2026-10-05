package br.edu.fiap.marketplace.entity;

import br.edu.fiap.marketplace.exception.ValorInformadoInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

/**
 * TODO implementar um teste unitário da regra de estoque ou preço.
 * Não iniciar o Spring e não usar repository neste arquivo.
 */
class CatalogoProdutoTest {


        CatalogoProduto produto = new CatalogoProduto(
                "Produto Teste",
                "Descricao Teste",
                new BigDecimal("100.00"),
                10,
                true);


    @Test
    @DisplayName("Deve alterar o preço de um produto")
    void deveAlterarOPreco() {
        produto.alterarPreco(new BigDecimal("10.00"));

        assertThat(produto.getPreco()).isEqualTo("10.00");
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar alterar o preço para um valor negativo")
    void naoDeveAlterarParaPrecoNegativo() {

        assertThatThrownBy(() -> produto.alterarPreco(new BigDecimal("-10.00")))
                .isInstanceOf(ValorInformadoInvalidoException.class)
                .hasMessageContaining("-10.00");
    }
}
