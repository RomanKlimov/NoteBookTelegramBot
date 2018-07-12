package ru.itis.bot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.bot.models.User;
import ru.itis.bot.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void addUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User getUserByName(String name) {
        return userRepository.findFirstByName(name);
    }


    @Override
    public void updateUser(String name, String newCity, String newDate) {
        User user = getUserByName(name);
        user.setCity(newCity);
        user.setDate(newDate);
        userRepository.save(user);

    }

    @Transactional
    @Override
    public void deleteUser(String name) {
        userRepository.deleteUserByName(name);

    }
}
