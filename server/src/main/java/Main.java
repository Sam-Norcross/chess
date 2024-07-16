import chess.*;
import dataaccess.MemoryUserDAO;
import server.Server;
import service.UserService;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);


        UserService userService = new UserService(new MemoryUserDAO());
        Server server = new Server(userService);
        server.run(8080);
        System.out.println("Server started!");

    }
}