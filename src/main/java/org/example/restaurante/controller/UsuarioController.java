package org.example.restaurante.controller;

import org.example.restaurante.dto.AtivoDTO;
import org.example.restaurante.model.Usuario;
import org.example.restaurante.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    @GetMapping("/{id}")
    public Usuario buscarUsuario(@PathVariable int id) {
        return usuarioService.buscarUsuario(id);
    }

    @PostMapping
    public Usuario salvarUsuario(@RequestBody Usuario usuario) {
        return usuarioService.salvarUsuario(usuario);
    }

    @PutMapping("/{id}")
    public Usuario atualizarUsuario(
            @PathVariable int id,
            @RequestBody Usuario usuario,
            @AuthenticationPrincipal Jwt jwt) {

        return usuarioService.atualizarUsuario(id, usuario, jwt.getSubject());
    }

    @PatchMapping("/{id}/ativo")
    public Usuario alterarAtivo(@PathVariable int id, @RequestBody AtivoDTO dados, @AuthenticationPrincipal Jwt jwt) {
        return usuarioService.alterarAtivo(id, dados.getAtivo(), jwt.getSubject());
    }

    @DeleteMapping("/{id}")
    public void excluirUsuario(@PathVariable int id, @AuthenticationPrincipal Jwt jwt) {
        usuarioService.excluirUsuario(id, jwt.getSubject());
    }
}