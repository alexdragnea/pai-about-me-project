package net.dg.paiproiect.service;

import net.dg.paiproiect.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    boolean saveNewUser(User user);
    List<User> findAll();
    Optional<User> findById(Long userId);
    void delete(User user);
    void deleteById(Long id);

}
