package org.example.restaurante.dto;

public class LoginDTO {
    private String usuario;
    private String senha;


    public LoginDTO() {
    }

    public String getSenha() {
        return senha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
