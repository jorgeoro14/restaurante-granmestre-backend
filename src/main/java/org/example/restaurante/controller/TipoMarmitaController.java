package org.example.restaurante.controller;

import org.example.restaurante.model.TipoMarmita;
import org.example.restaurante.service.TipoMarmitaService;
import org.example.restaurante.dto.AtivoDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipos-marmita")
public class TipoMarmitaController {

    private final TipoMarmitaService tipoMarmitaService;

    public TipoMarmitaController(TipoMarmitaService tipoMarmitaService) {
        this.tipoMarmitaService = tipoMarmitaService;
    }

    @GetMapping
    public List<TipoMarmita> listarTiposMarmita() {
        return tipoMarmitaService.listarTiposMarmita();
    }

    @GetMapping("/ativos")
    public List<TipoMarmita> listarTiposMarmitaAtivos() {
        return tipoMarmitaService.listarTiposMarmitaAtivos();
    }

    @GetMapping("/{id}")
    public TipoMarmita buscarTipoMarmita(@PathVariable int id) {
        return tipoMarmitaService.buscarTipoMarmita(id);
    }

    @PostMapping
    public TipoMarmita salvarTipoMarmita(@RequestBody TipoMarmita tipoMarmita) {
        return tipoMarmitaService.salvarTipoMarmita(tipoMarmita);
    }

    @PutMapping("/{id}")
    public TipoMarmita atualizarTipoMarmita(
            @PathVariable int id,
            @RequestBody TipoMarmita tipoMarmita) {

        return tipoMarmitaService.atualizarTipoMarmita(id, tipoMarmita);
    }

    @PatchMapping("/{id}/ativo")
    public TipoMarmita alterarAtivo(@PathVariable int id, @RequestBody AtivoDTO dados) {
        return tipoMarmitaService.alterarAtivo(id, dados.getAtivo());
    }

    @DeleteMapping("/{id}")
    public void excluirTipoMarmita(@PathVariable int id) {
        tipoMarmitaService.excluirTipoMarmita(id);
    }
}