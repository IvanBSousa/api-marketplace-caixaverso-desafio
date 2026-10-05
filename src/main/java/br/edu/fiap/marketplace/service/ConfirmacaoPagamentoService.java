package br.edu.fiap.marketplace.service;

import br.edu.fiap.marketplace.entity.ConfirmacaoPagamento;
import br.edu.fiap.marketplace.repository.ConfirmacaoPagamentoRepository;
import org.springframework.stereotype.Service;


@Service
public class ConfirmacaoPagamentoService {

    private final ConfirmacaoPagamentoRepository repository;

    public ConfirmacaoPagamentoService(
            ConfirmacaoPagamentoRepository repository) {

        this.repository = repository;
    }

    public void aprovar(Long id) {

        ConfirmacaoPagamento pagamento =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Pagamento não encontrado"));

        pagamento.aprovar();

        repository.save(pagamento);
    }

    public void recusar(Long id) {

        ConfirmacaoPagamento pagamento =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Pagamento não encontrado"));

        pagamento.recusar();

        repository.save(pagamento);
    }

}
