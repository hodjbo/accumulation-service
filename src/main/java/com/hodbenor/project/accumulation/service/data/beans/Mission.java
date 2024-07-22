package com.hodbenor.project.accumulation.service.data.beans;

import java.util.List;

public record Mission(List<RewardItem> rewards, int pointsGoal) {}
