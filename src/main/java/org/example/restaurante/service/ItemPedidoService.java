package org.example.restaurante.service;

import org.example.restaurante.model.Acompanhamento;
import org.example.restaurante.model.ItemPedido;
import org.example.restaurante.model.Pedido;
import org.example.restaurante.repository.AcompanhamentoRepository;
import org.example.restaurante.repository.ItemPedidoRepository;
import org.example.restaurante.repository.PedidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Os itens normalmente nascem junto com o pedido (POST /pedidos).
 * Este servico e o acesso direto, restrito ao ADMIN, e valida tudo antes de gravar.
 */
@Service
public class ItemPedidoService {

    private final ItemPedidoRepository itemPedidoRepository;
    private final PedidoRepository pedidoRepository;
    private final AcompanhamentoRepository acompanhamentoRepository;

    public ItemPedidoService(ItemPedidoRepository itemPedidoRepository,
                             PedidoRepository pedidoRepository,
                             AcompanhamentoRepository acompanhamentoRepository) {
        this.itemPedidoRepository = itemPedidoRepository;
        this.pedidoRepository = pedidoRepository;
        this.acompanhamentoRepository = acompanhamentoRepository;
    }

    public List<ItemPedido> listarItensPedido() {
        return itemPedidoRepository.findAll();
    }

    public ItemPedido salvarItemPedido(ItemPedido itemPedido) {
        validar(itemPedido);
        return itemPedidoRepository.save(itemPedido);
    }

    public ItemPedido buscarItemPedido(int id) {
        return buscarOuErro(id);
    }

    public ItemPedido atualizarItemPedido(int id, ItemPedido itemPedido) {

        ItemPedido itemExistente = buscarOuErro(id);

        validar(itemPedido);

        itemExistente.setPedido(itemPedido.getPedido());
        itemExistente.setAcompanhamento(itemPedido.getAcompanhamento());
        itemExistente.setQuantidade(itemPedido.getQuantidade());

        return itemPedidoRepository.save(itemExistente);
    }

    public void excluirItemPedido(int id) {
        itemPedidoRepository.deleteById(id);
    }

    private ItemPedido buscarOuErro(int id) {
        return itemPedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item de pedido não encontrado"));
    }

    // troca os objetos recebidos (so com o id) pelos registros reais do banco
    private void validar(ItemPedido item) {

        if (item.getQuantidade() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A quantidade deve ser maior que zero");
        }
        if (item.getPedido() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O pedido do item é obrigatório");
        }
        if (item.getAcompanhamento() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O acompanhamento do item é obrigatório");
        }

        Pedido pedido = pedidoRepository.findById(item.getPedido().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pedido não encontrado"));

        Acompanhamento acompanhamento = acompanhamentoRepository.findById(item.getAcompanhamento().getId())
                .filter(Acompanhamento::isAtivo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Acompanhamento não encontrado ou inativo"));

        item.setPedido(pedido);
        item.setAcompanhamento(acompanhamento);
    }
}
