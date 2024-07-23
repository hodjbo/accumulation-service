package com.hodbenor.project.accumulation.service.data;

import com.hodbenor.project.accumulation.service.data.beans.User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserDal {
    public static final String MISSION_ORDER_KEY = "missionOrder";
    public static final String MISSION_POINTS_GOAL_KEY = "missionPointsGoal";
    private final UserRepository userRepository;

    public UserDal(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUser(User user) {
        userRepository.saveUser(user);
    }

    public User getUser(long userId) {
        return userRepository.getUser(userId);
    }

    public int getCurrentUserMissionOrder(User user) {
        Map<String, String> userMissionData =  userRepository.getCurrentUserMission(user.id());
        String missionOrder = userMissionData.get(MISSION_ORDER_KEY);

        return missionOrder != null ? Integer.parseInt(missionOrder) : 0;
    }

    public void updateCurrentUserMission(User user, int missionOrder, int missionPointsGoal) {
        Map<String, String> missionData = Map.of(MISSION_ORDER_KEY, String.valueOf(missionOrder),
                MISSION_POINTS_GOAL_KEY, String.valueOf(missionPointsGoal));
        userRepository.updateCurrentUserMission(user.id(), missionData);
    }

    public void incrPointsBalance(User user, int incrBy) {
        userRepository.incrUserPointsBalance(user.id(), incrBy);
    }

    public void decrPointsBalance(User user, int decreaseBy) {
        userRepository.decrUserPointsBalance(user.id(), decreaseBy);
    }

    public void incrSpinsBalance(User user, int incBy) {
        userRepository.incrUserSpinsBalance(user.id(), incBy);
    }

    public void incrCoinsBalance(User user, int incBy) {
        userRepository.incrUserCoinsBalance(user.id(), incBy);
    }

    public int getPointsBalance(User user) {
        return userRepository.getPointsBalance(user.id());
    }
}