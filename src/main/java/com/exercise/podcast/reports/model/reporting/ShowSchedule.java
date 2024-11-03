package com.exercise.podcast.reports.model.reporting;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Builder
@Getter
@Data
public class ShowSchedule {
    private final String showId;
    private final DayOfWeek dayOfWeek;
    private final LocalTime timeOfDay;
}
