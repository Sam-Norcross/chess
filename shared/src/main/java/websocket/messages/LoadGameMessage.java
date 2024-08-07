package websocket.messages;

import chess.ChessGame;
import model.GameData;

public class LoadGameMessage extends ServerMessage {
    GameData game;
    ChessGame.TeamColor color;

    public LoadGameMessage(GameData game, ChessGame.TeamColor color) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.color = color;
    }

    public GameData getGame() {
        return game;
    }

    public ChessGame.TeamColor getColor() {
        return color;
    }
}
