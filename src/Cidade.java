import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Um ponto no mapa. Só sabe onde está e calcular a distância até outra cidade.
 */
public class Cidade {
    private static final float MIN_XY = 0f;
    private static final float MAX_XY = 1000f;

    private final float x;
    private final float y;

    public Cidade(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static Cidade gerar(Random random) {
        float x = random.nextFloat(MIN_XY, MAX_XY);
        float y = random.nextFloat(MIN_XY, MAX_XY);
        return new Cidade(x, y);
    }

    public static List<Cidade> gerarVarias(int quantidade, Random random) {
        List<Cidade> cidades = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            cidades.add(gerar(random));
        }
        return cidades;
    }

    public double distanciaAte(Cidade outra) {
        float dx = this.x - outra.x;
        float dy = this.y - outra.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public String toString() {
        return String.format("Cidade(x=%.1f, y=%.1f)", x, y);
    }
}
