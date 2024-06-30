package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (type == PieceType.PAWN) {
            return pawnMoves(board, myPosition);
        } else if (type == PieceType.ROOK) {
            return rookMoves(board, myPosition);
        } else if (type == PieceType.BISHOP) {
            return bishopMoves(board, myPosition);
        } else if (type == PieceType.QUEEN) {
            Collection<ChessMove> moves = new ArrayList<>();
            moves = rookMoves(board, myPosition);
            for (ChessMove move : bishopMoves(board, myPosition)) {
                moves.add(move);
            }
            return moves;
        } else if (type == PieceType.KING) {
            return kingMoves(board, myPosition);
        }
        else if (type == PieceType.KNIGHT){
            return knightMoves(board, myPosition);
        }
        return null;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        //White piece
        if (pieceColor == ChessGame.TeamColor.WHITE) {
            if (board.isEmpty(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()))) {

                //Promotion handling
                if (myPosition.getRow() + 1 == 8) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(8, myPosition.getColumn()), PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, new ChessPosition(8, myPosition.getColumn()), PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, new ChessPosition(8, myPosition.getColumn()), PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, new ChessPosition(8, myPosition.getColumn()), PieceType.ROOK));

                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn())));
                }

                //First move
                if (myPosition.getRow() == 2 && board.isEmpty(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn()))) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn())));
                }
            }

            //Capturing pieces
            if (!board.isEmpty(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1))) {
                if (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)).getTeamColor() == ChessGame.TeamColor.BLACK) {
                    if (myPosition.getRow() + 1 == 8) { //Promotion handling
                        moves.add(new ChessMove(myPosition, new ChessPosition(8, myPosition.getColumn() + 1), PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, new ChessPosition(8, myPosition.getColumn() + 1), PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, new ChessPosition(8, myPosition.getColumn() + 1), PieceType.KNIGHT));
                        moves.add(new ChessMove(myPosition, new ChessPosition(8, myPosition.getColumn() + 1), PieceType.ROOK));

                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)));
                    }
                }
            }
            if (!board.isEmpty(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1))) {
                if (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)).getTeamColor() == ChessGame.TeamColor.BLACK) {
                    if (myPosition.getRow() + 1 == 8) { //Promotion handling
                        moves.add(new ChessMove(myPosition, new ChessPosition(8, myPosition.getColumn() - 1), PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, new ChessPosition(8, myPosition.getColumn() - 1), PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, new ChessPosition(8, myPosition.getColumn() - 1), PieceType.KNIGHT));
                        moves.add(new ChessMove(myPosition, new ChessPosition(8, myPosition.getColumn() - 1), PieceType.ROOK));

                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)));
                    }

                }
            }

        }

        //Black piece
        else if (pieceColor == ChessGame.TeamColor.BLACK) {
            if (board.isEmpty(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()))) {
                //Promotion handling
                if (myPosition.getRow() - 1 == 1) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(1, myPosition.getColumn()), PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, new ChessPosition(1, myPosition.getColumn()), PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, new ChessPosition(1, myPosition.getColumn()), PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, new ChessPosition(1, myPosition.getColumn()), PieceType.ROOK));

                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn())));
                }

                //First move
                if (myPosition.getRow() == 7  && board.isEmpty(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn()))) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn())));
                }
            }

            //Capturing pieces
            if (!board.isEmpty(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1))) {
                if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)).getTeamColor() == ChessGame.TeamColor.WHITE) {
                    if (myPosition.getRow() - 1 == 1) { //Promotion handling
                        moves.add(new ChessMove(myPosition, new ChessPosition(1, myPosition.getColumn() + 1), PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, new ChessPosition(1, myPosition.getColumn() + 1), PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, new ChessPosition(1, myPosition.getColumn() + 1), PieceType.KNIGHT));
                        moves.add(new ChessMove(myPosition, new ChessPosition(1, myPosition.getColumn() + 1), PieceType.ROOK));

                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)));
                    }

                }
            }
            if (!board.isEmpty(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1))) {
                if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)).getTeamColor() == ChessGame.TeamColor.WHITE) {
                    if (myPosition.getRow() - 1 == 1) { //Promotion handling
                        moves.add(new ChessMove(myPosition, new ChessPosition(1, myPosition.getColumn() - 1), PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, new ChessPosition(1, myPosition.getColumn() - 1), PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, new ChessPosition(1, myPosition.getColumn() - 1), PieceType.KNIGHT));
                        moves.add(new ChessMove(myPosition, new ChessPosition(1, myPosition.getColumn() - 1), PieceType.ROOK));

                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)));
                    }
                }
            }

        }

        return moves;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        int r = 1;
        int c = 0;

        for (int i = 0; i < 4; i++) {
            if (i == 1) {
                r = -1;
            } else if (i == 2) {
                r = 0;
                c = 1;
            } else if (i == 3) {
                r = 0;
                c = -1;
            }
            ChessPosition nextPosition = new ChessPosition(myPosition.getRow() + r, myPosition.getColumn() + c);

            while (nextPosition.isValid() && board.isEmpty(nextPosition)) {
                moves.add(new ChessMove(myPosition, nextPosition));
                nextPosition = new ChessPosition(nextPosition.getRow() + r, nextPosition.getColumn() + c);
                if (!nextPosition.isValid()) {
                    break;
                }
            }

            //Capture handling
            if (nextPosition.isValid() && !board.isEmpty(nextPosition) && isEnemy(board.getPiece(nextPosition))) {
                moves.add(new ChessMove(myPosition, nextPosition));
            }
        }

        return moves;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) { //TODO--VERY unfinished
        Collection<ChessMove> moves = new ArrayList<>();

        for (int r = -1; r <= 1; r += 2) {
            for (int c = -1; c <= 1; c += 2) {
                ChessPosition nextPosition = new ChessPosition(myPosition.getRow() + r, myPosition.getColumn() + c);

                while (nextPosition.isValid() && board.isEmpty(nextPosition)) {
                    moves.add(new ChessMove(myPosition, nextPosition));
                    nextPosition = new ChessPosition(nextPosition.getRow() + r, nextPosition.getColumn() + c);
                    if (!nextPosition.isValid()) {
                        break;
                    }
                }

                //Capture handling
                if (nextPosition.isValid() && !board.isEmpty(nextPosition) && isEnemy(board.getPiece(nextPosition))) {
                    moves.add(new ChessMove(myPosition, nextPosition));
                }
            }
        }

        return moves;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        ChessPosition nextPosition;
        for (int r = -1; r <= 1; r++) {
            for (int c = -1; c <= 1; c++) {
                nextPosition = new ChessPosition(myPosition.getRow() + r, myPosition.getColumn() + c);
                if (nextPosition.isValid() && (board.isEmpty(nextPosition) || isEnemy(board.getPiece(nextPosition)))) {
                    moves.add(new ChessMove(myPosition, nextPosition));
                }
            }
        }

        return moves;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        ChessPosition nextPosition;

        int r = 2;
        int c = 1;

        for (int i = 0; i < 8; i++) {
            if (i == 2) {
                r = 1;
                c = 2;
            } else if (i == 4) {
                r = -1;
                c = 2;
            } else if (i == 6) {
                r = -2;
                c = 1;
            }
            if (i % 2 == 1) {
                r *= -1;
                c *= -1;
            }

            nextPosition = new ChessPosition(myPosition.getRow() + r, myPosition.getColumn() + c);

            if (nextPosition.isValid() && (board.isEmpty(nextPosition) || isEnemy(board.getPiece(nextPosition)))) {
                moves.add(new ChessMove(myPosition, nextPosition));
            }
        }

        return moves;
    }

    private boolean isEnemy(ChessPiece other) {
        return pieceColor != other.pieceColor;
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass()) {
            return false;
        }
        else {
            ChessPiece other = (ChessPiece) obj;
            return type.equals(other.getPieceType()) && pieceColor.equals(other.getTeamColor());
        }
    }

    @Override
    public String toString() {
        return "ChessPiece: type=" + type + ", pieceColor=" + pieceColor;
    }
}