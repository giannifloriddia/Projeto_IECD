package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import clientSerial.JogadorPerfil;
import dados_persistentes.GerirJogadores;

public class ServidorTCP {

	/**
	 * Porta padrão do servidor.
	 */
	private static final int DEFAULT_PORT = 5025;
	private static final char X = 226, O = 227;

	private static GerirJogadores gestorJogadores = new GerirJogadores();
	private static SalaDeEspera salaDeEspera = new SalaDeEspera();

	private static List<ConexaoJogador> jogadoresConexao = new ArrayList<>();

	/**
	 * Método principal do servidor.
	 *
	 * @param args argumentos da linha de comando (não utilizados)
	 * @throws IOException caso haja algum erro de E/S
	 */
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT);
		System.out.println("Servidor TCP iniciado no porto " + DEFAULT_PORT);

		for (;;) {
			Socket socket = serverSocket.accept();
			System.out.println("Novo jogador conectado: " + socket);

			// 1. ligaçao

			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			ConexaoJogador jogadorConexao = new ConexaoJogador(socket, out, in);
			jogadoresConexao.add(jogadorConexao);

			String loginInfo = null;
			try {
				loginInfo = (String) in.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String[] partes = loginInfo.split("\\|");
			String nickname = partes[0];
			String password = partes[1];

			System.out.println(loginInfo);

			gestorJogadores.carregarJogadoresDoXML();

			// Autenticação do jogador
			boolean autenticado = gestorJogadores.autenticarJogador(nickname, password);
			out.writeObject(autenticado ? "Login bem-sucedido!" : "Login falhou!");
			out.flush();

			if (!autenticado) {
				socket.close();
				continue;
			}
			// Após o login bem-sucedido, adicionar o jogador à sala de espera
			JogadorPerfil jogador = gestorJogadores.encontrarJogador(nickname);
			jogadorConexao.setJogadorPerfil(jogador);
			salaDeEspera.adicionarJogador(jogador, socket);
			System.out.println(salaDeEspera.listarJogadores(nickname));

			// 2. validaçao

			new Thread(() -> {
				boolean teste = true;
				try {
					while (true) {
						if (teste) {
							out.writeObject("Selecione um comando: /listar ou /desafiar + jogador");
							String comando = (String) in.readObject();
							if (comando.startsWith("/listar")) {
								String lista = salaDeEspera.listarJogadores(nickname);
								out.writeObject(lista.isEmpty() ? "Nenhum jogador disponível." : lista);
								out.flush();
							} else if (comando.startsWith("/desafiar")) {
								String desafiarQuem = comando.substring(9).trim();
								boolean emparelhado = salaDeEspera.desafiarJogador(nickname, desafiarQuem);

								// 3. jogo
								if (emparelhado) {
									ObjectOutputStream outAdversario = null;
									ConexaoJogador adversario = null;
									try {
										Socket socketAdversario = salaDeEspera.getSocketJogador(desafiarQuem);
										for (ConexaoJogador j : jogadoresConexao) {
											if (j.getJogadorNome() == salaDeEspera.getPerfilJogador(desafiarQuem)
													.getNickname()) {
												adversario = j;
											}
										}
										outAdversario = adversario.getOut();
										out.writeObject("Emparelhamento confirmado com " + desafiarQuem
												+ "! Sala de jogo criada.");
										out.flush();

										outAdversario.writeObject(
												"Emparelhamento confirmado com " + nickname + "! Sala de jogo criada.");
										outAdversario.flush();

										SalaDeJogo salaDeJogo = new SalaDeJogo(jogadorConexao, adversario, gestorJogadores);
										System.out.println("Sala de Jogo criada");
										out.writeObject("Iniciando jogo com " + desafiarQuem);
										out.flush();
										outAdversario.writeObject("Iniciando jogo com " + nickname);
										out.flush();

										// Remover ambos da sala de espera
										salaDeEspera.removerJogador(jogador);
										salaDeEspera.removerJogador(salaDeEspera.getPerfilJogador(desafiarQuem));

										new Thread(salaDeJogo).start();
										return;

									} catch (IOException e) {
										e.printStackTrace();
									}
								} else {
									out.writeObject(
											"Desafio enviado para " + desafiarQuem + ". Aguardando resposta...");
									System.out.println("segundo envio server - jogador");
									out.flush();
									teste = false;
								}
							} else {
								out.writeObject("Comando desconhecido.");
								out.flush();
							}
						}
					}
				} catch (Exception e) {
					System.out.println("Jogador desconectado: " + nickname);
					salaDeEspera.removerJogador(jogador);
				}
			}).start();

		}

	}

	/**
	 * Copia caracteres de um socket de entrada para um socket de saída.
	 *
	 * @param entrada socket de entrada
	 * @param saida   socket de saída
	 * @throws IOException caso haja algum erro de E/S
	 */
	private static void copiarCaracteres(final Socket entrada, final Socket saida, final int simbolo) {
		// Cria streams de entrada e saída para os sockets
		try (InputStream inputStream = entrada.getInputStream(); OutputStream outputStream = saida.getOutputStream();) {
			// atribui o simbolo
			outputStream.write(simbolo);
			outputStream.flush();
			// Enquanto houverem, lê bytes do socket de entrada
			// e escreve no socket de saída
			int caractere;
			while ((caractere = inputStream.read()) != -1) {
				// Escreve o caractere lido no socket de saída
				outputStream.write(caractere);
				// Esvazia o buffer de saída
				outputStream.flush();
			}
		} catch (IOException e) {
			System.out.println("Terminou " + entrada + "<->" + saida + ": " + e.getLocalizedMessage());
		} finally {
			// Fecha os sockets
			try {
				entrada.close();
				saida.close();
			} catch (IOException e) {
			}
		}
	}

}