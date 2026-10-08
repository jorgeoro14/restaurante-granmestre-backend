package org.example.restaurante.service;

import org.example.restaurante.model.Compra;
import org.example.restaurante.model.ItemCompra;
import org.example.restaurante.model.StatusItemCompra;
import org.example.restaurante.repository.CompraRepository;
import org.example.restaurante.repository.ItemCompraRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ItemCompraService {

    private final ItemCompraRepository itemCompraRepository;
    private final CompraRepository compraRepository;

    public ItemCompraService(ItemCompraRepository itemCompraRepository, CompraRepository compraRepository) {
        this.itemCompraRepository = itemCompraRepository;
        this.compraRepository = compraRepository;
    }

    // Todos os filtros sao opcionais: data (a lista de um dia) e status (ex.: PENDENTE)
    public List<ItemCompra> listarItensCompra(LocalDate data, StatusItemCompra status) {

        if (data != null && status != null) {
            return itemCompraRepository.buscarPorDataEStatus(data, status);
        }
        if (data != null) {
            return itemCompraRepository.buscarPorData(data);
        }
        if (status != null) {
            return itemCompraRepository.findByStatusOrderByIdAsc(status);
        }
        return itemCompraRepository.findAll(Sort.by("id"));
    }

    public List<ItemCompra> listarItensPorCompra(int compraId) {
        return itemCompraRepository.findByCompraId(compraId);
    }

    public ItemCompra salvarItemCompra(ItemCompra itemCompra) {

        validar(itemCompra);
        itemCompra.setCompra(buscarCompraOuErro(itemCompra));

        // todo item novo entra na lista como pendente, a menos que venha outro status
        if (itemCompra.getStatus() == null) {
            itemCompra.setStatus(StatusItemCompra.PENDENTE);
        }

        return itemCompraRepository.save(itemCompra);
    }

    public ItemCompra buscarItemCompra(int id) {
        return buscarOuErro(id);
    }

    public ItemCompra atualizarItemCompra(int id, ItemCompra itemCompra) {

        ItemCompra itemExistente = buscarOuErro(id);

        validar(itemCompra);

        itemExistente.setCompra(buscarCompraOuErro(itemCompra));
        itemExistente.setNome_produto(itemCompra.getNome_produto());
        itemExistente.setDescricao_produto(itemCompra.getDescricao_produto());
        itemExistente.setQuantidade_produto(itemCompra.getQuantidade_produto());

        // se o status nao for enviado, mantem o que ja estava
        if (itemCompra.getStatus() != null) {
            itemExistente.setStatus(itemCompra.getStatus());
        }

        return itemCompraRepository.save(itemExistente);
    }

    // RN009 - o cozinheiro marca o item como solicitado, comprado (ou cancelado)
    public ItemCompra atualizarStatus(int id, StatusItemCompra novoStatus) {

        if (novoStatus == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status é obrigatório");
        }

        ItemCompra item = buscarOuErro(id);
        item.setStatus(novoStatus);

        return itemCompraRepository.save(item);
    }

    public void excluirItemCompra(int id) {
        itemCompraRepository.deleteById(id);
    }

    private ItemCompra buscarOuErro(int id) {
        return itemCompraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item de compra não encontrado"));
    }

    private Compra buscarCompraOuErro(ItemCompra item) {
        if (item.getCompra() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A compra do item é obrigatória");
        }
        return compraRepository.findById(item.getCompra().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Compra não encontrada"));
    }

    // RN008 - cada item precisa de descricao (nome do produto) e quantidade necessaria
    private void validar(ItemCompra item) {

        if (item.getNome_produto() == null || item.getNome_produto().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome do produto é obrigatório");
        }
        if (item.getQuantidade_produto() == null || item.getQuantidade_produto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A quantidade deve ser maior que zero");
        }
    }
}
