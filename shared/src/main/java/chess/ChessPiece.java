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
            return pawnMove(board, myPosition);
        }
        else {
            throw new RuntimeException("Not implemented");
        }
    }

    private Collection<ChessMove> pawnMove(ChessBoard board, ChessPosition myPosition) {
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