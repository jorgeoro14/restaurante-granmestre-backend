package org.example.restaurante.service;

import org.example.restaurante.dto.ItemPedidoRequestDTO;
import org.example.restaurante.dto.ItemPedidoResponseDTO;
import org.example.restaurante.dto.PedidoRequestDTO;
import org.example.restaurante.dto.PedidoResponseDTO;
import org.example.restaurante.impressao.CupomFormatter;
import org.example.restaurante.impressao.Impressora;
import org.example.restaurante.impressao.ImpressoraException;
import org.example.restaurante.model.Acompanhamento;
import org.example.restaurante.model.ItemPedido;
import org.example.restaurante.model.Pedido;
import org.example.restaurante.model.StatusPedido;
import org.example.restaurante.model.TipoMarmita;
import org.example.restaurante.model.Usuario;
import org.example.restaurante.repository.AcompanhamentoRepository;
import org.example.restaurante.repository.ItemPedidoRepository;
import org.example.restaurante.repository.PedidoRepository;
import org.example.restaurante.repository.TipoMarmitaRepository;
import org.example.restaurante.repository.UsuarioRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final TipoMarmitaRepository tipoMarmitaRepository;
    private final AcompanhamentoRepository acompanhamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final Impressora impressora;
    private final CupomFormatter cupomFormatter;

    public PedidoService(PedidoRepository pedidoRepository,
                         ItemPedidoRepository itemPedidoRepository,
                         TipoMarmitaRepository tipoMarmitaRepository,
                         AcompanhamentoRepository acompanhamentoRepository,
                         UsuarioRepository usuarioRepository,
                         Impressora impressora,
                         CupomFormatter cupomFormatter) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.tipoMarmitaRepository = tipoMarmitaRepository;
        this.acompanhamentoRepository = acompanhamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.impressora = impressora;
        this.cupomFormatter = cupomFormatter;
    }

    public List<PedidoResponseDTO> listarPedidos(StatusPedido status) {

        List<Pedido> pedidos = (status == null)
                ? pedidoRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                : pedidoRepository.findByStatusOrderByIdDesc(status);

        return pedidos.stream().map(this::paraResposta).toList();
    }

    public PedidoResponseDTO buscarPedido(int id) {
        return paraResposta(buscarPedidoOuErro(id));
    }

    // Salva o pedido e todos os itens juntos: se algum item for invalido, nada e gravado
    @Transactional
    public PedidoResponseDTO registrarPedido(PedidoRequestDTO dados, String loginAtendente) {

        if (dados.getTipoMarmitaId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de marmita é obrigatório");
        }

        TipoMarmita tipoMarmita = tipoMarmitaRepository.findById(dados.getTipoMarmitaId())
                .filter(TipoMarmita::isAtivo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Tipo de marmita não encontrado ou inativo"));

        Usuario atendente = usuarioRepository.findByUsuario(loginAtendente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado"));

        List<ItemPedidoRequestDTO> itensEnviados =
                dados.getItens() == null ? List.of() : dados.getItens();

        // valida tudo antes de gravar
        List<ItemPedido> itens = new ArrayList<>();
        for (ItemPedidoRequestDTO enviado : itensEnviados) {

            if (enviado.getAcompanhamentoId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Acompanhamento é obrigatório no item");
            }
            if (enviado.getQuantidade() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A quantidade deve ser maior que zero");
            }

            Acompanhamento acompanhamento = acompanhamentoRepository.findById(enviado.getAcompanhamentoId())
                    .filter(Acompanhamento::isAtivo)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Acompanhamento " + enviado.getAcompanhamentoId() + " não encontrado ou inativo"));

            ItemPedido item = new ItemPedido();
            item.setAcompanhamento(acompanhamento);
            item.setQuantidade(enviado.getQuantidade());
            itens.add(item);
        }

        Pedido pedido = new Pedido();
        pedido.setNumero_pedido(pedidoRepository.proximoNumeroPedido());
        pedido.setData_hora(LocalDateTime.now());
        pedido.setStatus(StatusPedido.REGISTRADO);
        pedido.setTipoMarmita(tipoMarmita);
        pedido.setObservacao(dados.getObservacao());
        pedido.setUsuario(atendente);
        pedido.setImpresso(false);
        pedido = pedidoRepository.save(pedido);

        for (ItemPedido item : itens) {
            item.setPedido(pedido);
        }
        itemPedidoRepository.saveAll(itens);

        return paraResposta(pedido);
    }

    // RN001 - ao enviar para a cozinha o cupom e impresso automaticamente.
    // Se a impressao falhar, o status NAO muda: o pedido continua salvo e o atendente e avisado.
    @Transactional
    public PedidoResponseDTO atualizarStatus(int id, StatusPedido novoStatus) {

        if (novoStatus == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status é obrigatório");
        }

        Pedido pedido = buscarParaAtualizarOuErro(id);

        // so imprime se ainda nao foi impresso, para nao duplicar o cupom na cozinha
        if (novoStatus == StatusPedido.ENVIADO_PARA_COZINHA && !pedido.isImpresso()) {
            imprimirOuErro(pedido, false);
            pedido.setImpresso(true);
        }

        pedido.setStatus(novoStatus);

        return paraResposta(pedidoRepository.save(pedido));
    }

    // Reimpressao manual: o cupom sai marcado como REIMPRESSAO e o status nao muda
    @Transactional
    public PedidoResponseDTO reimprimir(int id) {

        Pedido pedido = buscarParaAtualizarOuErro(id);

        imprimirOuErro(pedido, true);
        pedido.setImpresso(true);

        return paraResposta(pedidoRepository.save(pedido));
    }

    // os itens têm FK para o pedido, entao precisam ser apagados primeiro
    @Transactional
    public void excluirPedido(int id) {
        itemPedidoRepository.deleteAll(itemPedidoRepository.findByPedidoId(id));
        pedidoRepository.deleteById(id);
    }

    private Pedido buscarPedidoOuErro(int id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
    }

    private Pedido buscarParaAtualizarOuErro(int id) {
        return pedidoRepository.buscarParaAtualizar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
    }

    private void imprimirOuErro(Pedido pedido, boolean reimpressao) {
        try {
            impressora.imprimir(cupomFormatter.formatar(paraResposta(pedido), reimpressao));
        } catch (ImpressoraException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Falha na impressão: " + e.getMessage()
                            + ". O pedido continua salvo e nada foi alterado. Tente novamente ou use a reimpressão.");
        }
    }

    private PedidoResponseDTO paraResposta(Pedido pedido) {

        List<ItemPedidoResponseDTO> itens = itemPedidoRepository.findByPedidoId(pedido.getId())
                .stream()
                .map(item -> new ItemPedidoResponseDTO(
                        item.getId(),
                        item.getAcompanhamento().getId(),
                        item.getAcompanhamento().getNome(),
                        item.getQuantidade()))
                .toList();

        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getNumero_pedido(),
                pedido.getData_hora(),
                pedido.getStatus(),
                pedido.getTipoMarmita().getId(),
                pedido.getTipoMarmita().getNome(),
                pedido.getObservacao(),
                pedido.getUsuario() == null ? null : pedido.getUsuario().getNome(),
                pedido.isImpresso(),
                itens
        );
    }
}
