package br.com.sistema_automotivo.gestao_veiculos.service;

import br.com.sistema_automotivo.gestao_veiculos.dto.*;
import br.com.sistema_automotivo.gestao_veiculos.exception.*;
import br.com.sistema_automotivo.gestao_veiculos.model.*;
import br.com.sistema_automotivo.gestao_veiculos.repository.VeiculoRepository;
import br.com.sistema_automotivo.gestao_veiculos.repository.MarcaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class VeiculoService {

    private final VeiculoRepository repository;
    private final MarcaRepository marcaRepository;

    public VeiculoService(VeiculoRepository repository, MarcaRepository marcaRepository) {
        this.repository = repository;
        this.marcaRepository = marcaRepository;
    }

    //buscar marca pelo ID

    private Marca buscarMarca(Long marcaId) {

    return marcaRepository.findById(marcaId)
        .orElseThrow(() -> new RecursoNaoEncontradoException(
            "Marca não encontrada com o ID: " + marcaId
        ));
}

    // Listar todos os veículos

    public List<VeiculoResponseDTO> listarTodos() {

        return repository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    // Buscar veículo pelo ID

    public VeiculoResponseDTO buscarPorId(Long id) {

        Veiculo veiculo = buscarEntidade(id);

        return converterParaDTO(veiculo);
    }

    // Cadastrar veículo

    @Transactional //peração deve acontecer dentro de uma transação do banco de dados, permite realizar o rollback quando uma exceção apropriada ocorre.
    public VeiculoResponseDTO cadastrar(VeiculoRequestDTO dados) {

        validarPlaca(dados.placa());

        validarChassi(dados.chassi());

        validarPrecos(
                dados.precoCompra(),
                dados.precoVenda()
        );
        //buscar a marca pelo ID fornecido no DTO
        Marca marca = buscarMarca(dados.marcaId());
        //criar o objeto Veiculo com a marca encontrada
        Veiculo veiculo = new Veiculo();

        veiculo.setMarca(marca);
        veiculo.setModelo(dados.modelo());
        veiculo.setAno(dados.ano());
        veiculo.setPlaca(dados.placa().trim().toUpperCase());
        veiculo.setChassi(dados.chassi().trim().toUpperCase());
        veiculo.setQuilometragem(dados.quilometragem());
        veiculo.setCombustivel(dados.combustivel());
        veiculo.setCambio(dados.cambio());
        veiculo.setPrecoCompra(dados.precoCompra());
        veiculo.setPrecoVenda(dados.precoVenda());

        veiculo.setStatus(StatusVeiculo.DISPONIVEL);

        veiculo.setDataEntrada(LocalDate.now());

        Veiculo salvo = repository.save(veiculo); // Hibernate gera e executa o comando SQL UPDATE no banco de dados automaticamente, quando é modificado.

        return converterParaDTO(salvo);
    }

    // Atualizar informações do veículo

    @Transactional
    public VeiculoResponseDTO atualizar(
            Long id,
            VeiculoRequestDTO dados) {

        Veiculo veiculo = buscarEntidade(id);

        validarPrecos(
                dados.precoCompra(),
                dados.precoVenda()
        );

        if (!veiculo.getPlaca().equalsIgnoreCase(dados.placa())
                && repository.existsByPlacaIgnoreCase(dados.placa())) {

            throw new RegraNegocioException(
                    "Esta placa já está cadastrada"
            );
        }

        if (!veiculo.getChassi().equalsIgnoreCase(dados.chassi())
                && repository.existsByChassiIgnoreCase(dados.chassi())) {

            throw new RegraNegocioException(
                    "Este chassi já está cadastrado"
            );
        }

        Marca marca = buscarMarca(dados.marcaId());

        veiculo.setMarca(marca);
        veiculo.setModelo(dados.modelo());
        veiculo.setAno(dados.ano());
        veiculo.setPlaca(dados.placa().trim().toUpperCase());
        veiculo.setChassi(dados.chassi().trim().toUpperCase());
        veiculo.setQuilometragem(dados.quilometragem());
        veiculo.setCombustivel(dados.combustivel());
        veiculo.setCambio(dados.cambio());
        veiculo.setPrecoCompra(dados.precoCompra());
        veiculo.setPrecoVenda(dados.precoVenda());

        return converterParaDTO(veiculo);
    }

    // Atualizar status

    @Transactional
    public VeiculoResponseDTO atualizarStatus(
            Long id,
            AtualizarStatusDTO dados) {

        Veiculo veiculo = buscarEntidade(id);

        StatusVeiculo novoStatus = dados.status();

        

        if (veiculo.getStatus() == StatusVeiculo.VENDIDO) {

            throw new RegraNegocioException(
                    "Veículo vendido não pode mudar de status"
            );
        }

        if (novoStatus == StatusVeiculo.DISPONIVEL
                && veiculo.getStatus() == StatusVeiculo.RESERVADO) {

            veiculo.setStatus(StatusVeiculo.DISPONIVEL);

        } else if (novoStatus == StatusVeiculo.RESERVADO
                && veiculo.getStatus() == StatusVeiculo.DISPONIVEL) {

            veiculo.setStatus(StatusVeiculo.RESERVADO);

        } else if (novoStatus == StatusVeiculo.VENDIDO
                && veiculo.getStatus() == StatusVeiculo.DISPONIVEL) {

            veiculo.setStatus(StatusVeiculo.VENDIDO);
            veiculo.setDataVenda(LocalDate.now());

        } else {

            throw new RegraNegocioException(
                    "Transição de status não permitida"
            );
        }

        return converterParaDTO(veiculo);
    }

    // Excluir veículo

    @Transactional
    public void excluir(Long id) {

        Veiculo veiculo = buscarEntidade(id);

        if (veiculo.getStatus() == StatusVeiculo.VENDIDO) {

            throw new RegraNegocioException(
                    "Não é permitido excluir um veículo vendido"
            );
        }

        repository.delete(veiculo);
    }

    // Consultar veículos disponíveis

    public List<VeiculoResponseDTO> listarDisponiveis() {

        return repository.findByStatus(StatusVeiculo.DISPONIVEL)
                .stream() // cria um fluxo para processar a coleção
                .map(this::converterParaDTO) // ransforma cada veículo em um DTO.
                .toList(); // reúne os resultados em uma lista.
    }

    // Calcular valor total do estoque disponível

    public BigDecimal calcularValorEstoque() {

        return repository.findByStatus(StatusVeiculo.DISPONIVEL)
                .stream()
                .map(Veiculo::getPrecoCompra)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Buscar entidade pelo ID

    private Veiculo buscarEntidade(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Veículo não encontrado: " + id
                        )
                );
    }

    // Validar placa

    private void validarPlaca(String placa) {

        if (repository.existsByPlacaIgnoreCase(placa.trim())) {

            throw new RegraNegocioException(
                    "Esta placa já está cadastrada"
            );
        }
    }

    // Validar chassi

    private void validarChassi(String chassi) {

        if (repository.existsByChassiIgnoreCase(chassi.trim())) {

            throw new RegraNegocioException(
                    "Este chassi já está cadastrado"
            );
        }
    }

    // Validar preços

    private void validarPrecos(
            BigDecimal precoCompra,
            BigDecimal precoVenda) {

        if (precoVenda.compareTo(precoCompra) <= 0) { //BigDecimal é um objeto e nao tipo primitivo, erro com  ><=; deve ser com compareTo

            throw new RegraNegocioException(
                    "O preço de venda deve ser superior ao preço de compra"
            );
        }
    }

    // Converter Entity para DTO

    private VeiculoResponseDTO converterParaDTO(Veiculo veiculo) {

        BigDecimal lucroEstimado =
                veiculo.getPrecoVenda()
                        .subtract(veiculo.getPrecoCompra());

        return new VeiculoResponseDTO(

                veiculo.getId(),
                veiculo.getMarca().getId(),
                veiculo.getModelo(),
                veiculo.getAno(),
                veiculo.getPlaca(),
                veiculo.getChassi(),
                veiculo.getQuilometragem(),
                veiculo.getCombustivel(),
                veiculo.getCambio(),
                veiculo.getPrecoCompra(),
                veiculo.getPrecoVenda(),
                lucroEstimado,
                veiculo.getStatus(),
                veiculo.getDataEntrada(),
                veiculo.getDataVenda()
        );
    }
}