package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class PrintUtils {

    public static void printBoard(GameData gameData, ChessGame.TeamColor color) {
        System.out.println(displayBoard(gameData, color));
        printPrompt();
    }

    public static String displayBoard(GameData gameData, ChessGame.TeamColor color) {
        ChessBoard board = gameData.game().getBoard();

        String setLabelColors = SET_BG_COLOR_LIGHT_GREY +  SET_TEXT_COLOR_BLACK;
        String resetColors = RESET_BG_COLOR + RESET_TEXT_COLOR;
        String space = "  ";
        String pad = " ";   //Half of a space

        String rowLabel;
        if (color == ChessGame.TeamColor.WHITE) {
            rowLabel = setLabelColors + space + space +
                    "A" + space + "B" + space + "C" + space + "D" + space +
                    "E" + space + "F" + space + "G" + space + "H" + space +
                    space + resetColors + "\n";
        } else {
            rowLabel = setLabelColors + space + space +
                    "H" + space + "G" + space + "F" + space + "E" + space +
                    "D" + space + "C" + space + "B" + space + "A" + space +
                    space + resetColors + "\n";

        }

        String boardString = "";
        String currentColor = SET_BG_COLOR_BLACK;
        for (int r = 8; r >= 1; r--) {
            int currentRow = r;
            if (color == ChessGame.TeamColor.BLACK) {
                currentRow = 9 - r;
            }

            boardString += setLabelColors + pad + currentRow + pad;
            for (int c = 1; c <= 8; c++) {
                int currentColumn = c;
                if (color == ChessGame.TeamColor.BLACK) {
                    currentColumn = 9 - c;
                }

                currentColor = updateSquareColor(currentColor);
                boardString += currentColor + SET_TEXT_COLOR_RED + getSymbol(board.getPiece(new ChessPosition(currentRow, currentColumn)));
            }
            boardString += setLabelColors + pad + currentRow + pad + resetColors + "\n";
            currentColor = updateSquareColor(currentColor);
        }

        return "\n" + rowLabel + boardString + rowLabel + resetColors;
    }

    private static String updateSquareColor(String color) {
        if (color.equals(SET_BG_COLOR_WHITE)) {
            return SET_BG_COLOR_BLACK;
        } else {
            return SET_BG_COLOR_WHITE;
        }
    }

    private static String getSymbol(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }

        String symbol = EMPTY;
        ChessPiece.PieceType type = piece.getPieceType();
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (type == ChessPiece.PieceType.PAWN) {
                symbol = WHITE_PAWN;
            } else if (type == ChessPiece.PieceType.ROOK) {
                symbol = WHITE_ROOK;
            } else if (type == ChessPiece.PieceType.KNIGHT) {
                symbol = WHITE_KNIGHT;
            } else if (type == ChessPiece.PieceType.BISHOP) {
                symbol = WHITE_BISHOP;
            } else if (type == ChessPiece.PieceType.KING) {
                symbol = WHITE_KING;
            } else if (type == ChessPiece.PieceType.QUEEN) {
                symbol = WHITE_QUEEN;
            }
        } else {
            if (type == ChessPiece.PieceType.PAWN) {
                symbol = BLACK_PAWN;
            } else if (type == ChessPiece.PieceType.ROOK) {
                symbol = BLACK_ROOK;
            } else if (type == ChessPiece.PieceType.KNIGHT) {
                symbol = BLACK_KNIGHT;
            } else if (type == ChessPiece.PieceType.BISHOP) {
                symbol = BLACK_BISHOP;
            } else if (type == ChessPiece.PieceType.KING) {
                symbol = BLACK_KING;
            } else if (type == ChessPiece.PieceType.QUEEN) {
                symbol = BLACK_QUEEN;
            }
        }

        return symbol;
    }

    public static void printPrompt() {
        System.out.print(RESET_BG_COLOR + RESET_TEXT_COLOR + "\n>>> ");
    }
}
