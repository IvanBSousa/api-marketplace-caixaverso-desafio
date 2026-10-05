package br.edu.fiap.marketplace.service;

import br.edu.fiap.marketplace.dto.ConfirmacaoPagamentoRequest;
import br.edu.fiap.marketplace.entity.Carrinho;
import br.edu.fiap.marketplace.entity.ConfirmacaoPagamento;
import br.edu.fiap.marketplace.entity.StatusCarrinho;
import br.edu.fiap.marketplace.entity.Usuario;
import br.edu.fiap.marketplace.exception.ConflitoNegocioException;
import br.edu.fiap.marketplace.exception.RecursoNaoEncontradoException;
import br.edu.fiap.marketplace.exception.RegraNegocioException;
import br.edu.fiap.marketplace.repository.CarrinhoRepository;
import br.edu.fiap.marketplace.repository.ConfirmacaoPagamentoRepository;
import br.edu.fiap.marketplace.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
public class ConfirmacaoPagamentoService {

    private final ConfirmacaoPagamentoRepository pagamentoRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final UsuarioRepository usuarioRepository;

    public ConfirmacaoPagamentoService(
            ConfirmacaoPagamentoRepository pagamentoRepository,
            CarrinhoRepository carrinhoRepository,
            UsuarioRepository usuarioRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.carrinhoRepository = carrinhoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void aprovar(Long id) {

        ConfirmacaoPagamento pagamento =
                pagamentoRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Pagamento não encontrado"));

        pagamento.aprovar();

        pagamentoRepository.save(pagamento);
    }

    public void recusar(Long id) {

        ConfirmacaoPagamento pagamento =
                pagamentoRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Pagamento não encontrado"));

        pagamento.recusar();

        pagamentoRepository.save(pagamento);
    }

    /**
     * Cria uma nova confirmação de pagamento com status PENDENTE.
     *
     * <p>Executa todas as validações de domínio antes de persistir.
     *
     * @param request contém carrinhoId, usuarioId, idPagamento
     * @return confirmação criada com status PENDENTE
     * @throws RecursoNaoEncontradoException se carrinho ou usuário inexistem
     * @throws ConflitoNegocioException se idPagamento é duplicado
     * @throws RegraNegocioException se carrinho não pertence ao usuário ou não está aberto
     */
    public ConfirmacaoPagamento criarConfirmacaoPagamento(ConfirmacaoPagamentoRequest request) {
        Carrinho carrinho = validarCarrinhoExiste(request.carrinhoId());
        Usuario usuario = validarUsuarioExiste(request.usuarioId());

        validarIdPagamentoUnico(request.idPagamento());
        validarCarrinhoPertenceAoUsuario(carrinho, usuario);
        validarCarrinhoAberto(carrinho);

        BigDecimal valorPago = calcularValorTotal(carrinho);

        ConfirmacaoPagamento confirmacao = new ConfirmacaoPagamento(
                carrinho,
                usuario,
                request.idPagamento(),
                valorPago);

        return pagamentoRepository.save(confirmacao);
    }

    /**
     * Valida se um carrinho existe no banco de dados.
     *
     * @param carrinhoId ID do carrinho
     * @return o carrinho encontrado
     * @throws RecursoNaoEncontradoException se carrinho não existe
     */
    private Carrinho validarCarrinhoExiste(Long carrinhoId) {
        return carrinhoRepository.findById(carrinhoId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Carrinho com ID " + carrinhoId + " não encontrado."));
    }

    /**
     * Valida se um usuário existe no banco de dados.
     *
     * @param usuarioId ID do usuário
     * @return o usuário encontrado
     * @throws RecursoNaoEncontradoException se usuário não existe
     */
    private Usuario validarUsuarioExiste(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Usuário com ID " + usuarioId + " não encontrado."));
    }

    /**
     * Valida se o idPagamento é único (não duplicado no banco).
     *
     * @param idPagamento identificador externo do pagamento
     * @throws ConflitoNegocioException se idPagamento já existe
     */
    private void validarIdPagamentoUnico(String idPagamento) {
        if (pagamentoRepository.findByIdPagamento(idPagamento).isPresent()) {
            throw new ConflitoNegocioException("ID de pagamento já existe: " + idPagamento + ".");
        }
    }

    /**
     * Valida se o carrinho pertence ao usuário informado.
     *
     * @param carrinho o carrinho a validar
     * @param usuario o usuário esperado
     * @throws RegraNegocioException se carrinho não pertence ao usuário
     */
    private void validarCarrinhoPertenceAoUsuario(Carrinho carrinho, Usuario usuario) {
        if (!carrinho.getUsuario().getId().equals(usuario.getId())) {
            throw new RegraNegocioException("Carrinho não pertence ao usuário informado.");
        }
    }

    /**
     * Valida se o carrinho está em estado ABERTO para receber pagamento.
     *
     * @param carrinho o carrinho a validar
     * @throws RegraNegocioException se carrinho não está ABERTO
     */
    private void validarCarrinhoAberto(Carrinho carrinho) {
        if (!StatusCarrinho.ABERTO.equals(carrinho.getStatus())) {
            throw new RegraNegocioException("Carrinho não está aberto para pagamento (status: " + carrinho.getStatus() + ").");
        }
    }

    /**
     * Calcula o valor total do carrinho (preço unitário × quantidade).
     *
     * <p>Usa BigDecimal para garantir precisão em operações monetárias.
     *
     * @param carrinho o carrinho para calcular o total
     * @return valor total com precisão monetária
     */
    private BigDecimal calcularValorTotal(Carrinho carrinho) {
        BigDecimal precoUnitario = carrinho.getProduto().getPreco();
        BigDecimal quantidade = new BigDecimal(carrinho.getQuantidade());
        return precoUnitario.multiply(quantidade);
    }

    /**
     * Obtém uma confirmação de pagamento pelo ID.
     */
    public ConfirmacaoPagamento obterPorId(Long id) {
        return pagamentoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Pagamento com ID " + id + " não encontrado."));
    }
}

