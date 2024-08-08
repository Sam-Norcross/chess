package client.websocket;

import chess.*;
import com.google.gson.Gson;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

import static ui.PrintUtils.printBoard;

public class WebSocketFacade extends Endpoint {

    private Session session;
    private NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    Gson serializer = new Gson();
                    ServerMessage serverMessage = serializer.fromJson(message, ServerMessage.class);
                    ServerMessage.ServerMessageType messageType = serverMessage.getServerMessageType();

                    if (messageType == ServerMessage.ServerMessageType.LOAD_GAME) {
                        LoadGameMessage loadGameMessage = serializer.fromJson(message, LoadGameMessage.class); //TODO--doesn't load based on player's color
                        printBoard(loadGameMessage.getGame(), loadGameMessage.getColor()); //TODO--better if this is in the client class
                    } else if (messageType == ServerMessage.ServerMessageType.ERROR) {
                        ErrorMessage errorMessage = serializer.fromJson(message, ErrorMessage.class);
                        notificationHandler.handleError(errorMessage);
                    } else if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                        NotificationMessage notification = serializer.fromJson(message, NotificationMessage.class);
                        notificationHandler.notify(notification);
                    }
                }
            });
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) { }

    public void connect(int gameID, String authToken) throws Exception {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws Exception {
        sendCommand(new MakeMoveCommand(authToken, gameID, move));
    }

    public void leave(int gameID, String authToken) throws Exception {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID));
    }

    public void resign(int gameID, String authToken) throws Exception {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID));
    }

    private void sendCommand(UserGameCommand command) throws Exception {
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }



//    private void printBoard(GameData gameData, ChessGame.TeamColor color) {
//        ChessBoard board = gameData.game().getBoard();
//
//        String setLabelColors = SET_BG_COLOR_LIGHT_GREY +  SET_TEXT_COLOR_BLACK;
//        String resetColors = RESET_BG_COLOR + RESET_TEXT_COLOR;
//        String space = "  ";
//        String pad = " ";   //Half of a space
//
//        String rowLabel;
//        if (color == ChessGame.TeamColor.WHITE) {
//            rowLabel = setLabelColors + space + space +
//                    "A" + space + "B" + space + "C" + space + "D" + space +
//                    "E" + space + "F" + space + "G" + space + "H" + space +
//                    space + resetColors + "\n";
//        } else {
//            rowLabel = setLabelColors + space + space +
//                    "H" + space + "G" + space + "F" + space + "E" + space +
//                    "D" + space + "C" + space + "B" + space + "A" + space +
//                    space + resetColors + "\n";
//
//        }
//
//        String boardString = "";
//        String currentColor = SET_BG_COLOR_BLACK;
//        for (int r = 8; r >= 1; r--) {
//            int currentRow = r;
//            if (color == ChessGame.TeamColor.BLACK) {
//                currentRow = 9 - r;
//            }
//
//            boardString += setLabelColors + pad + currentRow + pad;
//            for (int c = 1; c <= 8; c++) {
//                int currentColumn = c;
//                if (color == ChessGame.TeamColor.BLACK) {
//                    currentColumn = 9 - c;
//                }
//
//                currentColor = updateSquareColor(currentColor);
//                boardString += currentColor + SET_TEXT_COLOR_RED + getSymbol(board.getPiece(new ChessPosition(currentRow, currentColumn)));
//            }
//            boardString += setLabelColors + pad + currentRow + pad + resetColors + "\n";
//            currentColor = updateSquareColor(currentColor);
//        }
//
//        System.out.println("\n" + rowLabel + boardString + rowLabel + resetColors);
//    }
//
//    private String updateSquareColor(String color) {
//        if (color.equals(SET_BG_COLOR_WHITE)) {
//            return SET_BG_COLOR_BLACK;
//        } else {
//            return SET_BG_COLOR_WHITE;
//        }
//    }
//
//    private String getSymbol(ChessPiece piece) {
//        if (piece == null) {
//            return EMPTY;
//        }
//
//        String symbol = EMPTY;
//        ChessPiece.PieceType type = piece.getPieceType();
//        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
//            if (type == ChessPiece.PieceType.PAWN) {
//                symbol = WHITE_PAWN;
//            } else if (type == ChessPiece.PieceType.ROOK) {
//                symbol = WHITE_ROOK;
//            } else if (type == ChessPiece.PieceType.KNIGHT) {
//                symbol = WHITE_KNIGHT;
//            } else if (type == ChessPiece.PieceType.BISHOP) {
//                symbol = WHITE_BISHOP;
//            } else if (type == ChessPiece.PieceType.KING) {
//                symbol = WHITE_KING;
//            } else if (type == ChessPiece.PieceType.QUEEN) {
//                symbol = WHITE_QUEEN;
//            }
//        } else {
//            if (type == ChessPiece.PieceType.PAWN) {
//                symbol = BLACK_PAWN;
//            } else if (type == ChessPiece.PieceType.ROOK) {
//                symbol = BLACK_ROOK;
//            } else if (type == ChessPiece.PieceType.KNIGHT) {
//                symbol = BLACK_KNIGHT;
//            } else if (type == ChessPiece.PieceType.BISHOP) {
//                symbol = BLACK_BISHOP;
//            } else if (type == ChessPiece.PieceType.KING) {
//                symbol = BLACK_KING;
//            } else if (type == ChessPiece.PieceType.QUEEN) {
//                symbol = BLACK_QUEEN;
//            }
//        }
//
//        return symbol;
//    }

}
