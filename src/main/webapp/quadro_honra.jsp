<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, java.io.*, java.time.LocalDate, java.time.format.DateTimeFormatter" %>
<%@ page import="clientSerial.JogadorPerfil, dados_persistentes.GerirJogadores, dados_persistentes.JogadoresDataBase" %>

<%
	GerirJogadores gestor = new GerirJogadores();
    List<JogadorPerfil> listaJogadores = gestor.carregarJogadoresDoXML();

    if (listaJogadores != null) {
        listaJogadores.sort((j1, j2) -> {
            int comparacaoVitorias = Integer.compare(j2.getVitorias(), j1.getVitorias());
            if (comparacaoVitorias != 0) return comparacaoVitorias;
            return Double.compare(j1.getTempo(), j2.getTempo());
        });
    }

    if ("fazer_backup".equals(request.getParameter("action"))) {
        try {
            String caminhoPastaBackups = application.getRealPath("/WEB-INF/backups");
            File pastaBackups = new File(caminhoPastaBackups);
            if (!pastaBackups.exists()) {
                pastaBackups.mkdirs();
            }

            File ficheiroBackup = JogadoresDataBase.criarBackupQuadroDeHonra(listaJogadores, caminhoPastaBackups);

            response.setContentType("application/xml");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + ficheiroBackup.getName() + "\"");
            
            try (InputStream in = new FileInputStream(ficheiroBackup); 
            OutputStream outw = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    outw.write(buffer, 0, bytesRead);
                }
            }
            return;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quadro de Honra</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
            margin: 0;
            padding: 40px;
            color: #ecf0f1;
            display: flex;
            justify-content: center;
        }
        .container {
            width: 100%;
            max-width: 900px;
            background: rgba(255, 255, 255, 0.05);
            border-radius: 20px;
            padding: 30px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.5);
            border: 1px solid rgba(255, 255, 255, 0.1);
            backdrop-filter: blur(10px);
        }
        h1 {
            text-align: center;
            font-size: 3em;
            margin-bottom: 20px;
            text-transform: uppercase;
            letter-spacing: 3px;
            text-shadow: 0 0 15px rgba(255, 215, 0, 0.5);
        }
        .leaderboard {
            width: 100%;
            border-collapse: collapse;
        }
        .leaderboard th, .leaderboard td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid rgba(255, 255, 255, 0.2);
        }
        .leaderboard th {
            font-size: 1.2em;
            color: #f1c40f;
            text-transform: uppercase;
        }
        .leaderboard tr:last-child td {
            border-bottom: none;
        }
        .leaderboard tr:hover {
            background-color: rgba(255, 255, 255, 0.1);
        }
        .rank {
            font-size: 1.5em;
            font-weight: bold;
            text-align: center;
            width: 5%;
        }
        .player-info {
            display: flex;
            align-items: center;
        }
        .profile-pic {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            object-fit: cover;
            border: 2px solid #fff;
            margin-right: 15px;
        }
        .flag-pic {
            width: 30px;
            height: auto;
            margin-left: 10px;
            border-radius: 3px;
        }
        /* Estilos para o Pódio */
        .rank-1 .rank { color: #FFD700; } /* Ouro */
        .rank-2 .rank { color: #C0C0C0; } /* Prata */
        .rank-3 .rank { color: #CD7F32; } /* Bronze */

        .rank-1 { font-size: 1.1em; font-weight: bold; }
        .rank-2 { font-size: 1.05em; }
        .rank-3 { font-size: 1.02em; }
        
        .btn {
            display: inline-block;
            margin-top: 30px;
            padding: 12px 25px;
            background: #e74c3c;
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            text-decoration: none;
            transition: background-color 0.3s;
        }
        .btn:hover {
            background-color: #c0392b;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Quadro de Honra</h1>    

    <table class="leaderboard">
        <thead>
            <tr>
                <th class="rank">#</th>
                <th>Jogador</th>
                <th>Vitórias</th>
                <th>Tempo de Jogo</th>
            </tr>
        </thead>
        <tbody>
            <% if (listaJogadores != null && !listaJogadores.isEmpty()) {
                int rank = 1;
                for (JogadorPerfil jogador : listaJogadores) {
                    String rankClass = "";
                    if (rank <= 3) {
                        rankClass = "rank-" + rank;
                    }
                    String nacionalidade = jogador.getNacionalidade();
            %>
            <tr class="<%= rankClass %>">
                <td class="rank"><%= rank %></td>
                <td>
                    <div class="player-info">
						<img src="<%= request.getContextPath() %>/photos/<%= jogador.getFoto() %>" 
						     alt="Foto" 
						     class="profile-pic" 
						     onerror="this.src='<%= request.getContextPath() %>/photos/default_avatar.jpeg';">

                        
                        <span><%= jogador.getNickname() %></span>
                        
                        <% if (!nacionalidade.isEmpty()) { %>
                            <img src="flags/<%= nacionalidade %>.png" alt="<%= nacionalidade %>" title="<%= nacionalidade %>" class="flag-pic" onerror="this.style.display='none';">
                        <% } %>
                    </div>
                </td>
                <td><%= jogador.getVitorias() %></td>
                <td><%= String.format(JogadoresDataBase.formatarTempo(jogador.getTempo())) %> min</td>
            </tr>
            <%
                    rank++;
                }
            } else {
            %>
            <tr>
                <td colspan="4" style="text-align:center; padding: 30px;">Ainda não existem dados para exibir. Joguem umas partidas!</td>
            </tr>
            <% } %>
        </tbody>
    </table>
    
    <div style="text-align: right; margin-bottom: 20px;">
        <a href="?action=fazer_backup" class="btn btn-backup">💾 Salvar Cópia de Segurança (XML)</a>
    </div>

    <div style="text-align:center;">
        <a href="login.jsp" class="btn">Voltar à Sala de Espera</a>
    </div>
</div>
</body>
</html>

