package edu.school21.tanks.repositories;

import edu.school21.tanks.models.Statistic;
import edu.school21.tanks.models.User;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository("StatisticsRepository")
public class StatisticsRepositoryImpl implements StatisticsRepository {

    private final int USER_ID_INDEX = 1;
    private final int USER_NAME_INDEX = 2;
    private final int SHOTS_INDEX = 3;
    private final int HITS_INDEX = 4;
    private final int MISSES_INDEX = 5;

    private final RowMapper<Statistic> mapRow = (rs, rowNum) -> {
        User user =
            new User(rs.getLong(USER_ID_INDEX), rs.getString(USER_NAME_INDEX));
        return new Statistic(user, rs.getInt(SHOTS_INDEX),
                             rs.getInt(HITS_INDEX), rs.getInt(MISSES_INDEX));
    };

    private JdbcTemplate template;

    @Autowired
    public StatisticsRepositoryImpl(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public Optional<Statistic> findByUserId(Long id) {
        String query = "SELECT t_user.id, t_user.name, t_statistic.shots, "
                       + "t_statistic.hits, t_statistic.misses\n"
                       + "FROM t_statistic \nJOIN t_user\n"
                       + "ON t_statistic.user_id = t_user.id\n"
                       + "AND t_user.id = ?;";
        try {
            return Optional.ofNullable(
                this.template.queryForObject(query, mapRow, id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void update(Statistic statistic) {
        String query =
            "UPDATE t_statistic SET shots = ?, hits = ?, misses = ?\n"
            + "WHERE user_id = ?;";
        this.template.update(query, statistic.getShots(), statistic.getHits(),
                             statistic.getMisses(),
                             statistic.getUser().getId());
    }
}
