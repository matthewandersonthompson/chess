package chess;

import java.util.Objects;

public class ChessPosition {

    private final int row;
    private final int column;

    public ChessPosition(int row, int column) {
        if (row < 1 || row > 8 || column < 1 || column > 8) {
            throw new IllegalArgumentException("Invalid row or column value. Must be between 1 and 8.");
        }
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPosition that = (ChessPosition) o;
        return row == that.row && column == that.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", row, column);
    }
}
