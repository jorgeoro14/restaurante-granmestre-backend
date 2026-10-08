package org.example.restaurante.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DashboardPedidosDTO {

    private LocalDate data;
    private long totalPedidos;
    private long marmitasVendidas;
    private long pendentes;
    private long concluidos;
    private Map<String, Long> porStatus;
    private List<QuantidadePorTipoDTO> porTipoMarmita;

    public DashboardPedidosDTO() {
    }

    public DashboardPedidosDTO(LocalDate data, long totalPedidos, long marmitasVendidas, long pendentes,
                               long concluidos, Map<String, Long> porStatus,
                               List<QuantidadePorTipoDTO> porTipoMarmita) {
        this.data = data;
        this.totalPedidos = totalPedidos;
        this.marmitasVendidas = marmitasVendidas;
        this.pendentes = pendentes;
        this.concluidos = concluidos;
        this.porStatus = porStatus;
        this.porTipoMarmita = porTipoMarmita;
    }

    public LocalDate getData() {
        return data;
    }

    public long getTotalPedidos() {
        return totalPedidos;
    }

    public long getMarmitasVendidas() {
        return marmitasVendidas;
    }

    public long getPendentes() {
        return pendentes;
    }

    public long getConcluidos() {
        return concluidos;
    }

    public Map<String, Long> getPorStatus() {
        return porStatus;
    }

    public List<QuantidadePorTipoDTO> getPorTipoMarmita() {
        return porTipoMarmita;
    }
}
