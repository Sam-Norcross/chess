package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        //throw new RuntimeException("Not implemented");
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    public boolean isEmpty(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1] == null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //Add pawns
        for (int i = 0; i < 8; i++) {
            squares[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            squares[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }

        //Add the rest of the white pieces
        squares[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        squares[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        squares[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        squares[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        squares[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        squares[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        squares[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        squares[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);

        //Add the rest of the black pieces
        squares[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        squares[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        squares[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        squares[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        squares[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        squares[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        squares[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        squares[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
    }

    public void movePiece(ChessPosition startPosition, ChessPosition endPosition) {
        squares[endPosition.getRow() - 1][endPosition.getColumn() - 1] = squares[startPosition.getRow() - 1][startPosition.getColumn() - 1];
        squares[startPosition.getRow() - 1][startPosition.getColumn() - 1] = null;
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass()) {
            return false;
        } else {
            ChessBoard other = (ChessBoard) obj;
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 8; j++) {
                    if (getPiece(new ChessPosition(i, j)) == null && other.getPiece(new ChessPosition(i, j)) == null) { //Check for empty spaces
                        continue;
                    }
                    else if (!getPiece(new ChessPosition(i, j)).equals(other.getPiece(new ChessPosition(i, j)))) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    public ChessBoard clone() {
        ChessBoard clone = new ChessBoard();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                if (squares[i - 1][j - 1] != null) {
                    clone.addPiece(new ChessPosition(i, j), squares[i - 1][j - 1]);
                }
            }
        }
        return clone;
    }

    @Override
    public String toString() {
        String result = "";
        for (int r = 7; r >= 0; r--) {
            for (int c = 0; c < 8; c++) {
                result += "|";
                if (squares[r][c] == null) {
                    result += " ";
                } else {
                    ChessPiece.PieceType type = squares[r][c].getPieceType();
                    ChessGame.TeamColor color = squares[r][c].getTeamColor();
                    if (color == ChessGame.TeamColor.WHITE) {
                        if (type == ChessPiece.PieceType.PAWN) {
                            result += "P";
                        } else if (type == ChessPiece.PieceType.ROOK) {
                            result += "R";
                        } else if (type == ChessPiece.PieceType.KNIGHT) {
                            result += "N";
                        } else if (type == ChessPiece.PieceType.BISHOP) {
                            result += "B";
                        } else if (type == ChessPiece.PieceType.KING) {
                            result += "K";
                        } else if (type == ChessPiece.PieceType.QUEEN) {
                            result += "Q";
                        }
                    } else {
                        if (type == ChessPiece.PieceType.PAWN) {
                            result += "p";
                        } else if (type == ChessPiece.PieceType.ROOK) {
                            result += "r";
                        } else if (type == ChessPiece.PieceType.KNIGHT) {
                            result += "n";
                        } else if (type == ChessPiece.PieceType.BISHOP) {
                            result += "b";
                        } else if (type == ChessPiece.PieceType.KING) {
                            result += "k";
                        } else if (type == ChessPiece.PieceType.QUEEN) {
                            result += "q";
                        }
                    }
                }
            }
            result += "|\n";
        }
        return result;
    }
}
