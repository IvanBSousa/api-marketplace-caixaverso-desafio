package br.edu.fiap.marketplace.service;

import br.edu.fiap.marketplace.entity.Carrinho;
import br.edu.fiap.marketplace.exception.RecursoNaoEncontradoException;
import br.edu.fiap.marketplace.exception.ConflitoDeNegocioException;
import br.edu.fiap.marketplace.repository.CarrinhoRepository;
import org.springframework.stereotype.Service;

/** TODO coordenar usuário, produto, estoque, quantidade e estado do carrinho. */
@Service
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;

    public CarrinhoService(CarrinhoRepository carrinhoRepository) {
        this.carrinhoRepository = carrinhoRepository;
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

        carrinho.cancelar();
        carrinhoRepository.save(carrinho);
    }
}
