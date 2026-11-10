package br.com.sistema_automotivo.gestao_veiculos.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "veiculos")
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // relacionamento muitos para um com a tabela marcas
    @JoinColumn(name = "marca_id", nullable = false)
    private Marca marca;

    @Column(nullable = false)
    private String modelo;

    @Column(length = 50)
    private String cor;

    @Column(nullable = false)
    private Integer ano;

    @Column(nullable = false, unique = true) // Placa e chassi devem ser unicos
    private String placa;

    @Column(nullable = false, unique = true)
    private String chassi;

    private Integer quilometragem;

    private String combustivel;

    private String cambio;

    @Column(nullable = false, precision = 12, scale = 2) // ex: 9999999999.99
    private BigDecimal precoCompra;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precoVenda;

    @Enumerated(EnumType.STRING) // diz ao  Hibernate que queremos armazenar o nome do enum no banco
    @Column(nullable = false)
    private StatusVeiculo status;

    private LocalDate dataEntrada; // data local sem horario

    private LocalDate dataVenda;

    @Column(columnDefinition = "TEXT")
    private String fotos;

    // Construtor vazio exigido pelo JPA

    public Veiculo() {
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getChassi() {
        return chassi;
    }

    public void setChassi(String chassi) {
        this.chassi = chassi;
    }

    public Integer getQuilometragem() {
        return quilometragem;
    }

    public void setQuilometragem(Integer quilometragem) {
        this.quilometragem = quilometragem;
    }

    public String getCombustivel() {
        return combustivel;
    }

    public void setCombustivel(String combustivel) {
        this.combustivel = combustivel;
    }

    public String getCambio() {
        return cambio;
    }

    public void setCambio(String cambio) {
        this.cambio = cambio;
    }

    public BigDecimal getPrecoCompra() {
        return precoCompra;
    }

    public void setPrecoCompra(BigDecimal precoCompra) {
        this.precoCompra = precoCompra;
    }

    public BigDecimal getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(BigDecimal precoVenda) {
        this.precoVenda = precoVenda;
    }

    public StatusVeiculo getStatus() {
        return status;
    }

    public void setStatus(StatusVeiculo status) {
        this.status = status;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDate dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public LocalDate getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDate dataVenda) {
        this.dataVenda = dataVenda;
    }

    public String getFotos() {
        return fotos;
    }

    public void setFotos(String fotos) {
        this.fotos = fotos;
    }
}