package edu.school21.state;

import edu.school21.state.bullet.Bullet;
import edu.school21.state.player.Player;
import edu.school21.state.position.Position;

public class StateManager {

    public static final int FIELD_HEIGHT = 1042;
    public static final int FIELD_WIDTH = 1042;

    public static final int TANK_HEIGHT = 150;
    public static final int TANK_WIDTH = 150;

    public static final int START_X_POSITION = 446;

    public static final int D_X = 10;

    private Player first;
    private Player second;

    public StateManager() {
        this.first = new Player();
        this.second = new Player();
    }

    public StateManager(Long firstId, Long secondId) {
        this.first = new Player(new Position(START_X_POSITION, 0), firstId);
        this.second = new Player(new Position(START_X_POSITION, 0), secondId);
    }

    /*
        public void moveFirst(int direction) {
            move(this.first, dx);
        }

        public void moveSecond(int direction) {
            move(this.second, dx);
        }
    */
    public void move(Long playerId, int direction) {
        Player current = getPlayer(playerId);
        int dx = ((direction > 0) ? D_X : -D_X);
        if (isAvailableMove(current, dx)) {
            current.move(dx);
        }
    }

    public Player getPlayer(Long playerId) {
        if (this.first.getId().equals(playerId)) {
            return this.first;
        }
        if (this.second.getId().equals(playerId)) {
            return this.second;
        }
        return null;
    }

    private boolean isAvailableMove(Player player, int dx) {
        int newX = player.getPosition().x + dx;
        if ((newX < 0) || (newX > (FIELD_WIDTH - TANK_WIDTH))) {
            return false;
        }
        return true;
    }

    public void fire(Long playerId) { getPlayer(playerId).fire(); }

    public void moveBullets() {
        checkHits(first, second);
        checkHits(second, first);
        this.first.moveBullets(FIELD_HEIGHT);
        this.second.moveBullets(FIELD_HEIGHT);
    }

    private void checkHits(Player victim, Player killer) {
        Bullet current = killer.getFirstBullet();
        if ((current.getPosition().x > victim.getPosition().x) &&
            (current.getPosition().x < victim.getPosition().x + TANK_WIDTH)) {
            if (current.getPosition().y + current.D_Y >
                FIELD_HEIGHT + TANK_HEIGHT) {
                killer.removeFirstBullet();
                victim.hit();
            }
        }
    }
}