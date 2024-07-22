package com.hodbenor.project.accumulation.service.service;

import com.hodbenor.project.accumulation.service.rest.beans.WinSlotMachineResult;

import java.util.List;

public interface MissionsManager {

    WinSlotMachineResult winSlotMachine(long l, List<Integer> digits);
}
