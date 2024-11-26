package edu.school21.state;

import edu.school21.state.bullet.Bullet;
import edu.school21.state.player.Player;
import edu.school21.state.position.Position;

public class StateManager {

    public static final int FIELD_HEIGHT = 1024;
    public static final int FIELD_WIDTH = 1024;

    public static final int TANK_HEIGHT = 150;
    public static final int TANK_WIDTH = 150;

    public static final int START_X_POSITION = 437;

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

    public Player getEnemy(Long playerId) {
        if (!this.first.getId().equals(playerId)) {
            return this.first;
        }
        return this.second;
    }

    public int getShots(Long id) { return getPlayer(id).getShots(); }
    public int getHits(Long id) { return getEnemy(id).getDamageCount(); }
    public int getMisses(Long id) { return getShots(id) - getHits(id); }

    public int getTotalShots(Long id) { return getPlayer(id).getOldShots(); }
    public int getTotalHits(Long id) { return getPlayer(id).getOldHits(); }
    public int getTotalMisses(Long id) { return getPlayer(id).getOldMisses(); }

    public void setOldStatistic(Long id, int shots, int hits, int misses) {
        this.getPlayer(id).updateOldStat(shots, hits, misses);
    }

    public void updateStatistic(Long id) {
        this.getPlayer(id).updateOldStat(getShots(id), getHits(id),
                                         getMisses(id));
    }

    private boolean isAvailableMove(Player player, int dx) {
        int newX = player.getPosition().x + dx;
        int rightBorder = FIELD_WIDTH - TANK_WIDTH + 2 * D_X;
        if ((newX < 0) || (newX > rightBorder)) {
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
        if (current == null) {
            return;
        }
        if ((current.getPosition().x >
             victim.getPosition().x - TANK_WIDTH / 2) &&
            (current.getPosition().x <
             victim.getPosition().x - 2 * D_X + TANK_WIDTH / 2)) {
            if (current.getPosition().y > FIELD_HEIGHT - TANK_HEIGHT / 2) {
                killer.removeFirstBullet();
                victim.hit();
            }
        }
    }

    public boolean isKilled() {
        if ((this.first.getXP() <= 0) || (this.second.getXP() <= 0)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return first.toString() + second.toString();
    }
}
