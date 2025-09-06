/*package clientSerial;

import java.io.Serializable;

public class Jogo implements Serializable{

	private static final long serialVersionUID = 1L;
	private int[][] tabuleiro = new int[15][15];
	protected static final int X = 226, O = 227;

    public Jogo(){
        // Inicializa o tabuleiro com número das casas
    	int num = 1;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                getTabuleiro()[i][j] = num;
                num++;
            }
        }
    }

    public boolean vitoria(int simbolo) {
        // Verifica linhas
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j <= 10; j++) {
                if (getTabuleiro()[i][j] == simbolo && getTabuleiro()[i][j + 1] == simbolo && getTabuleiro()[i][j + 2] == simbolo &&
                        getTabuleiro()[i][j + 3] == simbolo && getTabuleiro()[i][j + 4] == simbolo) {
                    return true;
                }
            }
        }

        // Verifica colunas
        for (int i = 0; i <= 10; i++) {
            for (int j = 0; j < 15; j++) {
                if (getTabuleiro()[i][j] == simbolo && getTabuleiro()[i + 1][j] == simbolo && getTabuleiro()[i + 2][j] == simbolo &&
                        getTabuleiro()[i + 3][j] == simbolo && getTabuleiro()[i + 4][j] == simbolo) {
                    return true;
                }
            }
        }

        // Verifica diagonais (esquerda para direita)
        for (int i = 0; i <= 10; i++) {
            for (int j = 0; j <= 10; j++) {
                if (getTabuleiro()[i][j] == simbolo && getTabuleiro()[i + 1][j + 1] == simbolo && getTabuleiro()[i + 2][j + 2] == simbolo &&
                        getTabuleiro()[i + 3][j + 3] == simbolo && getTabuleiro()[i + 4][j + 4] == simbolo) {
                    return true;
                }
            }
        }

        // Verifica diagonais (direita para esquerda)
        for (int i = 0; i <= 10; i++) {
            for (int j = 4; j < 15; j++) {
                if (getTabuleiro()[i][j] == simbolo && getTabuleiro()[i + 1][j - 1] == simbolo && getTabuleiro()[i + 2][j - 2] == simbolo &&
                        getTabuleiro()[i + 3][j - 3] == simbolo && getTabuleiro()[i + 4][j - 4] == simbolo) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean empate() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (getTabuleiro()[i][j] != X && getTabuleiro()[i][j] != O) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean jogar(int numero, int simbolo) {
    	if(numero<=0 || numero >225) {
    		return false;
    	}
    	numero--;
    	int linha = numero / 15;
    	int coluna = numero % 15;

    	if(getTabuleiro()[linha][coluna] == X || getTabuleiro()[linha][coluna] == 0) {
    		return false;
    	}
    	getTabuleiro()[linha][coluna] = simbolo;
    	return true;
    }

	public int[][] getTabuleiro() {
		return tabuleiro;
	}

	public void setTabuleiro(int[][] tabuleiro) {
		this.tabuleiro = tabuleiro;
	}

}*/

package clientSerial;

import java.io.Serializable;

public class Jogo implements Serializable {

    private static final long serialVersionUID = 1L;
    private int[][] tabuleiro = new int[15][15];
    public static final int X = 226, O = 227;
    private boolean terminou = false;
    
    public boolean getTerminou() {
		return terminou;
	}

	public void setTerminou(boolean terminou) {
		this.terminou = terminou;
	}

	public Jogo() {
        int num = 1;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                tabuleiro[i][j] = num++;
            }
        }
    }

    public boolean vitoria(int simbolo) {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j <= 10; j++) {
                if (tabuleiro[i][j] == simbolo && tabuleiro[i][j + 1] == simbolo &&
                    tabuleiro[i][j + 2] == simbolo && tabuleiro[i][j + 3] == simbolo &&
                    tabuleiro[i][j + 4] == simbolo) {
                    return true;
                }
            }
        }
        for (int i = 0; i <= 10; i++) {
            for (int j = 0; j < 15; j++) {
                if (tabuleiro[i][j] == simbolo && tabuleiro[i + 1][j] == simbolo &&
                    tabuleiro[i + 2][j] == simbolo && tabuleiro[i + 3][j] == simbolo &&
                    tabuleiro[i + 4][j] == simbolo) {
                    return true;
                }
            }
        }
        for (int i = 0; i <= 10; i++) {
            for (int j = 0; j <= 10; j++) {
                if (tabuleiro[i][j] == simbolo && tabuleiro[i + 1][j + 1] == simbolo &&
                    tabuleiro[i + 2][j + 2] == simbolo && tabuleiro[i + 3][j + 3] == simbolo &&
                    tabuleiro[i + 4][j + 4] == simbolo) {
                    return true;
                }
            }
        }
        for (int i = 0; i <= 10; i++) {
            for (int j = 4; j < 15; j++) {
                if (tabuleiro[i][j] == simbolo && tabuleiro[i + 1][j - 1] == simbolo &&
                    tabuleiro[i + 2][j - 2] == simbolo && tabuleiro[i + 3][j - 3] == simbolo &&
                    tabuleiro[i + 4][j - 4] == simbolo) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean empate() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (tabuleiro[i][j] != X && tabuleiro[i][j] != O) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean jogar(int numero, int simbolo) {
        if (numero <= 0 || numero > 225) {
            return false;
        }
        numero--;
        int linha = numero / 15;
        int coluna = numero % 15;

        if (tabuleiro[linha][coluna] == X || tabuleiro[linha][coluna] == O) {
            return false;
        }
        tabuleiro[linha][coluna] = simbolo;
        return true;
    }

    public int[][] getTabuleiro() {
        return tabuleiro;
    }

    public void setTabuleiro(int[][] tabuleiro) {
        this.tabuleiro = tabuleiro;
    }
}
