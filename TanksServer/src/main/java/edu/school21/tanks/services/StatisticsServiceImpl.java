package edu.school21.tanks.services;

import edu.school21.tanks.models.Statistic;
import edu.school21.tanks.repositories.StatisticsRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("StatisticsService")
public class StatisticsServiceImpl implements StatisticsService {

    private StatisticsRepository repository;

    @Autowired
    public StatisticsServiceImpl(StatisticsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Statistic> findByUserId(Long id) {
        return repository.findByUserId(id);
    }

    @Override
    public void update(Statistic statistic) {
        repository.update(statistic);
    }
}
