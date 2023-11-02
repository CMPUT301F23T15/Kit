package com.example.kit.database;

import com.example.kit.data.User;

public interface UserSource {
    public void addInstance(User user);
    public void removeInstance(User user);

}
