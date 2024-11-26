package edu.school21.tanks.models;

import edu.school21.tanks.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Statistic {

    private User user;
    private int shots = 0;
    private int hits = 0;
    private int misses = 0;

    public Statistic add(Statistic other) {
        if (other.user.equals(this.user)) {
            this.shots += other.shots;
            this.hits += other.hits;
            this.misses += other.misses;
        }
        return this;
    }
}
