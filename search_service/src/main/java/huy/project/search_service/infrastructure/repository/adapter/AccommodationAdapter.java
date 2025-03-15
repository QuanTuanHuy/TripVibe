package huy.project.search_service.infrastructure.repository.adapter;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.json.JsonData;
import huy.project.search_service.core.domain.constant.ErrorCode;
import huy.project.search_service.core.domain.dto.request.AccommodationParams;
import huy.project.search_service.core.domain.dto.response.PageInfo;
import huy.project.search_service.core.domain.entity.AccommodationEntity;
import huy.project.search_service.core.domain.exception.AppException;
import huy.project.search_service.core.port.IAccommodationPort;
import huy.project.search_service.infrastructure.repository.IAccommodationRepository;
import huy.project.search_service.infrastructure.repository.document.AccommodationDocument;
import huy.project.search_service.infrastructure.repository.document.UnitAvailabilityDocument;
import huy.project.search_service.infrastructure.repository.document.UnitDocument;
import huy.project.search_service.infrastructure.repository.mapper.AccommodationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccommodationAdapter implements IAccommodationPort {
    private final ElasticsearchOperations elasticsearchOperations;
    private final IAccommodationRepository accommodationRepository;

    @Override
    public Pair<PageInfo, List<AccommodationEntity>> getAccommodations(AccommodationParams params) {
        // Create Query object with NativeQueryBuilder
        NativeQueryBuilder queryBuilder = NativeQuery.builder();
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        // Apply basic filters using simple term queries
        applyBasicFilters(boolQueryBuilder, params);

        // Apply price range filter
        applyPriceRangeFilter(boolQueryBuilder, params);

        // Apply date range and capacity filters
        applyDateRangeFilter(boolQueryBuilder, params);
        applyCapacityFilter(boolQueryBuilder, params);

        // Apply amenity and policy filters
        applyAmenityFilters(boolQueryBuilder, params);
        applyBookingPolicyFilter(boolQueryBuilder, params);

        // Apply geo distance filter
        applyGeoDistanceFilter(boolQueryBuilder, params);

        // Set the query
        queryBuilder.withQuery(q -> q.bool(boolQueryBuilder.build()));

        // Add pagination
        int page = params.getPage() != null ? params.getPage() : 0;
        int pageSize = params.getPageSize() != null ? params.getPageSize() : 10;
        queryBuilder.withPageable(PageRequest.of(page, pageSize));

        // Execute search
        SearchHits<AccommodationDocument> searchHits = elasticsearchOperations.search(
                queryBuilder.build(),
                AccommodationDocument.class
        );

        // Extract results
        List<AccommodationDocument> accommodations = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        // Build pagination info
        PageInfo pageInfo = PageInfo.builder()
                .pageSize((long) pageSize)
                .totalRecord(searchHits.getTotalHits())
                .totalPage((long) Math.ceil((double) searchHits.getTotalHits() / pageSize))
                .build();

        return Pair.of(pageInfo, AccommodationMapper.INSTANCE.toListEntity(accommodations));
    }

    private void applyBasicFilters(BoolQuery.Builder boolQueryBuilder, AccommodationParams params) {
        if (params.getProvinceId() != null) {
            boolQueryBuilder.must(TermQuery.of(t -> t
                    .field("location.provinceId")
                    .value(params.getProvinceId()))._toQuery());
        }

        if (params.getAccTypeId() != null) {
            boolQueryBuilder.must(TermQuery.of(t -> t
                    .field("typeId")
                    .value(params.getAccTypeId()))._toQuery());
        }

        if (params.getMinRatingStar() != null) {
            boolQueryBuilder.must(RangeQuery.of(r -> r
                    .field("ratingStar")
                    .gte(JsonData.of(params.getMinRatingStar())))._toQuery());
        }
    }

    private void applyPriceRangeFilter(BoolQuery.Builder boolQueryBuilder, AccommodationParams params) {
        if (params.getMinBudget() != null || params.getMaxBudget() != null) {
            NestedQuery.Builder nestedQuery = new NestedQuery.Builder()
                    .path("units");

            BoolQuery.Builder unitBoolQuery = new BoolQuery.Builder();
            RangeQuery.Builder priceQuery = new RangeQuery.Builder().field("units.pricePerNight");

            if (params.getMinBudget() != null) {
                priceQuery.gte(JsonData.of(params.getMinBudget()));
            }
            if (params.getMaxBudget() != null) {
                priceQuery.lte(JsonData.of(params.getMaxBudget()));
            }

            unitBoolQuery.must(priceQuery.build()._toQuery());
            nestedQuery.query(q -> q.bool(unitBoolQuery.build()));
            boolQueryBuilder.must(nestedQuery.build()._toQuery());
        }
    }

    private void applyDateRangeFilter(BoolQuery.Builder boolQueryBuilder, AccommodationParams params) {
        if (params.getStartDate() != null && params.getEndDate() != null) {
            List<Query> dateQueries = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(params.getStartDate());
            Date endDate = params.getEndDate();

            while (!calendar.getTime().after(endDate)) {
                Date currentDate = calendar.getTime();
                NestedQuery.Builder dateQuery = new NestedQuery.Builder()
                        .path("units.availability");

                BoolQuery.Builder dateBoolQuery = new BoolQuery.Builder();
                dateBoolQuery.must(TermQuery.of(t -> t
                        .field("units.availability.date")
                        .value(currentDate.getTime()))._toQuery());
                dateBoolQuery.must(RangeQuery.of(r -> r
                        .field("units.availability.availableCount")
                        .gt(JsonData.of(0)))._toQuery());

                dateQuery.query(q -> q.bool(dateBoolQuery.build()));
                dateQueries.add(dateQuery.build()._toQuery());
                calendar.add(Calendar.DATE, 1);
            }

            BoolQuery.Builder dateRangeQuery = new BoolQuery.Builder();
            for (Query query : dateQueries) {
                dateRangeQuery.must(query);
            }

            boolQueryBuilder.must(q -> q.bool(dateRangeQuery.build()));
        }
    }

    private void applyCapacityFilter(BoolQuery.Builder boolQueryBuilder, AccommodationParams params) {
        if (params.getNumAdults() != null || params.getNumChildren() != null) {
            NestedQuery.Builder nestedQuery = new NestedQuery.Builder()
                    .path("units");

            BoolQuery.Builder unitBoolQuery = new BoolQuery.Builder();

            if (params.getNumAdults() != null) {
                unitBoolQuery.must(RangeQuery.of(r -> r
                        .field("units.maxAdults")
                        .gte(JsonData.of(params.getNumAdults())))._toQuery());
            }

            if (params.getNumChildren() != null) {
                unitBoolQuery.must(RangeQuery.of(r -> r
                        .field("units.maxChildren")
                        .gte(JsonData.of(params.getNumChildren())))._toQuery());
            }

            nestedQuery.query(q -> q.bool(unitBoolQuery.build()));
            boolQueryBuilder.must(nestedQuery.build()._toQuery());
        }
    }

    private void applyAmenityFilters(BoolQuery.Builder boolQueryBuilder, AccommodationParams params) {
        // Accommodation amenities
        if (params.getAccAmenityIds() != null && !params.getAccAmenityIds().isEmpty()) {
            for (Long amenityId : params.getAccAmenityIds()) {
                boolQueryBuilder.must(TermQuery.of(t -> t
                        .field("amenityIds")
                        .value(amenityId))._toQuery());
            }
        }

        // Unit amenities
        if (params.getUnitAmenityIds() != null && !params.getUnitAmenityIds().isEmpty()) {
            NestedQuery.Builder nestedQuery = new NestedQuery.Builder()
                    .path("units");

            BoolQuery.Builder unitBoolQuery = new BoolQuery.Builder();
            for (Long amenityId : params.getUnitAmenityIds()) {
                unitBoolQuery.must(TermQuery.of(t -> t
                        .field("units.amenityIds")
                        .value(amenityId))._toQuery());
            }

            nestedQuery.query(q -> q.bool(unitBoolQuery.build()));
            boolQueryBuilder.must(nestedQuery.build()._toQuery());
        }
    }

    private void applyBookingPolicyFilter(BoolQuery.Builder boolQueryBuilder, AccommodationParams params) {
        // Booking policies filter
        if (params.getBookingPolicyIds() != null && !params.getBookingPolicyIds().isEmpty()) {
            for (Long policyId : params.getBookingPolicyIds()) {
                boolQueryBuilder.filter(TermQuery.of(t -> t
                        .field("bookingPolicyIds")
                        .value(policyId))._toQuery());
            }
        }
    }

    private void applyGeoDistanceFilter(BoolQuery.Builder boolQueryBuilder, AccommodationParams params) {
        if (params.getLatitude() != null && params.getLongitude() != null && params.getRadius() != null) {
            boolQueryBuilder.filter(GeoDistanceQuery.of(g -> g
                    .field("location")
                    .distance(params.getRadius() + "km")
                    .location(l -> l.latlon(ll -> ll
                            .lat(params.getLatitude())
                            .lon(params.getLongitude()))))._toQuery());
        }
    }

    @Override
    public void updateAvailability(Long accommodationId, Long unitId, Date startDate, Date endDate) {
        if (accommodationId == null || unitId == null || startDate == null || endDate == null) {
            throw new IllegalArgumentException("Required parameters cannot be null");
        }

        // Fetch the current document
        AccommodationDocument accommodation = _getAccById(accommodationId);
        if (accommodation == null) {
            throw new AppException(ErrorCode.ACCOMMODATION_NOT_FOUND);
        }

        // Find the specific unit
        UnitDocument unitToUpdate = accommodation.getUnits().stream()
                .filter(unit -> unit.getId().equals(unitId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.UNIT_NOT_FOUND));

        // For each day in the booking period, decrease availability
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        while (!calendar.getTime().after(endDate)) {
            Date currentDate = calendar.getTime();
            final Date searchDate = currentDate;

            // Find or create availability entry for this date
            UnitAvailabilityDocument dateAvailability = unitToUpdate.getAvailability().stream()
                    .filter(av -> av.getDate().equals(searchDate))
                    .findFirst()
                    .orElseGet(() -> {
                        // Create new availability entry if none exists
                        UnitAvailabilityDocument newAvailability = new UnitAvailabilityDocument(currentDate, 1);
                        unitToUpdate.getAvailability().add(newAvailability);
                        return newAvailability;
                    });

            // Decrement the available count
            dateAvailability.setAvailableCount(dateAvailability.getAvailableCount() - 1);

            calendar.add(Calendar.DATE, 1);
        }

        // Save the updated document and refresh index
        accommodationRepository.save(accommodation);
    }

    @Override
    public AccommodationEntity save(AccommodationEntity accommodation) {
        var accDocument = AccommodationMapper.INSTANCE.toDocument(accommodation);
        return AccommodationMapper.INSTANCE.toEntity(accommodationRepository.save(accDocument));
    }

    @Override
    public AccommodationEntity getAccById(Long accId) {
        return AccommodationMapper.INSTANCE.toEntity(accommodationRepository.findById(accId).orElse(null));
    }

    public AccommodationDocument _getAccById(Long accId) {
        return accommodationRepository.findById(accId).orElse(null);
    }

    @Override
    public void deleteAccById(Long accId) {
        accommodationRepository.deleteById(accId);
    }
}
