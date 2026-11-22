import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Um indivíduo: uma ordem de visita às cidades.
 *
 * Responsabilidade: representar uma rota e dizer o quão boa ela é.
 * Ela NÃO sabe nada de seleção, cruzamento ou mutação.
 *
 * É imutável: depois de criada, a ordem nunca muda. Para "alterar" uma rota,
 * cria-se uma nova. Por isso a distância pode ser calculada uma única vez,
 * no construtor, sem precisar de cache com -1.
 */
public final class Rota {
    private final List<Cidade> cidades;
    private final double distanciaTotal;

    public Rota(List<Cidade> cidades) {
        this.cidades = List.copyOf(cidades); // cópia que não pode ser modificada
        this.distanciaTotal = calcularDistancia(this.cidades);
    }

    public static Rota aleatoria(List<Cidade> cidadesBase, Random random) {
        List<Cidade> copia = new ArrayList<>(cidadesBase);
        Collections.shuffle(copia, random);
        return new Rota(copia);
    }

    private static double calcularDistancia(List<Cidade> cidades) {
        double total = 0;
        for (int i = 0; i < cidades.size(); i++) {
            Cidade atual = cidades.get(i);
            Cidade proxima = cidades.get((i + 1) % cidades.size()); // volta ao início
            total += atual.distanciaAte(proxima);
        }
        return total;
    }

    public double distanciaTotal() {
        return distanciaTotal;
    }

    // menor distância, maior fitness
    public double fitness() {
        return 1.0 / distanciaTotal;
    }

    public int tamanho() {
        return cidades.size();
    }

    public Cidade cidadeEm(int posicao) {
        return cidades.get(posicao);
    }

    public List<Cidade> getCidades() {
        return cidades; // seguro: a lista não pode ser modificada
    }

    @Override
    public String toString() {
        return String.format("Rota(distancia=%.2f)", distanciaTotal);
    }
}