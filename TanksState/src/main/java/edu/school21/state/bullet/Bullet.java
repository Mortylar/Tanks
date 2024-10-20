package edu.school21.state.bullet;

import edu.school21.state.position.Position;

public class Bullet {

    private Position position;
    private Position direction;

    public Bullet() {
        this.position = new Position();
        this.direction = new Position();
    }

    public Bullet(Position position, Position direction) {
        this.position = position;
        this.direction = direction;
    }

    public void setPosition(Position position) { this.position = position; }

    public void setDirection(Position direction) { this.direction = direction; }

    public Position getPosition() { return this.position; }

    public Position getDirection() { return this.direction; }

    public void move() { this.position.move(this.direction); }
}
