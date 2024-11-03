package com.exercise.podcast.reports.model.reporting;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DeviceStats {
    private final String deviceType;
    private final long downloads;
}
