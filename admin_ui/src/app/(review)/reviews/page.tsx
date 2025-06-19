"use client";

import { useEffect, useState } from "react";
import { useAuth } from "@/context/AuthContext";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
    Select,
    SelectContent,
    SelectGroup,
    SelectItem,
    SelectLabel,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";
import { DatePickerWithRange } from "@/components/ui/date-range-picker";
import { Button } from "@/components/ui/button";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
    PaginationContent,
    PaginationEllipsis,
    PaginationItem,
    PaginationLink,
    PaginationNext,
    PaginationPrevious,
    Pagination
} from "@/components/ui/pagination";
import { RatingsTable } from "@/components/review/ratings-table";
import { RatingSummaryCards } from "@/components/review/rating-summary-cards";
import { RatingChart } from "@/components/review/rating-chart";
import { toast } from "sonner";
import { Loader2, Star } from "lucide-react";
import { type DateRange as CalendarDateRange } from "react-day-picker";

// Import services and types
import { ratingService } from "@/services/rating/ratingService";
import accommodationService from "@/services/accommodation/accommodationService";
import { CreateRatingResponseDto } from "@/types/rating";
import { Accommodation } from "@/types/accommodation";

// Define types for our data structures
interface User {
    userId: number;
    fullName: string;
    avatarUrl: string;
}

interface Unit {
    id: number;
    name: string;
}

interface ResponseData {
    id: number;
    ownerId: number;
    content: string;
    createdAt: number;
}

interface RatingData {
    id: number;
    value: number;
    comment: string;
    createdAt: number;
    user: User;
    unit: Unit;
    response?: ResponseData | null;
}

interface RatingSummaryData {
    accommodationId: number;
    numberOfRatings: number;
    totalRating: number;
    averageRating: number;
    ratingDistribution: {
        [key: string]: number;
    };
}

interface FilterOptions {
    dateRange: CalendarDateRange | null;
    rating: number | null;
    search: string;
    sortBy: string;
    sortOrder: string;
}

interface PaginationButtonProps {
    children: React.ReactNode;
    isActive: boolean;
    onClick: () => void;
}

// Sample mock data for properties (fallback)
const mockAccommodations: Accommodation[] = [
    {
        id: 1,
        name: "Luxury Beach Resort & Spa",
        hostId: 1,
        locationId: 1,
        typeId: 1,
        currencyId: 1,
        description: "Beautiful beachfront resort",
        checkInTimeFrom: 14,
        checkInTimeTo: 22,
        checkOutTimeFrom: 6,
        checkOutTimeTo: 12
    },
    {
        id: 2,
        name: "Mountain View Hotel",
        hostId: 1,
        locationId: 2,
        typeId: 1,
        currencyId: 1,
        description: "Scenic mountain hotel",
        checkInTimeFrom: 15,
        checkInTimeTo: 23,
        checkOutTimeFrom: 7,
        checkOutTimeTo: 11
    },
    {
        id: 3,
        name: "City Center Boutique Hotel",
        hostId: 1,
        locationId: 3,
        typeId: 1,
        currencyId: 1,
        description: "Modern city center hotel",
        checkInTimeFrom: 14,
        checkInTimeTo: 24,
        checkOutTimeFrom: 8,
        checkOutTimeTo: 12
    }
];

// Component for pagination button
const PaginationButton = ({ children, isActive, onClick }: PaginationButtonProps) => {
    return (
        <PaginationItem>
            <PaginationLink
                onClick={onClick}
                isActive={isActive}
                className={isActive ? "bg-primary text-primary-foreground hover:bg-primary/90" : ""}
            >
                {children}
            </PaginationLink>
        </PaginationItem>
    );
};

export default function ReviewsPage() {
    const { user } = useAuth();
    const [isLoading, setIsLoading] = useState(true);
    const [activeTab, setActiveTab] = useState("overview");
    const [accommodations, setAccommodations] = useState<Accommodation[]>([]);
    const [selectedAccommodationId, setSelectedAccommodationId] = useState<number | null>(null);
    const [ratings, setRatings] = useState<RatingData[]>([]);
    const [ratingSummary, setRatingSummary] = useState<RatingSummaryData | null>(null);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(5);
    const [pageSize, setPageSize] = useState(10);
    const [filterOptions, setFilterOptions] = useState<FilterOptions>({
        dateRange: null,
        rating: null,
        search: "",
        sortBy: "createdAt",
        sortOrder: "desc"
    });

    useEffect(() => {
        fetchHostAccommodations();
    }, [user?.id]);

    useEffect(() => {
        if (selectedAccommodationId) {
            fetchRatings();
            fetchRatingSummary();
        }
    }, [selectedAccommodationId, currentPage, pageSize, filterOptions]);

    const fetchHostAccommodations = async () => {
        if (!user?.id) return;

        setIsLoading(true);
        try {
            const accommodationList = await accommodationService.getMyAccommodations(user.id);
            console.log("accommodationList", accommodationList);
            setAccommodations(accommodationList);
            if (accommodationList.length > 0) {
                setSelectedAccommodationId(accommodationList[0].id);
            }
        } catch (error) {
            console.error('Error fetching accommodations:', error);
            // Use mock data as fallback
            setAccommodations(mockAccommodations);
            if (mockAccommodations.length > 0) {
                setSelectedAccommodationId(mockAccommodations[0].id);
            }
            toast.error("Could not load your accommodations, using sample data");
        } finally {
            setIsLoading(false);
        }
    };

    const fetchRatings = async () => {
        if (!selectedAccommodationId) return;

        setIsLoading(true);
        try {
            const params = {
                accommodationId: selectedAccommodationId,
                page: currentPage - 1,
                pageSize: pageSize,
                sortBy: filterOptions.sortBy,
                sortType: filterOptions.sortOrder as 'asc' | 'desc',
                ...(filterOptions.rating && {
                    minValue: filterOptions.rating,
                    maxValue: filterOptions.rating
                }),
                ...(filterOptions.dateRange?.from && {
                    createdFrom: filterOptions.dateRange.from.getTime()
                }),
                ...(filterOptions.dateRange?.to && {
                    createdTo: filterOptions.dateRange.to.getTime()
                })
            };

            const response = await ratingService.getAllRatings(params);

            // Transform API response to local types
            const ratingsData: RatingData[] = response.data.map(rating => ({
                id: rating.id,
                value: rating.value,
                comment: rating.comment,
                createdAt: rating.createdAt,
                user: {
                    userId: rating.user?.userId || 0,
                    fullName: rating.user?.name || 'Anonymous',
                    avatarUrl: rating.user?.avatarUrl || `https://ui-avatars.com/api/?name=${rating.user?.name || 'Anonymous'}&background=random`
                },
                unit: {
                    id: rating.unit?.id || 0,
                    name: rating.unit?.name || 'Unknown Unit'
                },
                response: rating.ratingResponse ? {
                    id: rating.ratingResponse.id,
                    ownerId: user?.id || 0,
                    content: rating.ratingResponse.content,
                    createdAt: rating.ratingResponse.createdAt
                } : null
            }));

            setRatings(ratingsData);
            setTotalPages(response.totalPage || 5);
        } catch (error) {
            console.error('Error fetching ratings:', error);
            // Generate mock ratings data as fallback
            const reviewComments = [
                "The room was very spacious and clean. Staff was friendly and helpful. I would definitely stay here again!",
                "Nice location, close to all attractions. The bed was comfortable but bathroom needs updating.",
                "Great experience overall! The breakfast was delicious and had many options. Pool was clean and well-maintained.",
                "Beautiful hotel with amazing views. Service was excellent but the WiFi connection was unstable.",
                "Loved the modern design and amenities. The only downside was noise from the nearby street."
            ];

            const mockData: RatingData[] = Array.from({ length: 5 }).map((_, i) => ({
                id: i + 1,
                value: Math.floor(Math.random() * 4) + 7,
                comment: reviewComments[i],
                createdAt: Date.now() - Math.random() * 10000000000,
                user: {
                    userId: 100 + i,
                    fullName: `User ${i + 1}`,
                    avatarUrl: `https://ui-avatars.com/api/?name=User+${i + 1}&background=random`,
                },
                unit: {
                    id: 200 + i,
                    name: `Deluxe Room ${201 + i}`,
                },
                response: i % 3 === 0 ? {
                    id: i + 100,
                    ownerId: 1,
                    content: "Thank you for your feedback! We're delighted that you enjoyed your stay with us.",
                    createdAt: Date.now() - Math.random() * 5000000000
                } : null
            }));

            setRatings(mockData);
            setTotalPages(5);
            toast.error("Could not load ratings, using sample data");
        } finally {
            setIsLoading(false);
        }
    };

    const fetchRatingSummary = async () => {
        if (!selectedAccommodationId) return;

        try {
            const statisticResponse = await ratingService.getRatingStatistic(selectedAccommodationId);
            console.log("Rating statistic:", statisticResponse);

            setRatingSummary({
                accommodationId: statisticResponse.accommodationId,
                numberOfRatings: statisticResponse.totalRatings,
                totalRating: Math.round(statisticResponse.overallAverage * statisticResponse.totalRatings),
                averageRating: statisticResponse.overallAverage,
                ratingDistribution: Object.fromEntries(
                    Object.entries(statisticResponse.ratingDistribution).map(([key, value]) => [key, value])
                )
            });
        } catch (error) {
            console.error('Error fetching rating statistic:', error);

            // Fallback to getRatingSummaries if statistics API fails
            try {
                const summaries = await ratingService.getRatingSummaries([selectedAccommodationId]);
                console.log("Rating summaries fallback:", summaries);

                if (summaries.length > 0) {
                    const summary = summaries[0];
                    const averageRating = summary.numberOfRatings > 0
                        ? summary.totalRating / summary.numberOfRatings
                        : 0;

                    // Create distribution based on average rating (estimated)
                    const mockDistribution = {
                        "10": Math.floor(summary.numberOfRatings * 0.4),
                        "9": Math.floor(summary.numberOfRatings * 0.3),
                        "8": Math.floor(summary.numberOfRatings * 0.15),
                        "7": Math.floor(summary.numberOfRatings * 0.08),
                        "6": Math.floor(summary.numberOfRatings * 0.04),
                        "5": Math.floor(summary.numberOfRatings * 0.02),
                        "4": Math.floor(summary.numberOfRatings * 0.01),
                        "3": 0,
                        "2": 0,
                        "1": 0
                    };

                    setRatingSummary({
                        accommodationId: summary.accommodationId,
                        numberOfRatings: summary.numberOfRatings,
                        totalRating: summary.totalRating,
                        averageRating,
                        ratingDistribution: mockDistribution
                    });
                } else {
                    throw new Error("No rating summary found");
                }
            } catch (fallbackError) {
                console.error('Error with fallback rating summary:', fallbackError);
                // Mock summary as final fallback
                const mockSummary: RatingSummaryData = {
                    accommodationId: selectedAccommodationId,
                    numberOfRatings: 50,
                    totalRating: 450,
                    averageRating: 9.0,
                    ratingDistribution: {
                        "10": 20,
                        "9": 15,
                        "8": 8,
                        "7": 4,
                        "6": 1,
                        "5": 1,
                        "4": 0,
                        "3": 1,
                        "2": 0,
                        "1": 0
                    }
                };
                setRatingSummary(mockSummary);
                toast.error("Could not load rating statistics, using sample data");
            }
        }
    };

    const handleResponseSubmit = async (ratingId: number, content: string) => {
        try {
            setIsLoading(true);

            const responseData: CreateRatingResponseDto = {
                ratingId,
                content
            };

            const newResponse = await ratingService.createRatingResponse(responseData);

            // Update local state
            setRatings(prevRatings =>
                prevRatings.map(rating =>
                    rating.id === ratingId
                        ? {
                            ...rating,
                            response: {
                                id: newResponse.id,
                                ownerId: user?.id || 0,
                                content: newResponse.content,
                                createdAt: newResponse.createdAt
                            }
                        }
                        : rating
                )
            );

            toast.success("Your response has been submitted");
        } catch (error) {
            console.error('Error submitting response:', error);
            toast.error("Could not submit your response");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="container mx-auto py-6 space-y-6">
            <div className="flex flex-col space-y-2">
                <h1 className="text-3xl font-bold">Reviews & Ratings</h1>
                <p className="text-muted-foreground">
                    Manage and respond to guest ratings for your properties
                </p>
            </div>

            <div className="flex flex-col space-y-4">
                <div className="flex items-center space-x-4">
                    <Select
                        value={selectedAccommodationId?.toString() || ""}
                        onValueChange={(value) => setSelectedAccommodationId(Number(value))}
                    >
                        <SelectTrigger className="w-[280px]">
                            <SelectValue placeholder="Select a property" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectGroup>
                                <SelectLabel>Your Properties</SelectLabel>
                                {accommodations.map((accommodation) => (
                                    <SelectItem
                                        key={accommodation.id}
                                        value={accommodation.id.toString()}
                                    >
                                        {accommodation.name}
                                    </SelectItem>
                                ))}
                            </SelectGroup>
                        </SelectContent>
                    </Select>
                </div>

                {isLoading && !selectedAccommodationId ? (
                    <div className="flex items-center justify-center p-12">
                        <Loader2 className="h-8 w-8 animate-spin text-primary" />
                        <span className="ml-2">Loading properties...</span>
                    </div>
                ) : selectedAccommodationId ? (
                    <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
                        <TabsList className="grid grid-cols-3 w-[400px]">
                            <TabsTrigger value="overview">Overview</TabsTrigger>
                            <TabsTrigger value="all-reviews">All Reviews</TabsTrigger>
                            {/* <TabsTrigger value="responses">My Responses</TabsTrigger> */}
                        </TabsList>

                        <TabsContent value="overview">
                            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-6">
                                {ratingSummary && <RatingSummaryCards summary={ratingSummary} />}
                            </div>

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-6">
                                <Card>
                                    <CardHeader>
                                        <CardTitle>Rating Distribution</CardTitle>
                                    </CardHeader>
                                    <CardContent>
                                        {ratingSummary && <RatingChart data={ratingSummary.ratingDistribution} />}
                                    </CardContent>
                                </Card>

                                <Card>
                                    <CardHeader>
                                        <CardTitle>Recent Reviews</CardTitle>
                                    </CardHeader>
                                    <CardContent>
                                        <div className="space-y-4">
                                            {ratings.slice(0, 3).map(rating => (
                                                <div key={rating.id} className="border-b pb-4">
                                                    <div className="flex justify-between items-start">
                                                        <div>
                                                            <div className="flex items-center">
                                                                <span className="font-medium">{rating.user.fullName}</span>
                                                                <span className="mx-2">·</span>
                                                                <div className="flex items-center">
                                                                    <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
                                                                    <span className="ml-1 text-sm font-medium">{rating.value}</span>
                                                                </div>
                                                            </div>
                                                            <p className="text-sm text-muted-foreground mt-1">
                                                                {new Date(rating.createdAt).toLocaleDateString()}
                                                            </p>
                                                        </div>
                                                    </div>
                                                    <p className="mt-2 text-sm">{rating.comment}</p>
                                                </div>
                                            ))}
                                            {ratings.length > 3 && (
                                                <Button
                                                    variant="outline"
                                                    className="w-full"
                                                    onClick={() => setActiveTab("all-reviews")}
                                                >
                                                    View all reviews
                                                </Button>
                                            )}
                                        </div>
                                    </CardContent>
                                </Card>
                            </div>
                        </TabsContent>

                        <TabsContent value="all-reviews">
                            <div className="flex flex-col space-y-6">
                                <Card>
                                    <CardHeader>
                                        <CardTitle>Filter Reviews</CardTitle>
                                    </CardHeader>
                                    <CardContent>
                                        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                                            <div>
                                                <Select
                                                    value={filterOptions.rating?.toString() || "all"}
                                                    onValueChange={(value) => setFilterOptions({
                                                        ...filterOptions,
                                                        rating: value === "all" ? null : Number(value)
                                                    })}
                                                >
                                                    <SelectTrigger>
                                                        <SelectValue placeholder="Rating" />
                                                    </SelectTrigger>
                                                    <SelectContent>
                                                        <SelectItem value="all">All Ratings</SelectItem>
                                                        {[10, 9, 8, 7, 6, 5, 4, 3, 2, 1].map(rating => (
                                                            <SelectItem key={rating} value={rating.toString()}>
                                                                {rating} {rating === 1 ? 'Star' : 'Stars'}
                                                            </SelectItem>
                                                        ))}
                                                    </SelectContent>
                                                </Select>
                                            </div>

                                            <div className="md:col-span-2">
                                                <DatePickerWithRange
                                                    onDateChange={(date) => setFilterOptions({
                                                        ...filterOptions,
                                                        dateRange: date || null
                                                    })}
                                                    className="w-full"
                                                />
                                            </div>

                                            <div>
                                                <Select
                                                    value={`${filterOptions.sortBy}-${filterOptions.sortOrder}`}
                                                    onValueChange={(value) => {
                                                        const [sortBy, sortOrder] = value.split('-');
                                                        setFilterOptions({
                                                            ...filterOptions,
                                                            sortBy,
                                                            sortOrder
                                                        });
                                                    }}
                                                >
                                                    <SelectTrigger>
                                                        <SelectValue placeholder="Sort by" />
                                                    </SelectTrigger>
                                                    <SelectContent>
                                                        <SelectItem value="createdAt-desc">Newest</SelectItem>
                                                        <SelectItem value="createdAt-asc">Oldest</SelectItem>
                                                        <SelectItem value="value-desc">Highest Rating</SelectItem>
                                                        <SelectItem value="value-asc">Lowest Rating</SelectItem>
                                                    </SelectContent>
                                                </Select>
                                            </div>
                                        </div>
                                    </CardContent>
                                </Card>

                                <RatingsTable
                                    ratings={ratings.map(rating => ({
                                        ...rating,
                                        response: rating.response || undefined
                                    }))}
                                    isLoading={isLoading}
                                    onRespondClick={handleResponseSubmit}
                                />

                                <Pagination className="justify-center">
                                    <PaginationContent>
                                        <PaginationPrevious
                                            onClick={() => setCurrentPage(prev => Math.max(1, prev - 1))}
                                            className={currentPage === 1 ? "opacity-50 cursor-not-allowed" : ""}
                                            aria-disabled={currentPage === 1}
                                        />

                                        {Array.from({ length: totalPages }).map((_, i) => {
                                            const page = i + 1;

                                            if (
                                                page === 1 ||
                                                page === totalPages ||
                                                (page >= currentPage - 1 && page <= currentPage + 1)
                                            ) {
                                                return (
                                                    <PaginationButton
                                                        key={page}
                                                        onClick={() => setCurrentPage(page)}
                                                        isActive={page === currentPage}
                                                    >
                                                        {page}
                                                    </PaginationButton>
                                                );
                                            } else if (
                                                page === currentPage - 2 ||
                                                page === currentPage + 2
                                            ) {
                                                return <PaginationEllipsis key={`ellipsis-${page}`} />;
                                            }
                                            return null;
                                        })}

                                        <PaginationNext
                                            onClick={() => setCurrentPage(prev => Math.min(totalPages, prev + 1))}
                                            className={currentPage === totalPages ? "opacity-50 cursor-not-allowed" : ""}
                                            aria-disabled={currentPage === totalPages}
                                        />
                                    </PaginationContent>
                                </Pagination>
                            </div>
                        </TabsContent>

                        {/* <TabsContent value="responses">
                            <Card>
                                <CardHeader>
                                    <CardTitle>My Responses to Reviews</CardTitle>
                                </CardHeader>
                                <CardContent>
                                    <div className="space-y-6">
                                        {ratings
                                            .filter(rating => rating.response)
                                            .map(rating => (
                                                <div key={rating.id} className="border-b pb-6">
                                                    <div className="flex justify-between items-start">
                                                        <div>
                                                            <div className="flex items-center">
                                                                <span className="font-medium">{rating.user.fullName}</span>
                                                                <span className="mx-2">·</span>
                                                                <div className="flex items-center">
                                                                    <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
                                                                    <span className="ml-1 text-sm font-medium">{rating.value}</span>
                                                                </div>
                                                            </div>
                                                            <p className="text-sm text-muted-foreground mt-1">
                                                                {new Date(rating.createdAt).toLocaleDateString()}
                                                            </p>
                                                        </div>
                                                    </div>
                                                    <p className="mt-2">{rating.comment}</p>

                                                    <div className="mt-4 ml-6">
                                                        <div className="flex items-center">
                                                            <span className="font-medium">Your Response</span>
                                                            <span className="mx-2">·</span>
                                                            <span className="text-sm text-muted-foreground">
                                                                {rating.response && new Date(rating.response.createdAt).toLocaleDateString()}
                                                            </span>
                                                        </div>
                                                        <p className="mt-2 text-sm">{rating.response && rating.response.content}</p>
                                                    </div>
                                                </div>
                                            ))}

                                        {ratings.filter(rating => rating.response).length === 0 && (
                                            <div className="text-center py-6 text-muted-foreground">
                                                You haven't responded to any reviews yet.
                                            </div>
                                        )}
                                    </div>
                                </CardContent>
                            </Card>
                        </TabsContent> */}
                    </Tabs>
                ) : (
                    <Card className="mt-6">
                        <CardContent className="flex flex-col items-center justify-center py-12">
                            <h3 className="text-lg font-medium">No Properties Found</h3>
                            <p className="text-muted-foreground mt-2">
                                You don't have any properties registered yet.
                            </p>
                            <Button className="mt-4">Add a Property</Button>
                        </CardContent>
                    </Card>
                )}
            </div>
        </div>
    );
}