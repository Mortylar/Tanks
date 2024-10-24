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
        this.position = this.position.move(0, D_Y);
        return this;
    }
}
