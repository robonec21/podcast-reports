package com.exercise.podcast.reports.service;

import com.exercise.podcast.reports.exception.NoDataFoundException;
import com.exercise.podcast.reports.model.podcast.PodcastDownload;
import com.exercise.podcast.reports.model.reporting.DeviceStats;
import com.exercise.podcast.reports.model.reporting.PodcastShowStats;
import com.exercise.podcast.reports.model.reporting.ShowSchedule;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class PodcastAnalyticsService {
    private final List<PodcastDownload> podcastDownloadList;

    public PodcastAnalyticsService(List<PodcastDownload> podcastDownloadList) {
        this.podcastDownloadList = podcastDownloadList;
    }

    public PodcastShowStats getMostListenedShowByCity(String city) {
        return podcastDownloadList.stream()
                .filter(data -> city.equalsIgnoreCase(data.city()))
                .collect(Collectors.groupingBy(
                        data -> data.downloadIdentifier().showId(),
                        Collectors.counting()))
                .entrySet().stream()
                .map(entry -> PodcastShowStats.builder()
                        .showId(entry.getKey())
                        .downloads(entry.getValue())
                        .build())
                .max(Comparator.comparingLong(PodcastShowStats::getDownloads))
                .orElseThrow(() -> new NoDataFoundException("No data found for city: " + city));
    }

    public DeviceStats getMostUsedDevice() {
        return podcastDownloadList.stream()
                .collect(Collectors.groupingBy(
                        PodcastDownload::deviceType,
                        Collectors.counting()))
                .entrySet().stream()
                .map(entry -> DeviceStats.builder()
                        .deviceType(entry.getKey())
                        .downloads(entry.getValue())
                        .build())
                .max(Comparator.comparingLong(DeviceStats::getDownloads))
                .orElseThrow(() -> new NoDataFoundException("No device data found"));
    }

    public Map<String, Map<String, Integer>> getAdOpportunitiesByShow(String adBreakIndex) {
        return podcastDownloadList.stream()
                .collect(Collectors.groupingBy(
                        data -> data.downloadIdentifier().showId(),
                        Collectors.flatMapping(
                                data -> data.opportunities().stream()
                                        .flatMap(opp -> opp.positionUrlSegments()
                                                .getOrDefault("aw_0_ais.adBreakIndex", List.of())
                                                .stream()
                                                .filter(index -> index.equals(adBreakIndex))
                                        )
                                        .map(index -> Map.entry(index, 1)),
                                Collectors.groupingBy(
                                        Map.Entry::getKey,
                                        Collectors.summingInt(Map.Entry::getValue)
                                )
                        )
                ))
                .entrySet().stream()
                .sorted((e1, e2) -> {
                    // Sum up all opportunities for each show
                    int sum1 = e1.getValue().values().stream().mapToInt(Integer::valueOf).sum();
                    int sum2 = e2.getValue().values().stream().mapToInt(Integer::valueOf).sum();
                    // Sort in descending order
                    return Integer.compare(sum2, sum1);
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public List<ShowSchedule> getWeeklyShowsSchedule() {
        List<ShowSchedule> allSchedules = podcastDownloadList.stream()
                .flatMap(data -> data.opportunities().stream()
                        .map(opp -> {
                            ZonedDateTime dateTime = ZonedDateTime.ofInstant(
                                    Instant.ofEpochMilli(opp.originalEventTime()),
                                    ZoneId.of("UTC")
                            );
                            return ShowSchedule.builder()
                                    .showId(data.downloadIdentifier().showId())
                                    .dayOfWeek(dateTime.getDayOfWeek())
                                    .timeOfDay(LocalTime.of(dateTime.getHour(), dateTime.getMinute()))
                                    .build();
                        }))
                .distinct()
                .toList();

        return allSchedules.stream()
                .collect(Collectors.groupingBy(ShowSchedule::getShowId))
                .entrySet().stream()
                .filter(entry -> entry.getValue().stream()
                        .map(ShowSchedule::getDayOfWeek)
                        .distinct().count() == 1)
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toList());
    }
}
