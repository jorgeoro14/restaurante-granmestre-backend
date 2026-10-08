package org.example.restaurante.controller;

import org.example.restaurante.model.Acompanhamento;
import org.example.restaurante.service.AcompanhamentoService;
import org.example.restaurante.dto.AtivoDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/acompanhamentos")
public class AcompanhamentoController {

    private final AcompanhamentoService acompanhamentoService;

    public AcompanhamentoController(AcompanhamentoService acompanhamentoService) {
        this.acompanhamentoService = acompanhamentoService;
    }

    @GetMapping
    public List<Acompanhamento> listarAcompanhamentos() {
        return acompanhamentoService.listarAcompanhamentos();
    }

    @GetMapping("/ativos")
    public List<Acompanhamento> listarAcompanhamentosAtivos() {
        return acompanhamentoService.listarAcompanhamentosAtivos();
    }

    @GetMapping("/{id}")
    public Acompanhamento buscarAcompanhamento(@PathVariable int id) {
        return acompanhamentoService.buscarAcompanhamento(id);
    }

    @PostMapping
    public Acompanhamento salvarAcompanhamento(@RequestBody Acompanhamento acompanhamento) {
        return acompanhamentoService.salvarAcompanhamento(acompanhamento);
    }

    @PutMapping("/{id}")
    public Acompanhamento atualizarAcompanhamento(
            @PathVariable int id,
            @RequestBody Acompanhamento acompanhamento) {

        return acompanhamentoService.atualizarAcompanhamento(id, acompanhamento);
    }

    @PatchMapping("/{id}/ativo")
    public Acompanhamento alterarAtivo(@PathVariable int id, @RequestBody AtivoDTO dados) {
        return acompanhamentoService.alterarAtivo(id, dados.getAtivo());
    }

    @DeleteMapping("/{id}")
    public void excluirAcompanhamento(@PathVariable int id) {
        acompanhamentoService.excluirAcompanhamento(id);
    }
}