<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.ArrayList, dados_persistentes.GerirJogadores, clientSerial.JogadorPerfil" %>
<%@ page import="org.json.simple.JSONArray" %>
<%
    out.clear();

    List<String> playerNames = new ArrayList<>();
    GerirJogadores gestor = new GerirJogadores();

    try {
        List<JogadorPerfil> jogadores = gestor.carregarJogadoresDoXML();

        for (JogadorPerfil jogador : jogadores) {
            playerNames.add(jogador.getNickname());
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    StringBuilder json = new StringBuilder();
    json.append("[");
    for (int i = 0; i < playerNames.size(); i++) {
        json.append("\"").append(playerNames.get(i)).append("\"");
        if (i < playerNames.size() - 1) {
            json.append(",");
        }
    }
    json.append("]");

    out.print(json.toString());
%>
