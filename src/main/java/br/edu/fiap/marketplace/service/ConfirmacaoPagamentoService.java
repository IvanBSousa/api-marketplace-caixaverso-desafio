package br.edu.fiap.marketplace.service;

import br.edu.fiap.marketplace.dto.ConfirmacaoPagamentoRequest;
import br.edu.fiap.marketplace.dto.ConfirmacaoPagamentoResponse;
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
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Service
public class ConfirmacaoPagamentoService {

    private static final Logger logger = LoggerFactory.getLogger(ConfirmacaoPagamentoService.class);

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

    public ConfirmacaoPagamentoResponse aprovar(Long id) {

        ConfirmacaoPagamento pagamento =
                pagamentoRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Pagamento não encontrado"));

        pagamento.aprovar();

        pagamentoRepository.save(pagamento);

        return ConfirmacaoPagamentoResponse.de(pagamento);
    }

    public ConfirmacaoPagamentoResponse recusar(Long id) {

        ConfirmacaoPagamento pagamento =
                pagamentoRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Pagamento não encontrado"));

        pagamento.recusar();

        pagamentoRepository.save(pagamento);

        return ConfirmacaoPagamentoResponse.de(pagamento);
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
    @Transactional
    public ConfirmacaoPagamento criarConfirmacaoPagamento(ConfirmacaoPagamentoRequest request) {
        logger.info("=== INICIANDO CRIAÇÃO DE PAGAMENTO ===");
        logger.debug("Request recebido - carrinhoId: {}, usuarioId: {}, idPagamento: {}",
                request.carrinhoId(), request.usuarioId(), request.idPagamento());

        Carrinho carrinho = validarCarrinhoExiste(request.carrinhoId());
        logger.debug("Carrinho encontrado: ID={}, usuário={}, status={}, produto={}",
                carrinho.getId(), carrinho.getUsuario().getId(), carrinho.getStatus(), carrinho.getProduto().getNome());

        Usuario usuario = validarUsuarioExiste(request.usuarioId());
        logger.debug("Usuário encontrado: ID={}, email={}, ativo={}",
                usuario.getId(), usuario.getEmail(), usuario.isAtivo());

        validarIdPagamentoUnico(request.idPagamento());
        logger.debug("idPagamento é único: {}", request.idPagamento());

        validarCarrinhoPertenceAoUsuario(carrinho, usuario);
        logger.debug("Validação de pertencimento OK");

        validarCarrinhoAberto(carrinho);
        logger.debug("Carrinho está aberto");

        BigDecimal valorPago = calcularValorTotal(carrinho);
        logger.info("Valor total calculado: R$ {} (preço: R$ {}, quantidade: {})",
                valorPago, carrinho.getProduto().getPreco(), carrinho.getQuantidade());

        ConfirmacaoPagamento confirmacao = new ConfirmacaoPagamento(
                carrinho,
                usuario,
                request.idPagamento(),
                valorPago);
        logger.debug("Objeto ConfirmacaoPagamento criado");

        ConfirmacaoPagamento salva = pagamentoRepository.save(confirmacao);
        logger.info("=== PAGAMENTO CRIADO COM SUCESSO - ID: {} ===", salva.getId());

        return salva;
    }

    /**
     * Valida se um carrinho existe no banco de dados.
     *
     * @param carrinhoId ID do carrinho
     * @return o carrinho encontrado
     * @throws RecursoNaoEncontradoException se carrinho não existe
     */
    private Carrinho validarCarrinhoExiste(Long carrinhoId) {
        logger.debug("Validando existência do carrinho ID: {}", carrinhoId);
        return carrinhoRepository.findById(carrinhoId)
                .orElseThrow(() -> {
                    logger.warn("Carrinho não encontrado: ID={}", carrinhoId);
                    return new RecursoNaoEncontradoException("Carrinho com ID " + carrinhoId + " não encontrado.");
                });
    }

    /**
     * Valida se um usuário existe no banco de dados.
     *
     * @param usuarioId ID do usuário
     * @return o usuário encontrado
     * @throws RecursoNaoEncontradoException se usuário não existe
     */
    private Usuario validarUsuarioExiste(Long usuarioId) {
        logger.debug("Validando existência do usuário ID: {}", usuarioId);
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> {
                    logger.warn("Usuário não encontrado: ID={}", usuarioId);
                    return new RecursoNaoEncontradoException("Usuário com ID " + usuarioId + " não encontrado.");
                });
    }

    /**
     * Valida se o idPagamento é único (não duplicado no banco).
     *
     * @param idPagamento identificador externo do pagamento
     * @throws ConflitoNegocioException se idPagamento já existe
     */
    private void validarIdPagamentoUnico(String idPagamento) {
        logger.debug("Validando unicidade do idPagamento: {}", idPagamento);
        if (pagamentoRepository.findByIdPagamento(idPagamento).isPresent()) {
            logger.warn("idPagamento duplicado encontrado: {}", idPagamento);
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
        logger.debug("Validando pertencimento - carrinho.usuarioId={}, usuarioId={}",
                carrinho.getUsuario().getId(), usuario.getId());
        if (!carrinho.getUsuario().getId().equals(usuario.getId())) {
            logger.warn("Carrinho não pertence ao usuário - carrinho.usuarioId={}, usuarioId={}",
                    carrinho.getUsuario().getId(), usuario.getId());
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
        logger.debug("Validando status do carrinho: {}", carrinho.getStatus());
        if (!StatusCarrinho.ABERTO.equals(carrinho.getStatus())) {
            logger.warn("Carrinho não está aberto - status={}", carrinho.getStatus());
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
        logger.debug("Calculando valor total do carrinho");
        BigDecimal precoUnitario = carrinho.getProduto().getPreco();
        BigDecimal quantidade = new BigDecimal(carrinho.getQuantidade());
        BigDecimal total = precoUnitario.multiply(quantidade);
        logger.debug("Cálculo: {} × {} = {}", precoUnitario, quantidade, total);
        return total;
    }

    /**
     * Obtém uma confirmação de pagamento pelo ID.
     */
    public ConfirmacaoPagamento obterPorId(Long id) {
        logger.info("Buscando pagamento ID: {}", id);
        return pagamentoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Pagamento não encontrado: ID={}", id);
                    return new RecursoNaoEncontradoException("Pagamento com ID " + id + " não encontrado.");
                });
    }
}

