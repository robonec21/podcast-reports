package com.exercise.podcast.reports.model.podcast;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Builder
@Jacksonized
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record PodcastDownload(
        DownloadIdentifier downloadIdentifier,
        List<Opportunity> opportunities,
        Integer agency,
        String deviceType,
        String country,
        String city,
        String listenerId
) {}
