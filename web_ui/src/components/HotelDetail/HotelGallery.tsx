"use client";

import { useState, useEffect } from 'react';
import Image from 'next/image';
import { BsGrid3X3Gap } from 'react-icons/bs';
import { X, ChevronLeft, ChevronRight } from 'lucide-react';

interface HotelGalleryProps {
    hotelName: string;
    images: string[];
    maxThumbnails?: number;
    onImageClick?: (imageIndex: number) => void;
}

export default function HotelGallery({
    hotelName,
    images,
    maxThumbnails = 4,
    onImageClick
}: HotelGalleryProps) {
    const [showAllPhotos, setShowAllPhotos] = useState(false);
    const [selectedImageIndex, setSelectedImageIndex] = useState<number | null>(null);
    const [touchStart, setTouchStart] = useState<number | null>(null);
    const [touchEnd, setTouchEnd] = useState<number | null>(null);
    const [imageLoading, setImageLoading] = useState(false);

    // Add keyboard navigation for modal
    useEffect(() => {
        if (showAllPhotos || selectedImageIndex !== null) {
            const handleKeyDown = (e: KeyboardEvent) => {
                if (e.key === 'Escape') {
                    if (selectedImageIndex !== null) {
                        setSelectedImageIndex(null);
                    } else {
                        setShowAllPhotos(false);
                    }
                } else if (selectedImageIndex !== null) {
                    if (e.key === 'ArrowLeft') {
                        setSelectedImageIndex(prev =>
                            prev !== null ? (prev > 0 ? prev - 1 : images.length - 1) : 0
                        );
                    } else if (e.key === 'ArrowRight') {
                        setSelectedImageIndex(prev =>
                            prev !== null ? (prev < images.length - 1 ? prev + 1 : 0) : 0
                        );
                    }
                }
            };
            document.addEventListener('keydown', handleKeyDown);
            document.body.style.overflow = 'hidden';

            return () => {
                document.removeEventListener('keydown', handleKeyDown);
                document.body.style.overflow = 'unset';
            };
        }
    }, [showAllPhotos, selectedImageIndex, images.length]);

    const handleImageClick = (index: number) => {
        setSelectedImageIndex(index);
        onImageClick?.(index);
    };

    const goToPrevious = () => {
        setImageLoading(true);
        setSelectedImageIndex(prev =>
            prev !== null ? (prev > 0 ? prev - 1 : images.length - 1) : 0
        );
    };

    const goToNext = () => {
        setImageLoading(true);
        setSelectedImageIndex(prev =>
            prev !== null ? (prev < images.length - 1 ? prev + 1 : 0) : 0
        );
    };

    const closeImageViewer = () => {
        setSelectedImageIndex(null);
    };

    // Handle touch gestures for mobile
    const onTouchStart = (e: React.TouchEvent) => {
        setTouchEnd(null);
        setTouchStart(e.targetTouches[0].clientX);
    };

    const onTouchMove = (e: React.TouchEvent) => {
        setTouchEnd(e.targetTouches[0].clientX);
    };

    const onTouchEnd = () => {
        if (!touchStart || !touchEnd) return;
        const distance = touchStart - touchEnd;
        const isLeftSwipe = distance > 50;
        const isRightSwipe = distance < -50;

        if (isLeftSwipe && selectedImageIndex !== null) {
            goToNext();
        }
        if (isRightSwipe && selectedImageIndex !== null) {
            goToPrevious();
        }
    };

    // Image detail viewer modal
    if (selectedImageIndex !== null) {
        return (
            <div
                className="fixed inset-0 bg-black bg-opacity-90 z-50 flex items-center justify-center"
                role="dialog"
                aria-label="Xem ảnh chi tiết"
                onClick={(e) => {
                    if (e.target === e.currentTarget) {
                        closeImageViewer();
                    }
                }}
            >
                <div className="relative w-full h-full flex items-center justify-center">
                    {/* Close button */}
                    <button
                        onClick={closeImageViewer}
                        className="absolute top-4 right-4 bg-black bg-opacity-50 text-white p-2 rounded-full hover:bg-opacity-70 transition-colors z-10"
                        aria-label="Đóng xem ảnh chi tiết"
                    >
                        <X size={24} />
                    </button>                    {/* Previous button */}
                    <button
                        onClick={goToPrevious}
                        className="absolute left-4 top-1/2 transform -translate-y-1/2 bg-black bg-opacity-50 text-white p-2 rounded-full hover:bg-opacity-70 transition-colors z-10 hidden md:block"
                        aria-label="Ảnh trước"
                    >
                        <ChevronLeft size={32} />
                    </button>

                    {/* Next button */}
                    <button
                        onClick={goToNext}
                        className="absolute right-4 top-1/2 transform -translate-y-1/2 bg-black bg-opacity-50 text-white p-2 rounded-full hover:bg-opacity-70 transition-colors z-10 hidden md:block"
                        aria-label="Ảnh tiếp theo"
                    >
                        <ChevronRight size={32} />
                    </button>{/* Main image */}
                    <div
                        className="relative max-w-4xl max-h-[90vh] w-full h-full mx-4"
                        onTouchStart={onTouchStart}
                        onTouchMove={onTouchMove}
                        onTouchEnd={onTouchEnd}
                    >                        <Image
                            src={images[selectedImageIndex]}
                            alt={`${hotelName} - Ảnh ${selectedImageIndex + 1}`}
                            fill
                            className="object-contain"
                            priority
                            onLoad={() => setImageLoading(false)}
                            onLoadStart={() => setImageLoading(true)}
                        />
                        {imageLoading && (
                            <div className="absolute inset-0 flex items-center justify-center bg-black bg-opacity-50">
                                <div className="w-8 h-8 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                            </div>
                        )}
                    </div>

                    {/* Image counter */}
                    <div className="absolute bottom-4 left-1/2 transform -translate-x-1/2 bg-black bg-opacity-50 text-white px-4 py-2 rounded-full">
                        {selectedImageIndex + 1} / {images.length}
                    </div>

                    {/* Back to gallery button */}
                    <button
                        onClick={() => {
                            setSelectedImageIndex(null);
                        }}
                        className="absolute top-4 left-4 bg-black bg-opacity-50 text-white px-4 py-2 rounded-md hover:bg-opacity-70 transition-colors"
                    >
                        ← Quay lại thư viện
                    </button>
                </div>
            </div>
        );
    }
    if (showAllPhotos) {
        return (
            <div className="fixed inset-0 bg-white z-50 overflow-y-auto" role="dialog" aria-label="Thư viện ảnh khách sạn">
                <div className="sticky top-0 bg-white p-4 border-b flex justify-between items-center">
                    <h2 className="text-xl font-bold">{hotelName} - Tất cả hình ảnh</h2>
                    <button
                        onClick={() => setShowAllPhotos(false)}
                        className="bg-gray-200 hover:bg-gray-300 text-gray-800 px-4 py-2 rounded-md flex items-center gap-2 transition-colors"
                        aria-label="Đóng thư viện ảnh"
                    >
                        <X size={16} />
                        Đóng
                    </button>
                </div>                <div className="p-4">
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                        {images.map((image, idx) => (
                            <div
                                key={idx}
                                className="relative h-64 rounded-md overflow-hidden cursor-pointer hover:opacity-90 transition-opacity"
                                onClick={() => handleImageClick(idx)}
                            >
                                <Image
                                    src={image}
                                    alt={`${hotelName} - Ảnh ${idx + 1}`}
                                    fill
                                    className="object-cover"
                                />
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="mb-8 relative">
            <div className="grid grid-cols-12 gap-2 h-96">
                {/* Main large image */}
                <div
                    className="col-span-12 md:col-span-6 relative rounded-lg overflow-hidden cursor-pointer hover:opacity-95 transition-opacity"
                    onClick={() => setShowAllPhotos(true)}
                >
                    <Image
                        src={images[0]}
                        alt={`${hotelName} - Ảnh chính`}
                        fill
                        className="object-cover"
                        priority
                    />
                </div>

                {/* Right side smaller images */}
                <div className="hidden md:grid md:col-span-6 grid-cols-2 gap-2">
                    {images.slice(1, maxThumbnails + 1).map((image, idx) => (
                        <div
                            key={idx}
                            className="relative rounded-lg overflow-hidden cursor-pointer hover:opacity-95 transition-opacity"
                            onClick={() => setShowAllPhotos(true)}
                        >
                            <Image
                                src={image}
                                alt={`${hotelName} - Ảnh ${idx + 2}`}
                                fill
                                className="object-cover"
                            />
                        </div>
                    ))}
                </div>
                {/* View all photos button */}
                <button
                    onClick={() => setShowAllPhotos(true)}
                    className="absolute bottom-4 right-4 bg-white text-gray-800 px-4 py-2 rounded-md shadow-md flex items-center font-medium text-sm hover:bg-gray-50 transition-colors"
                >
                    <BsGrid3X3Gap className="mr-2" />
                    Xem tất cả ảnh ({images.length})
                </button>
            </div>
        </div>
    );
}
