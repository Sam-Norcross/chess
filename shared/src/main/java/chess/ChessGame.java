package chess;

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

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        turn  = TeamColor.WHITE;
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

            return piece.pieceMoves(board, startPosition);
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
        ChessPiece.PieceType type = board.getPiece(move.getStartPosition()).getPieceType();
        TeamColor color = board.getPiece(move.getStartPosition()).getTeamColor();

        if (!moves.contains(move)) {
            throw new InvalidMoveException("Invalid move.");
        } else if (type == ChessPiece.PieceType.KING && isInCheck(color)) { //TODO--WRONG--other pieces can be moved too
            throw new InvalidMoveException("Can't move while in check.");
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
        ChessPosition newPosition = new ChessPosition(1, 1);
        ChessPosition kingPosition = new ChessPosition(1, 1);
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                newPosition = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(newPosition);
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
        ChessPosition kingPosition = getKingPosition(teamColor);
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
        ChessPosition kingPosition = getKingPosition(teamColor);
        ChessPosition newPosition;
        for (int r = -1; r <= 1; r++) {
            for (int c = -1; c <= 1; c++) {
                newPosition = new ChessPosition(kingPosition.getRow() + r, kingPosition.getColumn() + c);
                if (r == 0 && c == 0) {
                    if (checkedPosition(teamColor, newPosition)) {
                        return false;
                    }
                } else if (newPosition.isValid() && !checkedPosition(teamColor, newPosition)) {
                    if (board.isEmpty(newPosition) || board.getPiece(newPosition).getTeamColor() != teamColor) {
                        return false;
                    }
                }
            }
        }
        return true;
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
