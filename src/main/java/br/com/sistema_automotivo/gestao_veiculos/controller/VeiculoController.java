package br.com.sistema_automotivo.gestao_veiculos.controller;

import br.com.sistema_automotivo.gestao_veiculos.dto.*;
import br.com.sistema_automotivo.gestao_veiculos.service.VeiculoService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {

    private final VeiculoService service;

    public VeiculoController(VeiculoService service) {
        this.service = service;
    }

    // Listar todos

    @GetMapping
    public ResponseEntity<List<VeiculoResponseDTO>> listar() {

        return ResponseEntity.ok(
                service.listarTodos()
        );
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