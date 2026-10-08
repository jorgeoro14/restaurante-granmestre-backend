package org.example.restaurante.service;

import org.example.restaurante.model.Compra;
import org.example.restaurante.model.ItemCompra;
import org.example.restaurante.model.Usuario;
import org.example.restaurante.repository.CompraRepository;
import org.example.restaurante.repository.ItemCompraRepository;
import org.example.restaurante.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class CompraService {

    private final CompraRepository compraRepository;
    private final ItemCompraRepository itemCompraRepository;
    private final UsuarioRepository usuarioRepository;

    public CompraService(CompraRepository compraRepository,
                         ItemCompraRepository itemCompraRepository,
                         UsuarioRepository usuarioRepository) {
        this.compraRepository = compraRepository;
        this.itemCompraRepository = itemCompraRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Compra> listarCompras() {
        return compraRepository.findAll();
    }

    // O responsavel vem do token e a data e a de hoje, se nao for informada
    public Compra salvarCompra(Compra compra, String loginResponsavel) {

        Usuario responsavel = usuarioRepository.findByUsuario(loginResponsavel)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado"));

        compra.setUsuario(responsavel);

        if (compra.getData_compra() == null) {
            compra.setData_compra(LocalDate.now());
        }

        return compraRepository.save(compra);
    }

    public Compra buscarCompra(int id) {
        return buscarOuErro(id);
    }

    public Compra atualizarCompra(int id, Compra compra) {

        Compra compraExistente = buscarOuErro(id);

        compraExistente.setDescricao(compra.getDescricao());
        compraExistente.setStatus(compra.getStatus());
        if (compra.getData_compra() != null) {
            compraExistente.setData_compra(compra.getData_compra());
        }

        return compraRepository.save(compraExistente);
    }

    // os itens tem FK para a compra, entao saem junto com ela
    @Transactional
    public void excluirCompra(int id) {
        List<ItemCompra> itens = itemCompraRepository.findByCompraId(id);
        itemCompraRepository.deleteAll(itens);
        compraRepository.deleteById(id);
    }

    private Compra buscarOuErro(int id) {
        return compraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra não encontrada"));
    }
}
