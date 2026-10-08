package org.example.restaurante.repository;

import org.example.restaurante.model.Acompanhamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcompanhamentoRepository extends JpaRepository<Acompanhamento, Integer> {

    List<Acompanhamento> findByAtivoTrue();
}