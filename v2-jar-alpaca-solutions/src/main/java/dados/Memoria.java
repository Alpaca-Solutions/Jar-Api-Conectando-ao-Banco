package dados;

import java.math.BigDecimal;

public class Memoria {

    private Double porcentagem_uso_memoria;

    private long qtd_memoria_disponivel;

    private long tamanho_memoria;

    private Double quantidade_de_ram;

    public Memoria(Double porcentagem_uso_memoria, long qtd_memoria_disponivel, long tamanho_memoria, Double quantidade_de_ram) {
        this.porcentagem_uso_memoria = porcentagem_uso_memoria;
        this.qtd_memoria_disponivel = qtd_memoria_disponivel;
        this.tamanho_memoria = tamanho_memoria;
        this.quantidade_de_ram = quantidade_de_ram;
    }

    public Double getPorcentagem_uso_memoria() {
        return porcentagem_uso_memoria;
    }

    public void setPorcentagem_uso_memoria(Double porcentagem_uso_memoria) {
        this.porcentagem_uso_memoria = porcentagem_uso_memoria;
    }

    public long getQtd_memoria_disponivel() {
        return qtd_memoria_disponivel;
    }

    public void setQtd_memoria_disponivel(long qtd_memoria_disponivel) {
        this.qtd_memoria_disponivel = qtd_memoria_disponivel;
    }

    public long getTamanho_memoria() {
        return tamanho_memoria;
    }

    public void setTamanho_memoria(long tamanho_memoria) {
        this.tamanho_memoria = tamanho_memoria;
    }

    public Double getQuantidade_de_ram() {
        return quantidade_de_ram;
    }

    public void setQuantidade_de_ram(Double quantidade_de_ram) {
        this.quantidade_de_ram = quantidade_de_ram;
    }

    @Override
    public String toString() {
        return String.format("Dados da Memoria\n" +
                "Porcentagem de uso da memoria:%.2f%%,\n" +
                "Quantidade de memoria Disponivel:%d GB,\n" +
                "Tamanho da Memoria:%d GB,\n" +
                "Quantidade de ram:%.2f GB\n", porcentagem_uso_memoria, qtd_memoria_disponivel, tamanho_memoria, quantidade_de_ram);
    }



}
