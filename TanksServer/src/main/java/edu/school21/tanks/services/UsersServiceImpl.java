package edu.school21.tanks.services;

import edu.school21.tanks.models.User;
import edu.school21.tanks.repositories.UsersRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("UsersService")
public class UsersServiceImpl implements UsersService {

    private static Long DEFAULT_ID = 1L;

    private UsersRepository repository;

    @Autowired
    public UsersServiceImpl(UsersRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<User> signIn(String name) {
        return repository.findByName(name);
    }

    @Override
    public Optional<User> signUp(String name) {
        if (!repository.findByName(name).isPresent()) {
            System.out.printf("\nreg = %s\n", name);
            repository.save(new User(DEFAULT_ID, name));
            return repository.findByName(name);
        }
        return Optional.empty();
    }
}
