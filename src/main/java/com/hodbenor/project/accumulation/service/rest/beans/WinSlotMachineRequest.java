package com.hodbenor.project.accumulation.service.rest.beans;

import java.util.List;

public record WinSlotMachineRequest(long userId, List<Integer> digits){}
