package org.example.restaurante.repository;

import org.example.restaurante.model.Pedido;
import org.example.restaurante.model.StatusPedido;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByStatusOrderByIdDesc(StatusPedido status);

    // Dashboard: pedidos do periodo agrupados por status e por tipo de marmita
    @Query("select p.status, count(p) from Pedido p where p.data_hora >= :inicio and p.data_hora < :fim group by p.status")
    List<Object[]> contarPorStatus(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("select p.tipoMarmita.id, p.tipoMarmita.nome, count(p) from Pedido p "
            + "where p.data_hora >= :inicio and p.data_hora < :fim "
            + "group by p.tipoMarmita.id, p.tipoMarmita.nome order by count(p) desc, p.tipoMarmita.nome")
    List<Object[]> contarPorTipoMarmita(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    // Trava a linha do pedido: duas requisicoes ao mesmo tempo nao imprimem duas vezes
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Pedido p where p.id = :id")
    Optional<Pedido> buscarParaAtualizar(@Param("id") int id);

    // RN002 - o numero vem de uma sequence do PostgreSQL, entao nunca se repete
    @Query(value = "select nextval('transacoes.pedido_numero_seq')", nativeQuery = true)
    int proximoNumeroPedido();
}
