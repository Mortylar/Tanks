package edu.school21.state.player;

import edu.school21.state.bullet.Bullet;
import edu.school21.state.position.Position;
import java.util.ArrayDeque;

public class Player {

    public static final int DAMAGE_VALUE = 5;
    public static final Long DEFAULT_ID = 0L;

    private int xp = 100;
    private Long id;
    private int shots = 0;
    private int damageCount = 0;
    private Position position;
    private ArrayDeque<Bullet> bullets;

    public Player() {
        this.id = DEFAULT_ID;
        this.position = new Position();
        this.bullets = new ArrayDeque<Bullet>();
    }

    public Player(Position position, Long id) {
        this.id = id;
        this.position = position;
        this.bullets = new ArrayDeque<Bullet>();
    }

    public Long getId() { return this.id; }

    public int getShots() { return this.shots; }

    public int getDamageCount() { return this.damageCount; }

    public void setId(Long id) { this.id = id; }

    public void setPosition(Position position) { this.position = position; }

    public Position getPosition() { return this.position; }

    public int getXP() { return this.xp; }

    public ArrayDeque<Bullet> getBullets() { return this.bullets; }

    public void move(int dx) { this.position = this.position.move(dx, 0); }

    public void fire() {
        this.bullets.offer(
            new Bullet(new Position(this.position.x(), 150)).move());
        ++this.shots;
    }

    public Bullet getFirstBullet() { return this.bullets.peek(); }

    public void removeFirstBullet() { this.bullets.poll(); }

    public void moveBullets(int border) {
        Bullet current = getFirstBullet();
        if (current == null) {
            return;
        }
        while (current.getPosition().y + current.D_Y > border) {
            removeFirstBullet();
            current = getFirstBullet();
            if (current == null) {
                return;
            }
        }
        for (Bullet bullet : bullets) {
            bullet.move();
        }
    }

    public void hit() {
        xp -= DAMAGE_VALUE;
        ++this.damageCount;
        if (xp < 0) {
            xp = 0;
        }
    }

    @Override
    public String toString() {
        return String.format("Player:\nid = %d\nxp = %d\n pos = %d", id, xp,
                             position.x());
    }
}
