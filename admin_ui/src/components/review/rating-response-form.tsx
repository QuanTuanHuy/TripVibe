"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { Loader2, Check } from "lucide-react";

interface RatingResponseFormProps {
    ratingId: number;
    onSubmit: (ratingId: number, content: string) => Promise<void>;
    defaultContent?: string;
}

export function RatingResponseForm({
    ratingId,
    onSubmit,
    defaultContent = ""
}: RatingResponseFormProps) {
    const [content, setContent] = useState(defaultContent);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!content.trim()) return;

        setIsSubmitting(true);
        try {
            await onSubmit(ratingId, content);
            setContent("");
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
                <label htmlFor="response" className="text-sm font-medium">
                    Your Response
                </label>
                <Textarea
                    id="response"
                    placeholder="Write your response to this review..."
                    value={content}
                    onChange={(e) => setContent(e.target.value)}
                    disabled={isSubmitting}
                    className="min-h-[100px]"
                />
            </div>

            <div className="flex justify-end">
                <Button
                    type="submit"
                    disabled={isSubmitting || !content.trim()}
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
        </form>
    );
}