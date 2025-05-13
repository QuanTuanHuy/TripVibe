"use client";

import React from 'react';
import { Heart } from 'lucide-react';
import { cn } from '@/lib/utils';
import { useAnimatedFavorite } from '@/hooks/useAnimatedFavorite';

interface FavoriteButtonProps {
    isFavorite: boolean;
    onClick: () => void;
    size?: 'sm' | 'md' | 'lg';
    className?: string;
}

export const FavoriteButton: React.FC<FavoriteButtonProps> = ({
    isFavorite,
    onClick,
    size = 'md',
    className
}) => {
    const { isAnimating, triggerAnimation } = useAnimatedFavorite();

    const handleClick = (e: React.MouseEvent) => {
        e.preventDefault();
        e.stopPropagation();
        triggerAnimation();
        onClick();
    };

    const sizeClasses = {
        sm: 'p-1.5 rounded-full',
        md: 'p-2 rounded-full',
        lg: 'p-2.5 rounded-full'
    };

    const iconSizes = {
        sm: 'h-4 w-4',
        md: 'h-5 w-5',
        lg: 'h-6 w-6'
    };

    return (
        <button
            onClick={handleClick}
            className={cn(
                'bg-white/70 hover:bg-white transition-all duration-200',
                sizeClasses[size],
                className
            )}
            aria-label={isFavorite ? "Remove from favorites" : "Add to favorites"}
        >
            <Heart
                className={cn(
                    iconSizes[size],
                    isFavorite ? 'fill-red-500 text-red-500' : 'text-gray-600',
                    isAnimating && 'animate-heartbeat'
                )}
            />
        </button>
    );
};
