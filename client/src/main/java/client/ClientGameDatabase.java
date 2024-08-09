package client;

import model.GameData;

public class ClientGameDatabase {

    private GameData currentGame;

    public ClientGameDatabase(GameData currentGame) {
        this.currentGame = currentGame;
    }

    public GameData getCurrentGame() {
        return currentGame;
    }

    public void updateCurrentGame(GameData currentGame) {
        this.currentGame = currentGame;
    }
}
