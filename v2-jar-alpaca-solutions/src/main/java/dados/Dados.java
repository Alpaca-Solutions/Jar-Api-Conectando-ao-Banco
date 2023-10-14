package dados;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Disco;
import com.github.britooo.looca.api.group.memoria.Memoria;
import com.github.britooo.looca.api.group.processador.Processador;
import com.github.britooo.looca.api.group.rede.RedeInterface;
import com.github.britooo.looca.api.group.sistema.Sistema;
import org.springframework.jdbc.core.JdbcTemplate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Scanner;


public class Dados {
    public static void main(String[] args) {
        Conexao conexao = new Conexao();
        JdbcTemplate con = conexao.getConexaoDoBanco();
        Scanner scanner = new Scanner(System.in);
        String continuar;


        con.update("create table IF NOT EXISTS servidor (\n" +
                "idservidor int primary key auto_increment,\n" +
                "porcentagem_uso_disco decimal(10 , 2),\n" +
                "porcentagem_uso_memoria decimal(10 , 2),\n" +
                "quantidade_de_ram decimal(10 , 2),\n" +
                "memoria_disponivel decimal(10 , 2),\n" +
                "tamanho_total_disco decimal(10 , 2),\n" +
                "porcentagem_uso_cpu decimal(10 , 2),\n" +
                "tamanho_disponivel_do_disco decimal(10 , 2),\n" +
                "memoria_total decimal(10 , 2),\n" +
                "quantidade_de_bytes_recebidos long,\n" +
                "quantidade_de_bytes_enviados long,\n" +
                "dthora datetime default current_timestamp\n" +
                ");");


        do {
            Looca looca = new Looca();
            Sistema sistema = new Sistema();
            Processador processador = new Processador();
            Memoria memoria = new Memoria();
            double tamanhoTotalGiB = 0;
            Double tot_disco = 0.0;
            Integer total_disco = 0;
            Double tamanho_disco = 0.0;

            for (Disco disco : looca.getGrupoDeDiscos().getDiscos()) {
                tamanhoTotalGiB = (double) disco.getTamanho() / (1024 * 1024 * 1024);
                tot_disco = Math.round(tamanhoTotalGiB * 100.0) / 100.0;
                tamanho_disco = Double.valueOf(disco.getTamanho());
                total_disco = (int) Math.round(tamanhoTotalGiB);
            }

            Double total_pro = processador.getUso();
            BigDecimal porcentagem_uso_disco = BigDecimal.valueOf(total_pro).setScale(2, RoundingMode.HALF_UP);
            BigDecimal porcentagem_uso_memoria = BigDecimal.valueOf((double) memoria.getEmUso() / memoria.getTotal() * 100).setScale(2, RoundingMode.HALF_UP);
            BigDecimal quantidade_de_ram = BigDecimal.valueOf((double) memoria.getDisponivel() / (1024 * 1024 * 1024)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal porcentagem_de_uso_da_cpu = BigDecimal.valueOf(processador.getUso() / processador.getNumeroCpusFisicas()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal tamanho_disponivel_do_disco = new BigDecimal(tamanho_disco)
                    .setScale(2, RoundingMode.HALF_UP)
                    .divide(new BigDecimal(1024 * 1024 * 1024), 2, RoundingMode.HALF_UP);
            Double memoria_total = (double) memoria.getTotal() / (1024 * 1024 * 1024.0);

            List<RedeInterface> interfaces = looca.getRede().getGrupoDeInterfaces().getInterfaces();
            long pacotesRecebidosWlan6 = -1;
            long pacotesEnviadosWlan6 = -1;

            for (RedeInterface interfaceRede : interfaces) {
                if ("wlan6".equals(interfaceRede.getNome())) {
                    pacotesRecebidosWlan6 = interfaceRede.getPacotesRecebidos();
                    pacotesEnviadosWlan6 = interfaceRede.getPacotesEnviados();
                    break;
                }
            }

            System.out.println("Deseja continuar inserindo dados? Digite '1' para continuar ou qualquer outra tecla para sair: ");
            continuar = scanner.nextLine();

            if (!"1".equals(continuar)) {
                break;
            }

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
                                    Memoria Total: %s,
                                    Quantidade de Bytes Recebidos: %d
                                    Quantidade de Bytes Enviados: %d
                                    """, porcentagem_uso_disco, porcentagem_uso_memoria,
                            quantidade_de_ram, (double) memoria.getDisponivel() / (1024 * 1024 * 1024.0),
                            tot_disco, porcentagem_de_uso_da_cpu, tamanho_disponivel_do_disco, memoria_total, pacotesRecebidosWlan6, pacotesEnviadosWlan6));

            // Inserir os dados no banco de dados
            con.update("insert into servidor (porcentagem_uso_disco, porcentagem_uso_memoria, quantidade_de_ram, memoria_disponivel, tamanho_total_disco, porcentagem_uso_cpu, tamanho_disponivel_do_disco, memoria_total," +
                            "quantidade_de_bytes_recebidos, quantidade_de_bytes_enviados) values (?, ?, ?, ?, ?, ?, ?, ? , ? , ?)",
                    porcentagem_uso_disco, porcentagem_uso_memoria,
                    quantidade_de_ram, (double) memoria.getDisponivel() / (1024 * 1024 * 1024.0), tot_disco, porcentagem_de_uso_da_cpu, tamanho_disponivel_do_disco, memoria_total,
                    pacotesRecebidosWlan6, pacotesEnviadosWlan6);

            System.out.println("Inserido Com Sucesso");
        } while (true);
    }
}
