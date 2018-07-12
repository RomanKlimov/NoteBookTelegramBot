package ru.itis.bot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.bot.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findFirstByName(String name);
    void deleteUserByName(String name);
}
