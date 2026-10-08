package org.example.restaurante.service;

import org.example.restaurante.dto.DashboardComprasDTO;
import org.example.restaurante.dto.DashboardPedidosDTO;
import org.example.restaurante.dto.ItemCompraResumoDTO;
import org.example.restaurante.dto.QuantidadePorTipoDTO;
import org.example.restaurante.model.ItemCompra;
import org.example.restaurante.model.StatusItemCompra;
import org.example.restaurante.model.StatusPedido;
import org.example.restaurante.repository.CompraRepository;
import org.example.restaurante.repository.ItemCompraRepository;
import org.example.restaurante.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Numeros do Dashboard. As definicoes abaixo ainda precisam ser validadas com o PO:
 * - pedido pendente  = ainda nao foi finalizado (REGISTRADO, ENVIADO_PARA_COZINHA, EM_PREPARO, PRONTO)
 * - pedido concluido = FINALIZADO
 * - marmitas vendidas = pedidos do dia (cada pedido tem uma marmita)
 * - compra pendente  = item que ainda nao foi comprado (PENDENTE ou SOLICITADO)
 */
@Service
public class DashboardService {

    private static final Set<StatusPedido> PEDIDOS_CONCLUIDOS = EnumSet.of(StatusPedido.FINALIZADO);
    private static final Set<StatusItemCompra> ITENS_PENDENTES =
            EnumSet.of(StatusItemCompra.PENDENTE, StatusItemCompra.SOLICITADO);

    private final PedidoRepository pedidoRepository;
    private final CompraRepository compraRepository;
    private final ItemCompraRepository itemCompraRepository;

    public DashboardService(PedidoRepository pedidoRepository,
                            CompraRepository compraRepository,
                            ItemCompraRepository itemCompraRepository) {
        this.pedidoRepository = pedidoRepository;
        this.compraRepository = compraRepository;
        this.itemCompraRepository = itemCompraRepository;
    }

    public DashboardPedidosDTO resumoPedidos(LocalDate data) {

        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.plusDays(1).atStartOfDay();

        // todos os status aparecem, mesmo com zero, para o front nao precisar tratar ausencia
        Map<String, Long> porStatus = new LinkedHashMap<>();
        for (StatusPedido status : StatusPedido.values()) {
            porStatus.put(status.name(), 0L);
        }
        for (Object[] linha : pedidoRepository.contarPorStatus(inicio, fim)) {
            porStatus.put(((StatusPedido) linha[0]).name(), (Long) linha[1]);
        }

        long total = 0;
        long concluidos = 0;
        for (StatusPedido status : StatusPedido.values()) {
            long quantidade = porStatus.get(status.name());
            total += quantidade;
            if (PEDIDOS_CONCLUIDOS.contains(status)) {
                concluidos += quantidade;
            }
        }

        List<QuantidadePorTipoDTO> porTipo = pedidoRepository.contarPorTipoMarmita(inicio, fim).stream()
                .map(l -> new QuantidadePorTipoDTO((Integer) l[0], (String) l[1], (Long) l[2]))
                .toList();

        return new DashboardPedidosDTO(data, total, total, total - concluidos, concluidos, porStatus, porTipo);
    }

    public DashboardComprasDTO resumoCompras(LocalDate data) {

        Map<String, Long> porStatus = new LinkedHashMap<>();
        for (StatusItemCompra status : StatusItemCompra.values()) {
            porStatus.put(status.name(), 0L);
        }
        long totalItens = 0;
        for (Object[] linha : itemCompraRepository.contarPorStatus(data)) {
            porStatus.put(((StatusItemCompra) linha[0]).name(), (Long) linha[1]);
            totalItens += (Long) linha[1];
        }

        List<ItemCompra> itens = itemCompraRepository.buscarPorData(data);

        List<ItemCompraResumoDTO> pendentes = itens.stream()
                .filter(i -> ITENS_PENDENTES.contains(i.getStatus()))
                .map(this::paraResumo)
                .toList();

        List<ItemCompraResumoDTO> comprados = itens.stream()
                .filter(i -> i.getStatus() == StatusItemCompra.COMPRADO)
                .map(this::paraResumo)
                .toList();

        return new DashboardComprasDTO(data, compraRepository.contarPorData(data), totalItens,
                porStatus, pendentes, comprados);
    }

    private ItemCompraResumoDTO paraResumo(ItemCompra item) {
        return new ItemCompraResumoDTO(item.getId(), item.getNome_produto(), item.getDescricao_produto(),
                item.getQuantidade_produto(), item.getStatus());
    }
}
