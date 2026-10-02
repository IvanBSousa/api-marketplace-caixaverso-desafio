package br.edu.fiap.marketplace.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

/**
 * TODO implementar um teste unitário de quantidade, total ou estado do carrinho.
 * Instanciar somente objetos Java reais.
 */
@ExtendWith(MockitoExtension.class)
class CarrinhoTest {

    @Mock
    private Usuario usuario;

    @Mock
    private CatalogoProduto produto;

    Carrinho carrinho = new Carrinho(usuario, produto, 10);

    @Test
    void deveAlterarQuantidade() {
        carrinho.alterarQuantidade(5);

        assertThat(carrinho.getQuantidade()).isEqualTo(5);
    }

    @Test
    void lancaExcecaoAoTentarAlterarQuantidadeNegativa() {

        assertThatThrownBy(() -> carrinho.alterarQuantidade(-10))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("A quantidade deve ser positiva");
    }
}
