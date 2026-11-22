/**
 * Responsabilidade: decidir quando o algoritmo pode seguir para a próxima geração.
 * Os botões mudam o estado; a thread do algoritmo pergunta "posso continuar?".
 * Não sabe nada de desenho nem de algoritmo genético.
 */
public class Controle {
    private boolean pausado = true;          // começa parado, esperando o Start
    private boolean avancarUmPasso = false;

    public synchronized void iniciar() {
        pausado = false;
        notifyAll(); // acorda a thread do algoritmo, se estiver esperando
    }

    public synchronized void parar() {
        pausado = true;
    }

    public synchronized void avancar() {
        avancarUmPasso = true;
        notifyAll();
    }

    /** Chamado pela thread do algoritmo: fica esperando enquanto estiver pausado. */
    public synchronized void aguardarVez() {
        while (pausado && !avancarUmPasso) {
            try {
                wait(); // dorme até alguém chamar notifyAll()
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        avancarUmPasso = false; // o passo foi usado
    }
}