package clientSerial;


public class JogadorPerfil {
    private String nickname;
    private String passwordHash;
    private String nacionalidade;
    private String datanascimento;
    private String cor;
    private int registoderrota;
    private int registovitoria;
    private String foto;
    private double tempo;

    public JogadorPerfil(String nickname, String datanascimento, String passwordHash,String foto, String nacionalidade) {

        this.nickname=nickname ;
        this.passwordHash=passwordHash;
        this.nacionalidade = nacionalidade;
        this.datanascimento=datanascimento;

        this.registoderrota = 0;
        this.registovitoria = 0;

        this.foto = foto;

        this.tempo = 0.0f;

        this.cor = "#d3d3d3";

    }

    public JogadorPerfil(String nickname, String datanascimento, String passwordHash,String foto, String nacionalidade, int registovitoria, int registoderrota, Double tempo, String cor) {

        this.nickname=nickname ;
        this.passwordHash=passwordHash;
        this.nacionalidade = nacionalidade;
        this.datanascimento=datanascimento;
        this.registoderrota = registoderrota;
        this.registovitoria = registovitoria;
        this.tempo = tempo;
        this.foto = foto;

        this.cor = cor;

    }
    
    public void setDataNascimento(String data) {
    	this.datanascimento = data;
    }

	public String getCor() {
    	return this.cor;
    }

    public void setCor(String cor) {
    	this.cor = cor;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getNickname() {
    	return nickname;
    }

    public void setPassword(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setFotografia(String foto) {
        this.foto = foto;
    }

    public void printInfoCompleta() {
        System.out.println(this.toString());
    }

    public void incrementar_vitorias() {
    	registovitoria++;
    }

    public void incrementar_derrotas() {
    	registoderrota++;
    }

    public void adicionar_tempo(double tempo_partida) {
    	tempo += tempo_partida;
    }

    @Override
    public String toString() {
        return String.format("Jogador: %s\nNacionalidade: %s\nData de Nascimento: %s\nFoto: %s\nVitorias: %d\nDerrotas: %d\nTempo de Jogo: %.2f\n",
                this.nickname, this.nacionalidade, this.datanascimento, this.foto, this.registovitoria, this.registoderrota, this.tempo);
    }

	public String getNacionalidade() {
		return nacionalidade;
	}

	public int getVitorias() {
		// TODO Auto-generated method stub
		return registovitoria;
	}

	public int getDerrotas() {
		// TODO Auto-generated method stub
		return registoderrota;
	}

	public String getFoto() {
		// TODO Auto-generated method stub
		return foto;
	}

	public String getDatanascimento() {
		// TODO Auto-generated method stub
		return datanascimento;
	}

	public double getTempo() {
		return tempo;
	}




}
