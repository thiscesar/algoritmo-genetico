import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Rota {
    private List<Cidade> cidades;
    private double distancia = -1;

    public Rota(List<Cidade> cidades) {
        this.cidades = new ArrayList<>(cidades); // copia, para não mexer na lista original
    }

    public static Rota aleatoria(List<Cidade> cidadesBase, Random random) {
        List<Cidade> copia = new ArrayList<>(cidadesBase);
        Collections.shuffle(copia, random);
        return new Rota(copia);
    }

    public double distanciaTotal() {
        if (distancia < 0) {
            distancia = 0;
            for (int i = 0; i < cidades.size(); i++) {
                Cidade atual = cidades.get(i);
                Cidade proxima = cidades.get((i + 1) % cidades.size());
                distancia += atual.distanciaAte(proxima);
            }
        }

        return distancia;
    }

    //  menor distancia, maior  fitness
    public double fitness() {
        return 1.0 / distanciaTotal();
    }

    // Mutação: troca duas cidades de lugar
    public void mutar(double taxa, Random random) {
        if (random.nextDouble() < taxa) {
            int i = random.nextInt(cidades.size());
            int j = random.nextInt(cidades.size());
            Collections.swap(cidades, i, j);
            distancia = -1; // a ordem mudou, precisa recalcular
        }
    }

    public List<Cidade> getCidades() {
        return cidades;
    }

    @Override
    public String toString() {
        return "Rota(distancia=" + String.format("%.2f", distanciaTotal()) + ")";
    }
}