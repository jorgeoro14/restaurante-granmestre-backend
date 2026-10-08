package org.example.restaurante.repository;

import org.example.restaurante.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface CompraRepository extends JpaRepository<Compra, Integer> {

    @Query("select count(c) from Compra c where c.data_compra = :data")
    long contarPorData(@Param("data") LocalDate data);
}
