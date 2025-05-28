"use client";

import { useState, useEffect, Suspense } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { useAuth } from '@/context/AuthContext';
import Image from 'next/image';
import { Star, Calendar, MapPin, ArrowLeft, AlertCircle } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Textarea } from '@/components/ui/textarea';
import { CreateRatingDto, RatingCriteriaType } from '@/types/rating/rating.types';
import Header from '@/components/Header';
import { ratingService } from '@/services/rating/ratingService';
import { bookingService, BookingV2 } from '@/services/booking/bookingService';
import accommodationService from '@/services/accommodation/accommodation.service';
import { AccommodationThumbnail } from '@/types/accommodation';
import { epochToDate, formatDate } from '@/lib/datetime.utils';

function WriteReviewContent() {
    const router = useRouter();
    const searchParams = useSearchParams();
    const { isAuthenticated } = useAuth();

    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState(false);
    const [rating, setRating] = useState(0);
    const [comment, setComment] = useState('');
    const [booking, setBooking] = useState<BookingV2 | null>(null);
    const [accommodationDetails, setAccommodationDetails] = useState<AccommodationThumbnail | null>(null);

    // Detailed rating criteria
    const [criteriaRatings, setCriteriaRatings] = useState<Record<RatingCriteriaType, number>>({
        [RatingCriteriaType.CLEANLINESS]: 8,
        [RatingCriteriaType.COMFORT]: 8,
        [RatingCriteriaType.LOCATION]: 8,
        [RatingCriteriaType.FACILITIES]: 8,
        [RatingCriteriaType.STAFF]: 8,
        [RatingCriteriaType.VALUE_FOR_MONEY]: 8,
    });

    // Get URL params
    const stayId = searchParams.get('stayId');
    const accommodationId = searchParams.get('accommodationId');
    const unitId = searchParams.get('unitId');

    useEffect(() => {
        if (!isAuthenticated) {
            router.push('/login');
            return;
        }

        if (!stayId || !accommodationId || !unitId) {
            setError('Thông tin không đầy đủ để viết đánh giá.');
            setIsLoading(false);
            return;
        }

        fetchDetails();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isAuthenticated, router, stayId, accommodationId, unitId]);

    const fetchDetails = async () => {
        try {
            setIsLoading(true);
            setError(null);

            const booking = await bookingService.getBookingDetails(Number(stayId));
            console.log('Booking details:', booking);
            if (!booking) {
                setError('Không tìm thấy thông tin đặt phòng.');
                return;
            }
            setBooking(booking);

            const accommodation = await accommodationService.getAccommodationThumbnails([Number(accommodationId)]);
            console.log('Accommodation details:', accommodation);
            if (!accommodation || accommodation.length === 0) {
                setError('Không tìm thấy thông tin chỗ nghỉ.');
                return;
            }
            setAccommodationDetails(accommodation[0]);
        } catch (error: unknown) {
            const errorMessage = error instanceof Error ? error.message : 'Vui lòng thử lại sau.';
            setError(`Không thể tải thông tin khách sạn: ${errorMessage}`);
            setIsLoading(false);
        } finally {
            setIsLoading(false);
        }
    };

    const handleStarClick = (value: number) => {
        setRating(value);
    };

    // Update overall rating when criteria ratings change
    useEffect(() => {
        const calculateAverageRating = (): number => {
            const values = Object.values(criteriaRatings);
            const sum = values.reduce((acc, curr) => acc + curr, 0);
            return Math.round(sum / values.length);
        };

        const average = calculateAverageRating();
        setRating(average);
    }, [criteriaRatings]);

    const handleSubmit = async () => {
        // Form validation
        if (rating === 0) {
            setError('Vui lòng chọn số sao đánh giá.');
            return;
        }

        if (!comment.trim()) {
            setError('Vui lòng viết nhận xét của bạn về chỗ nghỉ này.');
            return;
        }

        if (comment.trim().length < 20) {
            setError('Đánh giá phải có ít nhất 20 ký tự.');
            return;
        }

        if (!booking || !accommodationDetails) {
            setError('Không có đủ thông tin về chỗ nghỉ để đánh giá.');
            return;
        }

        setIsSubmitting(true);
        setError(null);

        try {
            const reviewData: CreateRatingDto = {
                accommodationId: booking.accommodationId,
                unitId: booking.units?.[0]?.id || Number(unitId),
                bookingId: booking.id,
                value: rating,
                comment: comment.trim(),
                languageId: 1, // Assuming Vietnamese is language ID 1
                ratingDetails: criteriaRatings
            };

            await ratingService.createRating(reviewData);
            setSuccess(true);

            // Chuyển hướng về trang đánh giá sau khi gửi thành công
            setTimeout(() => {
                router.push('/myreviews');
            }, 2000);
        } catch (error: unknown) {
            console.error('Error submitting review:', error);

            let errorMessage = 'Có lỗi xảy ra khi gửi đánh giá. Vui lòng thử lại sau.';

            if (error instanceof Error) {
                if (error.message.includes('401') || error.message.includes('Unauthorized')) {
                    errorMessage = 'Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại để tiếp tục.';
                } else if (error.message.includes('400') || error.message.includes('Bad Request')) {
                    errorMessage = 'Dữ liệu đánh giá không hợp lệ. Vui lòng kiểm tra lại thông tin.';
                } else if (error.message.includes('409') || error.message.includes('Conflict')) {
                    errorMessage = 'Bạn đã đánh giá chỗ nghỉ này rồi.';
                } else if (error.message.includes('403') || error.message.includes('Forbidden')) {
                    errorMessage = 'Bạn không có quyền đánh giá chỗ nghỉ này.';
                } else if (error.message.includes('Network Error')) {
                    errorMessage = 'Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối mạng của bạn.';
                }
            }

            setError(errorMessage);
            setIsSubmitting(false);
        }
    };

    if (isLoading) {
        return (
            <div className="container mx-auto px-4 py-8">
                <div className="flex justify-center items-center min-h-[50vh]">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
                </div>
            </div>
        );
    }

    if (error && !accommodationDetails) {
        return (
            <div className="container mx-auto px-4 py-8">
                <div className="flex flex-col justify-center items-center min-h-[50vh]">
                    <div className="text-red-500 mb-4">
                        <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <circle cx="12" cy="12" r="10"></circle>
                            <line x1="12" y1="8" x2="12" y2="12"></line>
                            <line x1="12" y1="16" x2="12.01" y2="16"></line>
                        </svg>
                    </div>
                    <h2 className="text-xl font-semibold mb-2">Đã xảy ra lỗi</h2>
                    <p>{error}</p>
                    <Button className="mt-4" onClick={() => router.push('/myreviews')}>
                        Quay lại trang đánh giá
                    </Button>
                </div>
            </div>
        );
    }

    if (success) {
        return (
            <div className="container mx-auto px-4 py-8">
                <div className="flex flex-col justify-center items-center min-h-[50vh]">
                    <div className="text-green-500 mb-4">
                        <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                            <polyline points="22 4 12 14.01 9 11.01"></polyline>
                        </svg>
                    </div>
                    <h2 className="text-xl font-semibold mb-2">Đánh giá của bạn đã được gửi thành công!</h2>
                    <p>Cảm ơn bạn đã chia sẻ trải nghiệm của mình.</p>
                    <Button className="mt-4" onClick={() => router.push('/myreviews')}>
                        Quay lại trang đánh giá
                    </Button>
                </div>
            </div>
        );
    }

    return (
        <div>
            <Header />
            <div className="container mx-auto px-4 py-8">
                <Button
                    variant="ghost"
                    className="mb-6 flex items-center text-gray-500 hover:text-black"
                    onClick={() => router.push('/myreviews')}
                >
                    <ArrowLeft className="mr-2 h-4 w-4" /> Quay lại trang đánh giá
                </Button>

                <h1 className="text-2xl font-bold mb-6">Viết đánh giá</h1>

                {error && (
                    <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-6 flex items-center gap-2">
                        <AlertCircle className="w-5 h-5" />
                        <span>{error}</span>
                    </div>
                )}

                {booking && accommodationDetails && (
                    <Card className="mb-8">
                        <CardContent className="pt-6">
                            <div className="flex flex-col md:flex-row gap-6">
                                <div className="relative h-48 w-full md:w-64 rounded-md overflow-hidden">
                                    <Image
                                        src={accommodationDetails.thumbnailUrl || '/placeholder-hotel.jpg'}
                                        alt={accommodationDetails.name}
                                        fill
                                        className="object-cover"
                                    />
                                </div>
                                <div className="flex-1">
                                    <h2 className="text-xl font-bold">{accommodationDetails.name}</h2>
                                    <p className="text-gray-500 flex items-center mt-2">
                                        <MapPin className="h-4 w-4 mr-2" />
                                        {accommodationDetails.location.address || 'Địa chỉ không rõ'}
                                    </p>
                                    <div className="mt-3 text-sm">
                                        <div className="flex items-center">
                                            <Calendar className="h-4 w-4 mr-2" />
                                            <span>
                                                Nhận phòng: <span className="font-medium">{formatDate(epochToDate(booking.stayFrom))}</span>
                                            </span>
                                        </div>
                                        <div className="flex items-center mt-1">
                                            <Calendar className="h-4 w-4 mr-2" />
                                            <span>
                                                Trả phòng: <span className="font-medium">{formatDate(epochToDate(booking.stayTo))}</span>
                                            </span>
                                        </div>
                                    </div>
                                    <p className="text-sm mt-3 font-medium">{accommodationDetails.units.find(u => u.id === booking.units[0].id)?.name || "Phong khong xac dinh"}</p>
                                </div>
                            </div>
                        </CardContent>
                    </Card>
                )}

                <div className="bg-white rounded-lg shadow-sm p-6 mb-8">
                    <h3 className="text-lg font-bold mb-4">Đánh giá của bạn</h3>
                    <div className="mb-6">
                        <label className="block text-sm font-medium mb-2">
                            Xếp hạng tổng thể
                        </label>
                        <div className="flex flex-col">
                            <div className="flex items-center">
                                {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((value) => (
                                    <button
                                        key={value}
                                        type="button"
                                        onClick={() => handleStarClick(value)}
                                        className={`w-8 h-8 mr-1 flex justify-center items-center rounded-full transition-colors ${rating >= value
                                            ? 'text-yellow-400 bg-yellow-50'
                                            : 'text-gray-300 hover:text-yellow-400 hover:bg-yellow-50'
                                            }`}
                                    >
                                        <Star className="w-5 h-5 fill-current" />
                                    </button>
                                ))}
                                <span className="ml-2 text-sm font-medium">
                                    {rating > 0 ? `${rating}/10` : 'Chưa đánh giá'}
                                </span>
                            </div>
                            <div className="mt-2 text-sm text-gray-500">
                                {rating <= 4 && 'Kém - Tôi không hài lòng với trải nghiệm này'}
                                {rating > 4 && rating <= 6 && 'Trung bình - Có một số điểm chưa tốt'}
                                {rating > 6 && rating <= 8 && 'Tốt - Tôi hài lòng với trải nghiệm này'}
                                {rating > 8 && 'Xuất sắc - Trải nghiệm tuyệt vời, vượt mong đợi'}
                            </div>
                        </div>
                    </div>
                    <div className="border-t border-gray-200 my-6 pt-6">
                        <h4 className="text-md font-medium mb-4">Đánh giá chi tiết từng hạng mục</h4>

                        <div className="space-y-4">
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                {/* Cleanliness Rating */}
                                <div className="flex flex-col">
                                    <div className="flex justify-between items-center mb-2">
                                        <label className="text-sm font-medium">Sạch sẽ</label>
                                        <span className="text-sm font-medium">{criteriaRatings[RatingCriteriaType.CLEANLINESS]}/10</span>
                                    </div>
                                    <input
                                        type="range"
                                        min="1"
                                        max="10"
                                        value={criteriaRatings[RatingCriteriaType.CLEANLINESS]}
                                        onChange={(e) => setCriteriaRatings({
                                            ...criteriaRatings,
                                            [RatingCriteriaType.CLEANLINESS]: Number(e.target.value)
                                        })}
                                        className="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-blue-600"
                                    />
                                </div>

                                {/* Comfort Rating */}
                                <div className="flex flex-col">
                                    <div className="flex justify-between items-center mb-2">
                                        <label className="text-sm font-medium">Thoải mái</label>
                                        <span className="text-sm font-medium">{criteriaRatings[RatingCriteriaType.COMFORT]}/10</span>
                                    </div>
                                    <input
                                        type="range"
                                        min="1"
                                        max="10"
                                        value={criteriaRatings[RatingCriteriaType.COMFORT]}
                                        onChange={(e) => setCriteriaRatings({
                                            ...criteriaRatings,
                                            [RatingCriteriaType.COMFORT]: Number(e.target.value)
                                        })}
                                        className="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-blue-600"
                                    />
                                </div>

                                {/* Location Rating */}
                                <div className="flex flex-col">
                                    <div className="flex justify-between items-center mb-2">
                                        <label className="text-sm font-medium">Vị trí</label>
                                        <span className="text-sm font-medium">{criteriaRatings[RatingCriteriaType.LOCATION]}/10</span>
                                    </div>
                                    <input
                                        type="range"
                                        min="1"
                                        max="10"
                                        value={criteriaRatings[RatingCriteriaType.LOCATION]}
                                        onChange={(e) => setCriteriaRatings({
                                            ...criteriaRatings,
                                            [RatingCriteriaType.LOCATION]: Number(e.target.value)
                                        })}
                                        className="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-blue-600"
                                    />
                                </div>

                                {/* Facilities Rating */}
                                <div className="flex flex-col">
                                    <div className="flex justify-between items-center mb-2">
                                        <label className="text-sm font-medium">Tiện nghi</label>
                                        <span className="text-sm font-medium">{criteriaRatings[RatingCriteriaType.FACILITIES]}/10</span>
                                    </div>
                                    <input
                                        type="range"
                                        min="1"
                                        max="10"
                                        value={criteriaRatings[RatingCriteriaType.FACILITIES]}
                                        onChange={(e) => setCriteriaRatings({
                                            ...criteriaRatings,
                                            [RatingCriteriaType.FACILITIES]: Number(e.target.value)
                                        })}
                                        className="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-blue-600"
                                    />
                                </div>

                                {/* Staff Rating */}
                                <div className="flex flex-col">
                                    <div className="flex justify-between items-center mb-2">
                                        <label className="text-sm font-medium">Nhân viên</label>
                                        <span className="text-sm font-medium">{criteriaRatings[RatingCriteriaType.STAFF]}/10</span>
                                    </div>
                                    <input
                                        type="range"
                                        min="1"
                                        max="10"
                                        value={criteriaRatings[RatingCriteriaType.STAFF]}
                                        onChange={(e) => setCriteriaRatings({
                                            ...criteriaRatings,
                                            [RatingCriteriaType.STAFF]: Number(e.target.value)
                                        })}
                                        className="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-blue-600"
                                    />
                                </div>

                                {/* Value for Money Rating */}
                                <div className="flex flex-col">
                                    <div className="flex justify-between items-center mb-2">
                                        <label className="text-sm font-medium">Đáng giá tiền</label>
                                        <span className="text-sm font-medium">{criteriaRatings[RatingCriteriaType.VALUE_FOR_MONEY]}/10</span>
                                    </div>
                                    <input
                                        type="range"
                                        min="1"
                                        max="10"
                                        value={criteriaRatings[RatingCriteriaType.VALUE_FOR_MONEY]}
                                        onChange={(e) => setCriteriaRatings({
                                            ...criteriaRatings,
                                            [RatingCriteriaType.VALUE_FOR_MONEY]: Number(e.target.value)
                                        })}
                                        className="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-blue-600"
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="border-t border-gray-200 my-6 pt-6">
                        <label htmlFor="comment" className="block text-sm font-medium mb-2">
                            Chi tiết đánh giá
                        </label>
                        <p className="text-sm text-gray-500 mb-3">Hãy chia sẻ trải nghiệm của bạn. Điều gì bạn thích nhất? Có điều gì bạn nghĩ có thể được cải thiện không?</p>
                        <Textarea
                            id="comment"
                            placeholder="Hãy chia sẻ trải nghiệm của bạn về chỗ nghỉ này..."
                            value={comment}
                            onChange={(e) => setComment(e.target.value)}
                            rows={5}
                            className="w-full border-gray-200"
                        />
                        <div className="text-xs text-gray-500 mt-2">
                            Số ký tự tối thiểu: 20 | Đã nhập: {comment.length} ký tự
                        </div>
                    </div>

                    <Button
                        onClick={handleSubmit}
                        disabled={isSubmitting}
                        className="w-full md:w-auto"
                    >
                        {isSubmitting ? (
                            <>
                                <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                </svg>
                                Đang gửi...
                            </>
                        ) : (
                            'Gửi đánh giá'
                        )}
                    </Button>
                </div>
            </div>
        </div>
    );
}

export default function WriteReviewPage() {
    return (
        <Suspense fallback={
            <div className="min-h-screen bg-gray-50">
                <Header />
                <div className="flex justify-center items-center py-12">
                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                    <span className="ml-3 text-lg">Đang tải...</span>
                </div>
            </div>
        }>
            <WriteReviewContent />
        </Suspense>
    );
}
