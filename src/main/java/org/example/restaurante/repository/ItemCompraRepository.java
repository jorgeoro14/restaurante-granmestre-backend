package org.example.restaurante.repository;

import org.example.restaurante.model.ItemCompra;
import org.example.restaurante.model.StatusItemCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ItemCompraRepository extends JpaRepository<ItemCompra, Integer> {

    List<ItemCompra> findByCompraId(int compraId);

    List<ItemCompra> findByStatusOrderByIdAsc(StatusItemCompra status);

    // itens das compras feitas em uma data (a "lista do dia")
    @Query("select i from ItemCompra i where i.compra.data_compra = :data order by i.id")
    List<ItemCompra> buscarPorData(@Param("data") LocalDate data);

    @Query("select i from ItemCompra i where i.compra.data_compra = :data and i.status = :status order by i.id")
    List<ItemCompra> buscarPorDataEStatus(@Param("data") LocalDate data, @Param("status") StatusItemCompra status);

    // Dashboard: itens das compras de uma data, agrupados por status
    @Query("select i.status, count(i) from ItemCompra i where i.compra.data_compra = :data group by i.status")
    List<Object[]> contarPorStatus(@Param("data") LocalDate data);
}
