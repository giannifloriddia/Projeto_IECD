# Projeto_IECD

A Java-based Gomoku game with client-server architecture, user authentication, and persistent player data.


## ✨ Features
*   **Gomoku Game:** Implements the classic Gomoku (Five in a Row) game logic.
*   **Client-Server Architecture:** Uses a client-server model for multiplayer gameplay.
*   **User Authentication:** Players can log in with a nickname and password.
*   **Player Profiles:** Stores player data such as wins, losses, play time, and profile information in XML files.
*   **Game Lobby:** Players can list available players and challenge them to a game.
*   **Persistent Data:** Player profiles are saved in XML files using DOM parsing.
*   **Real-time Gameplay:** Utilizes sockets for real-time communication between clients and the server.
*   **Automated Winner Detection:** Determines if a player won or if it is a tie.
*   **Timeout Feature:** Implements a timeout mechanic to prevent players from stalling.

## 🛠️ Tech Stack
*   **Language:** Java
*   **Data Persistence:** XML (DOM Parser)
*   **Networking:** Sockets

## 🚀 Installation
1.  **Clone the repository:**
    ```bash
    git clone https://github.com/giannifloriddia/Projeto_IECD.git
    cd Projeto_IECD
    ```
2.  **Import the project into Eclipse:**
    *   Open Eclipse.
    *   Go to `File` -> `Import` -> `Existing Projects into Workspace`.
    *   Browse to the directory where you cloned the repository and select the `Projeto_IECD` project.
    *   Click `Finish`.

3.  **Set up the XML Data Directory:**
    *   Ensure the following directory exists or create it:
        `src/main/webapp/WEB-INF/data/`
    *   The project uses `Jogadores.xml` to store players data

## 🎮 Usage
1.  **Run the Server:**
    *   In Eclipse, navigate to `src/main/java/server/ServidorTCP.java`.
    *   Run the `ServidorTCP.java` file. This will start the Gomoku server on port `5025`.
    ```java
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT);
    ```

2.  **Run the Client:**
    *   Navigate to `src/main/java/clientSerial/Jogador.java`.
    *   Run the `Jogador.java` file. This will start a Gomoku client.
    ```java
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // Leitura de informações de login
        System.out.print("Digite seu nickname: ");
        String nickname = leitor.nextLine();
        System.out.print("Digite sua password: ");
        String password = leitor.nextLine();
        Jogador jogo = new Jogador();
    ```

3.  **Play the Game:**
    *   Enter your nickname and password when prompted.
    *   Use the `/listar` command to view available players.
    *   Use the `/desafiar <nickname>` command to challenge a player.
    *   The game will start once the challenge is accepted.
    *   Enter the number corresponding to the cell where you want to place your symbol.

## 🏗️ Project Structure
```
Projeto_IECD/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── clientSerial/
│   │   │   │   ├── Jogador.java
│   │   │   │   ├── JogadorPerfil.java
│   │   │   │   └── Jogo.java
│   │   │   ├── dados_persistentes/
│   │   │   │   ├── GerirJogadores.java
│   │   │   │   ├── JogadoresDataBase.java
│   │   │   │   └── Util.java
│   │   │   └── server/
│   │   │       ├── ConexaoJogador.java
│   │   │       ├── SalaDeEspera.java
│   │   │       ├── SalaDeJogo.java
│   │   │       └── ServidorTCP.java
│   │   └── webapp/
│   │       ├── META-INF/
│   │       │   └── MANIFEST.MF
│   │       ├── WEB-INF/
│   │       │   ├── data/
│   │       │   │   ├── Jogadores.xml
│   │       │   │   └── Jogadores.xsd
│   │       │   └── lib/
│   │       │       └── json-simple-1.1.1.jar
│   │       ├── game.jsp
│   │       ├── getPlayers.jsp
│   │       ├── login.jsp
│   │       ├── quadro_honra.jsp
│   │       └── settings.jsp
```

## 📝 Footer
*   **Repository Name:** Projeto_IECD
*   **Repository URL:** [https://github.com/giannifloriddia/Projeto_IECD](https://github.com/giannifloriddia/Projeto_IECD)
*   **Author:** giannifloriddia
