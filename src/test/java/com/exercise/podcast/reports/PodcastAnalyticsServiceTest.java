package com.exercise.podcast.reports;

import com.exercise.podcast.reports.model.podcast.PodcastDownload;
import com.exercise.podcast.reports.model.reporting.DeviceStats;
import com.exercise.podcast.reports.model.reporting.PodcastShowStats;
import com.exercise.podcast.reports.model.reporting.ShowSchedule;
import com.exercise.podcast.reports.service.PodcastAnalyticsService;
import com.exercise.podcast.reports.util.PodcastDataLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PodcastAnalyticsServiceTest {
    private static final Logger log = LoggerFactory.getLogger(PodcastAnalyticsServiceTest.class);

    private PodcastAnalyticsService analyticsService;
    private List<PodcastDownload> podcastDownloadList;

    @BeforeAll
    void setUp() throws URISyntaxException {
        Path dataFile = Path.of(getClass().getClassLoader().getResource("downloads.txt").toURI());
        PodcastDataLoader loader = new PodcastDataLoader();
        podcastDownloadList = loader.loadFromFile(dataFile);

        assertThat(podcastDownloadList)
                .isNotEmpty()
                .withFailMessage("No data loaded from test file");

        analyticsService = new PodcastAnalyticsService(podcastDownloadList);

        log.info("Loaded {} download records for testing", podcastDownloadList.size());
    }

    @Test
    void testGetMostListenedShowByCity() {
        PodcastShowStats sfStats = analyticsService.getMostListenedShowByCity("san francisco");

        assertThat(sfStats).isNotNull();
        assertThat(sfStats.getShowId()).isEqualTo("Who Trolled Amber");
        assertThat(sfStats.getDownloads()).isEqualTo(24);

        log.info("Most popular show is: {}", sfStats.getShowId());
        log.info("Number of downloads is: {}", sfStats.getDownloads());
    }

    @Test
    void testGetMostUsedDevice() {
        DeviceStats deviceStats = analyticsService.getMostUsedDevice();

        assertThat(deviceStats).isNotNull();
        assertThat(deviceStats.getDeviceType()).isEqualTo("mobiles & tablets");
        assertThat(deviceStats.getDownloads()).isPositive();

        log.info("Most popular device is: {}", deviceStats.getDeviceType());
        log.info("Number of downloads is: {}", deviceStats.getDownloads());
    }

    @Test
    void testGetAdOpportunitiesByShow() {
        Map<String, Map<String, Integer>> prerollStats = analyticsService.getAdOpportunitiesByShow("preroll");

        Map<String, Map<String, Integer>> expectedPrerollStats = new LinkedHashMap<>();
        Map<String, Integer> expectedShow1OpportunityCount = new HashMap<>();
        expectedShow1OpportunityCount.put("preroll", 40);
        expectedPrerollStats.put("Stuff You Should Know", expectedShow1OpportunityCount);

        Map<String, Integer> expectedShow2OpportunityCount = new HashMap<>();
        expectedShow2OpportunityCount.put("preroll", 40);
        expectedPrerollStats.put("Who Trolled Amber", expectedShow2OpportunityCount);

        Map<String, Integer> expectedShow3OpportunityCount = new HashMap<>();
        expectedShow3OpportunityCount.put("preroll", 30);
        expectedPrerollStats.put("Crime Junkie", expectedShow3OpportunityCount);

        Map<String, Integer> expectedShow4OpportunityCount = new HashMap<>();
        expectedShow4OpportunityCount.put("preroll", 10);
        expectedPrerollStats.put("The Joe Rogan Experience", expectedShow4OpportunityCount);

        assertThat(prerollStats)
                .isNotEmpty()
                .containsExactlyEntriesOf(expectedPrerollStats);

        prerollStats.forEach((showId, stats) ->
                log.info("Show Id: {}, Preroll Opportunity Number: {}",
                        showId, stats.getOrDefault("preroll", 0)));
    }

    @Test
    void testGetWeeklyShowsSchedule() {
        List<ShowSchedule> schedule = analyticsService.getWeeklyShowsSchedule();

        assertThat(schedule)
                .isNotEmpty()
                .allSatisfy(show -> {
                    assertThat(show.getShowId())
                            .isNotEmpty()
                            .isIn("Crime Junkie", "Who Trolled Amber");

                    if ("Crime Junkie".equals(show.getShowId())) {
                        assertThat(show.getDayOfWeek()).isEqualTo(DayOfWeek.WEDNESDAY);
                        assertThat(show.getTimeOfDay()).isEqualTo(LocalTime.of(22, 0));
                    } else {
                        assertThat(show.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
                        assertThat(show.getTimeOfDay()).isEqualTo(LocalTime.of(20, 0));
                    }
                });

        log.info("Weekly shows are:");
        schedule.forEach(show ->
                log.info("{} - {} {}:{}",
                        show.getShowId(),
                        show.getDayOfWeek().toString().substring(0, 1).toUpperCase() + show.getDayOfWeek().toString().substring(1, 3).toLowerCase(),
                        String.format("%02d", show.getTimeOfDay().getHour()),
                        String.format("%02d", show.getTimeOfDay().getMinute())
                )
        );
    }
}
