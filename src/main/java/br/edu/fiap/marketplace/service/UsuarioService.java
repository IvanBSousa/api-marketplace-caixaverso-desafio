package br.edu.fiap.marketplace.service;

import br.edu.fiap.marketplace.dto.UsuarioRequest;
import br.edu.fiap.marketplace.dto.UsuarioResponse;
import br.edu.fiap.marketplace.entity.Usuario;
import br.edu.fiap.marketplace.exception.EmailJaCadastradoException;
import br.edu.fiap.marketplace.exception.UsuarioNaoEncontradoException;
import br.edu.fiap.marketplace.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** TODO implementar cadastro, consulta e proteção da senha com PasswordEncoder. */
@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponse cadastrar(UsuarioRequest request) {
        if (usuarioRepository.existsByEmailIgnoreCase(request.email())) {
            throw new EmailJaCadastradoException(request.email());
        }

        String senhaHash = passwordEncoder.encode(request.senha());
        Usuario usuario = new Usuario(request.nome(), request.email(), senhaHash);
        return UsuarioResponse.de(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(UsuarioResponse::de)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
    }

    @Transactional
    public UsuarioResponse alteraSenha(UsuarioRequest request, Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));

        String novaSenhaHash = passwordEncoder.encode(request.senha());
        usuario.atualizarSenhaHash(novaSenhaHash);
        return UsuarioResponse.de(usuario);
    }
}
