package com.hodbenor.project.accumulation.service.service;

import com.hodbenor.project.accumulation.service.rest.beans.WinSlotMachineResponse;

import java.util.List;

public interface MissionsManager {

    WinSlotMachineResponse winSlotMachine(long l, List<Integer> digits);
}
