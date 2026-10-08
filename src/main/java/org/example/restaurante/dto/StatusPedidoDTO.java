package org.example.restaurante.dto;

import org.example.restaurante.model.StatusPedido;

public class StatusPedidoDTO {

    private StatusPedido status;

    public StatusPedidoDTO() {
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }
}
