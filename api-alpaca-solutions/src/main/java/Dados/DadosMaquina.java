package Dados;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Disco;
import com.github.britooo.looca.api.group.memoria.Memoria;
import com.github.britooo.looca.api.group.processador.Processador;
import com.github.britooo.looca.api.group.sistema.Sistema;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DadosMaquina {

    // codigo ta documentado para que todo mundo entenda
    public static void main(String[] args) {
        // Inicializar Looca para obter informações do sistema
        Looca looca = new Looca();

        // Inicializar a conexão com o banco de dados
        Conexao conexao = new Conexao();
        JdbcTemplate con = conexao.getConexaoDoBanco();

        // Obter informações do sistema
        Sistema sistema = new Sistema();

        // Obter informações do processador
        Processador processador = new Processador();

        // Variáveis para armazenar os valores calculados
        double tamanhoTotalGiB = 0;
        Double tot_disco = 0.0;
        Integer total_disco = 0;
        Double tamanho_disco = 0.0;

        // Obter informações da memória
        Memoria memoria = new Memoria();
        Double percentual_de_uso_mem = (double) memoria.getEmUso() / memoria.getTotal() * 100;
        Double ramDisponivel = (double) memoria.getDisponivel() / (1024 * 1024 * 1024);
        Double porcentagem_uso_cpu = processador.getUso() / processador.getNumeroCpusFisicas();

        // Obter informações sobre os discos
        for (Disco disco : looca.getGrupoDeDiscos().getDiscos()) {
            tamanhoTotalGiB = (double) disco.getTamanho() / (1024 * 1024 * 1024);
            tot_disco = Math.round(tamanhoTotalGiB * 100.0) / 100.0;
            tamanho_disco = Double.valueOf(disco.getTamanho());
            total_disco = (int) Math.round(tamanhoTotalGiB);
        }

        System.out.println("Inserindo os dados da máquina");
        try {
            // Arredondar valores e converter para BigDecimal quando necessário
            Double total_pro = processador.getUso();
            BigDecimal porcentagem_uso_disco = BigDecimal.valueOf(total_pro).setScale(2, RoundingMode.HALF_UP);
            BigDecimal porcentagem_uso_memoria = BigDecimal.valueOf(percentual_de_uso_mem).setScale(2 , RoundingMode.HALF_UP);
            BigDecimal quantidade_de_ram = BigDecimal.valueOf(ramDisponivel).setScale(2 , RoundingMode.HALF_UP);

            // Converter memória disponível para gigabytes
            Double memoria_disponivel = memoria.getDisponivel() / (1024 * 1024 * 1024.0);
            memoria_disponivel = Math.round(memoria_disponivel * 100.0) / 100.0;

            BigDecimal porcentagem_de_uso_da_cpu = BigDecimal.valueOf(porcentagem_uso_cpu).setScale(2 , RoundingMode.HALF_UP);

            // Arredondar tamanho disponível do disco e converter para gigabytes
            BigDecimal tamanho_disponivel_do_disco = new BigDecimal(tamanho_disco)
                    .setScale(2, RoundingMode.HALF_UP)
                    .divide(new BigDecimal(1024 * 1024 * 1024), 2, RoundingMode.HALF_UP);

            // Converter memória total para gigabytes
            Double memoria_total = memoria.getDisponivel() / (1024 * 1024 * 1024.0);
            memoria_total = Math.round(memoria_total * 100.0) / 100.0;

            System.out.println(
                    String.format(
                            """
                            Porcentagem de Uso do Disco %s:
                            Porcentagem do Uso de Memória: %s
                            Quantidade de Ram Disponível: %s 
                            Memoria Disponível: %s
                            Tamanho Total do Disco: %s
                            Porcentagem de Uso da CPU: %s
                            Tamanho disponivel do disco: %s
                            Memoria Total: %s
                            """, porcentagem_uso_disco, porcentagem_uso_memoria,
                            quantidade_de_ram, memoria_disponivel, tot_disco, porcentagem_de_uso_da_cpu, tamanho_disponivel_do_disco, memoria_total));

            // Inserir os dados no banco de dados
            con.update("insert into servidor (porcentagem_uso_disco, porcentagem_uso_memoria, quantidade_de_ram, memoria_disponivel, tamanho_total_disco, porcentagem_uso_cpu, tamanho_disponivel_do_disco, memoria_total) values (?, ?, ?, ?, ?, ?, ?, ?)",
                    porcentagem_uso_disco, porcentagem_uso_memoria,
                    quantidade_de_ram, memoria_disponivel, tot_disco, porcentagem_de_uso_da_cpu, tamanho_disponivel_do_disco, memoria_total);

            System.out.println("Inserido Com Sucesso");
        } catch (Error erro) {
            System.out.println("Deu Erro , Se lascou");
        }
    }
}
