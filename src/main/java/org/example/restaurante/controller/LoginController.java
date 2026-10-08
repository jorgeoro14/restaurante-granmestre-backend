package org.example.restaurante.controller;

import org.example.restaurante.dto.LoginDTO;
import org.example.restaurante.dto.LoginResponseDTO;
import org.example.restaurante.service.UsuarioService;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {

    private final UsuarioService usuarioService;

    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginDTO loginDTO) {
        return usuarioService.login(loginDTO);
    }
}
