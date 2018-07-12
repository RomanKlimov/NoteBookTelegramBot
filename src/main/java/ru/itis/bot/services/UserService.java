package ru.itis.bot.services;

import ru.itis.bot.models.User;

public interface UserService {
    void addUser(User user);
    User getUserByName(String name);
    void updateUser(String name, String newCity, String newDate);
    void deleteUser(String name);
}
