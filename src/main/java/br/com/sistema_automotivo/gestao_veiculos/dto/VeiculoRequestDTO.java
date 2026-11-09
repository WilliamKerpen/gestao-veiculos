package br.com.sistema_automotivo.gestao_veiculos.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

// record - transferir os dados de forma imutavel
public record VeiculoRequestDTO(

    @NotNull(message = "A marca é obrigatória") 
    Long marcaId, //permite valor null, possibilitando que o @NotNull valide se o usuário realmente informou o campo.

    @NotBlank(message = "O modelo é obrigatório") //@NotBlank: não aceita texto vazio ou espaços
    String modelo,

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
    BigDecimal precoVenda

) {
}