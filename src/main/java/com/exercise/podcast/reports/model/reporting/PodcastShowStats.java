package com.exercise.podcast.reports.model.reporting;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PodcastShowStats {
    private final String showId;
    private final long downloads;
}
