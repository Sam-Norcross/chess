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
        wKingMoved = true;
        wrRookMoved = true;
        wlRookMoved = true;
        bKingMoved = true;
        brRookMoved = true;
        blRookMoved = true;
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
        }

        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> valids = new ArrayList<>();
        ChessBoard clone;
        for (ChessMove move : moves) {
            clone = board.clone();
            clone.movePiece(move.getStartPosition(), move.getEndPosition());

            if (getKingPosition(piece.getTeamColor(), clone) == null) {
                valids.add(move);
            } else if (!checkedPosition(piece.getTeamColor(), getKingPosition(piece.getTeamColor(), clone), clone)) {
                valids.add(move);
            }

        }

        //En Passant handling
        if (doubleMovedPawn != null && piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            int forward = 1;
            if (piece.getTeamColor() == TeamColor.BLACK) {
                forward = -1;
            }
            int diff = doubleMovedPawn.getColumn() - startPosition.getColumn();
            if (doubleMovedPawn.getRow() == startPosition.getRow() && (diff == 1 || diff == -1)) {
                valids.add(new ChessMove(startPosition, new ChessPosition(startPosition.getRow() + forward, doubleMovedPawn.getColumn())));
            }
        }

        //Castling handling
        if (piece.getPieceType() == ChessPiece.PieceType.KING && !isInCheck(piece.getTeamColor())) {
            if (piece.getTeamColor() == TeamColor.WHITE) {
                if (canCastleLeft(TeamColor.WHITE, wKingMoved, wlRookMoved)) {
                    valids.add(new ChessMove(startPosition, new ChessPosition(1, 3)));
                }
                if (canCastleRight(TeamColor.WHITE, wKingMoved, wrRookMoved)) {
                    valids.add(new ChessMove(startPosition, new ChessPosition(1, 7)));
                }
            }
            if (piece.getTeamColor() == TeamColor.BLACK) {
                if (canCastleLeft(TeamColor.BLACK, bKingMoved, blRookMoved)) {
                    valids.add(new ChessMove(startPosition, new ChessPosition(8, 3)));
                }
                if (canCastleRight(TeamColor.BLACK, bKingMoved, brRookMoved)) {
                    valids.add(new ChessMove(startPosition, new ChessPosition(8, 7)));
                }
            }
        }

        return valids;
    }

    private boolean canCastleLeft(TeamColor color, boolean kingMoved, boolean rookMoved) {
        int row = 1;
        if (color == TeamColor.BLACK) {
            row = 8;
        }

        return !kingMoved && !rookMoved
                && board.isEmpty(new ChessPosition(row, 2))
                && board.isEmpty(new ChessPosition(row, 3))
                && board.isEmpty(new ChessPosition(row, 4))
                && !checkedPosition(color, new ChessPosition(row, 3))
                && !checkedPosition(color, new ChessPosition(row, 4));
    }

    private boolean canCastleRight(TeamColor color, boolean kingMoved, boolean rookMoved) {
        int row = 1;
        if (color == TeamColor.BLACK) {
            row = 8;
        }

        return !kingMoved && !rookMoved
                && board.isEmpty(new ChessPosition(row, 7))
                && board.isEmpty(new ChessPosition(row, 6))
                && !checkedPosition(color, new ChessPosition(row, 7))
                && !checkedPosition(color, new ChessPosition(row, 6));
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
        }

        //En Passant handling
        if (doubleMovedPawn != null) {
            int diff = doubleMovedPawn.getColumn() - move.getStartPosition().getColumn();
            if (piece.getPieceType() == ChessPiece.PieceType.PAWN
                    && move.getStartPosition().getRow() == doubleMovedPawn.getRow()
                    && diff == 1 || diff == -1) {
                board.removePiece(doubleMovedPawn);
            }
            //Reset doubleMovedPawn for the next turn
            doubleMovedPawn = null;
        }
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if (move.getEndPosition().getRow() - move.getStartPosition().getRow() == 2
                    || move.getEndPosition().getRow() - move.getStartPosition().getRow() == -2) {
                doubleMovedPawn = move.getEndPosition();
            }
        }

        board.movePiece(move.getStartPosition(), move.getEndPosition(), move.getPromotionPiece());

        //Castling handling (moving the rook)
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            int diff = move.getEndPosition().getColumn() - move.getStartPosition().getColumn();
            if (diff == -2) {
                if (piece.getTeamColor() == TeamColor.WHITE) {
                    board.movePiece(new ChessPosition(1, 1), new ChessPosition(1, 4));
                    wlRookMoved = true;
                } else {
                    board.movePiece(new ChessPosition(8, 1), new ChessPosition(8, 4));
                    blRookMoved = true;
                }
            }
            if (diff == 2) {
                if (piece.getTeamColor() == TeamColor.WHITE) {
                    board.movePiece(new ChessPosition(1, 8), new ChessPosition(1, 6));
                    wrRookMoved = true;
                } else {
                    board.movePiece(new ChessPosition(8, 8), new ChessPosition(8, 6));
                    brRookMoved = true;
                }
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
                if (piece != null && piece.getTeamColor() != teamColor
                        && hasMoveEndingOnPosition(newPosition, position, chessBoard)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasMoveEndingOnPosition(ChessPosition startPosition, ChessPosition endPosition, ChessBoard chessBoard) {
        ChessPiece piece = chessBoard.getPiece(startPosition);
        Collection<ChessMove> moves = piece.pieceMoves(chessBoard, startPosition);
        for (ChessMove move : moves) {
            if (move.getEndPosition().equals(endPosition)) {
                return true;
            }
        }
        return false;
    }

    private ChessPosition getKingPosition(TeamColor teamColor) {
        return getKingPosition(teamColor, board);
    }

    private ChessPosition getKingPosition(TeamColor teamColor, ChessBoard chessBoard) {
        ChessPosition newPosition;
        ChessPosition kingPosition = null;
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
        return isInCheck(teamColor) && hasNoValidMoves(teamColor);
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

        return hasNoValidMoves(teamColor);
    }

    private boolean hasNoValidMoves(TeamColor teamColor) {
        int count = 0;  //Number of possible moves the team can make
        ChessPosition kingPosition;
        ChessPosition newPosition;

        //Try every possible piece on the opposite team
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessBoard clone = board.clone();
                newPosition = new ChessPosition(r, c);
                ChessPiece piece = clone.getPiece(newPosition);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    count += numberOfPossibleMoves(teamColor, newPosition);
                }
            }
        }
        return count == 0;
    }

    private int numberOfPossibleMoves(TeamColor teamColor, ChessPosition position) {
        int count = 0;
        ChessPiece piece = board.getPiece(position);
        Collection<ChessMove> moves = piece.pieceMoves(board, position);
        ChessPosition kingPosition;
        ChessBoard clone;

        for (ChessMove move : moves) {
            count++;

            clone = board.clone();
            kingPosition = getKingPosition(teamColor);
            ChessPosition endPosition = move.getEndPosition();
            clone.movePiece(position, endPosition);

            if (kingPosition != null) {
                if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPosition = endPosition;
                }

                //Check if the king's position is still in check if the king moves
                if (checkedPosition(teamColor, kingPosition, clone)) {
                    count--;
                }
            }
        }

        return count;
    }


    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;

        doubleMovedPawn = null;


        //Set castling flags
        wKingMoved = true;
        wlRookMoved = true;
        wrRookMoved = true;
        bKingMoved = true;
        blRookMoved = true;
        brRookMoved = true;
        if (board.getPiece(new ChessPosition(1, 5)) != null
                && board.getPiece(new ChessPosition(1, 5)).getPieceType() == ChessPiece.PieceType.KING
                && board.getPiece(new ChessPosition(1, 5)).getTeamColor() == TeamColor.WHITE) {
            wKingMoved = false;
        }
        if (board.getPiece(new ChessPosition(1, 1)) != null
                && board.getPiece(new ChessPosition(1, 1)).getPieceType() == ChessPiece.PieceType.ROOK
                && board.getPiece(new ChessPosition(1, 1)).getTeamColor() == TeamColor.WHITE) {
            wlRookMoved = false;
        }
        if (board.getPiece(new ChessPosition(1, 8)) != null
                && board.getPiece(new ChessPosition(1, 8)).getPieceType() == ChessPiece.PieceType.ROOK
                && board.getPiece(new ChessPosition(1, 8)).getTeamColor() == TeamColor.WHITE) {
            wrRookMoved = false;
        }
        if (board.getPiece(new ChessPosition(8, 5)) != null
                && board.getPiece(new ChessPosition(8, 5)).getPieceType() == ChessPiece.PieceType.KING
                && board.getPiece(new ChessPosition(8, 5)).getTeamColor() == TeamColor.BLACK) {
            bKingMoved = false;
        }
        if (board.getPiece(new ChessPosition(8, 1)) != null
                && board.getPiece(new ChessPosition(8, 1)).getPieceType() == ChessPiece.PieceType.ROOK
                && board.getPiece(new ChessPosition(8, 1)).getTeamColor() == TeamColor.BLACK) {
            blRookMoved = false;
        }
        if (board.getPiece(new ChessPosition(8, 8)) != null
                && board.getPiece(new ChessPosition(8, 8)).getPieceType() == ChessPiece.PieceType.ROOK
                && board.getPiece(new ChessPosition(8, 8)).getTeamColor() == TeamColor.BLACK) {
            brRookMoved = false;
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public String showBoard() {
        return board.displayBoard();
    }

}
