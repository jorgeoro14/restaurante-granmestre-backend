package org.example.restaurante.controller;

import org.example.restaurante.dto.DashboardComprasDTO;
import org.example.restaurante.dto.DashboardPedidosDTO;
import org.example.restaurante.service.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // Sem o parametro data, usa o dia de hoje. Exemplo: ?data=2026-10-07
    @GetMapping("/resumo-pedidos")
    public DashboardPedidosDTO resumoPedidos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {

        return dashboardService.resumoPedidos(data == null ? LocalDate.now() : data);
    }

    @GetMapping("/resumo-compras")
    public DashboardComprasDTO resumoCompras(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {

        return dashboardService.resumoCompras(data == null ? LocalDate.now() : data);
    }
}
