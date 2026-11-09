package br.com.sistema_automotivo.gestao_veiculos.service;

import br.com.sistema_automotivo.gestao_veiculos.dto.MarcaRequestDTO;
import br.com.sistema_automotivo.gestao_veiculos.dto.MarcaResponseDTO;
import br.com.sistema_automotivo.gestao_veiculos.exception.RegraNegocioException;
import br.com.sistema_automotivo.gestao_veiculos.model.Marca;
import br.com.sistema_automotivo.gestao_veiculos.repository.MarcaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MarcaService {

    private final MarcaRepository marcaRepository;

    public MarcaService(MarcaRepository marcaRepository) {
        this.marcaRepository = marcaRepository;
    }

    @Transactional
    public MarcaResponseDTO cadastrar(MarcaRequestDTO dto) {

        if (marcaRepository.existsByNomeIgnoreCase(dto.nome())) {
            throw new RegraNegocioException(
                "Esta marca já está cadastrada"
            );
        }

        Marca marca = new Marca(dto.nome().trim());

        Marca marcaSalva = marcaRepository.save(marca);

        return converterParaDTO(marcaSalva);
    }

    @Transactional(readOnly = true)
    public List<MarcaResponseDTO> listar() {

        return marcaRepository.findAll()
            .stream()
            .map(this::converterParaDTO)
            .toList();
    }

    @Transactional(readOnly = true)
    public MarcaResponseDTO buscarPorId(Long id) {

        Marca marca = marcaRepository.findById(id)
            .orElseThrow(() -> new RegraNegocioException(
                "Marca não encontrada"
            ));

        return converterParaDTO(marca);
    }

    private MarcaResponseDTO converterParaDTO(Marca marca) {

        return new MarcaResponseDTO(
            marca.getId(),
            marca.getNome()
        );
    }
}