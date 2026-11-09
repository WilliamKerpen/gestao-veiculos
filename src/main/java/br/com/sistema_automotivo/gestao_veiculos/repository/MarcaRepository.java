package br.com.sistema_automotivo.gestao_veiculos.repository;

import br.com.sistema_automotivo.gestao_veiculos.model.Marca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarcaRepository extends JpaRepository<Marca, Long> {

    Optional<Marca> findByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCase(String nome);
}