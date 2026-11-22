import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Responsabilidade: fazer a população de rotas evoluir.
 * Aqui ficam todos os operadores genéticos: seleção, cruzamento e mutação.
 * Não imprime nada: quem quiser acompanhar o progresso passa um "ouvinte".
 */
public class AlgoritmoGenetico {
    private final int tamanhoPopulacao;
    private final int geracoes;
    private final double taxaMutacao;
    private final int tamanhoTorneio;
    private final Random random;

    public AlgoritmoGenetico(int tamanhoPopulacao, int geracoes, double taxaMutacao,
            int tamanhoTorneio, Random random) {
        this.tamanhoPopulacao = tamanhoPopulacao;
        this.geracoes = geracoes;
        this.taxaMutacao = taxaMutacao;
        this.tamanhoTorneio = tamanhoTorneio;
        this.random = random;
    }

    /**
     * Roda o algoritmo e devolve a melhor rota encontrada.
     * O ouvinte é chamado ao fim de cada geração com (número da geração, melhor
     * rota).
     */
    public Rota executar(List<Cidade> cidades, BiConsumer<Integer, Rota> aoFimDaGeracao) {
        List<Rota> populacao = populacaoInicial(cidades);

        for (int g = 1; g <= geracoes; g++) {
            populacao = proximaGeracao(populacao);
            aoFimDaGeracao.accept(g, melhorDe(populacao));
        }

        return melhorDe(populacao);
    }

    // ---------- etapas do algoritmo ----------

    private List<Rota> populacaoInicial(List<Cidade> cidades) {
        List<Rota> populacao = new ArrayList<>();
        for (int i = 0; i < tamanhoPopulacao; i++) {
            populacao.add(Rota.aleatoria(cidades, random));
        }
        return populacao;
    }

    private List<Rota> proximaGeracao(List<Rota> atual) {
        List<Rota> nova = new ArrayList<>();
        nova.add(melhorDe(atual)); // elitismo: a melhor passa direto

        while (nova.size() < tamanhoPopulacao) {
            Rota pai1 = torneio(atual);
            Rota pai2 = torneio(atual);
            Rota filho = mutar(cruzar(pai1, pai2));
            nova.add(filho);
        }
        return nova;
    }

    // ---------- operadores genéticos ----------

    /** Seleção por torneio: sorteia algumas rotas e devolve a melhor. */
    private Rota torneio(List<Rota> populacao) {
        Rota melhor = null;
        for (int i = 0; i < tamanhoTorneio; i++) {
            Rota candidata = populacao.get(random.nextInt(populacao.size()));
            if (melhor == null || candidata.fitness() > melhor.fitness()) {
                melhor = candidata;
            }
        }
        return melhor;
    }

    /** Order Crossover (OX): trecho do pai 1 + ordem relativa do pai 2. */
    private Rota cruzar(Rota pai1, Rota pai2) {
        int n = pai1.tamanho();

        int inicio = random.nextInt(n);
        int fim = random.nextInt(n);
        if (inicio > fim) {
            int temp = inicio;
            inicio = fim;
            fim = temp;
        }

        Cidade[] filho = new Cidade[n];
        Set<Cidade> usadas = new HashSet<>();

        for (int i = inicio; i <= fim; i++) {
            filho[i] = pai1.cidadeEm(i);
            usadas.add(filho[i]);
        }

        int pos = (fim + 1) % n;
        for (int k = 0; k < n; k++) {
            Cidade c = pai2.cidadeEm((fim + 1 + k) % n);
            if (!usadas.contains(c)) {
                filho[pos] = c;
                pos = (pos + 1) % n;
            }
        }

        return new Rota(List.of(filho));
    }

    /** Mutação por troca: devolve uma NOVA rota com duas cidades trocadas. */
    private Rota mutar(Rota rota) {
        if (random.nextDouble() >= taxaMutacao) {
            return rota; // sem mutação
        }
        List<Cidade> cidades = new ArrayList<>(rota.getCidades());
        int i = random.nextInt(cidades.size());
        int j = random.nextInt(cidades.size());
        Collections.swap(cidades, i, j);
        return new Rota(cidades);
    }

    private Rota melhorDe(List<Rota> populacao) {
        return Collections.min(populacao, Comparator.comparingDouble(Rota::distanciaTotal));
    }
}