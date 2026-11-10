package br.com.sistema_automotivo.gestao_veiculos.service;

import br.com.sistema_automotivo.gestao_veiculos.dto.VeiculoRequestDTO;
import br.com.sistema_automotivo.gestao_veiculos.dto.AtualizarStatusDTO;
import br.com.sistema_automotivo.gestao_veiculos.exception.RegraNegocioException;
import br.com.sistema_automotivo.gestao_veiculos.model.Marca;
import br.com.sistema_automotivo.gestao_veiculos.model.StatusVeiculo;
import br.com.sistema_automotivo.gestao_veiculos.model.Veiculo;
import br.com.sistema_automotivo.gestao_veiculos.repository.MarcaRepository;
import br.com.sistema_automotivo.gestao_veiculos.repository.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VeiculoFotosTests {

    private VeiculoRepository veiculoRepository;
    private MarcaRepository marcaRepository;
    private VeiculoService service;

    @BeforeEach
    void setUp() {
        veiculoRepository = mock(VeiculoRepository.class);
        marcaRepository = mock(MarcaRepository.class);
        service = new VeiculoService(veiculoRepository, marcaRepository);
        when(marcaRepository.findById(1L)).thenReturn(Optional.of(new Marca("Marca")));
        when(veiculoRepository.save(any(Veiculo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void cadastraAteQuatroLinksEmUmaString() {
        String links = "https://exemplo.com/frente.jpg\nhttps://exemplo.com/lado.jpg";

        var response = service.cadastrar(request(links));

        assertEquals(links, response.fotos());
        assertEquals("Prata", response.cor());
    }

    @Test
    void rejeitaMaisDeQuatroLinks() {
        String links = """
                https://exemplo.com/1.jpg
                https://exemplo.com/2.jpg
                https://exemplo.com/3.jpg
                https://exemplo.com/4.jpg
                https://exemplo.com/5.jpg
                """;

        assertThrows(RegraNegocioException.class, () -> service.cadastrar(request(links)));
    }

    @Test
    void rejeitaLinkForaDeHttpOuHttps() {
        assertThrows(RegraNegocioException.class,
                () -> service.cadastrar(request("javascript:alert(1)")));
    }

    @Test
    void aplicaOsFiltrosSolicitadosNaConsulta() {
        service.buscarComFiltros(
                "civic", 2L, new BigDecimal("50000"), new BigDecimal("100000"),
                2018, 2024, StatusVeiculo.DISPONIVEL);

        verify(veiculoRepository).buscarComFiltros(
                eq("civic"), eq(2L), eq(new BigDecimal("50000")), eq(new BigDecimal("100000")),
                eq(2018), eq(2024), eq(StatusVeiculo.DISPONIVEL));
    }

    @Test
    void rejeitaFaixasInvertidas() {
        assertThrows(RegraNegocioException.class,
                () -> service.buscarComFiltros(
                        null, null, new BigDecimal("100000"), new BigDecimal("50000"),
                        null, null, null));
    }

    @Test
    void mantemVeiculoVendidoParaHistorico() {
        Veiculo vendido = new Veiculo();
        vendido.setStatus(StatusVeiculo.VENDIDO);
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(vendido));

        assertThrows(RegraNegocioException.class, () -> service.excluir(1L));

        verify(veiculoRepository, never()).delete(vendido);
    }

    @Test
    void permiteVenderVeiculoReservado() {
        Veiculo reservado = new Veiculo();
        reservado.setMarca(new Marca("Marca"));
        reservado.setPrecoCompra(new BigDecimal("100.00"));
        reservado.setPrecoVenda(new BigDecimal("200.00"));
        reservado.setStatus(StatusVeiculo.RESERVADO);
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(reservado));

        var response = service.atualizarStatus(1L, new AtualizarStatusDTO(StatusVeiculo.VENDIDO));

        assertEquals(StatusVeiculo.VENDIDO, response.status());
        assertEquals(LocalDate.now(), response.dataVenda());
    }

    private VeiculoRequestDTO request(String fotos) {
        return new VeiculoRequestDTO(
                1L, "Modelo", "Prata", 2024, "ABC1234", "CHASSI123", 0,
                "Flex", "Automático", new BigDecimal("100.00"),
                new BigDecimal("200.00"), fotos);
    }
}
