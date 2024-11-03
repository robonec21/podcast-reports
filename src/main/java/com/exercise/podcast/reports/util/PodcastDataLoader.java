package com.exercise.podcast.reports.util;

import com.exercise.podcast.reports.exception.DataLoadException;
import com.exercise.podcast.reports.model.podcast.PodcastDownload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class PodcastDataLoader {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Loads download data from a file where each line is a JSON object
     */
    public List<PodcastDownload> loadFromFile(Path filePath) {
        try {
            return Files.lines(filePath)
                    .parallel()
                    .map(this::parseJson)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error reading file: {}", filePath, e);
            throw new DataLoadException("Failed to load podcast data", e);
        }
    }

    private Optional<PodcastDownload> parseJson(String json) {
        try {
            return Optional.of(objectMapper.readValue(json, PodcastDownload.class));
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON: {}", json, e);
            return Optional.empty();
        }
    }
}
