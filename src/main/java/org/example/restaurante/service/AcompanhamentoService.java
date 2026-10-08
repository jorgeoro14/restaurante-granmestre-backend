package org.example.restaurante.service;

import org.example.restaurante.model.Acompanhamento;
import org.example.restaurante.repository.AcompanhamentoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AcompanhamentoService {

    private final AcompanhamentoRepository acompanhamentoRepository;

    public AcompanhamentoService(AcompanhamentoRepository acompanhamentoRepository) {
        this.acompanhamentoRepository = acompanhamentoRepository;
    }

    public List<Acompanhamento> listarAcompanhamentos() {
        return acompanhamentoRepository.findAll();
    }

    public List<Acompanhamento> listarAcompanhamentosAtivos() {
        return acompanhamentoRepository.findByAtivoTrue();
    }

    public Acompanhamento salvarAcompanhamento(Acompanhamento acompanhamento) {
        validar(acompanhamento);
        return acompanhamentoRepository.save(acompanhamento);
    }

    public Acompanhamento buscarAcompanhamento(int id) {
        return buscarOuErro(id);
    }

    public Acompanhamento atualizarAcompanhamento(int id, Acompanhamento acompanhamento) {

        Acompanhamento acompanhamentoExistente = buscarOuErro(id);
        validar(acompanhamento);

        acompanhamentoExistente.setNome(acompanhamento.getNome());
        acompanhamentoExistente.setDescricao(acompanhamento.getDescricao());
        acompanhamentoExistente.setAtivo(acompanhamento.isAtivo());

        return acompanhamentoRepository.save(acompanhamentoExistente);
    }

    public Acompanhamento alterarAtivo(int id, Boolean ativo) {

        if (ativo == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O campo ativo é obrigatório");
        }

        Acompanhamento acompanhamentoExistente = buscarOuErro(id);
        acompanhamentoExistente.setAtivo(ativo);

        return acompanhamentoRepository.save(acompanhamentoExistente);
    }

    public void excluirAcompanhamento(int id) {
        try {
            acompanhamentoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Este acompanhamento já foi usado em pedidos. Desative-o em vez de excluir");
        }
    }

    private Acompanhamento buscarOuErro(int id) {
        return acompanhamentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Acompanhamento não encontrado"));
    }

    private void validar(Acompanhamento acompanhamento) {

        if (acompanhamento.getNome() == null || acompanhamento.getNome().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome é obrigatório");
        }
    }
}
