package server;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import clientSerial.JogadorPerfil;

public class SalaDeEspera {

    private Map<JogadorPerfil, Socket> jogadores = new HashMap<>();
    private Map<String, String> desafiosPendentes = new HashMap<>();

    public synchronized void adicionarJogador(JogadorPerfil jogador, Socket socket) {
        jogadores.put(jogador, socket);
    }

    public synchronized void removerJogador(JogadorPerfil jogador) {
        jogadores.remove(jogador);
        desafiosPendentes.remove(jogador.getNickname());
    }

    public synchronized String listarJogadores(String solicitante) {
        StringBuilder lista = new StringBuilder();
        for (JogadorPerfil jogador : jogadores.keySet()) {
            if (!jogador.getNickname().equals(solicitante)) {
                lista.append(jogador.getNickname()).append("\n");
            }
        }
        return lista.toString();
    }

    public synchronized boolean desafiarJogador(String desafiante, String desafiado) {
        desafiosPendentes.put(desafiante, desafiado);

        // Verificar se o desafiado já tinha desafiado o desafiante
        String respostaDoDesafiado = desafiosPendentes.get(desafiado);
        if (respostaDoDesafiado != null && respostaDoDesafiado.equals(desafiante)) {
            return true; // Emparelhamento confirmado
        }
        return false;
    }
    public synchronized Socket getSocketJogador(String nickname) {
        for (Map.Entry<JogadorPerfil, Socket> entry : jogadores.entrySet()) {
            if (entry.getKey().getNickname().equals(nickname)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public synchronized JogadorPerfil getPerfilJogador(String nickname) {
        for (JogadorPerfil jogador : jogadores.keySet()) {
            if (jogador.getNickname().equals(nickname)) {
                return jogador;
            }
        }
        return null;
    }
}
