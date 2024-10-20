package edu.school21.tanks.services;

import edu.school21.tanks.models.User;
import java.util.Optional;

public interface UsersService {

    public Optional<User> signIn(String name);
    public Optional<User> signUp(String name);
}
