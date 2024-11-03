package com.exercise.podcast.reports.model.podcast;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record DownloadIdentifier(
        String client,
        Integer publisher,
        String podcastId,
        String showId,
        String episodeId,
        String downloadId
) {}
