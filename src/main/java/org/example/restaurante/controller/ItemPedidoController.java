package org.example.restaurante.controller;

import org.example.restaurante.model.ItemPedido;
import org.example.restaurante.service.ItemPedidoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/itens-pedido")
public class ItemPedidoController {

    private final ItemPedidoService itemPedidoService;

    public ItemPedidoController(ItemPedidoService itemPedidoService) {
        this.itemPedidoService = itemPedidoService;
    }

    @GetMapping
    public List<ItemPedido> listarItensPedido() {
        return itemPedidoService.listarItensPedido();
    }

    @GetMapping("/{id}")
    public ItemPedido buscarItemPedido(@PathVariable int id) {
        return itemPedidoService.buscarItemPedido(id);
    }

    @PostMapping
    public ItemPedido salvarItemPedido(@RequestBody ItemPedido itemPedido) {
        return itemPedidoService.salvarItemPedido(itemPedido);
    }

    @PutMapping("/{id}")
    public ItemPedido atualizarItemPedido(
            @PathVariable int id,
            @RequestBody ItemPedido itemPedido) {

        return itemPedidoService.atualizarItemPedido(id, itemPedido);
    }

    @DeleteMapping("/{id}")
    public void excluirItemPedido(@PathVariable int id) {
        itemPedidoService.excluirItemPedido(id);
    }
}