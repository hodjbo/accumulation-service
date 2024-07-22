package com.hodbenor.project.accumulation.service.service;

import com.hodbenor.project.accumulation.service.data.UserMissionDal;
import com.hodbenor.project.accumulation.service.data.beans.Mission;
import com.hodbenor.project.accumulation.service.data.beans.User;
import com.hodbenor.project.accumulation.service.rest.beans.WinSlotMachineResult;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MissionsManagerImpl implements MissionsManager {

    private final UserMissionDal userMissionDal;

    public MissionsManagerImpl(UserMissionDal userMissionDal) {
        this.userMissionDal = userMissionDal;
    }

    @Override
    public WinSlotMachineResult winSlotMachine(long userId, List<Integer> digits) {
        User user = userMissionDal.getUser(userId);
        userMissionDal.incPointsBalance(user, calcWinningPoints(digits));
        Map<String, Integer> rewards = new HashMap<>();
        Mission currentMission = userMissionDal.getCurrentMission(user);
        while (currentMission.pointsGoal() <= userMissionDal.getPointsBalance(user)) {
            userMissionDal.decreasePointsBalance(user, currentMission.pointsGoal());
            handleRewards(user, currentMission);
            currentMission.rewards().forEach(rewardItem -> {
                int value = rewards.getOrDefault(rewardItem.name(), 0) + rewardItem.value();
                rewards.put(rewardItem.name(), value);
            });
            currentMission = promoteUserToNextMission(user);
        }

        return new WinSlotMachineResult(rewards);
    }

    private int calcWinningPoints(List<Integer> digits) {
        return digits.stream().mapToInt(Integer::intValue).sum();
    }

    private void handleRewards(User user, Mission currentMission) {
        currentMission.rewards().forEach(rewardItem -> {
            switch (rewardItem.name()) {
                case "spins" -> userMissionDal.incSpinsBalance(user, rewardItem.value());
                case "coins" -> userMissionDal.incCoinsBalance(user, rewardItem.value());
            }
        });
    }

    private Mission promoteUserToNextMission(User user) {
        int currentMissionOrder = userMissionDal.getCurrentMissionOrder(user);

        if (currentMissionOrder < userMissionDal.getMissions().size()) {
            userMissionDal.updateCurrentMissionOrder(user, currentMissionOrder + 1);
        } else {
            userMissionDal.updateCurrentMissionOrder(user, userMissionDal.getRepeatedOrder());
        }

        return userMissionDal.getCurrentMission(user);
    }
}