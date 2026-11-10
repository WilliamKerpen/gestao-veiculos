package br.com.sistema_automotivo.gestao_veiculos.controller;

import java.math.BigDecimal;
import java.util.List;

import br.com.sistema_automotivo.gestao_veiculos.exception.RegraNegocioException;
import br.com.sistema_automotivo.gestao_veiculos.model.StatusVeiculo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.sistema_automotivo.gestao_veiculos.dto.AtualizarStatusDTO;
import br.com.sistema_automotivo.gestao_veiculos.dto.VeiculoRequestDTO;
import br.com.sistema_automotivo.gestao_veiculos.dto.VeiculoResponseDTO;
import br.com.sistema_automotivo.gestao_veiculos.service.VeiculoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {

    private final VeiculoService service;

    public VeiculoController(VeiculoService service) {
        this.service = service;
    }

    // Listar todos

    @GetMapping
    public ResponseEntity<List<VeiculoResponseDTO>> listar(
            @RequestParam(required = false) String modelo,
            @RequestParam(required = false) Long marcaId,
            @RequestParam(required = false) BigDecimal precoMin,
            @RequestParam(required = false) BigDecimal precoMax,
            @RequestParam(required = false) Integer anoMin,
            @RequestParam(required = false) Integer anoMax,
            @RequestParam(required = false) StatusVeiculo status) {
        if ((precoMin != null && precoMin.signum() < 0)
                || (precoMax != null && precoMax.signum() < 0)) {
            throw new RegraNegocioException("Os preços para filtro não podem ser negativos");
        }
        if ((anoMin != null && anoMin < 1900) || (anoMax != null && anoMax < 1900)) {
            throw new RegraNegocioException("O ano para filtro deve ser igual ou maior que 1900");
        }
        return ResponseEntity.ok(service.buscarComFiltros(
                modelo, marcaId, precoMin, precoMax, anoMin, anoMax, status));
    }

    // Buscar por ID

    @GetMapping("/{id}")
    public ResponseEntity<VeiculoResponseDTO> buscar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.buscarPorId(id)
        );
    }

    // Cadastrar veículo

    @PostMapping
    public ResponseEntity<VeiculoResponseDTO> cadastrar(
            @RequestBody @Valid VeiculoRequestDTO dados) {

        VeiculoResponseDTO veiculo = service.cadastrar(dados);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(veiculo);
    }

    // Atualizar veículo

    @PutMapping("/{id}")
    public ResponseEntity<VeiculoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid VeiculoRequestDTO dados) {

        return ResponseEntity.ok(
                service.atualizar(id, dados)
        );
    }

    // Alterar status

    @PatchMapping("/{id}/status")
    public ResponseEntity<VeiculoResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestBody @Valid AtualizarStatusDTO dados) {

        return ResponseEntity.ok(
                service.atualizarStatus(id, dados)
        );
    }

    // Excluir veículo

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id) {

        service.excluir(id);

        return ResponseEntity.noContent().build();
    }

    // Listar disponíveis

    @GetMapping("/disponiveis")
    public ResponseEntity<List<VeiculoResponseDTO>> listarDisponiveis() {

        return ResponseEntity.ok(
                service.listarDisponiveis()
        );
    }

    // Valor total do estoque disponível

    @GetMapping("/estoque/valor")
    public ResponseEntity<BigDecimal> valorEstoque() {

        return ResponseEntity.ok(
                service.calcularValorEstoque()
        );
    }
}