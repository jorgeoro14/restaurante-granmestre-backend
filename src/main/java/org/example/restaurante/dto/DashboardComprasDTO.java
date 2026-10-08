package org.example.restaurante.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DashboardComprasDTO {

    private LocalDate data;
    private long totalCompras;
    private long totalItens;
    private Map<String, Long> porStatus;
    private List<ItemCompraResumoDTO> pendentes;
    private List<ItemCompraResumoDTO> comprados;

    public DashboardComprasDTO() {
    }

    public DashboardComprasDTO(LocalDate data, long totalCompras, long totalItens, Map<String, Long> porStatus,
                               List<ItemCompraResumoDTO> pendentes, List<ItemCompraResumoDTO> comprados) {
        this.data = data;
        this.totalCompras = totalCompras;
        this.totalItens = totalItens;
        this.porStatus = porStatus;
        this.pendentes = pendentes;
        this.comprados = comprados;
    }

    public LocalDate getData() {
        return data;
    }

    public long getTotalCompras() {
        return totalCompras;
    }

    public long getTotalItens() {
        return totalItens;
    }

    public Map<String, Long> getPorStatus() {
        return porStatus;
    }

    public List<ItemCompraResumoDTO> getPendentes() {
        return pendentes;
    }

    public List<ItemCompraResumoDTO> getComprados() {
        return comprados;
    }
}
