<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.io.*, java.net.*, clientSerial.Jogador, clientSerial.Jogo, clientSerial.JogadorPerfil, dados_persistentes.GerirJogadores" %>

<%!
    private String obterClassePeca(int valor) {
        if (valor == Jogo.X) return "preta";
        if (valor == Jogo.O) return "branca";
        return "";
    }
%>

<%
	Jogador jogo = (Jogador) session.getAttribute("jogador");
	String nickname = (String) session.getAttribute("nickname");
	
	if (jogo == null) {
	    response.sendRedirect("login.jsp");
	    return;
	}
	
	String corFundo = "#d3d3d3";
	try {
	    GerirJogadores gestor = new GerirJogadores();
	    gestor.carregarJogadoresDoXML();
	    JogadorPerfil jogadorPerfil = gestor.encontrarJogador(nickname);
	    
	    if (jogadorPerfil != null && jogadorPerfil.getCor() != null && !jogadorPerfil.getCor().isEmpty()) {
	        corFundo = jogadorPerfil.getCor();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

    String mensagemStatus = "";
    boolean eMinhaVez = false;
    boolean jogoTerminou = false;
    Integer meuSimbolo = (Integer) session.getAttribute("meuSimbolo");

    try {
        if (meuSimbolo == null) {
            Object obj;
            do {
                obj = jogo.getMessage();
            } while (obj instanceof String);

            if (obj instanceof Integer) {
                meuSimbolo = (Integer) obj;
                session.setAttribute("meuSimbolo", meuSimbolo);
                eMinhaVez = (meuSimbolo == Jogo.X);
                session.setAttribute("eMinhaVez", eMinhaVez);
                mensagemStatus = eMinhaVez ? "Jogo iniciado! É a sua vez." : "Jogo iniciado! Aguarde o adversário...";
            } else {
                throw new IllegalStateException("Erro: Esperava um símbolo (Integer), mas recebi " + obj.getClass());
            }
        } else {
             eMinhaVez = (Boolean) session.getAttribute("eMinhaVez");
             mensagemStatus = eMinhaVez ? "É a sua vez." : "Aguarde o adversário...";
        }

        String action = request.getParameter("action");
        
        if ("fazer_jogada".equals(action) && eMinhaVez) {
            int pos = Integer.parseInt(request.getParameter("pos"));
            if (jogo.jogar(pos + 1, meuSimbolo)) {
                jogo.sendMessage(jogo);
                session.setAttribute("eMinhaVez", false);
                response.sendRedirect(request.getRequestURI());
                return;
            } else {
                mensagemStatus = "Posição inválida ou já ocupada. Tente novamente.";
            }
        }
        
        if (jogo.terminou()) {
            jogoTerminou = true;
        }

        if (!eMinhaVez && !jogoTerminou) {
            Jogador jogoAtualizadoDoServidor = (Jogador) jogo.getMessage();
            jogo.setTabuleiro(jogoAtualizadoDoServidor.getTabuleiro());
            eMinhaVez = true;
            session.setAttribute("eMinhaVez", true);
            mensagemStatus = "Adversário jogou! É a sua vez.";
        }

        if (jogo.terminou() || jogo.getTerminou()) {
            jogoTerminou = true;
            eMinhaVez = false;
            if (jogo.vitoria(Jogo.X)) mensagemStatus = "Fim de jogo: Vitória das Pretas (X)!";
            else if (jogo.vitoria(Jogo.O)) mensagemStatus = "Fim de jogo: Vitória das Brancas (O)!";
            else if (jogo.empate()) mensagemStatus = "Fim de jogo: Empate!";
            else mensagemStatus = "Fim de jogo: Excedeu o tempo de jogada!";
        }

    } catch (Exception e) {
        mensagemStatus = "Erro de comunicação: " + e.getMessage();
        jogoTerminou = true;
        e.printStackTrace();
    }
    
    final int TAMANHO_LOGICO = 15;
    final int MARGEM_VISUAL = 1;
    final int TAMANHO_VISUAL = TAMANHO_LOGICO + 2 * MARGEM_VISUAL;
    final double LARGURA_TABULEIRO_PX = 640.0;
    final double ESPACAMENTO = LARGURA_TABULEIRO_PX / (TAMANHO_VISUAL - 1);
%>
<!DOCTYPE html>
<html lang="pt">
<head>
    <meta charset="UTF-8" />
    <title>Gobang - Jogo Online</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { background-color:<%= corFundo %>; font-family: Arial, sans-serif; min-height: 100vh; display: flex; flex-direction: column; align-items: center; padding: 20px 0; }
        nav { display: flex; justify-content: space-between; align-items: center; background-color: #333; width: 100%; padding: 0 20px; height: 60px; position: fixed; top: 0; left: 0; z-index: 100; }
        .logo { display: flex; align-items: center; color: white; font-size: 24px; font-weight: bold; }
        .logo-icon { width: 36px; height: 36px; margin-right: 10px; background-color: #4CAF50; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: white; font-size: 20px; }
        .logout-button { color: white; font-size: 16px; cursor: pointer; background-color: #dc3545; border: none; border-radius: 5px; padding: 8px 15px; text-decoration: none; }
        .logout-button:hover { background-color: #c82333; }
        .game-container { margin-top: 80px; display: flex; flex-direction: column; align-items: center; gap: 10px; }
        .player-info { width: 640px; display: flex; justify-content: space-between; align-items: center; background-color: #222; color: white; padding: 10px; border-radius: 5px; }
        .player { display: flex; align-items: center; }
        .player-avatar { width: 40px; height: 40px; background-color: #555; border-radius: 5px; margin-right: 10px; display: flex; align-items: center; justify-content: center; }
        .player-name { font-weight: bold; }
        .tabuleiro { position: relative; width: <%= LARGURA_TABULEIRO_PX %>px; height: <%= LARGURA_TABULEIRO_PX %>px; background-color: #f1d28f; border: 2px solid #333; }
        .linha-horizontal, .linha-vertical { position: absolute; background-color: #333; }
        .linha-horizontal { height: 1px; width: 100%; }
        .linha-vertical { width: 1px; height: 100%; }
        .ponto { position: absolute; width: 8px; height: 8px; background-color: #000; border-radius: 50%; transform: translate(-50%, -50%); }
        .peca { position: absolute; width: 32px; height: 32px; border-radius: 50%; transform: translate(-50%, -50%); z-index: 2; }
        .preta { background-color: black; }
        .branca { background-color: white; border: 1px solid #333; }
        .jogada-link { position: absolute; width: 32px; height: 32px; display: block; border-radius: 50%; transform: translate(-50%, -50%); z-index: 3; cursor: pointer; }
        .jogada-link:hover { background-color: rgba(0, 123, 255, 0.3); }
        .status { width: 640px; text-align: center; padding: 12px; border-radius: 5px; font-weight: bold; font-size: 1.1em; }
        .my-turn { background-color: #d4edda; color: #155724; }
        .opponent-turn { background-color: #fff3cd; color: #856404; }
        .game-over { background-color: #f8d7da; color: #721c24; }

        /* === NOVOS ESTILOS PARA BLOQUEIO E FEEDBACK VISUAL === */
        .tabuleiro.bloqueado { cursor: not-allowed; }
        .overlay-bloqueio {
            position: absolute;
            top: 0; left: 0;
            width: 100%; height: 100%;
            z-index: 5; /* Fica por cima dos links de jogada */
            display: none; /* Começa escondido */
        }
        .peca-fantasma {
            opacity: 0.6;
            pointer-events: none; /* A peça fantasma não pode ser clicada */
        }
    </style>
</head>
<body>
    <nav>
        <div class="logo">
            <div class="logo-icon">G</div>
            <span>Gobang.com</span>
        </div>
        <div class="settings">
            <a href="login.jsp?action=logout" class="logout-button">Sair</a>
        </div>
    </nav>
	
	 <div class="game-container">
        <div class="player-info">
            <div class="player">
                <div class="player-avatar">👤</div>
                <div class="player-name">Adversário joga com: <%= meuSimbolo != null ? (meuSimbolo == Jogo.X ? "O" : "X") : "..." %></div>
            </div>
        </div>

        <div class="status <%= jogoTerminou ? "game-over" : (eMinhaVez ? "my-turn" : "opponent-turn") %>">
            <%= mensagemStatus %>
        </div>

        <!-- Tabuleiro do Jogo com o overlay de bloqueio -->
        <div class="tabuleiro" id="tabuleiro">
            <div class="overlay-bloqueio" id="overlay"></div>
            <%
                for (int i = 0; i < TAMANHO_VISUAL; i++) {
                    double pos = i * ESPACAMENTO;
            %><div class="linha-horizontal" style="top: <%= pos %>px;"></div><div class="linha-vertical" style="left: <%= pos %>px;"></div><%
                }
                int[] hoshiCoords = {3, 7, 11};
                for (int i : hoshiCoords) {
                    for (int j : hoshiCoords) {
                        double top = (i + MARGEM_VISUAL) * ESPACAMENTO;
                        double left = (j + MARGEM_VISUAL) * ESPACAMENTO;
            %><div class="ponto" style="top: <%= top %>px; left: <%= left %>px;"></div><%
                    }
                }
                int[][] tabuleiro = jogo.getTabuleiro();
                for (int i = 0; i < TAMANHO_LOGICO; i++) {
                    for (int j = 0; j < TAMANHO_LOGICO; j++) {
                        int valor = tabuleiro[i][j];
                        int posicaoLinear = i * TAMANHO_LOGICO + j;
                        double top = (i + MARGEM_VISUAL) * ESPACAMENTO;
                        double left = (j + MARGEM_VISUAL) * ESPACAMENTO;
                        if (valor == Jogo.X || valor == Jogo.O) {
            %><div class="peca <%= obterClassePeca(valor) %>" style="top: <%= top %>px; left: <%= left %>px;"></div><%
                        } else if (eMinhaVez && !jogoTerminou) {
            %><a href="?action=fazer_jogada&pos=<%= posicaoLinear %>" class="jogada-link" style="top: <%= top %>px; left: <%= left %>px;"></a><%
                        }
                    }
                }
            %>
        </div>
		
		<div class="player-info">
            <div class="player">
                <div class="player-avatar">👤</div>
                <div class="player-name"><%= nickname %> joga com: <%= meuSimbolo != null ? (meuSimbolo == Jogo.X ? "X" : "O") : "..." %></div>
            </div>
        </div>
        
        <% if (jogoTerminou) { %>
            <a href="login.jsp" class="logout-button" style="margin-top: 10px; background-color: #007bff;">Jogar Novamente</a>
        <% } %>
    </div>
	
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const tabuleiro = document.getElementById('tabuleiro');
            const overlay = document.getElementById('overlay');
            const linksJogada = document.querySelectorAll('.jogada-link');

            // Determinar a classe da peça do jogador atual (preta ou branca)
            const classePecaJogador = '<%= meuSimbolo != null ? obterClassePeca(meuSimbolo) : "" %>';

            linksJogada.forEach(link => {
                link.addEventListener('click', function(event) {
                    // impede click
                    event.preventDefault();

                    // desenha peça "fantasma"
                    if (classePecaJogador) {
                        const pecaFantasma = document.createElement('div');
                        pecaFantasma.className = 'peca peca-fantasma ' + classePecaJogador;
                        pecaFantasma.style.top = this.style.top;
                        pecaFantasma.style.left = this.style.left;
                        tabuleiro.appendChild(pecaFantasma);
                    }

                    // bloqueia o tabuleiro
                    tabuleiro.classList.add('bloqueado');
                    overlay.style.display = 'block';

                    // só depois navega para URL
                    const urlDestino = this.href;
                    setTimeout(function() {
                        window.location.href = urlDestino;
                    }, 50); // Pequeno delay (50ms) para garantir que o browser desenha as alterações
                });
            });
        });
    </script>
</body>
</html>
