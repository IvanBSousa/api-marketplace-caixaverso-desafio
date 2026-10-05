package br.edu.fiap.marketplace.controller;

import br.edu.fiap.marketplace.config.SecurityConfig;
import br.edu.fiap.marketplace.dto.CarrinhoRequest;
import br.edu.fiap.marketplace.dto.CarrinhoResponse;
import br.edu.fiap.marketplace.entity.StatusCarrinho;
import br.edu.fiap.marketplace.entity.Usuario;
import br.edu.fiap.marketplace.exception.GlobalExceptionHandler;
import br.edu.fiap.marketplace.security.JwtService;
import br.edu.fiap.marketplace.service.AutenticacaoService;
import br.edu.fiap.marketplace.service.CarrinhoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TODO implementar um @WebMvcTest com MockMvc e o CarrinhoService mockado.
 * O cenário mínimo deve comprovar status, JSON e validação HTTP.
 */

@SpringBootTest
@AutoConfigureMockMvc
class CarrinhoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarrinhoService carrinhoService;

    @Test
    void deveCriarCarrinho() throws Exception {
        CarrinhoResponse response = new CarrinhoResponse(
                1L,
                2L,
                "Usuario Teste",
                3L,
                "Protudo Teste",
                new BigDecimal("100.00"),
                10,
                new BigDecimal("1000.00"),
                StatusCarrinho.ABERTO,
                Instant.parse("2026-10-05T00:00:00Z")
        );

        when(carrinhoService.criar(any(CarrinhoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/carrinhos")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "usuarioId": "1",
                            "produtoId": "2",
                            "quantidade": "3"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.usuarioNome").value("Usuario Teste"));
    }
}
