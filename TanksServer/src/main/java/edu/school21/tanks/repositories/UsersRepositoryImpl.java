package edu.school21.tanks.repositories;

import edu.school21.tanks.models.User;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository("UsersRepository")
public class UsersRepositoryImpl implements UsersRepository {

    private final int ID_INDEX = 1;
    private final int NAME_INDEX = 2;

    private final RowMapper<User> mapRow = (rs, rowNum) -> {
        return new User(rs.getLong(ID_INDEX), rs.getString(NAME_INDEX));
    };

    private JdbcTemplate template;

    @Autowired
    public UsersRepositoryImpl(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public Optional<User> findByName(String name) {
        String query = "SELECT * FROM t_user WHERE name = ?;";
        try {
            return Optional.ofNullable(
                this.template.queryForObject(query, mapRow, name));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void save(User user) {
        String query = "INSERT INTO t_user(name) VALUES(?);";
        this.template.update(query, user.getName());
    }
}
