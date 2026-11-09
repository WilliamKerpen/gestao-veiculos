package br.com.sistema_automotivo.gestao_veiculos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MarcaRequestDTO(

    @NotBlank(message = "O nome da marca é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    String nome

) {
}