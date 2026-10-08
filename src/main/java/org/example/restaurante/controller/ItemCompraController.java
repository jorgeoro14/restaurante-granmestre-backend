package org.example.restaurante.controller;

import org.example.restaurante.dto.StatusItemCompraDTO;
import org.example.restaurante.model.ItemCompra;
import org.example.restaurante.model.StatusItemCompra;
import org.example.restaurante.service.ItemCompraService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/itens-compra")
public class ItemCompraController {

    private final ItemCompraService itemCompraService;

    public ItemCompraController(ItemCompraService itemCompraService) {
        this.itemCompraService = itemCompraService;
    }

    // GET /itens-compra, ?status=PENDENTE, ?data=2026-10-07 ou os dois juntos
    @GetMapping
    public List<ItemCompra> listarItensCompra(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(required = false) StatusItemCompra status) {

        return itemCompraService.listarItensCompra(data, status);
    }

    // Lista de compras do dia: GET /itens-compra/hoje ou /itens-compra/hoje?status=PENDENTE
    @GetMapping("/hoje")
    public List<ItemCompra> listarItensDeHoje(@RequestParam(required = false) StatusItemCompra status) {
        return itemCompraService.listarItensCompra(LocalDate.now(), status);
    }

    @GetMapping("/compra/{compraId}")
    public List<ItemCompra> listarItensPorCompra(@PathVariable int compraId) {
        return itemCompraService.listarItensPorCompra(compraId);
    }

    @GetMapping("/{id}")
    public ItemCompra buscarItemCompra(@PathVariable int id) {
        return itemCompraService.buscarItemCompra(id);
    }

    @PostMapping
    public ItemCompra salvarItemCompra(@RequestBody ItemCompra itemCompra) {
        return itemCompraService.salvarItemCompra(itemCompra);
    }

    @PutMapping("/{id}")
    public ItemCompra atualizarItemCompra(
            @PathVariable int id,
            @RequestBody ItemCompra itemCompra) {

        return itemCompraService.atualizarItemCompra(id, itemCompra);
    }

    @PutMapping("/{id}/status")
    public ItemCompra atualizarStatus(
            @PathVariable int id,
            @RequestBody StatusItemCompraDTO dados) {

        return itemCompraService.atualizarStatus(id, dados.getStatus());
    }

    @DeleteMapping("/{id}")
    public void excluirItemCompra(@PathVariable int id) {
        itemCompraService.excluirItemCompra(id);
    }
}
