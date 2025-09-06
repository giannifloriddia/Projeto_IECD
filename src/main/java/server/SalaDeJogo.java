package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import clientSerial.Jogo;
import dados_persistentes.GerirJogadores;

public class SalaDeJogo implements Runnable {

    private ConexaoJogador jogadorX;
    private ConexaoJogador jogadorO;
    private GerirJogadores gestorJogadores;
    private static final int X = 226, O = 227;
    private Jogo jogo;
    private boolean turnoJogadorX = true;
    private boolean acabou = false;
    private double timerX;
    private double timerO;
    private boolean terminou;

    public SalaDeJogo(ConexaoJogador jogadorConexao, ConexaoJogador adversario, GerirJogadores gestorJogadores) {
        this.jogadorX = jogadorConexao;
        this.jogadorO = adversario;
        this.gestorJogadores = gestorJogadores;
        this.jogo = new Jogo();  // Inicializa o tabuleiro
        this.timerX = 0;
        this.timerO = 0;
    }

    @Override
    public void run() {
        try {
            System.out.println("Iniciando partida: " + jogadorX + " vs " + jogadorO);

            // Streams de comunicação
            ObjectOutputStream outX = jogadorX.getOut();
            ObjectInputStream inX = jogadorX.getIn();

            ObjectOutputStream outO = jogadorO.getOut();
            ObjectInputStream inO = jogadorO.getIn();

            // Envia mensagens iniciais
            outX.writeObject("A partida começou! Você é o X.");
            outO.writeObject("A partida começou! Você é o O.");
            outX.flush();
            outO.flush();

            // Envia o símbolo como objeto
            outX.writeObject(X);
            outO.writeObject(O);
            outX.flush();
            outO.flush();

            // Loop principal do jogo
            while (!acabou) {
                if (turnoJogadorX) {
                	Object jogadaX;
                	do {
                    	double comecou = System.currentTimeMillis();
                		// Recebe jogada do jogador X
                        jogadaX = inX.readObject();
                        System.out.println(jogadaX.toString());
                        double acabou = System.currentTimeMillis() - comecou;
                        if (acabou > 30000) {
							terminou = true;
						}
                        timerX += acabou;
                	} while (jogadaX instanceof String);

                    this.jogo = (Jogo) jogadaX;


                    fimDeJogo();

                    // Envia jogada para jogador O
                    outO.writeObject(jogo);
                    outO.reset();
                    turnoJogadorX = false;
                } else {
                	Object jogadaO;
                	do {
                		double comecou = System.currentTimeMillis();
                        // Recebe jogada do jogador O
                        jogadaO = inO.readObject();
                        double acabou = System.currentTimeMillis() - comecou;
                        if (acabou > 30000) {
							terminou = true;
						}
                        timerO += acabou;
                	} while (jogadaO instanceof String);

                    this.jogo = (Jogo) jogadaO;


                    fimDeJogo();

                    // Envia jogada para jogador X
                    outX.writeObject(jogo);
                    outX.reset();
                    turnoJogadorX = true;
                }
            }

            System.out.println("Partida terminou!");

            fechar(jogadorX.getSocket());
            fechar(jogadorO.getSocket());

        } catch (Exception e) {
            System.out.println("Erro na sala de jogo: " + e.getMessage());
            fechar(jogadorX.getSocket());
            fechar(jogadorO.getSocket());
        }
    }

    private void fechar(Socket s) {
        try {
            if (s != null && !s.isClosed()) {
                s.close();
            }
        } catch (IOException e) {
            // Ignorar
        }
    }

    private void fimDeJogo() {
    	if (jogo.vitoria(X)) {

    		gestorJogadores.incrementarVitoria(jogadorX.getJogadorNome());
    		gestorJogadores.incrementarDerrota(jogadorO.getJogadorNome());

    		gestorJogadores.adicionarTempo(jogadorX.getJogadorNome(), timerX);
    		gestorJogadores.adicionarTempo(jogadorO.getJogadorNome(), timerO);

            acabou = true;
        } else if (jogo.vitoria(O)){

        	gestorJogadores.incrementarVitoria(jogadorO.getJogadorNome());
    		gestorJogadores.incrementarDerrota(jogadorX.getJogadorNome());

    		gestorJogadores.adicionarTempo(jogadorX.getJogadorNome(), timerX);
    		gestorJogadores.adicionarTempo(jogadorO.getJogadorNome(), timerO);

            acabou = true;
        } else if (jogo.empate()) {

        	gestorJogadores.adicionarTempo(jogadorX.getJogadorNome(), timerX);
    		gestorJogadores.adicionarTempo(jogadorO.getJogadorNome(), timerO);

            acabou = true;

        } else if (terminou){
        	if (turnoJogadorX) {
        		
        		jogo.setTerminou(true);
        		
        		gestorJogadores.incrementarVitoria(jogadorO.getJogadorNome());
        		gestorJogadores.incrementarDerrota(jogadorX.getJogadorNome());

        		gestorJogadores.adicionarTempo(jogadorX.getJogadorNome(), timerX);
        		gestorJogadores.adicionarTempo(jogadorO.getJogadorNome(), timerO);
                System.out.println("Ganhou Branco (O)!");
        		acabou = true;
        	} else if (!turnoJogadorX) {
        		
        		jogo.setTerminou(true);
        		
        		gestorJogadores.incrementarVitoria(jogadorX.getJogadorNome());
        		gestorJogadores.incrementarDerrota(jogadorO.getJogadorNome());

        		gestorJogadores.adicionarTempo(jogadorX.getJogadorNome(), timerX);
        		gestorJogadores.adicionarTempo(jogadorO.getJogadorNome(), timerO);
                System.out.println("Ganhou Preto (X)!");
        		acabou = true;
        	}
        }
    }
}

