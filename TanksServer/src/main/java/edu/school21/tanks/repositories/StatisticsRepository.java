package edu.school21.tanks.repositories;

import edu.school21.tanks.models.Statistic;
import edu.school21.tanks.models.User;
import java.util.Optional;

public interface StatisticsRepository {

    public Optional<Statistic> findByUserId(Long id);

    public void update(Statistic statistic);
}
