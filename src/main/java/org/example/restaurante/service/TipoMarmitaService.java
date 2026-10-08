package org.example.restaurante.service;

import org.example.restaurante.model.TipoMarmita;
import org.example.restaurante.repository.TipoMarmitaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TipoMarmitaService {

    private final TipoMarmitaRepository tipoMarmitaRepository;

    public TipoMarmitaService(TipoMarmitaRepository tipoMarmitaRepository) {
        this.tipoMarmitaRepository = tipoMarmitaRepository;
    }

    public List<TipoMarmita> listarTiposMarmita() {
        return tipoMarmitaRepository.findAll();
    }

    public List<TipoMarmita> listarTiposMarmitaAtivos() {
        return tipoMarmitaRepository.findByAtivoTrue();
    }

    public TipoMarmita salvarTipoMarmita(TipoMarmita tipoMarmita) {
        validar(tipoMarmita);
        return tipoMarmitaRepository.save(tipoMarmita);
    }

    public TipoMarmita buscarTipoMarmita(int id) {
        return buscarOuErro(id);
    }

    public TipoMarmita atualizarTipoMarmita(int id, TipoMarmita tipoMarmita) {

        TipoMarmita tipoExistente = buscarOuErro(id);
        validar(tipoMarmita);

        tipoExistente.setNome(tipoMarmita.getNome());
        tipoExistente.setDescricao(tipoMarmita.getDescricao());
        tipoExistente.setPreco(tipoMarmita.getPreco());
        tipoExistente.setAtivo(tipoMarmita.isAtivo());

        return tipoMarmitaRepository.save(tipoExistente);
    }

    public TipoMarmita alterarAtivo(int id, Boolean ativo) {

        if (ativo == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O campo ativo é obrigatório");
        }

        TipoMarmita tipoExistente = buscarOuErro(id);
        tipoExistente.setAtivo(ativo);

        return tipoMarmitaRepository.save(tipoExistente);
    }

    public void excluirTipoMarmita(int id) {
        try {
            tipoMarmitaRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Este tipo de marmita já foi usado em pedidos. Desative-o em vez de excluir");
        }
    }

    private TipoMarmita buscarOuErro(int id) {
        return tipoMarmitaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Tipo de marmita não encontrado"));
    }

    private void validar(TipoMarmita tipoMarmita) {

        if (tipoMarmita.getNome() == null || tipoMarmita.getNome().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome é obrigatório");
        }
        if (tipoMarmita.getPreco() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O preço não pode ser negativo");
        }
    }
}
