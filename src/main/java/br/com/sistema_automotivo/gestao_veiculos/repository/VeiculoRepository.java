//O Spring Data JPA consegue interpretar os nomes dos métodos e gerar as consultas necessárias.
//repository.findAll();
//repository.findById(1L);
//repository.save(veiculo);
//repository.deleteById(1L);

package br.com.sistema_automotivo.gestao_veiculos.repository;

import br.com.sistema_automotivo.gestao_veiculos.model.Veiculo;
import br.com.sistema_automotivo.gestao_veiculos.model.StatusVeiculo;

import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VeiculoRepository
        extends JpaRepository<Veiculo, Long> {

    boolean existsByPlacaIgnoreCase(String placa);

    boolean existsByChassiIgnoreCase(String chassi);

    List<Veiculo> findByStatus(StatusVeiculo status);

    List<Veiculo> findByMarcaIgnoreCase(String marca);

    @Query("""
            select v
            from Veiculo v
            where (:modelo is null or lower(v.modelo) like lower(concat('%', :modelo, '%')))
              and (:marcaId is null or v.marca.id = :marcaId)
              and (:precoMin is null or v.precoVenda >= :precoMin)
              and (:precoMax is null or v.precoVenda <= :precoMax)
              and (:anoMin is null or v.ano >= :anoMin)
              and (:anoMax is null or v.ano <= :anoMax)
              and (:status is null or v.status = :status)
            order by v.dataEntrada desc, v.id desc
            """)
    List<Veiculo> buscarComFiltros(
            @Param("modelo") String modelo,
            @Param("marcaId") Long marcaId,
            @Param("precoMin") BigDecimal precoMin,
            @Param("precoMax") BigDecimal precoMax,
            @Param("anoMin") Integer anoMin,
            @Param("anoMax") Integer anoMax,
            @Param("status") StatusVeiculo status);

}