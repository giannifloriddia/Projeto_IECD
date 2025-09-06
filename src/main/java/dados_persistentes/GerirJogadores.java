package dados_persistentes;
import java.util.List;

import clientSerial.JogadorPerfil;

public class GerirJogadores {
    private List<JogadorPerfil> jogadores;

    public void adicionarJogador(String nickname, String password, String nacionalidade, String fotografia,
                                  String dataNascimento) {
        String passwordHash = Util.hashPassword(password);

        JogadorPerfil jogador = new JogadorPerfil(nickname, dataNascimento, passwordHash, fotografia, nacionalidade);
        jogadores.add(jogador);

        gravarJogadorNoXML(jogador);
    }

    public boolean autenticarJogador(String nickname, String password) {

    	String passwordHash = Util.hashPassword(password);
        JogadorPerfil jogador = encontrarJogador(nickname);

        // Comparar o hash da password
        if (jogador != null) {
            // Se o jogador existe, verificar se o hash da password coincide
            if (jogador.getPasswordHash().equals(passwordHash)) {
                System.out.println("Autenticação bem-sucedida!");
                return true;
            }
        }
        System.out.println("Falha na autenticação.");
        return false;
    }


    public JogadorPerfil encontrarJogador(String nickname) {
        for (JogadorPerfil jogador : jogadores) {
            if (jogador.getNickname().equals(nickname)) {
                return jogador;
            }
        }
        return null;
    }

    private void gravarJogadorNoXML(JogadorPerfil jogador) {
       JogadoresDataBase.salvarJogador(jogador);
    }

    public List<JogadorPerfil> carregarJogadoresDoXML() {
        jogadores = JogadoresDataBase.carregar();
        return jogadores;
    }

    public void incrementarVitoria(String nickname) {
        JogadorPerfil jogador = encontrarJogador(nickname);
        if (jogador != null) {
            jogador.incrementar_vitorias();
            gravarJogadorNoXML(jogador);
        }
    }

    public void incrementarDerrota(String nickname) {
        JogadorPerfil jogador = encontrarJogador(nickname);
        if (jogador != null) {
            jogador.incrementar_derrotas();
            gravarJogadorNoXML(jogador);
        }
    }

    public void adicionarTempo(String nickname, double tempo) {
        JogadorPerfil jogador = encontrarJogador(nickname);
        if (jogador != null) {
            jogador.adicionar_tempo(tempo);
            gravarJogadorNoXML(jogador);
        }
    }
}