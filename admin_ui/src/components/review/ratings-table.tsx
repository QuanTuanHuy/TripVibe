"use client";

import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Textarea } from "@/components/ui/textarea";
import { Loader2, Star, MessageSquare, Check, Calendar, Home } from "lucide-react";
import { formatDistanceToNow } from "date-fns";

interface User {
    userId: number;
    fullName: string;
    avatarUrl?: string;
}

interface Unit {
    id: number;
    name: string;
}

interface Rating {
    id: number;
    value: number;
    comment: string;
    createdAt: number;
    user: User;
    unit: Unit;
    response?: {
        id: number;
        ownerId: number;
        content: string;
        createdAt: number;
    };
}

interface RatingsTableProps {
    ratings: Rating[];
    isLoading: boolean;
    onRespondClick: (ratingId: number, content: string) => Promise<void>;
}

export function RatingsTable({ ratings, isLoading, onRespondClick }: RatingsTableProps) {
    const [selectedRating, setSelectedRating] = useState<Rating | null>(null);
    const [responseContent, setResponseContent] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [responseDialogOpen, setResponseDialogOpen] = useState(false);

    const handleResponseSubmit = async () => {
        if (!selectedRating || !responseContent.trim()) return;

        setIsSubmitting(true);
        try {
            await onRespondClick(selectedRating.id, responseContent);
            setResponseDialogOpen(false);
            setResponseContent("");
        } finally {
            setIsSubmitting(false);
        }
    };

    if (isLoading) {
        return (
            <Card>
                <CardContent className="flex justify-center items-center py-12">
                    <Loader2 className="h-8 w-8 animate-spin text-primary" />
                    <span className="ml-2">Loading reviews...</span>
                </CardContent>
            </Card>
        );
    }

    if (ratings.length === 0) {
        return (
            <Card>
                <CardContent className="flex flex-col justify-center items-center py-12">
                    <Star className="h-12 w-12 text-muted-foreground mb-4" />
                    <h3 className="text-lg font-medium">No reviews found</h3>
                    <p className="text-muted-foreground mt-2">
                        No reviews matching your filter criteria were found.
                    </p>
                </CardContent>
            </Card>
        );
    }

    return (
        <Card>
            <CardHeader>
                <CardTitle>All Reviews</CardTitle>
            </CardHeader>
            <CardContent>
                <div className="space-y-8">
                    {ratings.map((rating) => (
                        <div
                            key={rating.id}
                            className="border rounded-md p-4 transition-colors hover:bg-muted/10"
                        >
                            <div className="flex justify-between flex-col sm:flex-row gap-4">
                                <div className="flex gap-4">
                                    <Avatar className="h-12 w-12">
                                        {rating.user.avatarUrl ? (
                                            <AvatarImage src={rating.user.avatarUrl} alt={rating.user.fullName} />
                                        ) : (
                                            <AvatarFallback>
                                                {rating.user.fullName.split(" ").map(n => n[0]).join("").toUpperCase()}
                                            </AvatarFallback>
                                        )}
                                    </Avatar>

                                    <div>
                                        <h3 className="font-medium">{rating.user.fullName}</h3>
                                        <div className="flex gap-2 mt-1">
                                            <div className="flex items-center text-yellow-500">
                                                <Star className="h-4 w-4 fill-yellow-500" />
                                                <span className="ml-1 text-sm font-medium">{rating.value}</span>
                                            </div>
                                            <span className="text-muted-foreground">•</span>
                                            <div className="flex items-center text-sm text-muted-foreground">
                                                <Calendar className="h-3.5 w-3.5 mr-1" />
                                                {formatDistanceToNow(new Date(rating.createdAt), { addSuffix: true })}
                                            </div>
                                            <span className="text-muted-foreground">•</span>
                                            <div className="flex items-center text-sm text-muted-foreground">
                                                <Home className="h-3.5 w-3.5 mr-1" />
                                                {rating.unit.name}
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                {!rating.response && (
                                    <Dialog open={responseDialogOpen && selectedRating?.id === rating.id} onOpenChange={(open) => {
                                        setResponseDialogOpen(open);
                                        if (!open) setResponseContent("");
                                    }}>
                                        <DialogTrigger asChild>
                                            <Button
                                                variant="outline"
                                                size="sm"
                                                className="flex gap-2 whitespace-nowrap"
                                                onClick={() => {
                                                    setSelectedRating(rating);
                                                    setResponseDialogOpen(true);
                                                }}
                                            >
                                                <MessageSquare className="h-4 w-4" />
                                                Respond
                                            </Button>
                                        </DialogTrigger>
                                        <DialogContent>
                                            <DialogHeader>
                                                <DialogTitle>Respond to Review</DialogTitle>
                                            </DialogHeader>
                                            <div className="mt-4 p-4 bg-muted/20 rounded-md">
                                                <div className="flex items-center gap-2 mb-2">
                                                    <div className="flex items-center text-yellow-500">
                                                        <Star className="h-4 w-4 fill-yellow-500" />
                                                        <span className="ml-1 text-sm font-medium">{selectedRating?.value}</span>
                                                    </div>
                                                    <span>•</span>
                                                    <span className="text-sm text-muted-foreground">
                                                        {selectedRating && formatDistanceToNow(new Date(selectedRating.createdAt), { addSuffix: true })}
                                                    </span>
                                                </div>
                                                <p>{selectedRating?.comment}</p>
                                            </div>

                                            <div className="mt-4">
                                                <label className="text-sm font-medium mb-2 block">Your Response</label>
                                                <Textarea
                                                    placeholder="Write your response to this review..."
                                                    value={responseContent}
                                                    onChange={(e) => setResponseContent(e.target.value)}
                                                    className="min-h-[100px]"
                                                />
                                            </div>

                                            <div className="flex justify-end gap-2 mt-4">
                                                <Button variant="outline" onClick={() => setResponseDialogOpen(false)}>
                                                    Cancel
                                                </Button>
                                                <Button
                                                    onClick={handleResponseSubmit}
                                                    disabled={isSubmitting || !responseContent.trim()}
                                                >
                                                    {isSubmitting ? (
                                                        <>
                                                            <Loader2 className="h-4 w-4 animate-spin mr-2" />
                                                            Submitting...
                                                        </>
                                                    ) : (
                                                        <>
                                                            <Check className="h-4 w-4 mr-2" />
                                                            Submit Response
                                                        </>
                                                    )}
                                                </Button>
                                            </div>
                                        </DialogContent>
                                    </Dialog>
                                )}
                            </div>

                            <div className="mt-4">
                                <p>{rating.comment}</p>
                            </div>

                            {rating.response && (
                                <div className="mt-4 ml-12 p-4 rounded-md bg-muted/10 border-l-2 border-primary">
                                    <div className="flex items-center gap-2">
                                        <span className="font-medium">Your Response</span>
                                        <span className="text-muted-foreground">•</span>
                                        <span className="text-sm text-muted-foreground">
                                            {formatDistanceToNow(new Date(rating.response.createdAt), { addSuffix: true })}
                                        </span>
                                    </div>
                                    <p className="mt-2 text-sm">{rating.response.content}</p>
                                </div>
                            )}
                        </div>
                    ))}
                </div>
            </CardContent>
        </Card>
    );
}