package br.edu.fiap.marketplace.repository;

import br.edu.fiap.marketplace.entity.Carrinho;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** TODO adicionar consultas por usuário e status do carrinho. */
public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {

    @EntityGraph(attributePaths = "usuario")
    List<Carrinho> findByUsuarioId(Long usuarioId);
}
