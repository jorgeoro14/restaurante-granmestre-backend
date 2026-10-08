package org.example.restaurante.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.restaurante.model.TipoMarmita;

import java.util.List;

public interface TipoMarmitaRepository extends JpaRepository<TipoMarmita, Integer> {

    List<TipoMarmita> findByAtivoTrue();
}