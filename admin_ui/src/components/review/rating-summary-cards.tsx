"use client";

import { Card, CardContent } from "@/components/ui/card";
import { Star, ThumbsUp, BarChart4 } from "lucide-react";

interface RatingSummary {
    accommodationId: number;
    numberOfRatings: number;
    totalRating: number;
    averageRating: number;
    ratingDistribution: Record<string, number>;
}

interface RatingSummaryCardsProps {
    summary: RatingSummary;
}

export function RatingSummaryCards({ summary }: RatingSummaryCardsProps) {
    // Calculate positive rating percentage (ratings >= 8)
    const positiveRatingCount =
        summary.ratingDistribution["10"] +
        summary.ratingDistribution["9"] +
        summary.ratingDistribution["8"] || 0;
    const positiveRatingPercentage =
        summary.numberOfRatings > 0
            ? Math.round((positiveRatingCount / summary.numberOfRatings) * 100)
            : 0;

    return (
        <>
            <Card>
                <CardContent className="pt-6">
                    <div className="flex items-center justify-between">
                        <div className="flex flex-col">
                            <span className="text-2xl font-bold">
                                {summary.averageRating.toFixed(1)}
                            </span>
                            <span className="text-xs text-muted-foreground mt-1">
                                Average Rating
                            </span>
                        </div>
                        <Star className="h-8 w-8 text-yellow-500 fill-yellow-500" />
                    </div>
                    <div className="flex items-center mt-4">
                        <div className="w-full bg-muted rounded-full h-2.5">
                            <div
                                className="bg-yellow-500 h-2.5 rounded-full"
                                style={{ width: `${(summary.averageRating / 10) * 100}%` }}
                            ></div>
                        </div>
                    </div>
                </CardContent>
            </Card>

            <Card>
                <CardContent className="pt-6">
                    <div className="flex items-center justify-between">
                        <div className="flex flex-col">
                            <span className="text-2xl font-bold">{summary.numberOfRatings}</span>
                            <span className="text-xs text-muted-foreground mt-1">
                                Total Reviews
                            </span>
                        </div>
                        <BarChart4 className="h-8 w-8 text-blue-500" />
                    </div>
                    <div className="mt-4 text-sm">
                        <div className="flex justify-between items-center">
                            <span>Last 30 days</span>
                            <span className="font-medium">
                                {/* Placeholder - would be actual data in real implementation */}
                                {Math.floor(summary.numberOfRatings * 0.2)} reviews
                            </span>
                        </div>
                    </div>
                </CardContent>
            </Card>

            <Card>
                <CardContent className="pt-6">
                    <div className="flex items-center justify-between">
                        <div className="flex flex-col">
                            <span className="text-2xl font-bold">{positiveRatingPercentage}%</span>
                            <span className="text-xs text-muted-foreground mt-1">
                                Positive Ratings
                            </span>
                        </div>
                        <ThumbsUp className="h-8 w-8 text-green-500" />
                    </div>
                    <div className="flex items-center mt-4">
                        <div className="w-full bg-muted rounded-full h-2.5">
                            <div
                                className="bg-green-500 h-2.5 rounded-full"
                                style={{ width: `${positiveRatingPercentage}%` }}
                            ></div>
                        </div>
                    </div>
                </CardContent>
            </Card>
        </>
    );
}