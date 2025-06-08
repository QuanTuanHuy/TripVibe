"use client";

import { useState } from 'react';

interface UseAnimatedFavoriteOptions {
    duration?: number;
    delay?: number;
    onComplete?: () => void;
}

export function useAnimatedFavorite({
    duration = 300,
    delay = 0,
    onComplete
}: UseAnimatedFavoriteOptions = {}) {
    const [isAnimating, setIsAnimating] = useState(false);
    const [animationComplete, setAnimationComplete] = useState(false);

    // Function to trigger animation
    const triggerAnimation = () => {
        setIsAnimating(true);
        setAnimationComplete(false);

        // Reset animation after duration
        setTimeout(() => {
            setIsAnimating(false);
            setAnimationComplete(true);
            if (onComplete) onComplete();
        }, duration + delay);
    };

    return {
        isAnimating,
        animationComplete,
        triggerAnimation
    };
}
