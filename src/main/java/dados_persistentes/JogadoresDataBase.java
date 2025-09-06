package dados_persistentes;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import clientSerial.JogadorPerfil;

public class JogadoresDataBase {
    //private static final String XML = "src\\main\\webapp\\WEB-INF\\data\\Jogadores.xml";
    private static final String XML = "C:\\Users\\giann\\Ambiente de Trabalho\\Eu\\NewEclipseWorkspace\\IECD_P1\\src\\main\\webapp\\WEB-INF\\data\\Jogadores.xml";

    public static List<JogadorPerfil> carregar() {
        List<JogadorPerfil> jogadores = new ArrayList<>();

        try {
            File ficheiro = new File(XML);
            if (!ficheiro.exists()) {
				return jogadores;
			}

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(ficheiro);
            doc.getDocumentElement().normalize();

            NodeList listaJogadores = doc.getElementsByTagName("jogador");

            for (int i = 0; i < listaJogadores.getLength(); i++) {
                Node node = listaJogadores.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) node;

                    String nickname = elem.getElementsByTagName("nickname").item(0).getTextContent();
                    String password = elem.getElementsByTagName("password").item(0).getTextContent();
                    String nacionalidade = elem.getElementsByTagName("nacionalidade").item(0).getTextContent();
                    String fotografia = elem.getElementsByTagName("fotografia").item(0).getTextContent().trim();
                    String cor = elem.getElementsByTagName("cor-preferida").item(0).getTextContent().trim();
                    int vitorias = Integer.parseInt(elem.getElementsByTagName("vitorias").item(0).getTextContent());
                    int derrotas = Integer.parseInt(elem.getElementsByTagName("derrotas").item(0).getTextContent());
                    Double tempoJogo = desformatarTempo(elem.getElementsByTagName("tempoJogo").item(0).getTextContent());
                    String dataNascimento = elem.getElementsByTagName("dataNascimento").item(0).getTextContent();
                    
                    JogadorPerfil jogador = new JogadorPerfil(nickname, dataNascimento, password, fotografia, nacionalidade,vitorias,derrotas,tempoJogo, cor);

                    jogadores.add(jogador);
                    //System.out.println(jogador.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jogadores;
    }

    public static void salvarJogador(JogadorPerfil novoJogador) {
        try {
            List<JogadorPerfil> jogadores = carregar();

            boolean jogadorExiste = false;

            // Verifica se já existe um jogador com o mesmo nickname
            for (int i = 0; i < jogadores.size(); i++) {
                JogadorPerfil j = jogadores.get(i);
                if (j.getNickname().equals(novoJogador.getNickname())) {
                    // Atualiza os dados do jogador existente
                    jogadores.set(i, novoJogador);
                    jogadorExiste = true;
                    break;
                }
            }

            // Se não existir, adiciona como novo
            if (!jogadorExiste) {
                jogadores.add(novoJogador);
            }

            // Escrever novamente todos no XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            Element raiz = doc.createElement("jogadores");
            doc.appendChild(raiz);

            for (JogadorPerfil j : jogadores) {
                Element jogador = doc.createElement("jogador");
                raiz.appendChild(jogador);

                jogador.appendChild(criarElemento(doc, "nickname", j.getNickname()));
                jogador.appendChild(criarElemento(doc, "password", j.getPasswordHash()));
                jogador.appendChild(criarElemento(doc, "nacionalidade", j.getNacionalidade()));
                jogador.appendChild(criarElemento(doc, "fotografia", j.getFoto()));
                jogador.appendChild(criarElemento(doc, "cor-preferida", j.getCor()));
                jogador.appendChild(criarElemento(doc, "vitorias", String.valueOf(j.getVitorias())));
                jogador.appendChild(criarElemento(doc, "derrotas", String.valueOf(j.getDerrotas())));
                jogador.appendChild(criarElemento(doc, "tempoJogo", formatarTempo(j.getTempo())));
                jogador.appendChild(criarElemento(doc, "dataNascimento", j.getDatanascimento()));
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(XML));
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String formatarTempo(double tempo) {

    	int totalSegundos = (int) Math.round(tempo / 1000);
        int minutos = totalSegundos / 60;
        int segundos = totalSegundos % 60;

        return String.format("%02d:%02d", minutos, segundos);

    }

    private static double desformatarTempo(String tempo) {

        String[] partes = tempo.split(":");
        int minutos = Integer.parseInt(partes[0]);
        int segundos = Integer.parseInt(partes[1]);

        double milissegundos = (minutos * 60 + segundos) * 1000;

        return milissegundos;
    }


    private static Element criarElemento(Document doc, String nome, String valor) {
        Element elem = doc.createElement(nome);
        elem.appendChild(doc.createTextNode(valor));
        return elem;
    }
    
    public static File criarBackupQuadroDeHonra(List<JogadorPerfil> listaOrdenada, String caminhoPasta) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        Element rootElement = doc.createElement("quadroDeHonra");
        
        // Adiciona um atributo com a data do backup
        String dataHoje = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        rootElement.setAttribute("dataBackup", dataHoje);
        
        doc.appendChild(rootElement);

        int rank = 1;
        for (JogadorPerfil jogador : listaOrdenada) {
            // Cria o elemento <jogador> com o seu ranking
            Element jogadorElement = doc.createElement("jogador");
            jogadorElement.setAttribute("rank", String.valueOf(rank));
            
            criarElemento(doc, jogadorElement, "nickname", jogador.getNickname());
            criarElemento(doc, jogadorElement, "vitorias", String.valueOf(jogador.getVitorias()));
            criarElemento(doc, jogadorElement, "tempoJogo", formatarTempo(jogador.getTempo()));
            criarElemento(doc, jogadorElement, "nacionalidade", jogador.getNacionalidade());
            
            rootElement.appendChild(jogadorElement);
            rank++;
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes"); // Para indentação
        DOMSource source = new DOMSource(doc);

        String nomeFicheiro = "quadro_honra_" + dataHoje + ".xml";
        File ficheiroBackup = new File(caminhoPasta, nomeFicheiro);

        StreamResult result = new StreamResult(ficheiroBackup);
        transformer.transform(source, result);
        
        return ficheiroBackup;
    }
    
    private static void criarElemento(Document doc, Element pai, String nomeTag, String valor) {
        Element elemento = doc.createElement(nomeTag);
        elemento.appendChild(doc.createTextNode(valor));
        pai.appendChild(elemento);
    }
    
}