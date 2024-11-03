package com.exercise.podcast.reports.model.podcast;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Map;

@Builder
@Jacksonized
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record Opportunity(
        Long originalEventTime,
        Integer maxDuration,
        Map<String, Zone> zones,
        Map<String, List<String>> positionUrlSegments,
        Integer insertionRate
) {}
