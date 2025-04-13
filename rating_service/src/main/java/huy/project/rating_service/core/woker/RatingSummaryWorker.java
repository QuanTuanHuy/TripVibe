package huy.project.rating_service.core.woker;

import huy.project.rating_service.core.domain.constant.TopicConstant;
import huy.project.rating_service.core.domain.dto.kafka.SyncRatingMessage;
import huy.project.rating_service.core.port.IKafkaPublisher;
import huy.project.rating_service.core.service.IRatingSummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RatingSummaryWorker {
    private final IRatingSummaryService ratingSummaryService;
    private final IKafkaPublisher kafkaPublisher;

    // every 5 minutes
    @Scheduled(fixedRate = 300000)
    public void syncRatingWithSearch() {
        log.info("Start syncing rating summary with search");
        var ratingSummaries = ratingSummaryService.getRatingSummariesToSync(50);
        if (ratingSummaries == null || ratingSummaries.isEmpty()) {
            log.info("No rating summaries to sync");
            return;
        }

        var syncRatingMessages = ratingSummaries.stream()
                .map(rm -> SyncRatingMessage.builder()
                        .accommodationId(rm.getAccommodationId())
                        .rating(rm.getNumberOfRatings() == 0 ? 0 : (double) rm.getTotalRating() / rm.getNumberOfRatings())
                        .build())
                .toList();

        var kafkaBaseDto = SyncRatingMessage.toKafkaDto(syncRatingMessages);
        kafkaPublisher.pushAsync(kafkaBaseDto, TopicConstant.SearchCommand.TOPIC, "");
        log.info("Sent {} rating summaries to search service", syncRatingMessages.size());

        ratingSummaries.forEach(rm -> rm.setIsSyncedWithSearchService(true));
        ratingSummaryService.updateRatingSummaries(ratingSummaries);
        log.info("Updated {} rating summaries to synced with search service", ratingSummaries.size());
    }
}
