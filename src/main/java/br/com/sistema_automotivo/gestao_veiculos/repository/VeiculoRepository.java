//O Spring Data JPA consegue interpretar os nomes dos métodos e gerar as consultas necessárias.
//repository.findAll();
//repository.findById(1L);
//repository.save(veiculo);
//repository.deleteById(1L);

package br.com.sistema_automotivo.gestao_veiculos.repository;

import br.com.sistema_automotivo.gestao_veiculos.model.Veiculo;
import br.com.sistema_automotivo.gestao_veiculos.model.StatusVeiculo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VeiculoRepository
        extends JpaRepository<Veiculo, Long> {

    boolean existsByPlacaIgnoreCase(String placa);

    boolean existsByChassiIgnoreCase(String chassi);

    List<Veiculo> findByStatus(StatusVeiculo status);

    List<Veiculo> findByMarcaIgnoreCase(String marca);

}