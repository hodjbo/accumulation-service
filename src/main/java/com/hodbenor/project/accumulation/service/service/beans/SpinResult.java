package com.hodbenor.project.accumulation.service.service.beans;

import com.hodbenor.project.accumulation.service.data.beans.Mission;
import com.hodbenor.project.accumulation.service.rest.beans.BaseResult;
import lombok.Getter;

import java.util.List;

@Getter
public class SpinResult extends BaseResult{
    private final int pointBalance;
    private final int spinBalance;
    private final List<Integer> digits;
    private final Mission currentMission;
    private final List<Mission> missionsCompleted;

    public SpinResult(int pointBalance, int spinBalance, List<Integer> digits, Mission currentMission, List<Mission> missionsCompleted) {
        super(0);
        this.pointBalance = pointBalance;
        this.spinBalance = spinBalance;
        this.digits = digits;
        this.currentMission = currentMission;
        this.missionsCompleted = missionsCompleted;
    }

    public SpinResult(int pointBalance, int spinBalance, Mission currentMission) {
        super(1, "insufficient spins balance");
        this.pointBalance = pointBalance;
        this.spinBalance = spinBalance;
        this.digits = List.of();
        this.currentMission = currentMission;
        this.missionsCompleted = List.of();
    }
}
