package br.com.sistema_automotivo.gestao_veiculos.dto;

import br.com.sistema_automotivo.gestao_veiculos.model.StatusVeiculo;

import jakarta.validation.constraints.NotNull;  

public record AtualizarStatusDTO(

    @NotNull(message = "O status é obrigatório")
    StatusVeiculo status

) {
}