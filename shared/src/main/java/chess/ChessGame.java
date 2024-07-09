package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard board;
    TeamColor turn;

    ChessPosition doubleMovedPawn;

    boolean wKingMoved;
    boolean wrRookMoved;
    boolean wlRookMoved;
    boolean bKingMoved;
    boolean brRookMoved;
    boolean blRookMoved;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        turn  = TeamColor.WHITE;

        doubleMovedPawn = null;
        wKingMoved = false;
        wrRookMoved = false;
        wlRookMoved = false;
        bKingMoved = false;
        brRookMoved = false;
        blRookMoved = false;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece =  board.getPiece(startPosition);

        if (piece == null) {
            return null;
        } else {
            Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
            Collection<ChessMove> valids = new ArrayList<>();
            ChessBoard clone;
            for (ChessMove move : moves) {
                clone = board.clone();
                clone.movePiece(move.getStartPosition(), move.getEndPosition());

                if (!checkedPosition(piece.getTeamColor(), getKingPosition(piece.getTeamColor(), clone), clone)) {
                    valids.add(move);
                }

            }

            //En Passant handling
            if (doubleMovedPawn != null && piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                int forward = 1;
                if (piece.getTeamColor() == TeamColor.BLACK) {
                    forward = -1;
                }
                if (doubleMovedPawn.getRow() == startPosition.getRow()) {
                    int diff = doubleMovedPawn.getColumn() - startPosition.getColumn();
                    if (diff == 1 || diff == -1) {
                        valids.add(new ChessMove(startPosition, new ChessPosition(startPosition.getRow() + forward, doubleMovedPawn.getColumn())));
                    }
                }
            }

            //Castling handling
            if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                if (piece.getTeamColor() == TeamColor.WHITE) {
                    if (!wKingMoved) {
                        if (!wlRookMoved && board.isEmpty(new ChessPosition(1, 2)) && board.isEmpty(new ChessPosition(1, 2)) && board.isEmpty(new ChessPosition(1, 4))) {
                            valids.add(new ChessMove(startPosition, new ChessPosition(1, 3)));
                        }
                        if (!wrRookMoved && board.isEmpty(new ChessPosition(1, 7)) && board.isEmpty(new ChessPosition(1, 6))) {
                            valids.add(new ChessMove(startPosition, new ChessPosition(1, 7)));
                        }
                    }
                }
                if (piece.getTeamColor() == TeamColor.BLACK) {
                    if (!bKingMoved) {
                        if (!blRookMoved && board.isEmpty(new ChessPosition(8, 2)) && board.isEmpty(new ChessPosition(8, 2)) && board.isEmpty(new ChessPosition(8, 4))) {
                            valids.add(new ChessMove(startPosition, new ChessPosition(8, 3)));
                        }
                        if (!brRookMoved && board.isEmpty(new ChessPosition(8, 7)) && board.isEmpty(new ChessPosition(8, 6))) {
                            valids.add(new ChessMove(startPosition, new ChessPosition(8, 7)));
                        }
                    }
                }
            }


            return valids;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> moves = validMoves(move.getStartPosition());
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("No piece selected.");
        } else if (getTeamTurn() != piece.getTeamColor()) {
            throw new InvalidMoveException("It is the other team's turn.");
        } else if (!moves.contains(move)) {
            throw new InvalidMoveException("Invalid move.");
        } else {


            //En Passant handling
            if (doubleMovedPawn != null) {
                if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                    if (move.getStartPosition().getRow() == doubleMovedPawn.getRow()) {
                        int diff = doubleMovedPawn.getColumn() - move.getStartPosition().getColumn();
                        if (diff == 1 || diff == -1) {
                            board.removePiece(doubleMovedPawn);
                        }
                    }
                }
                //Reset doubleMovedPawn for the next turn
                doubleMovedPawn = null;
            }
            if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                if (move.getEndPosition().getRow() - move.getStartPosition().getRow() == 2 || move.getEndPosition().getRow() - move.getStartPosition().getRow() == -2) {
                    doubleMovedPawn = move.getEndPosition();
                }
            }


            board.movePiece(move.getStartPosition(), move.getEndPosition(), move.getPromotionPiece());


            //Castling handling (moving the rook)--TODO--resetBoard doesn't reset castling flags
            if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                int diff = move.getEndPosition().getColumn() - move.getStartPosition().getColumn();
                if (diff == -2) {
                    board.movePiece(new ChessPosition(1, 1), new ChessPosition(1, 4));
                    wlRookMoved = true;
                }
                if (diff == 2) {
                    board.movePiece(new ChessPosition(1, 8), new ChessPosition(1, 6));
                    wrRookMoved = true;
                }
            }

            //Set castling flags
            ChessPosition startPosition = move.getStartPosition();
            if (!wKingMoved && startPosition.equals(new ChessPosition(1, 5))) {
                wKingMoved = true;
            } else if (!wlRookMoved && startPosition.equals(new ChessPosition(1, 1))) {
                wlRookMoved = true;
            } else if (!wrRookMoved && startPosition.equals(new ChessPosition(1, 8))) {
                wrRookMoved = true;
            } else if (!bKingMoved && startPosition.equals(new ChessPosition(8, 5))) {
                bKingMoved = true;
            } else if (!blRookMoved &&startPosition.equals(new ChessPosition(8, 1))) {
                blRookMoved = true;
            } else if (!brRookMoved && startPosition.equals(new ChessPosition(8, 8))) {
                brRookMoved = true;
            }
        }

        if (getTeamTurn() == TeamColor.WHITE) {
            turn = TeamColor.BLACK;
        } else {
            turn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return checkedPosition(teamColor, getKingPosition(teamColor));
    }

    private boolean checkedPosition(TeamColor teamColor, ChessPosition position) {
        return checkedPosition(teamColor, position, board);
    }

    private boolean checkedPosition(TeamColor teamColor, ChessPosition position, ChessBoard chessBoard) {
        ChessPosition newPosition;
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                newPosition = new ChessPosition(r, c);
                ChessPiece piece = chessBoard.getPiece(newPosition);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(chessBoard, newPosition);
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(position)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private ChessPosition getKingPosition(TeamColor teamColor) {
        return getKingPosition(teamColor, board);
    }

    private ChessPosition getKingPosition(TeamColor teamColor, ChessBoard chessBoard) {
        ChessPosition newPosition;
        ChessPosition kingPosition = new ChessPosition(1, 1);
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                newPosition = new ChessPosition(r, c);
                ChessPiece piece = chessBoard.getPiece(newPosition);
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPosition = new ChessPosition(r, c);;
                    break;
                }
            }
        }
        return kingPosition;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPosition;
        ChessPosition newPosition;

        //Try every possible piece on the opposite team
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessBoard clone = board.clone();
                newPosition = new ChessPosition(r, c);
                if (!clone.isEmpty(newPosition)) {
                    ChessPiece piece = clone.getPiece(newPosition);
                    if (piece.getTeamColor() == teamColor) {
                        Collection<ChessMove> moves = piece.pieceMoves(clone, newPosition);

                        //Try every possible move
                        for (ChessMove move : moves) {
                            clone = board.clone();
                            kingPosition = getKingPosition(teamColor);
                            ChessPosition endPosition = move.getEndPosition();
                            clone.movePiece(newPosition, endPosition);

                            if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                                kingPosition = endPosition;
                            }

                            //Check if the king's position is still in check if the king moves
                            if (!checkedPosition(teamColor, kingPosition, clone)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        int count = 0;  //Number of possible moves the team can make
        ChessPosition kingPosition;
        ChessPosition newPosition;

        //Try every possible piece on the opposite team
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessBoard clone = board.clone();
                newPosition = new ChessPosition(r, c);
                if (!clone.isEmpty(newPosition)) {
                    ChessPiece piece = clone.getPiece(newPosition);
                    if (piece.getTeamColor() == teamColor) {
                        Collection<ChessMove> moves = piece.pieceMoves(clone, newPosition);

                        //Try every possible move
                        for (ChessMove move : moves) {
                            count++;

                            clone = board.clone();
                            kingPosition = getKingPosition(teamColor);
                            ChessPosition endPosition = move.getEndPosition();
                            clone.movePiece(newPosition, endPosition);

                            if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                                kingPosition = endPosition;
                            }

                            //Check if the king's position is still in check if the king moves
                            if (checkedPosition(teamColor, kingPosition, clone)) {
                                count--;
                            }
                        }
                    }
                }
            }
        }
        return count == 0;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
