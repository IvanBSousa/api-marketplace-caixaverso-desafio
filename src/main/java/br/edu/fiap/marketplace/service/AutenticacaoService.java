package br.edu.fiap.marketplace.service;

import br.edu.fiap.marketplace.dto.LoginRequest;
import br.edu.fiap.marketplace.dto.TokenResponse;
import br.edu.fiap.marketplace.entity.Usuario;
import br.edu.fiap.marketplace.exception.CredenciaisInvalidasException;
import br.edu.fiap.marketplace.repository.UsuarioRepository;
import br.edu.fiap.marketplace.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** TODO localizar usuário, comparar BCrypt e solicitar a emissão do JWT. */
@Service
public class AutenticacaoService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AutenticacaoService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(CredenciaisInvalidasException::new);

        boolean senhaCorreta = passwordEncoder.matches(
                request.senha(),
                usuario.getSenha());

        if (!usuario.isAtivo() || !senhaCorreta) {
            throw new CredenciaisInvalidasException();
        }

        return jwtService.gerarToken(usuario);
    }
}
