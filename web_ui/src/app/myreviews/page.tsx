"use client";

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/context/AuthContext';
import Image from 'next/image';
import { Star, ThumbsUp, ThumbsDown, Calendar, MapPin, Edit, Trash2, MessageSquare, AlertCircle, Loader2 } from 'lucide-react';
import { formatDistanceToNow } from 'date-fns';
import { vi } from 'date-fns/locale';
import { Rating } from '@/types/rating/rating.types';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { Textarea } from '@/components/ui/textarea';
import Header from '@/components/Header';

export default function MyReviewsPage() {
    const router = useRouter();
    const { user, isAuthenticated } = useAuth();
    const [activeTab, setActiveTab] = useState('my-reviews');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [editDialogOpen, setEditDialogOpen] = useState(false);
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [currentReview, setCurrentReview] = useState<Rating | null>(null);
    const [editComment, setEditComment] = useState('');
    const [reviews, setReviews] = useState<Rating[]>([]);
    const [pendingReviews, setPendingReviews] = useState<any[]>([]); // Properties to review
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const [isEditLoading, setIsEditLoading] = useState(false);

    const fetchUserReviews = async () => {
        setIsLoading(true);
        setError(null);
        try {
            // In a real implementation, we would call an API to get user's reviews
            // For production, uncomment the following code:
            /*
            const response = await RatingService.getUserRatings({ 
                page: page, 
                pageSize: 10 
            });
            setReviews(response.items);
            setTotalPages(Math.ceil(response.totalCount / response.pageSize));
            */

            // For now, use mock data
            setTimeout(() => {
                const mockReviews: Rating[] = [
                    {
                        id: 1,
                        value: 9,
                        comment: 'Phòng rất rộng và thoáng mát, nhân viên phục vụ nhiệt tình, vị trí tuyệt vời, gần biển và các điểm tham quan. Tôi sẽ quay lại vào lần tới khi du lịch.',
                        languageId: 1,
                        createdAt: Date.now() - 86400000 * 3, // 3 days ago
                        unit: { id: 101, name: 'Deluxe Ocean View', accommodationId: 201 },
                        user: { userId: user?.id || 1, firstName: user?.name?.split(' ')[0] || 'Khách', lastName: user?.name?.split(' ').slice(1).join(' ') || 'Hàng', avatarUrl: user?.avatarUrl },
                        numberOfHelpful: 5,
                        numberOfUnhelpful: 1
                    },
                    {
                        id: 2,
                        value: 7,
                        comment: 'Khách sạn sạch sẽ, nhân viên thân thiện. Tuy nhiên, hơi ồn vào buổi tối do có quán bar gần đó. Bữa sáng ngon, nhiều lựa chọn.',
                        languageId: 1,
                        createdAt: Date.now() - 86400000 * 10, // 10 days ago
                        unit: { id: 102, name: 'Superior City View', accommodationId: 202 },
                        user: { userId: user?.id || 1, firstName: user?.name?.split(' ')[0] || 'Khách', lastName: user?.name?.split(' ').slice(1).join(' ') || 'Hàng', avatarUrl: user?.avatarUrl },
                        numberOfHelpful: 2,
                        numberOfUnhelpful: 0
                    }
                ];

                setReviews(mockReviews);
                setTotalPages(2); // Mock total pages
                setIsLoading(false);
            }, 800);
        } catch (error: unknown) {
            const errorMessage = error instanceof Error ? error.message : 'Không thể tải đánh giá của bạn. Vui lòng thử lại sau.';
            setError(errorMessage);
            setIsLoading(false);
        }
    };

    const fetchPendingReviews = async () => {
        try {
            setIsLoading(true);
            // In production, we would fetch stays that haven't been reviewed yet
            // For production, uncomment the following code:
            /*
            const response = await RatingService.getPendingReviews({ 
                page: 1, 
                pageSize: 10 
            });
            setPendingReviews(response.items);
            */

            // For now, use mock data
            setTimeout(() => {
                const mockPendingReviews = [{
                    id: 301,
                    hotelId: 201,
                    roomId: 101,
                    hotelName: 'Sài Gòn Luxury Hotel & Spa',
                    roomName: 'Phòng Deluxe Hướng Biển',
                    checkIn: '2025-04-20',
                    checkOut: '2025-04-23',
                    hotelImage: 'https://images.unsplash.com/photo-1566073771259-6a8506099945',
                    location: 'Quận 1, Tp. Hồ Chí Minh'
                },
                {
                    id: 302,
                    hotelId: 202,
                    roomId: 102,
                    hotelName: 'Đà Nẵng Beach Resort',
                    roomName: 'Phòng Superior Hướng Vườn',
                    checkIn: '2025-05-15',
                    checkOut: '2025-05-18',
                    hotelImage: 'https://images.unsplash.com/photo-1520250497591-112f2f40a3f4',
                    location: 'Sơn Trà, Đà Nẵng'
                },
                {
                    id: 303,
                    hotelId: 203,
                    roomId: 103,
                    hotelName: 'Đà Nẵng Beachfront Resort',
                    roomName: 'Biệt Thự Hồ Bơi Riêng',
                    checkIn: '2025-03-15',
                    checkOut: '2025-03-18',
                    hotelImage: 'https://images.unsplash.com/photo-1520250497591-112f2f40a3f4',
                    location: 'Bãi biển Mỹ Khê, Đà Nẵng'
                }
                ];
                setPendingReviews(mockPendingReviews);
                setIsLoading(false);
            }, 800);
        } catch (error: unknown) {
            const errorMessage = error instanceof Error ? error.message : 'Không thể tải danh sách chờ đánh giá. Vui lòng thử lại sau.';
            setError(errorMessage);
            setIsLoading(false);
        }
    };

    useEffect(() => {
        // Redirect to login if not authenticated
        if (!isAuthenticated) {
            router.push('/login');
            return;
        }

        fetchUserReviews();
        fetchPendingReviews();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isAuthenticated, router, page]);

    const handleReviewLike = async (reviewId: number, isHelpful: boolean) => {
        try {
            // In production, call the API
            // await RatingService.createRatingHelpfulness(reviewId, { ratingId: reviewId, isHelpful });

            // For now, update locally
            setReviews(prev => prev.map(review => {
                if (review.id === reviewId) {
                    if (isHelpful) {
                        return { ...review, numberOfHelpful: (review.numberOfHelpful || 0) + 1 };
                    } else {
                        return { ...review, numberOfUnhelpful: (review.numberOfUnhelpful || 0) + 1 };
                    }
                }
                return review;
            }));
        } catch (error: unknown) {
            // Log error for debugging in development
            console.error('Error when liking/disliking a review:', error);
            setError('Không thể đánh giá đánh giá này. Vui lòng thử lại.');
        }
    };

    const handleEditClick = (review: Rating) => {
        setCurrentReview(review);
        setEditComment(review.comment);
        setEditDialogOpen(true);
    };

    const handleDeleteClick = (review: Rating) => {
        setCurrentReview(review);
        setDeleteDialogOpen(true);
    };

    const handleEditSubmit = async () => {
        if (!currentReview) return;

        setIsEditLoading(true);
        try {
            // In production, call the API
            // await RatingService.updateRating(currentReview.id, { comment: editComment });

            // For now, update locally
            setReviews(prev => prev.map(review =>
                review.id === currentReview.id ? { ...review, comment: editComment } : review
            ));

            setEditDialogOpen(false);
            setCurrentReview(null);
        } catch (error: unknown) {
            const errorMessage = error instanceof Error ? error.message : 'Không thể cập nhật đánh giá. Vui lòng thử lại.';
            setError(errorMessage);
        } finally {
            setIsEditLoading(false);
        }
    };

    const handleDeleteConfirm = async () => {
        if (!currentReview) return;

        setIsEditLoading(true);
        try {
            // In production, call the API
            // await RatingService.deleteRating(currentReview.id);

            // For now, update locally
            setReviews(prev => prev.filter(review => review.id !== currentReview.id));

            setDeleteDialogOpen(false);
            setCurrentReview(null);
        } catch (error: unknown) {
            const errorMessage = error instanceof Error ? error.message : 'Không thể xóa đánh giá. Vui lòng thử lại.';
            setError(errorMessage);
        } finally {
            setIsEditLoading(false);
        }
    };

    const handleWriteReview = (stayId: number) => {
        // Get the hotel details from pendingReviews
        const stay = pendingReviews.find(item => item.id === stayId);
        if (stay) {
            // Navigate to write-review page with query parameters
            router.push(`/myreviews/write-review?stayId=${stayId}&hotelId=${stay.hotelId || 0}&roomId=${stay.roomId || 0}`);
        }
    };

    const formatTimeAgo = (timestamp: number) => {
        return formatDistanceToNow(new Date(timestamp), { addSuffix: true, locale: vi });
    };

    const renderRating = (rating: number) => {
        return (
            <div className="flex items-center gap-1">
                <Star className="w-5 h-5 text-yellow-400 fill-yellow-400" />
                <span className="font-medium">{rating.toFixed(1)}</span>
            </div>
        );
    };

    // Custom pagination component
    const CustomPagination = ({ currentPage, totalPages, onPageChange }: {
        currentPage: number;
        totalPages: number;
        onPageChange: (page: number) => void;
    }) => {
        return (
            <div className="flex items-center justify-center mt-8 gap-1">
                <Button
                    variant="outline"
                    size="icon"
                    onClick={() => onPageChange(Math.max(1, currentPage - 1))}
                    disabled={currentPage <= 1}
                    className="h-8 w-8"
                >
                    <span className="sr-only">Previous page</span>
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-4 w-4">
                        <path d="m15 18-6-6 6-6" />
                    </svg>
                </Button>
                {Array.from({ length: totalPages }, (_, i) => i + 1).map((p) => (
                    <Button
                        key={p}
                        variant={currentPage === p ? "default" : "outline"}
                        size="icon"
                        onClick={() => onPageChange(p)}
                        className="h-8 w-8"
                    >
                        {p}
                    </Button>
                ))}
                <Button
                    variant="outline"
                    size="icon"
                    onClick={() => onPageChange(Math.min(totalPages, currentPage + 1))}
                    disabled={currentPage >= totalPages}
                    className="h-8 w-8"
                >
                    <span className="sr-only">Next page</span>
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-4 w-4">
                        <path d="m9 18 6-6-6-6" />
                    </svg>
                </Button>
            </div>
        );
    };

    return (
        <div>
            <Header />
            <div className="container mx-auto py-8 px-4">

                <h1 className="text-2xl md:text-3xl font-bold mb-6">Đánh giá của tôi</h1>

                {error && (
                    <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4 flex items-center gap-2">
                        <AlertCircle className="w-5 h-5" />
                        <span>{error}</span>
                    </div>
                )}

                <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
                    <TabsList className="grid grid-cols-2 w-full max-w-md mb-6">
                        <TabsTrigger value="my-reviews">Đánh giá của tôi</TabsTrigger>
                        <TabsTrigger value="pending-reviews">Chỗ nghỉ chờ đánh giá</TabsTrigger>
                    </TabsList>

                    <TabsContent value="my-reviews" className="space-y-4">
                        {isLoading ? (
                            <div className="flex justify-center items-center py-12">
                                <Loader2 className="w-8 h-8 animate-spin text-blue-600" />
                                <span className="ml-3 text-lg">Đang tải...</span>
                            </div>
                        ) : reviews.length > 0 ? (
                            <>
                                {reviews.map((review) => (
                                    <Card key={review.id} className="overflow-hidden">
                                        <CardContent className="p-0">
                                            <div className="p-6">
                                                <div className="flex items-start justify-between">
                                                    <div className="flex items-start gap-3">
                                                        <div className="relative h-14 w-14 rounded-full overflow-hidden bg-gray-100 flex items-center justify-center">
                                                            {review.user?.avatarUrl ? (
                                                                <Image
                                                                    src={review.user.avatarUrl}
                                                                    alt={`${review.user.firstName} ${review.user.lastName}`}
                                                                    fill
                                                                    className="object-cover"
                                                                />
                                                            ) : (
                                                                <span className="text-xl font-bold text-gray-500">
                                                                    {review.user?.firstName?.[0] || 'U'}
                                                                </span>
                                                            )}
                                                        </div>
                                                        <div>
                                                            <h3 className="font-semibold text-lg">{review.unit?.name}</h3>
                                                            <div className="flex items-center gap-2 text-sm text-gray-500 mt-1">
                                                                {renderRating(review.value)}
                                                                <span className="text-gray-300">•</span>
                                                                <span>{formatTimeAgo(review.createdAt)}</span>
                                                            </div>
                                                        </div>
                                                    </div>

                                                    <div className="flex items-center gap-2">
                                                        <Button
                                                            variant="outline"
                                                            size="icon"
                                                            onClick={() => handleEditClick(review)}
                                                            className="h-8 w-8"
                                                        >
                                                            <Edit className="h-4 w-4" />
                                                            <span className="sr-only">Sửa</span>
                                                        </Button>
                                                        <Button
                                                            variant="outline"
                                                            size="icon"
                                                            onClick={() => handleDeleteClick(review)}
                                                            className="h-8 w-8 text-red-500 border-red-200 hover:bg-red-50 hover:text-red-600"
                                                        >
                                                            <Trash2 className="h-4 w-4" />
                                                            <span className="sr-only">Xóa</span>
                                                        </Button>
                                                    </div>
                                                </div>

                                                <div className="mt-4">
                                                    <p className="text-gray-700">{review.comment}</p>
                                                </div>

                                                <div className="mt-4 flex items-center justify-between">
                                                    <div className="flex items-center gap-4">
                                                        <button
                                                            className="flex items-center gap-1 text-gray-500 hover:text-blue-600"
                                                            onClick={() => handleReviewLike(review.id, true)}
                                                        >
                                                            <ThumbsUp className="w-4 h-4" />
                                                            <span>{review.numberOfHelpful || 0}</span>
                                                        </button>
                                                        <button
                                                            className="flex items-center gap-1 text-gray-500 hover:text-blue-600"
                                                            onClick={() => handleReviewLike(review.id, false)}
                                                        >
                                                            <ThumbsDown className="w-4 h-4" />
                                                            <span>{review.numberOfUnhelpful || 0}</span>
                                                        </button>
                                                    </div>

                                                    {/* Management response would go here */}
                                                </div>
                                            </div>
                                        </CardContent>
                                    </Card>
                                ))}

                                {totalPages > 1 && (
                                    <CustomPagination
                                        currentPage={page}
                                        totalPages={totalPages}
                                        onPageChange={setPage}
                                    />
                                )}
                            </>
                        ) : (
                            <div className="text-center py-12 bg-gray-50 rounded-lg">
                                <MessageSquare className="mx-auto h-12 w-12 text-gray-300 mb-3" />
                                <h3 className="text-lg font-medium text-gray-800 mb-1">Bạn chưa có đánh giá nào</h3>
                                <p className="text-gray-500">Sau khi hoàn thành kỳ nghỉ, hãy chia sẻ trải nghiệm của bạn về chỗ nghỉ</p>
                            </div>
                        )}
                    </TabsContent>

                    <TabsContent value="pending-reviews" className="space-y-4">
                        {pendingReviews.length > 0 ? (
                            <>
                                {pendingReviews.map((stay) => (
                                    <Card key={stay.id} className="overflow-hidden">
                                        <div className="md:flex">
                                            <div className="relative h-48 md:h-auto md:w-1/3 md:min-h-[180px]">
                                                <Image
                                                    src={stay.hotelImage}
                                                    alt={stay.hotelName}
                                                    fill
                                                    className="object-cover"
                                                />
                                            </div>
                                            <CardContent className="p-6 md:w-2/3">
                                                <h3 className="font-semibold text-xl mb-2">{stay.hotelName}</h3>
                                                <div className="flex items-center text-gray-500 mb-2">
                                                    <MapPin className="mr-1 h-4 w-4" />
                                                    <span className="text-sm">{stay.location}</span>
                                                </div>

                                                <div className="mt-4 flex flex-col gap-2">
                                                    <div className="flex items-center text-sm">
                                                        <Calendar className="mr-2 h-4 w-4 text-gray-500" />
                                                        <span>
                                                            Check-in: <strong>{new Date(stay.checkIn).toLocaleDateString('vi-VN')}</strong>
                                                            {' — '}
                                                            Check-out: <strong>{new Date(stay.checkOut).toLocaleDateString('vi-VN')}</strong>
                                                        </span>
                                                    </div>
                                                    <div className="text-sm">
                                                        <strong>{stay.roomName}</strong>
                                                    </div>
                                                </div>

                                                <Button
                                                    className="mt-4 w-full md:w-auto"
                                                    onClick={() => handleWriteReview(stay.id)}
                                                >
                                                    Viết đánh giá
                                                </Button>
                                            </CardContent>
                                        </div>
                                    </Card>
                                ))}
                            </>
                        ) : (
                            <div className="text-center py-12 bg-gray-50 rounded-lg">
                                <Star className="mx-auto h-12 w-12 text-gray-300 mb-3" />
                                <h3 className="text-lg font-medium text-gray-800 mb-1">Không có chỗ nghỉ nào chờ đánh giá</h3>
                                <p className="text-gray-500">Các chỗ nghỉ bạn đã ở sẽ xuất hiện ở đây sau khi bạn hoàn thành việc lưu trú</p>
                            </div>
                        )}
                    </TabsContent>
                </Tabs>

                {/* Edit Review Dialog */}
                <Dialog open={editDialogOpen} onOpenChange={setEditDialogOpen}>
                    <DialogContent className="sm:max-w-lg">
                        <DialogHeader>
                            <DialogTitle>Chỉnh sửa đánh giá</DialogTitle>
                        </DialogHeader>

                        {currentReview && (
                            <>
                                <div className="py-4">
                                    <div className="mb-4">
                                        <h3 className="text-sm font-medium mb-2">Đánh giá của bạn</h3>
                                        <div className="flex items-center gap-1">
                                            {renderRating(currentReview.value)}
                                            <span className="text-sm text-gray-500">
                                                ({currentReview.value}/10)
                                            </span>
                                        </div>
                                    </div>

                                    <div className="mb-4">
                                        <label htmlFor="edit-comment" className="text-sm font-medium mb-2 block">
                                            Nhận xét
                                        </label>
                                        <Textarea
                                            id="edit-comment"
                                            value={editComment}
                                            onChange={e => setEditComment(e.target.value)}
                                            placeholder="Chia sẻ trải nghiệm của bạn về chỗ nghỉ này"
                                            rows={5}
                                        />
                                    </div>
                                </div>

                                <DialogFooter>
                                    <Button variant="outline" onClick={() => setEditDialogOpen(false)}>
                                        Hủy
                                    </Button>
                                    <Button
                                        onClick={handleEditSubmit}
                                        disabled={isEditLoading}
                                        className="ml-2"
                                    >
                                        {isEditLoading ? 'Đang lưu...' : 'Lưu thay đổi'}
                                    </Button>
                                </DialogFooter>
                            </>
                        )}
                    </DialogContent>
                </Dialog>

                {/* Delete Review Dialog */}
                <Dialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
                    <DialogContent className="sm:max-w-md">
                        <DialogHeader>
                            <DialogTitle>Xóa đánh giá</DialogTitle>
                        </DialogHeader>

                        <div className="py-4">
                            <p>Bạn có chắc chắn muốn xóa đánh giá này không? Hành động này không thể hoàn tác.</p>
                        </div>

                        <DialogFooter>
                            <Button variant="outline" onClick={() => setDeleteDialogOpen(false)}>
                                Hủy
                            </Button>
                            <Button
                                variant="destructive"
                                onClick={handleDeleteConfirm}
                                disabled={isEditLoading}
                                className="ml-2"
                            >
                                {isEditLoading ? 'Đang xóa...' : 'Xóa đánh giá'}
                            </Button>
                        </DialogFooter>
                    </DialogContent>
                </Dialog>
            </div>
        </div>

    );
}