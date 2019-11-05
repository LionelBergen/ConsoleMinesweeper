package console.minesweeper.model;

public class Coordinate {
    int x, y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int[] toArray() {
        return new int[]{x, y};
    }
}
