import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cidade {
    private static final float MIN_XY = 0f;
    private static final float MAX_XY = 1000f;

    private float x;
    private float y;

    public Cidade(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Cidade() {
        Random random = new Random();
        gerar(random);
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

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Cidade(x=" + x + ", y=" + y + ")";
    }
}