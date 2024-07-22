package com.hodbenor.project.accumulation.service.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hodbenor.project.accumulation.service.data.beans.Mission;
import com.hodbenor.project.accumulation.service.data.beans.MissionsConfiguration;
import com.hodbenor.project.accumulation.service.data.beans.RewardItem;
import com.hodbenor.project.accumulation.service.data.beans.User;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserMissionDal {
    private final UserRepository userRepository;
/*
    private final UserDao userDao;
    private final MissionDao missionDao;

    public UserMissionDal(UserDao userDao, MissionDao missionDao) {
        this.userDao = userDao;
        this.missionDao = missionDao;
    }*/

    private final List<User> users = List.of(new User(1234, "1234", "James"));
    private int currentMissionOrder = 1;
    private final ObjectMapper mapper = new ObjectMapper();
    private List<Mission> missions;
    private int repeatedOrder;
    private Map<User, Integer> userPoints = new HashMap<>();
    private Map<User, Integer> userCoins = new HashMap<>();
    private Map<User, Integer> userSpins = new HashMap<>();

    public UserMissionDal(UserRepository userRepository) {
        this.userRepository = userRepository;
        loadUsers();
        loadMissions();
        userPoints.put(users.get(0), 0);
        userCoins.put(users.get(0), 0);
        userSpins.put(users.get(0), 1);
    }

    private void loadUsers() {
        User zohar = new User(1234, "1234", "Zohar");
        User batsheva = new User(5678, "5678", "James");
        User avigail = new User(666, "666", "James");

        userRepository.saveUser(zohar);
        userRepository.saveUser(batsheva);
        userRepository.saveUser(avigail);
    }

    public User getUser(long userId) {

        return userRepository.findUser(userId);
    }

    public int getRepeatedOrder() {
        return repeatedOrder;
    }

    public List<Mission> getMissions() {
        return missions;
    }

    public Mission getCurrentMission(User user) {
        return getCurrentMission(getCurrentMissionOrder(user));
    }

    public Mission getCurrentMission(int missionOrder) {
        return missionOrder > 0 && missionOrder <= missions.size() ? missions.get(missionOrder - 1) : missions.get(repeatedOrder - 1);
    }

    public int getCurrentMissionOrder(User user) {
        return currentMissionOrder;
    }

    public void updateCurrentMissionOrder(User user, int missionOrder) {
        currentMissionOrder = missionOrder;
    }

    private void loadMissions() {
        try {
            File missionsFile = new File(Objects.requireNonNull(getClass().getResource("/missions-config.json")).getFile());
            MissionsConfiguration missionsConfiguration = mapper.readValue(missionsFile, MissionsConfiguration.class);
            repeatedOrder = missionsConfiguration.repeatedIndex();

            for (Mission mission : missionsConfiguration.missions()) {
                System.out.println("Points Goal: " + mission.pointsGoal());
                System.out.println("Rewards:");
                for (RewardItem reward : mission.rewards()) {
                    System.out.println("\tName: " + reward.name() + ", Value: " + reward.value());
                }
                System.out.println();
            }

            missions = missionsConfiguration.missions().stream()
                    .sorted(Comparator.comparing(Mission::pointsGoal))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getUser(String loginToken) {

        return users.get(0);
    }

    public void updatePointsBalance(User user, int points) {

        userRepository.updateUserItemsBalance(user.userId(), "points", points);
    }

    public void incPointsBalance(User user, int incBy) {
        userPoints.computeIfPresent(user, (user1, currentPoints) -> currentPoints + incBy);
    }

    public void decreasePointsBalance(User user, int decreaseBy) {
        userPoints.computeIfPresent(user, (user1, currentPoints) -> currentPoints - decreaseBy);
    }

    public void incSpinsBalance(User user, int incBy) {
        userSpins.computeIfPresent(user, (user1, currentPoints) -> currentPoints + incBy);
    }

    public void incCoinsBalance(User user, int incBy) {
        userCoins.computeIfPresent(user, (user1, currentPoints) -> currentPoints + incBy);
    }

    public void decreaseSpinsBalance(User user, int decreaseBy) {
        userSpins.computeIfPresent(user, (user1, currentPoints) -> currentPoints - decreaseBy);
    }

    public int getPointsBalance(User user) {
        return userPoints.getOrDefault(user, 0);
    }

    public int getSpinsBalance(User user) {
        return userSpins.getOrDefault(user, 0);
    }
}