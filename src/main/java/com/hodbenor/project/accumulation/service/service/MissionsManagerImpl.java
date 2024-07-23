package com.hodbenor.project.accumulation.service.service;

import com.hodbenor.project.accumulation.service.data.MissionDal;
import com.hodbenor.project.accumulation.service.data.UserDal;
import com.hodbenor.project.accumulation.service.data.beans.Mission;
import com.hodbenor.project.accumulation.service.data.beans.User;
import com.hodbenor.project.accumulation.service.rest.beans.WinSlotMachineResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MissionsManagerImpl implements MissionsManager {

    private final UserDal userDal;
    private final MissionDal missionDal;

    public MissionsManagerImpl(UserDal userDal, MissionDal missionDal) {
        this.userDal = userDal;
        this.missionDal = missionDal;

        //Mocks for testing
        User testUser = new User(1234, "1234", "James");
        userDal.saveUser(testUser);
        userDal.updateCurrentUserMission(testUser, 1, missionDal.getMissions().get(0).pointsGoal());
        userDal.incrSpinsBalance(testUser, 10);
    }

    @Override
    public WinSlotMachineResponse winSlotMachine(long userId, List<Integer> digits) {
        User user = userDal.getUser(userId);
        userDal.incrPointsBalance(user, calcWinningPoints(digits));
        Map<String, Integer> rewards = new HashMap<>();
        Mission currentMission = missionDal.getCurrentMission(userDal.getCurrentUserMissionOrder(user));
        while (currentMission.pointsGoal() <= userDal.getPointsBalance(user)) {
            userDal.decrPointsBalance(user, currentMission.pointsGoal());
            handleRewards(user, currentMission);
            currentMission.rewards().forEach(rewardItem -> {
                int value = rewards.getOrDefault(rewardItem.name(), 0) + rewardItem.value();
                rewards.put(rewardItem.name(), value);
            });
            currentMission = promoteUserToNextMission(user);
        }

        return new WinSlotMachineResponse(rewards);
    }

    private int calcWinningPoints(List<Integer> digits) {
        return digits.stream().mapToInt(Integer::intValue).sum();
    }

    private void handleRewards(User user, Mission currentMission) {
        currentMission.rewards().forEach(rewardItem -> {
            switch (rewardItem.name()) {
                case "spins" -> userDal.incrSpinsBalance(user, rewardItem.value());
                case "coins" -> userDal.incrCoinsBalance(user, rewardItem.value());
            }
        });
    }

    private Mission promoteUserToNextMission(User user) {
        int currentMissionOrder = userDal.getCurrentUserMissionOrder(user);
        int nextMissionOrder = currentMissionOrder < missionDal.getMissions().size() ? currentMissionOrder + 1 : missionDal.getRepeatedOrder();
        Mission mission = missionDal.getCurrentMission(nextMissionOrder);
        userDal.updateCurrentUserMission(user, nextMissionOrder, mission.pointsGoal());

        return missionDal.getCurrentMission(nextMissionOrder);
    }
}