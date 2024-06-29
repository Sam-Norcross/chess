package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {


    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row; //The -1 corrects for 0-indexed arrays
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    @Override
    public int hashCode() {
        return 31 * row + col;
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass()) {
            return false;
        } else {
            ChessPosition other = (ChessPosition) obj;
            return row == other.getRow() && col == other.getColumn();
        }
    }

    @Override
    public String toString() {
        return "ChessPosition{" + "row=" + row + ", col=" + col + '}';
    }
}
