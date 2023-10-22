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
        // Inicializa a conexão com o banco de dados
        Conexao conexao = new Conexao();
        JdbcTemplate con = conexao.getConexaoDoBanco();
        Scanner scanner = new Scanner(System.in);
        String continuar;
        Rede rede02 = new Rede();

        // Cria a tabela 'servidor' se ela não existir
        con.update("create table IF NOT EXISTS servidor (\n" +
                "idservidor int primary key auto_increment,\n" +
                "porcentagem_uso_disco decimal,\n" +
                "porcentagem_uso_memoria decimal,\n" +
                "quantidade_de_ram decimal,\n" +
                "memoria_disponivel decimal,\n" +
                "tamanho_total_disco decimal,\n" +
                "porcentagem_uso_cpu decimal,\n" +
                "tamanho_disponivel_do_disco decimal,\n" +
                "memoria_total decimal,\n" +
                "quantidade_de_bytes_recebidos decimal,\n" +
                "quantidade_de_bytes_enviados decimal,\n" +
                "dthora datetime default current_timestamp\n" +
                ");");

        do {
            Looca looca = new Looca();
            Sistema sistema = new Sistema();
            Processador processador = new Processador();
            Memoria memoria = new Memoria();
            double tamanhoTotalGiB = 0;
            double tot_disco = 0.0;
            int total_disco = 0;
            double tamanho_disco = 0.0;

            // Coleta informações sobre os discos
            for (Disco disco : looca.getGrupoDeDiscos().getDiscos()) {
                tamanhoTotalGiB = (double) disco.getTamanho() / (1024 * 1024 * 1024);
                tot_disco = (double) Math.round(tamanhoTotalGiB * 100) / 100;
                tamanho_disco = Double.valueOf(disco.getTamanho());
                total_disco = (int) Math.round(tamanhoTotalGiB * 100) / 100;
            }

            Double total_pro = processador.getUso();
            Double porcentagem_uso_memoria = (double) memoria.getEmUso() / memoria.getTotal() * 100;
            Double quantidade_de_ram = (double) memoria.getDisponivel() / (1024 * 1024 * 1024);
            Double porcentagem_de_uso_da_cpu = processador.getUso() / processador.getNumeroCpusFisicas();
            Double tamanho_disponivel_do_disco = tamanho_disco / (1024 * 1024 * 1024);
            Double memoria_total = (double) memoria.getTotal() / (1024 * 1024 * 1024.0);

            List<RedeInterface> dados_de_rede = looca.getRede().getGrupoDeInterfaces().getInterfaces();
            RedeInterface adaptador_com_dados = null;
            long maiorBytesrecebidos = 0;
            long total_bytes_recebidos = 0;
            long total_bytes_enviados = 0;

            // Coleta informações de rede
            for (RedeInterface interfaceRede : dados_de_rede) {
                long bytes_recebidos = interfaceRede.getBytesRecebidos();
                if (bytes_recebidos > maiorBytesrecebidos) {
                    adaptador_com_dados = interfaceRede;
                    maiorBytesrecebidos = bytes_recebidos;
                }
            }

            long memoria_disponivel = memoria.getDisponivel();
            long total_memoria = memoria.getTotal();
            total_bytes_enviados = adaptador_com_dados.getBytesEnviados();
            total_bytes_recebidos = adaptador_com_dados.getBytesRecebidos();
            long qtd_nucleos_cpu = processador.getNumeroCpusFisicas() + processador.getNumeroCpusLogicas();

            // Cria objetos com os dados coletados
            Rede rede01 = new Rede(total_bytes_recebidos, total_bytes_enviados);
            dados.Memoria memoria01 = new dados.Memoria(porcentagem_uso_memoria, memoria_disponivel, total_memoria, quantidade_de_ram);
            dados.CPU cpu01 = new dados.CPU(porcentagem_de_uso_da_cpu, qtd_nucleos_cpu);
            dados.Disco disco01 = new dados.Disco(total_pro, tot_disco, tamanho_disponivel_do_disco);

            // Exibe os dados coletados
            System.out.println(memoria01);
            System.out.println(rede01);
            System.out.println(disco01);
            System.out.println(cpu01);

            // Insere os dados no banco de dados
            con.update("insert into servidor (porcentagem_uso_disco, porcentagem_uso_memoria, quantidade_de_ram, memoria_disponivel, tamanho_total_disco, porcentagem_uso_cpu, tamanho_disponivel_do_disco, memoria_total," +
                            "quantidade_de_bytes_recebidos, quantidade_de_bytes_enviados) values (?, ?, ?, ?, ?, ?, ?, ? , ? , ?)",
                    disco01.getPorcentagem_de_Uso_do_Disco(), memoria01.getPorcentagem_uso_memoria(), memoria01.getQuantidade_de_ram(),
                    memoria01.getQtd_memoria_disponivel(), disco01.getTamanho_Total_do_Disco(),
                    cpu01.getUso_da_cpu(), disco01.getTamanho_Total_do_Disco(), memoria01.getTamanho_memoria(),
                    rede01.getQuantidade_bytes_recebidos(), rede01.getQuantidade_bytes_enviados());
        } while (true);
    }
}
