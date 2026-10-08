package org.example.restaurante.service;

import org.example.restaurante.dto.LoginDTO;
import org.example.restaurante.model.TipoUsuario;
import org.example.restaurante.model.Usuario;
import org.example.restaurante.repository.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.example.restaurante.dto.LoginResponseDTO;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository usuarioRepository, TokenService tokenService) {

        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
    }

    public List<Usuario> listarUsuarios() {

        return usuarioRepository.findAll();
    }

    public Usuario salvarUsuario(Usuario usuario) {

        validarDados(usuario, true);

        if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nome de usuário já está em uso");
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public Usuario buscarUsuario(int id) {
        return buscarOuErro(id);
    }

    public Usuario atualizarUsuario(int id, Usuario usuario, String loginLogado) {

        Usuario usuarioExistente = buscarOuErro(id);

        validarDados(usuario, false);

        // ninguem pode se desativar nem tirar o proprio perfil de ADMIN
        boolean proprioUsuario = usuarioExistente.getUsuario().equals(loginLogado);
        boolean perderiaAcesso = !usuario.isAtivo() || usuario.getTipo_usuario() != usuarioExistente.getTipo_usuario();
        if (proprioUsuario && perderiaAcesso && ehAdminAtivo(usuarioExistente)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Você não pode desativar a própria conta nem mudar o próprio tipo de usuário");
        }
        if (ehAdminAtivo(usuarioExistente) && (!usuario.isAtivo() || usuario.getTipo_usuario() != TipoUsuario.ADMIN)) {
            garantirOutroAdminAtivo();
        }

        usuarioRepository.findByUsuario(usuario.getUsuario())
                .filter(outro -> outro.getId() != id)
                .ifPresent(outro -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Nome de usuário já está em uso");
                });

        usuarioExistente.setNome(usuario.getNome());
        usuarioExistente.setUsuario(usuario.getUsuario());
        if (usuario.getSenha() != null && !usuario.getSenha().isBlank()) {
            usuarioExistente.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }
        usuarioExistente.setTipo_usuario(usuario.getTipo_usuario());
        usuarioExistente.setAtivo(usuario.isAtivo());

        return usuarioRepository.save(usuarioExistente);
    }

    public Usuario alterarAtivo(int id, Boolean ativo, String loginLogado) {

        if (ativo == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O campo ativo é obrigatório");
        }

        Usuario usuarioExistente = buscarOuErro(id);

        if (!ativo && ehAdminAtivo(usuarioExistente)) {
            if (usuarioExistente.getUsuario().equals(loginLogado)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Você não pode desativar a própria conta");
            }
            garantirOutroAdminAtivo();
        }

        usuarioExistente.setAtivo(ativo);

        return usuarioRepository.save(usuarioExistente);
    }

    public void excluirUsuario(int id, String loginLogado) {

        usuarioRepository.findById(id).ifPresent(alvo -> {
            if (alvo.getUsuario().equals(loginLogado)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Você não pode excluir a própria conta");
            }
            if (ehAdminAtivo(alvo)) {
                garantirOutroAdminAtivo();
            }
        });

        try {
            usuarioRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Este usuário possui pedidos ou compras vinculados. Desative-o em vez de excluir");
        }
    }

    private boolean ehAdminAtivo(Usuario usuario) {
        return usuario.isAtivo() && usuario.getTipo_usuario() == TipoUsuario.ADMIN;
    }

    // o sistema nunca pode ficar sem um ADMIN ativo: so ele cadastra usuarios
    private void garantirOutroAdminAtivo() {
        if (usuarioRepository.contarAtivosPorTipo(TipoUsuario.ADMIN) <= 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "O sistema precisa de pelo menos um ADMIN ativo. Cadastre outro ADMIN antes");
        }
    }

    private Usuario buscarOuErro(int id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    // RN01 - todo usuario precisa de um tipo de usuario. A senha so e obrigatoria no cadastro.
    private void validarDados(Usuario usuario, boolean senhaObrigatoria) {

        if (usuario.getNome() == null || usuario.getNome().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome é obrigatório");
        }
        if (usuario.getUsuario() == null || usuario.getUsuario().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O usuário (login) é obrigatório");
        }
        if (usuario.getTipo_usuario() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O tipo de usuário é obrigatório (ADMIN, ATENDENTE ou COZINHEIRO)");
        }
        if (senhaObrigatoria && (usuario.getSenha() == null || usuario.getSenha().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A senha é obrigatória");
        }
    }

    public LoginResponseDTO login(LoginDTO loginDTO) {

        if (loginDTO.getUsuario() == null || loginDTO.getUsuario().isBlank()
                || loginDTO.getSenha() == null || loginDTO.getSenha().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos");
        }

        Usuario usuario = usuarioRepository
                .findByUsuario(loginDTO.getUsuario())
                .orElse(null);

        if (usuario == null || !passwordEncoder.matches(loginDTO.getSenha(), usuario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos");
        }

        if (!usuario.isAtivo()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário inativo");
        }

        return new LoginResponseDTO(
                usuario.getNome(),
                usuario.getUsuario(),
                usuario.getTipo_usuario(),
                tokenService.gerarToken(usuario)
        );
    }
}