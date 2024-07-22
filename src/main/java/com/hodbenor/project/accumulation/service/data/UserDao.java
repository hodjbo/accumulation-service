package com.hodbenor.project.accumulation.service.data;

import com.hodbenor.project.accumulation.service.data.beans.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao {

    private final List<User> users = List.of(new User(1234, "1234", "James"));
    private int currentMissionIndex = 1;

    public User getUser(long userId) {

        return users.get(0);
    }

    public int getCurrentMissionIndex(User user) {
        return currentMissionIndex;
    }

    public void updateCurrentMissionIndex(User user, int missionIndex) {
        currentMissionIndex = missionIndex;
    }


}
