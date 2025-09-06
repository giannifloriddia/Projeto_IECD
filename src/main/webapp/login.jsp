<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.io.*, java.net.*, clientSerial.Jogador" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Jogo Online - Sala de Espera</title>
    <link rel="stylesheet" href="https://code.jquery.com/ui/1.13.2/themes/base/jquery-ui.css">
    <script src="https://code.jquery.com/jquery-3.6.0.js"></script>
    <script src="https://code.jquery.com/ui/1.13.2/jquery-ui.js"></script>

    <style>
        /* O seu CSS original de paste.txt continua aqui... */
        body { font-family: Arial, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); margin: 0; padding: 20px; min-height: 100vh; display: flex; justify-content: center; align-items: center; }
        .container { background: white; border-radius: 15px; box-shadow: 0 10px 30px rgba(0,0,0,0.3); padding: 30px; width: 100%; max-width: 600px; }
        .logo { text-align: center; margin-bottom: 30px; }
        .logo h1 { color: #333; margin: 0; font-size: 2.5em; }
        .logo p { color: #666; margin: 5px 0 0 0; }
        .form-group { margin-bottom: 20px; }
        label { display: block; margin-bottom: 5px; color: #333; font-weight: bold; }
        input[type="text"], input[type="password"] { width: 100%; padding: 12px; border: 2px solid #ddd; border-radius: 8px; font-size: 16px; box-sizing: border-box; transition: border-color 0.3s; }
        input[type="text"]:focus, input[type="password"]:focus { outline: none; border-color: #667eea; }
        .btn { padding: 12px 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; border-radius: 8px; font-size: 16px; font-weight: bold; cursor: pointer; transition: transform 0.2s; margin: 5px; text-decoration: none; display: inline-block; }
        .btn:hover { transform: translateY(-2px); }
        .btn-full { width: 100%; }
        .btn-logout { background: #dc3545; }
        .btn-settings { background: #17a2b8; }
        
        /* NOVO ESTILO PARA O BOTÃO DO QUADRO DE HONRA */
        .btn-honor { background: #f1c40f; }

        .game-controls { display: flex; gap: 10px; margin: 20px 0; align-items: center; }
        .server-response { background-color: #f8f9fa; border: 1px solid #dee2e6; border-radius: 8px; padding: 15px; margin: 15px 0; font-family: monospace; white-space: pre-wrap; max-height: 300px; overflow-y: auto; font-size: 14px; }
        .message { margin: 20px 0; padding: 15px; border-radius: 8px; text-align: center; font-weight: bold; }
        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .warning { background-color: #fff3cd; color: #856404; border: 1px solid #ffeaa7; }
        .ui-autocomplete { max-height: 200px; overflow-y: auto; overflow-x: hidden; }
    </style>
</head>
<body>

<div class="container">
    <div class="logo">
        <h1>🎮</h1>
        <h1>Jogo Online</h1>
        <p>Sala de Espera - Multiplayer</p>
    </div>

    <%
    String action = request.getParameter("action");
    String nickname = request.getParameter("nickname");
    String password = request.getParameter("password");
    String desafiarQuem = request.getParameter("desafiarQuem");
    String message = "";
    String messageType = "";
    String serverOutput = "";
    Jogador jogador = null;
    boolean isConnected = false;
    boolean gameStarting = false;
    boolean shouldRefresh = false;
    
    // Recuperar jogador da sessão se existir
    if (session.getAttribute("jogador") != null) {
        jogador = (Jogador) session.getAttribute("jogador");
        isConnected = true;
    }
    
    if ("login".equals(action) && nickname != null && password != null) {
        try {
            jogador = new Jogador();
            
            // Enviar credenciais
            String loginInfo = nickname + "|" + password;
            jogador.sendMessage(loginInfo);
            
            // Receber resposta do servidor
            Object resposta = jogador.getMessage();
            if (resposta instanceof String) {
                String respostaServidor = (String) resposta;
                
                if (respostaServidor.contains("bem-sucedido") || respostaServidor.contains("sucesso")) {
                    message = "Login realizado com sucesso!";
                    messageType = "success";
                    isConnected = true;
                    
                    // Armazenar na sessão
                    session.setAttribute("jogador", jogador);
                    session.setAttribute("nickname", nickname);
                    session.setAttribute("connected", true);
                    
                    // Ler mensagem inicial do servidor
                    try {
                        Object initialMsg = jogador.getMessage();
                        if (initialMsg instanceof String) {
                            serverOutput = (String) initialMsg;
                        }
                    } catch (Exception e) {
                        serverOutput = "Aguardando instruções do servidor...";
                    }
                    
                } else {
                    message = "Falha no login: " + respostaServidor;
                    messageType = "error";
                }
            }
            
        } catch (Exception e) {
            message = "Erro ao conectar: " + e.getMessage();
            messageType = "error";
        }
    }
    
    if ("listar".equals(action) && jogador != null) {
        try {
            jogador.sendMessage("/listar");
            Object resposta = jogador.getMessage();
            if (resposta instanceof String) {
                serverOutput = (String) resposta;
                message = "Lista de jogadores atualizada";
                messageType = "success";
            }
        } catch (Exception e) {
            message = "Erro ao listar jogadores: " + e.getMessage();
            messageType = "error";
        }
    }
    
    if ("desafiar".equals(action) && desafiarQuem != null && !desafiarQuem.isEmpty() && jogador != null) {
        try {
            jogador.sendMessage("/desafiar " + desafiarQuem);
            Object resposta = jogador.getMessage();
            if (resposta instanceof String) {
                serverOutput = (String) resposta;
                
                if (serverOutput.contains("Iniciando jogo") || serverOutput.contains("Emparelhamento confirmado")) {
                    gameStarting = true;
                    session.setAttribute("gameStarting", true);
                    response.setHeader("Refresh", "2; URL=game.jsp");
                    message = "Jogo iniciando! Redirecionando...";
                    messageType = "success";
                } else {
                    message = "Desafio enviado para " + desafiarQuem;
                    messageType = "warning";
                    // Forçar refresh da página para verificar status
                    shouldRefresh = true;
                }
            }
        } catch (Exception e) {
            message = "Erro ao enviar desafio: " + e.getMessage();
            messageType = "error";
        }
    }
    
    // Logout
    if ("logout".equals(action)) {
        try {
            if (jogador != null && jogador.socket != null) {
                jogador.socket.close();
            }
        } catch (Exception e) {
        }
        session.invalidate();
        response.sendRedirect(request.getRequestURI());
        return;
    }
    %>

    <% if (!isConnected) { %>
        <form method="post">
            <input type="hidden" name="action" value="login">
            <div class="form-group">
                <label for="nickname">Nickname:</label>
                <input type="text" id="nickname" name="nickname" required>
            </div>
            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required>
            </div>
            <button type="submit" class="btn btn-full">Conectar ao Servidor</button>
        </form>
    <% } %>

    <% if (isConnected && !gameStarting) { %>
    <div class="waiting-area">
        <h3>Bem-vindo, <%= session.getAttribute("nickname") %>!</h3>
        
        <!-- Ações do Jogador -->
        <div>
            <form method="post" style="display: inline;">
                <input type="hidden" name="action" value="listar">
                <button type="submit" class="btn">📋 Listar Jogadores</button>
            </form>
            <a href="settings.jsp" class="btn btn-settings">⚙️ Definições</a>
            <a href="quadro_honra.jsp" class="btn btn-honor">🏆 Quadro de Honra</a>
        </div>
        
        <!-- Desafiar Jogador com Autocomplete -->
        <form method="post" style="margin: 20px 0;">
            <input type="hidden" name="action" value="desafiar">
            <div class="game-controls">
                <input type="text" id="desafioInput" name="desafiarQuem" placeholder="Nome do adversário" required style="flex: 1;">
                <button type="submit" class="btn">⚔️ Desafiar</button>
            </div>
        </form>
        
        <!-- Resposta do servidor -->
        <% if (serverOutput != null && !serverOutput.isEmpty()) { %>
        <div class="server-response"><%= serverOutput.replace("\n", "<br>") %></div>
        <% } %>
        
        <!-- Logout  -->
        <form method="post" style="margin-top: 30px;">
            <input type="hidden" name="action" value="logout">
            <button type="submit" class="btn btn-logout">🚪 Sair</button>
        </form>
    </div>
    <% } %>

</div>

<script>
$(function() {
    $("#desafioInput").autocomplete({
        source: "getPlayers.jsp",
        minLength: 1
    });
});
</script>

</body>
</html>
