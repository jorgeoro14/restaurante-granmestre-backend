package org.example.restaurante.dto;

import org.example.restaurante.model.TipoUsuario;

public class LoginResponseDTO {

    private String nome;
    private String usuario;
    private TipoUsuario tipo_usuario;
    private String token;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String nome, String usuario, TipoUsuario tipo_usuario, String token) {
        this.nome = nome;
        this.usuario = usuario;
        this.tipo_usuario = tipo_usuario;
        this.token = token;
    }

    public String getNome() {
        return nome;
    }

    public String getUsuario() {
        return usuario;
    }

    public TipoUsuario getTipo_usuario() {
        return tipo_usuario;
    }

    public String getToken() {
        return token;
    }
}
