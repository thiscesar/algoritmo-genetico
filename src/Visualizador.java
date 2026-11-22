import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Responsabilidade: mostrar na tela a evolução do algoritmo.
 * Desenha a melhor rota da geração atual e um gráfico da distância ao longo do tempo.
 * Não sabe nada sobre como o algoritmo funciona: só recebe (geração, rota) e desenha.
 */
public class Visualizador extends JPanel {
    private static final int MARGEM = 30;
    private static final int ALTURA_TEXTO = 40;
    private static final int ALTURA_GRAFICO = 150;

    private static final Color FUNDO = new Color(24, 24, 32);
    private static final Color COR_ROTA = new Color(90, 170, 255);
    private static final Color COR_CIDADE = new Color(255, 200, 80);
    private static final Color COR_GRAFICO = new Color(120, 220, 140);
    private static final Color COR_TEXTO = new Color(230, 230, 240);
    private static final Color COR_BORDA = new Color(60, 60, 75);

    private final int pausaMs;
    private final List<Double> historico = new ArrayList<>();
    private Rota melhorRota;
    private int geracao;

    private Visualizador(int pausaMs) {
        this.pausaMs = pausaMs;
        setPreferredSize(new Dimension(800, 900));
        setBackground(FUNDO);
    }

    /** Cria a janela e devolve o visualizador pronto para receber atualizações. */
    public static Visualizador abrir(String titulo, int pausaMs) {
        Visualizador visualizador = new Visualizador(pausaMs);
        SwingUtilities.invokeLater(() -> {
            JFrame janela = new JFrame(titulo);
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            janela.add(visualizador);
            janela.pack();
            janela.setLocationRelativeTo(null);
            janela.setVisible(true);
        });
        return visualizador;
    }

    /** Recebe a melhor rota de uma geração e redesenha a tela. */
    public void atualizar(int geracao, Rota rota) {
        // tudo que mexe na tela precisa rodar na thread do Swing
        SwingUtilities.invokeLater(() -> {
            this.geracao = geracao;
            this.melhorRota = rota;
            this.historico.add(rota.distanciaTotal());
            repaint();
        });
        pausar();
    }

    private void pausar() {
        try {
            Thread.sleep(pausaMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ---------- desenho ----------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (melhorRota == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        desenharTexto(g2);
        desenharRota(g2);
        desenharGrafico(g2);
    }

    private void desenharTexto(Graphics2D g2) {
        g2.setColor(COR_TEXTO);
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        String texto = String.format("Geração %d    Distância: %.2f", geracao, melhorRota.distanciaTotal());
        g2.drawString(texto, MARGEM, MARGEM + 10);
    }

    private void desenharRota(Graphics2D g2) {
        List<Cidade> cidades = melhorRota.getCidades();

        // limites das coordenadas, para encaixar o mapa na área disponível
        float minX = Float.MAX_VALUE, maxX = -Float.MAX_VALUE;
        float minY = Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
        for (Cidade c : cidades) {
            minX = Math.min(minX, c.getX());
            maxX = Math.max(maxX, c.getX());
            minY = Math.min(minY, c.getY());
            maxY = Math.max(maxY, c.getY());
        }
        float faixaX = Math.max(maxX - minX, 1f);
        float faixaY = Math.max(maxY - minY, 1f);

        int esquerda = MARGEM;
        int topo = MARGEM + ALTURA_TEXTO;
        int largura = getWidth() - 2 * MARGEM;
        int altura = getHeight() - topo - ALTURA_GRAFICO - 2 * MARGEM;

        int n = cidades.size();
        int[] xs = new int[n];
        int[] ys = new int[n];
        for (int i = 0; i < n; i++) {
            Cidade c = cidades.get(i);
            xs[i] = esquerda + (int) ((c.getX() - minX) / faixaX * largura);
            ys[i] = topo + (int) ((c.getY() - minY) / faixaY * altura);
        }

        // o polígono já fecha a volta da última cidade para a primeira
        g2.setColor(COR_ROTA);
        g2.setStroke(new BasicStroke(2f));
        g2.drawPolygon(xs, ys, n);

        g2.setColor(COR_CIDADE);
        for (int i = 0; i < n; i++) {
            g2.fillOval(xs[i] - 5, ys[i] - 5, 10, 10);
        }
    }

    private void desenharGrafico(Graphics2D g2) {
        int x0 = MARGEM;
        int y0 = getHeight() - ALTURA_GRAFICO - MARGEM;
        int largura = getWidth() - 2 * MARGEM;
        int altura = ALTURA_GRAFICO;

        g2.setStroke(new BasicStroke(1f));
        g2.setColor(COR_BORDA);
        g2.drawRect(x0, y0, largura, altura);

        g2.setColor(COR_TEXTO);
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        g2.drawString("Distância ao longo das gerações", x0 + 8, y0 + 16);

        int n = historico.size();
        if (n < 2) {
            return;
        }

        double min = Collections.min(historico);
        double max = Collections.max(historico);
        double faixa = Math.max(max - min, 1e-9);

        int[] xs = new int[n];
        int[] ys = new int[n];
        for (int i = 0; i < n; i++) {
            xs[i] = x0 + (int) ((double) i / (n - 1) * largura);
            ys[i] = y0 + altura - (int) ((historico.get(i) - min) / faixa * (altura - 25));
        }

        g2.setColor(COR_GRAFICO);
        g2.setStroke(new BasicStroke(2f));
        g2.drawPolyline(xs, ys, n);
    }
}