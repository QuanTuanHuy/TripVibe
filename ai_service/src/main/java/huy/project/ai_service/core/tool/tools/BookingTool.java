package huy.project.ai_service.core.tool.tools;

import huy.project.ai_service.core.tool.dto.request.BookingDates;
import huy.project.ai_service.core.tool.dto.request.GuestInfo;
import huy.project.ai_service.core.tool.dto.response.AccommodationSearchResult;
import huy.project.ai_service.core.tool.dto.response.BookingResult;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class BookingTool {

    @Tool(description = "Search for accommodations based on location, dates, and number of adults/children")
    public AccommodationSearchResult searchAccommodation(
            @ToolParam(description = "Location to search for accommodations") String location,
            @ToolParam(description = "Check-in date in ISO-8601 format") String checkInDate,
            @ToolParam(description = "Check-out date in ISO-8601 format") String checkOutDate,
            @ToolParam(description = "Number of adults", required = false) Integer adults,
            @ToolParam(description = "Number of children", required = false) Integer children) {

        return AccommodationSearchResult.builder()
                .build();
    }

    @Tool(description = "Create a new booking for accommodation")
    public BookingResult createBooking(
            @ToolParam(description = "Accommodation ID to book") String accommodationId,
            @ToolParam(description = "Guest information") GuestInfo guestInfo,
            @ToolParam(description = "Booking dates") BookingDates dates) {

        return new BookingResult();
    }
}