package br.edu.fiap.marketplace.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Representa uma escolha simples de produto, quantidade e usuário.
 * Cada registro corresponde a um produto no carrinho do desafio.
 */
@Entity
@Table(name = "carrinhos")
public class Carrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private CatalogoProduto produto;

    @Column(nullable = false)
    private Integer quantidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusCarrinho status;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    protected Carrinho() {
    }

    public Carrinho(Usuario usuario, CatalogoProduto produto, Integer quantidade) {
        this.usuario = usuario;
        this.produto = produto;
        this.quantidade = quantidade;
        this.status = StatusCarrinho.ABERTO;
        this.criadoEm = Instant.now();
    }

    /** TODO aceitar somente quantidade positiva enquanto o carrinho estiver aberto. */
    public void alterarQuantidade(int novaQuantidade) {
        if(novaQuantidade <= 0 && status == StatusCarrinho.ABERTO) {
            throw new UnsupportedOperationException("A quantidade deve ser positiva");
        }
        quantidade = novaQuantidade;
    }

    /** TODO calcular preço do produto multiplicado pela quantidade. */
    public BigDecimal calcularTotal() {
        return new BigDecimal(String.valueOf(produto.getPreco().multiply(BigDecimal.valueOf(quantidade))));
    }

    /** TODO impedir finalizar carrinho cancelado ou já finalizado. */
    public void finalizar() {
        if(status == StatusCarrinho.CANCELADO ||status == StatusCarrinho.FINALIZADO) {
            throw new UnsupportedOperationException("O carrinho está " + status + ". Não é permitido FINALIZAR.");
        }
        status = StatusCarrinho.FINALIZADO;
    }

    /** TODO impedir alterações posteriores ao cancelamento. */
    public void cancelar() {
        if(status != StatusCarrinho.ABERTO) {
            throw new UnsupportedOperationException("O carrinho está " + status + ". Apenas carrinhos ABERTOS podem ser cancelados.");
        }
        status = StatusCarrinho.CANCELADO;
    }

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public CatalogoProduto getProduto() { return produto; }
    public Integer getQuantidade() { return quantidade; }
    public StatusCarrinho getStatus() { return status; }
    public Instant getCriadoEm() { return criadoEm; }
}
