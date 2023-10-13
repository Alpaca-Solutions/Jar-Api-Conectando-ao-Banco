package dados;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Disco;
import com.github.britooo.looca.api.group.memoria.Memoria;
import com.github.britooo.looca.api.group.processador.Processador;
import com.github.britooo.looca.api.group.rede.RedeInterface;
import com.github.britooo.looca.api.group.sistema.Sistema;
import org.springframework.jdbc.core.JdbcTemplate;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class DadosMaquina {

    // codigo ta documentado para que todo mundo entenda
    public static void main(String[] args) {
        // Inicializar Looca para obter informações do sistema
        Looca looca = new Looca();
        JFrame frame = new JFrame("Informações da Máquina");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);


        // Cria um botão

        JPanel panel = new JPanel();
        frame.add(panel);


        // Cria uma área de texto para exibir informações
        JTextArea textArea = new JTextArea(10, 30);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);


        JButton button = new JButton("Obter Informações da Máquina");




        // Adiciona o botão e a área de texto ao painel
        panel.add(button);
        panel.add(scrollPane);

        // Adiciona um listener ao botão
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Código para obter informações da máquina e exibi-las na área de texto
                Looca looca = new Looca();
                Processador processador = new Processador();
                Memoria memoria = new Memoria();

                textArea.setText("");


                // Adicione mais informações conforme necessário

                // Exibe informações usando a biblioteca Oshi
                SystemInfo systemInfo = new SystemInfo();
                HardwareAbstractionLayer hardware = systemInfo.getHardware();
                // Adicione mais informações da biblioteca Oshi conforme necessário
            }
        });

        // Torna a janela visível
        frame.setVisible(true);

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

            textArea.append("Informações da Máquina:\n");
            textArea.append("Porcentagem de Uso do Disco: " + porcentagem_uso_disco + "%\n");
            textArea.append("Porcentagem do Uso de Memória: " + porcentagem_uso_memoria + "%\n");
            textArea.append("Quantidade de RAM Disponível: " + quantidade_de_ram + " GB\n");
            textArea.append("Memória Disponível: " + memoria_disponivel + " GB\n");
            textArea.append("Tamanho Total do Disco: " + tot_disco + " GB\n");
            textArea.append("Porcentagem de Uso da CPU: " + porcentagem_de_uso_da_cpu + "%\n");
            textArea.append("Tamanho Disponível do Disco: " + tamanho_disponivel_do_disco + " GB\n");
            textArea.append("Memória Total: " + memoria_total + " GB\n");
            textArea.append("Quantidade de Bytes Recebidos: " + pacotesRecebidosWlan6 + " Mbs\n");
            textArea.append("Quantidade de Bytes Enviados " + pacotesEnviadosWlan6 + "Mbs\n");



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
                            quantidade_de_ram, memoria_disponivel, tot_disco, porcentagem_de_uso_da_cpu, tamanho_disponivel_do_disco, memoria_total, pacotesRecebidosWlan6 , pacotesEnviadosWlan6));

            // Inserir os dados no banco de dados
            con.update("insert into servidor (porcentagem_uso_disco, porcentagem_uso_memoria, quantidade_de_ram, memoria_disponivel, tamanho_total_disco, porcentagem_uso_cpu, tamanho_disponivel_do_disco, memoria_total," +
                            "quantidade_de_bytes_recebidos, quantidade_de_bytes_enviados) values (?, ?, ?, ?, ?, ?, ?, ? , ? , ?)",
                    porcentagem_uso_disco, porcentagem_uso_memoria,
                    quantidade_de_ram, memoria_disponivel, tot_disco, porcentagem_de_uso_da_cpu, tamanho_disponivel_do_disco, memoria_total,
                    pacotesRecebidosWlan6 , pacotesRecebidosWlan6);

            System.out.println("Inserido Com Sucesso");
        } catch (Error erro) {
            System.out.println("Deu Erro , Se lascou");
        }
    }
}
