package edu.school21.state.bullet;

import edu.school21.state.position.Position;

public class Bullet {

    public static final int D_Y = 10;

    private Position position;

    public Bullet() { this.position = new Position(); }

    public Bullet(Position position) { this.position = position; }

    public void setPosition(Position position) { this.position = position; }

    public Position getPosition() { return this.position; }

    public Bullet move() {
        System.out.printf("Move from %d %d ", position.x(), position.y());
        this.position = this.position.move(0, D_Y);
        System.out.printf("Move to %d %d\n", position.x(), position.y());
        return this;
    }
}
