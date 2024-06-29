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
        if (pieceColor == ChessGame.TeamColor.WHITE && board.isEmpty(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()))) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn())));
            //First move
            if (myPosition.getRow() == 2  && board.isEmpty(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn()))) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn())));
            }
        }
        else if (pieceColor == ChessGame.TeamColor.BLACK && board.isEmpty(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()))) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn())));
            //First move
            if (myPosition.getRow() == 7  && board.isEmpty(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn()))) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn())));
            }
        }

//        System.out.println("AAAAA");
//        System.out.println(moves.size());
//        for (ChessMove move : moves) {
//            System.out.println(move);
//        }
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