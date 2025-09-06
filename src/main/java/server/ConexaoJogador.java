package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import clientSerial.JogadorPerfil;

public class ConexaoJogador {
	private JogadorPerfil jogador;
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    public ConexaoJogador(Socket socket, ObjectOutputStream out, ObjectInputStream in) {
        this.jogador = null;
		this.socket = socket;
        this.out = out;
        this.in = in;
    }

    public void setJogadorPerfil(JogadorPerfil jogador) {
    	this.jogador = jogador;
    }

    public JogadorPerfil getJogadorPerfil() {
    	return this.jogador;
    }

    public String getJogadorNome() {
    	return jogador.getNickname();
    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public ObjectInputStream getIn() {
        return in;
    }

}
