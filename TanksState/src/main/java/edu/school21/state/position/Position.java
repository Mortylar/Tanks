package edu.school21.state.position;

public class Position {

    public static final Position UP = new Position(-1, 0);
    public static final Position DOWN = new Position(1, 0);
    public static final Position RIGHT = new Position(0, 1);
    public static final Position LEFT = new Position(0, -1);

    public int x = 0;
    public int y = 0;

    public Position() {}

    public Position(Position position) {
        this.x = position.x;
        this.y = position.y;
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position move(int dx, int dy) {
        this.x += dx;
        this.y += dy;
        return this;
    }

    public Position move(Position direction) {
        this.x += direction.x;
        this.y += direction.y;
        return this;
    }

    public int x() { return x; }

    public int y() { return y; }
}
