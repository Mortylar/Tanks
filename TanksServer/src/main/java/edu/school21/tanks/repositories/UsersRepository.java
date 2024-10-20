package edu.school21.tanks.repositories;

import edu.school21.tanks.models.User;
import java.util.Optional;

public interface UsersRepository {

    public Optional<User> findByName(String name);
    public void save(User user);
}
