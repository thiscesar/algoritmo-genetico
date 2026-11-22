import java.util.List;
import java.util.Random;

/**
 * Responsabilidade: montar o problema, configurar o algoritmo e escolher como mostrar o resultado.
 * Não contém nenhuma lógica do algoritmo genético nem de desenho.
 */
public class Main {
    public static void main(String[] args) {
        Random seed = new Random(42);

        // 1. o problema
        List<Cidade> cidades = Cidade.gerarVarias(30, seed);

        // 2. a configuração
        AlgoritmoGenetico ag = new AlgoritmoGenetico(
                100,   // tamanho da população
                100,   // gerações
                0.02,  // taxa de mutação
                5,     // tamanho do torneio
                seed
        );

        // 3. a tela (20 ms de pausa entre gerações, para dar tempo de ver a evolução)
        Visualizador tela = Visualizador.abrir("Caixeiro-viajante com Algoritmo Genético", 600);

        // 4. a execução: a cada geração, o algoritmo avisa a tela
        Rota melhor = ag.executar(cidades, tela::atualizar);

        System.out.println("Melhor rota encontrada: " + melhor);
    }
}