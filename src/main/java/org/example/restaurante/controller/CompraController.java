package org.example.restaurante.controller;

import org.example.restaurante.model.Compra;
import org.example.restaurante.service.CompraService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compras")
public class CompraController {

    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @GetMapping
    public List<Compra> listarCompras() {
        return compraService.listarCompras();
    }

    @GetMapping("/{id}")
    public Compra buscarCompra(@PathVariable int id) {
        return compraService.buscarCompra(id);
    }

    @PostMapping
    public Compra salvarCompra(@RequestBody Compra compra, @AuthenticationPrincipal Jwt jwt) {
        return compraService.salvarCompra(compra, jwt.getSubject());
    }

    @PutMapping("/{id}")
    public Compra atualizarCompra(
            @PathVariable int id,
            @RequestBody Compra compra) {

        return compraService.atualizarCompra(id, compra);
    }

    @DeleteMapping("/{id}")
    public void excluirCompra(@PathVariable int id) {
        compraService.excluirCompra(id);
    }
}