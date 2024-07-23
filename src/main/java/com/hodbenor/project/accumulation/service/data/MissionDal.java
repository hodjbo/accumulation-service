package com.hodbenor.project.accumulation.service.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hodbenor.project.accumulation.service.data.beans.Mission;
import com.hodbenor.project.accumulation.service.data.beans.MissionsConfiguration;
import com.hodbenor.project.accumulation.service.data.beans.RewardItem;
import com.hodbenor.project.accumulation.service.data.beans.User;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class MissionDal {
    private final ObjectMapper mapper = new ObjectMapper();
    private List<Mission> missions;
    private int repeatedOrder;

    public MissionDal() {
        loadMissions();
    }

    public int getRepeatedOrder() {
        return repeatedOrder;
    }

    public List<Mission> getMissions() {
        return missions;
    }

    public Mission getCurrentMission(int missionOrder) {
        return missionOrder > 0 && missionOrder <= missions.size() ? missions.get(missionOrder - 1) : missions.get(repeatedOrder - 1);
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
}