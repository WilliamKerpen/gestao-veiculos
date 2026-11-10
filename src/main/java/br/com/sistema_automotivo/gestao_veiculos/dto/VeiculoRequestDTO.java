package br.com.sistema_automotivo.gestao_veiculos.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// record - transferir os dados de forma imutavel
public record VeiculoRequestDTO(

    @NotNull(message = "A marca é obrigatória") 
    Long marcaId, //permite valor null, possibilitando que o @NotNull valide se o usuário realmente informou o campo.

    @NotBlank(message = "O modelo é obrigatório") //@NotBlank: não aceita texto vazio ou espaços
    String modelo,

    @NotBlank(message = "A cor é obrigatória")
    @Size(max = 50, message = "A cor deve ter no máximo 50 caracteres")
    String cor,

    @NotNull(message = "O ano é obrigatório") //@NotNull: não aceita nulo.
    @Min(value = 1900, message = "Ano inválido")
    Integer ano,

    @NotBlank(message = "A placa é obrigatória")
    String placa,

    @NotBlank(message = "O chassi é obrigatório")
    String chassi,

    @NotNull(message = "A quilometragem é obrigatória")
    @Min(value = 0, message = "Quilometragem inválida")
    Integer quilometragem,

    @NotBlank(message = "O combustível é obrigatório")
    String combustivel,

    @NotBlank(message = "O câmbio é obrigatório")
    String cambio,

    @NotNull(message = "O preço de compra é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço de compra inválido")
    BigDecimal precoCompra,

    @NotNull(message = "O preço de venda é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço de venda inválido")
    BigDecimal precoVenda,

    @Size(max = 20003, message = "Os links das fotos excedem o limite permitido")
    String fotos

) {
}