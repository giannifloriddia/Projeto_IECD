<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.io.File, java.nio.file.Paths" %>
<%@ page import="jakarta.servlet.http.Part" %>
<%@ page import="dados_persistentes.JogadoresDataBase, dados_persistentes.GerirJogadores" %>
<%@ page import="clientSerial.JogadorPerfil" %>
<%@ page trimDirectiveWhitespaces="true" %>

<%
    String nicknameSessao = (String) session.getAttribute("nickname");
    if (nicknameSessao == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    GerirJogadores gestor = new GerirJogadores();

    if ("POST".equalsIgnoreCase(request.getMethod())) {
        try {
            gestor.carregarJogadoresDoXML();
            JogadorPerfil jogadorParaAtualizar = gestor.encontrarJogador(nicknameSessao);

            if (jogadorParaAtualizar != null) {
                // Atualizar o Nickname (se alterado)
                String novoNickname = request.getParameter("nickname");
                if (novoNickname != null && !novoNickname.trim().isEmpty() && (novoNickname.equals(nicknameSessao) || gestor.encontrarJogador(novoNickname) == null)) {
                    jogadorParaAtualizar.setNickname(novoNickname.trim());
                }

                String novaDataNascimento = request.getParameter("dataNascimento");
                jogadorParaAtualizar.setDataNascimento(novaDataNascimento);

                String novaCor = request.getParameter("corPreferida");
                jogadorParaAtualizar.setCor(novaCor);

                Part filePart = request.getPart("foto");
                if (filePart != null && filePart.getSize() > 0) {
                    String nomeFicheiroOriginal = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    if (!nomeFicheiroOriginal.isEmpty()) {
                        String extensao = nomeFicheiroOriginal.substring(nomeFicheiroOriginal.lastIndexOf("."));
                        String novoNomeFicheiro = novoNickname.trim() + "_" + System.currentTimeMillis() + extensao;
                        String pastaUploads = application.getRealPath("/photos");
                        File uploadsDir = new File(pastaUploads);
                        if (!uploadsDir.exists()) uploadsDir.mkdirs();
                        String caminhoGuardar = pastaUploads + File.separator + novoNomeFicheiro;
                        filePart.write(caminhoGuardar);
                        jogadorParaAtualizar.setFotografia(novoNomeFicheiro);
                    }
                }

                // 5. Salvar o objeto atualizado
                JogadoresDataBase.salvarJogador(jogadorParaAtualizar);

                // Se o nickname foi alterado, atualiza a sessão
                if (!novoNickname.trim().equals(nicknameSessao)) {
                    session.setAttribute("nickname", novoNickname.trim());
                }

                session.setAttribute("formMessage", "Perfil atualizado com sucesso!");
                session.setAttribute("formMessageType", "success");
            }
            
        } catch (Exception e) {
            session.setAttribute("formMessage", "Erro ao atualizar o perfil: " + e.getMessage());
            session.setAttribute("formMessageType", "error");
            e.printStackTrace();
        }

        response.sendRedirect(request.getRequestURI());
        return;
    }

    String message = "";
    String messageType = "";
    if (session.getAttribute("formMessage") != null) {
        message = (String) session.getAttribute("formMessage");
        messageType = (String) session.getAttribute("formMessageType");
        session.removeAttribute("formMessage");
        session.removeAttribute("formMessageType");
    }

    gestor.carregarJogadoresDoXML();
    JogadorPerfil jogadorAtual = gestor.encontrarJogador(nicknameSessao);

    // Valores padrão para evitar erros
    String nicknameAtual = (jogadorAtual != null) ? jogadorAtual.getNickname() : "";
    String dataNascimentoAtual = (jogadorAtual != null && jogadorAtual.getDatanascimento() != null) ? jogadorAtual.getDatanascimento() : "";
    String fotoPerfil = (jogadorAtual != null && jogadorAtual.getFoto() != null) ? jogadorAtual.getFoto() : "default_avatar.jpeg";
    String corPreferida = (jogadorAtual != null && jogadorAtual.getCor() != null) ? jogadorAtual.getCor() : "#e0f7fa";
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Definições de Perfil</title>
<style>
body { font-family: Arial, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); margin: 0; padding: 20px; display: flex; justify-content: center; align-items: center; min-height: 100vh; }
.container { background: white; border-radius: 15px; box-shadow: 0 10px 30px rgba(0,0,0,0.3); padding: 30px; width: 100%; max-width: 600px; }
h1 { color: #333; }
.form-group { margin-bottom: 20px; }
label { display: block; margin-bottom: 5px; color: #333; font-weight: bold; }
input[type="text"], input[type="file"], input[type="color"], input[type="date"] { width: 100%; padding: 12px; border: 2px solid #ddd; border-radius: 8px; font-size: 16px; box-sizing: border-box; }
.btn { padding: 12px 20px; background: #28a745; color: white; border: none; border-radius: 8px; font-size: 16px; font-weight: bold; cursor: pointer; text-decoration: none; display: inline-block; margin-top: 10px; }
.btn-back { background: #6c757d; }
.profile-pic { width: 100px; height: 100px; border-radius: 50%; object-fit: cover; border: 3px solid #ddd; margin-bottom: 15px; }
.message { padding: 10px; border-radius: 5px; margin-bottom: 15px; }
.success { background: #d4edda; color: #155724; }
.error { background: #f8d7da; color: #721c24; }
</style>
</head>
<body>

<div class="container">
    <h1>Definições de Perfil</h1>
    <p>Olá, <strong><%= nicknameSessao %></strong>. Altere os seus dados abaixo.</p>

    <% if (!message.isEmpty()) { %>
        <div class="message <%= messageType %>"><%= message %></div>
    <% } %>

    <form method="post" enctype="multipart/form-data">
        <div class="form-group" style="text-align: center;">
            <img src="<%= request.getContextPath() %>/photos/<%= fotoPerfil %>" alt="Foto de Perfil" class="profile-pic" onerror="this.src='<%= request.getContextPath() %>/photos/default_avatar.jpeg';">
            <label for="foto">Alterar Foto de Perfil</label>
            <input type="file" id="foto" name="foto" accept="image/*">
        </div>
        <div class="form-group">
            <label for="nickname">Nickname</label>
            <input type="text" id="nickname" name="nickname" value="<%= nicknameAtual %>" required>
        </div>

        <div class="form-group">
            <label for="dataNascimento">Data de Nascimento</label>
            <input type="date" id="dataNascimento" name="dataNascimento" value="<%= dataNascimentoAtual %>">
        </div>
        
        <div class="form-group">
            <label for="corPreferida">Cor de Fundo Preferida para o Jogo</label>
            <input type="color" id="corPreferida" name="corPreferida" value="<%= corPreferida %>">
        </div>

        <button type="submit" class="btn">Guardar Alterações</button>
        <a href="login.jsp" class="btn btn-back">Voltar</a>
    </form>
</div>

</body>
</html>
