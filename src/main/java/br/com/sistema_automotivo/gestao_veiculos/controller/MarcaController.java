package br.com.sistema_automotivo.gestao_veiculos.controller;

import br.com.sistema_automotivo.gestao_veiculos.dto.MarcaRequestDTO;
import br.com.sistema_automotivo.gestao_veiculos.dto.MarcaResponseDTO;
import br.com.sistema_automotivo.gestao_veiculos.service.MarcaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marcas")
public class MarcaController {

    private final MarcaService marcaService;

    public MarcaController(MarcaService marcaService) {
        this.marcaService = marcaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarcaResponseDTO cadastrar(
        @Valid @RequestBody MarcaRequestDTO dto
    ) {
        return marcaService.cadastrar(dto);
    }

    @GetMapping
    public List<MarcaResponseDTO> listar() {
        return marcaService.listar();
    }

    @GetMapping("/{id}")
    public MarcaResponseDTO buscarPorId(@PathVariable Long id) {
        return marcaService.buscarPorId(id);
    }
}