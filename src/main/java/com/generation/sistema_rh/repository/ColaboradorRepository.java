package com.generation.sistema_rh.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.generation.sistema_rh.model.Colaborador;

public interface ColaboradorRepository extends JpaRepository<Colaborador, Long> {
	
	List<Colaborador> findAllByNomeContainingIgnoreCase(String nome);

}
