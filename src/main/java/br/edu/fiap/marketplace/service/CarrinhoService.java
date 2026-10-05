package br.edu.fiap.marketplace.service;

import br.edu.fiap.marketplace.dto.CarrinhoRequest;
import br.edu.fiap.marketplace.dto.CarrinhoResponse;
import br.edu.fiap.marketplace.entity.Carrinho;
import br.edu.fiap.marketplace.entity.CatalogoProduto;
import br.edu.fiap.marketplace.entity.StatusCarrinho;
import br.edu.fiap.marketplace.entity.Usuario;
import br.edu.fiap.marketplace.exception.CarrinhoNaoEncontradoException;
import br.edu.fiap.marketplace.exception.CarrinhoNaoPermiteCancelamentoException;
import br.edu.fiap.marketplace.exception.ProdutoInativoException;
import br.edu.fiap.marketplace.exception.ProdutoNaoEncontradoException;
import br.edu.fiap.marketplace.exception.RecursoNaoEncontradoException;
import br.edu.fiap.marketplace.exception.ConflitoDeNegocioException;
import br.edu.fiap.marketplace.exception.UsuarioInativoException;
import br.edu.fiap.marketplace.exception.UsuarioNaoEncontradoException;
import br.edu.fiap.marketplace.repository.CarrinhoRepository;
import br.edu.fiap.marketplace.repository.CatalogoProdutoRepository;
import br.edu.fiap.marketplace.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** TODO coordenar usuário, produto, estoque, quantidade e estado do carrinho. */
@Service
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CatalogoProdutoRepository produtoRepository;

    public CarrinhoService(CarrinhoRepository carrinhoRepository,
                           UsuarioRepository usuarioRepository,
                           CatalogoProdutoRepository produtoRepository) {
        this.carrinhoRepository = carrinhoRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
    }

    /**
     * Cancela um carrinho existente e aberto.
     * 
     * @param carrinhoId ID do carrinho a ser cancelado
     * @throws RecursoNaoEncontradoException quando o carrinho não existe
     * @throws ConflitoDeNegocioException quando o carrinho não está aberto
     */
    public void cancelarCarrinho(Long carrinhoId) {
        Carrinho carrinho = carrinhoRepository.findById(carrinhoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Carrinho com ID " + carrinhoId + " não encontrado"));

        if(carrinho.getStatus() != StatusCarrinho.ABERTO) {
            throw new CarrinhoNaoPermiteCancelamentoException(carrinhoId);
        }

        CatalogoProduto produto = produtoRepository.findById(carrinho.getProduto().getId())
                .orElseThrow(() -> new UsuarioNaoEncontradoException(carrinho.getProduto().getId()));

        carrinho.cancelar();
        produto.reporEstoque(carrinho.getQuantidade());
        carrinhoRepository.save(carrinho);
    }

    @Transactional
    public CarrinhoResponse criar(@Valid CarrinhoRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new UsuarioNaoEncontradoException(request.usuarioId()));
        CatalogoProduto produto = produtoRepository.findById(request.produtoId())
                .orElseThrow(() -> new ProdutoNaoEncontradoException(request.produtoId()));

        if(!produto.isAtivo()) {
            throw new ProdutoInativoException(request.produtoId());
        }
        if(!usuario.isAtivo()) {
            throw new UsuarioInativoException(request.usuarioId());
        }

        Carrinho carrinho = new Carrinho(usuario, produto, request.quantidade());
        produto.baixarEstoque(request.quantidade());

        return CarrinhoResponse.de(carrinho);
    }

    @Transactional(readOnly = true)
    public CarrinhoResponse buscar(Long id) {
        Carrinho response = carrinhoRepository.findById(id)
                .orElseThrow(() -> new CarrinhoNaoEncontradoException(id));

        return CarrinhoResponse.de(response);
    }

    @Transactional(readOnly = true)
    public List<CarrinhoResponse> buscarCarrinhosUsuario(Long usuarioId) {
        return  carrinhoRepository.findByUsuarioId(usuarioId)
                .stream().map(CarrinhoResponse::de)
                .toList();
    }
}
