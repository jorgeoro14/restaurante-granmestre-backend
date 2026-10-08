package org.example.restaurante.controller;

import org.example.restaurante.dto.PedidoRequestDTO;
import org.example.restaurante.dto.PedidoResponseDTO;
import org.example.restaurante.dto.StatusPedidoDTO;
import org.example.restaurante.model.StatusPedido;
import org.example.restaurante.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // GET /pedidos ou GET /pedidos?status=REGISTRADO
    @GetMapping
    public List<PedidoResponseDTO> listarPedidos(@RequestParam(required = false) StatusPedido status) {
        return pedidoService.listarPedidos(status);
    }

    @GetMapping("/{id}")
    public PedidoResponseDTO buscarPedido(@PathVariable int id) {
        return pedidoService.buscarPedido(id);
    }

    // O atendente vem do token (subject do JWT), nao do corpo da requisicao
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponseDTO registrarPedido(
            @RequestBody PedidoRequestDTO pedido,
            @AuthenticationPrincipal Jwt jwt) {

        return pedidoService.registrarPedido(pedido, jwt.getSubject());
    }

    @PutMapping("/{id}/status")
    public PedidoResponseDTO atualizarStatus(
            @PathVariable int id,
            @RequestBody StatusPedidoDTO dados) {

        return pedidoService.atualizarStatus(id, dados.getStatus());
    }

    // Reimpressao manual do cupom na cozinha (ADMIN e ATENDENTE)
    @PostMapping("/{id}/reimprimir")
    public PedidoResponseDTO reimprimir(@PathVariable int id) {
        return pedidoService.reimprimir(id);
    }

    @DeleteMapping("/{id}")
    public void excluirPedido(@PathVariable int id) {
        pedidoService.excluirPedido(id);
    }
}
