package br.edu.fiap.marketplace.config;

import br.edu.fiap.marketplace.security.JwtAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/** Define as rotas públicas e protege o restante da API com Bearer JWT. */
@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationEntryPoint authenticationEntryPoint
    ) throws Exception {
        return http
                // CSRF protege formulário que envia cookie de sessão. Esta API
                // autentica pelo header Authorization, então o filtro não se aplica.
                .csrf(AbstractHttpConfigurer::disable)
                // Não cria HttpSession. Cada chamada traz o próprio JWT e o
                // servidor não guarda estado de login entre requisições.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Ordem importa: a primeira regra que casar decide o acesso.
                .authorizeHttpRequests(authorize -> authorize
                        // Entrar na API não pode exigir um token que ainda não existe.
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        // O cadastro de usuário também é público nesta etapa.
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                        // Swagger e o JSON do OpenAPI ficam abertos para consulta da aula.
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**").permitAll()
                        // Todo o resto só passa com um usuário autenticado.
                        .anyRequest().authenticated())
                // Sem token, ou com token rejeitado antes do resource server,
                // responde 401 no formato da API em vez da página padrão do Spring.
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint))
                // Trata a API como resource server: lê o Bearer, valida o JWT
                // com o decoder configurado e usa o mesmo 401 se o token falhar.
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(authenticationEntryPoint))
                // Fecha a configuração e devolve a cadeia que o Spring aplica
                // antes de chegar no controller.
                .build();
    }
}