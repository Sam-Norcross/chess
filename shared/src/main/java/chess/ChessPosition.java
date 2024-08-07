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

    public boolean isValid() {
        return row > 0 && row <= 8 && col > 0 && col <= 8;
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

    public String boardLocation() {
        String location = "";
        if (col == 1) {
            location = "a";
        } else if (col == 2) {
            location = "b";
        } else if (col == 3) {
            location = "c";
        } else if (col == 4) {
            location = "d";
        } else if (col == 5) {
            location = "e";
        } else if (col == 6) {
            location = "f";
        } else if (col == 7) {
            location = "g";
        } else if (col == 8) {
            location = "h";
        }

        return location + row;
    }
}
