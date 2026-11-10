package br.com.sistema_automotivo.gestao_veiculos.dto;

import br.com.sistema_automotivo.gestao_veiculos.model.StatusVeiculo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VeiculoResponseDTO(

    Long id,
    Long marcaId,
    String modelo,
    String cor,
    Integer ano,
    String placa,
    String chassi,
    Integer quilometragem,
    String combustivel,
    String cambio,
    BigDecimal precoCompra,
    BigDecimal precoVenda,
    BigDecimal lucroEstimado,
    StatusVeiculo status,
    LocalDate dataEntrada,
    LocalDate dataVenda,
    String fotos

) {
}