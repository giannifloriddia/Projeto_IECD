package clientSerial;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Jogador extends Jogo implements Serializable{

	private static final long serialVersionUID = 1L;
	private final static String DEFAULT_HOST = "localhost";
	private final static int DEFAULT_PORT = 5025;
	private static PrintStream saida = System.out;
	private static Scanner leitor = new Scanner(System.in);
	public transient Socket socket = null;
	public transient ObjectOutputStream out = null;
	public transient ObjectInputStream in = null;

	public Jogador() throws UnknownHostException, IOException {

		this.socket = new Socket(DEFAULT_HOST, DEFAULT_PORT);
		this.out = new ObjectOutputStream(socket.getOutputStream());
		this.in = new ObjectInputStream(socket.getInputStream());

	}

	public int jogar(int simbolo) {

		saida.println(print());

		saida.println("Joga " + convert(simbolo) + ": ");
		saida.println(); 		// importante para sincronizar

		while (true) {
			if (leitor.hasNextInt()) {
				int numero = leitor.nextInt();
				// Concretiza a jogada.
				if (!jogar(numero, simbolo)) {
					// Jogada inválida.
					saida.println("Jogada inválida!\n");  // linha vazia sincroniza
				}
				else {
					// Jogada válida.
					System.out.println("Jogada concretizada!");
					return numero;
				}
			} else {
				// Ignora a linha
				leitor.nextLine();
			}
		}
	}

	public boolean terminou() {
		String msg = "";
		if (vitoria(X)) {
			msg="Vitória do X (Pretas)";
		} else if (vitoria(O)) {
			msg="Vitória do O (Brancas)";
		} else if (empate()) {
			msg="Empate";
		}
		if(!msg.equals(""))	 {
			saida.println(print());
			saida.println(msg+"!");
			return true;
		}
		return false;
	}

	public String print() {
	    String s = "";
	    for (int i = 0; i < 15; i++) {
	        for (int j = 0; j < 15; j++) {
	            s += String.format("%3s", convert(getTabuleiro()[i][j]));

	            if (j < 14) {
	                s += " |";
	            }
	        }
	        s += "\n";

	        // Linha separadora
	        if (i < 14) {
	            for (int j = 0; j < 15; j++) {
	                s += "---";
	                if (j < 14) {
	                    s += "+";
	                }
	            }
	            s += "\n";
	        }
	    }

	    return s;
	}

	public String convert(int num) {
		if (num == X) {
			return "X";
		} else if (num == O) {
			return "O";
		} else {
			return String.valueOf(num);
		}
	}

	public Object getMessage() throws ClassNotFoundException, IOException {
		return in.readObject();
	}

	public void sendMessage(Object object) throws IOException {
	    out.reset();
		out.writeObject(object);
		out.flush();
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// Leitura de informações de login
        System.out.print("Digite seu nickname: ");
        String nickname = leitor.nextLine();
        System.out.print("Digite sua password: ");
        String password = leitor.nextLine();
		Jogador jogo = new Jogador();

	    try (
		    Socket socket = jogo.socket;
		  ) {

		System.out.println("Java-> Ligação estabelecida: " + socket);

		// Estabelece a conexão e cria streams de comunicação
		ObjectOutputStream objectOutputStream = jogo.out;
		ObjectInputStream objectInputStream = jogo.in;

        // Enviar as informações de login para o servidor
        String loginInfo = nickname + "|" + password;
        objectOutputStream.writeObject(loginInfo);
        objectOutputStream.flush();

        // Espera pela resposta do servidor
        String respostaServidor = (String) objectInputStream.readObject();
        System.out.println("Resposta do servidor: " + respostaServidor);

        //2. validaçao

        System.out.println(objectInputStream.readObject());
    	System.out.println("primeiro recebido server - jogador");

        String comando = leitor.nextLine();
        objectOutputStream.writeObject(comando);
        objectOutputStream.flush();

        if (comando.startsWith("/listar")) {
            String lista = (String) objectInputStream.readObject();
            System.out.println(lista);
        }

	     // 3. início do jogo
        Object obj;
        int simbolo;
        boolean vez;

        // 1) lê tudo o que for String
	     do {
	         obj = objectInputStream.readObject();
	         if (obj instanceof String) {
	             String s = (String) obj;
	             if (!s.isEmpty()) {
					System.out.println(s);
				}
	         }
	     } while (obj instanceof String);

	     // 2) chegou ao símbolo (Integer)
	     if (obj instanceof Integer) {
	         simbolo = ((Integer) obj).intValue();
	         System.out.println("Símbolo atribuído: " + jogo.convert(simbolo));
	         vez = (simbolo == X);
	     } else {
	         throw new IllegalStateException("Esperava um Integer mas recebi: " + obj.getClass());
	     }

		while (true) {
		  if (vez) {
		    jogo.jogar(simbolo);
		    objectOutputStream.writeObject(jogo);
		    objectOutputStream.flush();
		    if (jogo.terminou()) {
				break;
			}
		  }
		  jogo = (Jogador) objectInputStream.readObject();
		  if (jogo.terminou()) {
			break;
		}
		  vez = true;  // depois de receber, passa a ser a tua vez
		}

    	for(;;) {}
	    } catch (IOException e) {
	    	System.out.println("Ligação fechada: "+e.getLocalizedMessage());
	    }
	    System.out.println("Terminou o jogo!");
	}


}