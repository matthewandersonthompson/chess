package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int column;

    /**
     * Constructor to initialize the position
     *
     * @param row    Row number (1 to 8)
     * @param column Column number (1 to 8)
     */
    public ChessPosition(int row, int column) {
        if (row < 1 || row > 8 || column < 1 || column > 8) {
            throw new IllegalArgumentException("Invalid row or column value. Must be between 1 and 8.");
        }
        this.row = row;
        this.column = column;
    }

    /**
     * @return which row this position is in (1 codes for the bottom row)
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in (1 codes for the leftmost column)
     */
    public int getColumn() {
        return column;
    }

    /**
     * Override the equals method to compare chess positions
     *
     * @param o Other object to compare against
     * @return True if the two positions are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPosition that = (ChessPosition) o;
        return row == that.row && column == that.column;
    }

    /**
     * Generate a unique hash code for this position based on row and column attributes
     *
     * @return Unique hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    /**
     * Provide a string representation of the ChessPosition object
     *
     * @return String representation of the position
     */
    @Override
    public String toString() {
        return String.format("(%d, %d)", row, column);
    }
}
