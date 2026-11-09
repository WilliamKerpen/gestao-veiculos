/*
Não permitir duas placas iguais.
Não permitir dois chassis iguais.
O preço de venda deve ser superior ao preço de compra.
Todo veículo novo começa como DISPONIVEL.
Não permitir reservar um veículo vendido.
Não permitir vender um veículo que já foi vendido.
Registrar a data de entrada automaticamente.
Registrar a data da venda quando o veículo for vendido.
*/

package br.com.sistema_automotivo.gestao_veiculos.exception;

public class RegraNegocioException
        extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}