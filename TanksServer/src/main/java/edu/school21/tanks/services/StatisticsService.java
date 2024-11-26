package edu.school21.tanks.services;

import edu.school21.tanks.models.Statistic;
import java.util.Optional;

public interface StatisticsService {

    public Optional<Statistic> findByUserId(Long id);
    public void update(Statistic statistic);
    public void save(Statistic statistic);
}
