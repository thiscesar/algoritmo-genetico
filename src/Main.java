import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        System.out.println("Olá!");

        List<Cidade> cidadesAleatorias = Cidade.gerarVarias(10, new Random());
        for (Cidade cidade : cidadesAleatorias) {
            System.out.println(cidade);
        }
    }
}