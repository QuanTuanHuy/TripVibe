"use client";

import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { BadgeCheck, BedDouble, PlusCircle, Search, ChevronRight } from "lucide-react";
import Link from "next/link";
import { accommodationService } from "@/services";
import { useParams, useRouter } from "next/navigation";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import {
    Breadcrumb,
    BreadcrumbItem,
    BreadcrumbLink,
    BreadcrumbList,
    BreadcrumbSeparator,
    BreadcrumbPage
} from "@/components/ui/breadcrumb";
import { Accommodation } from "@/types/accommodation/accommodation";

export default function HotelRoomsPage() {
    const params = useParams();
    const router = useRouter();
    const hotelId = params.hotelId as string;

    const [hotel, setHotel] = useState<Accommodation | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [searchTerm, setSearchTerm] = useState("");
    const [activeTab, setActiveTab] = useState("all");

    useEffect(() => {
        const fetchHotelDetails = async () => {
            try {
                setLoading(true);
                setError(null);

                // Fetch hotel details including units
                const response = await accommodationService.getAccommodationById(Number(hotelId));
                setHotel(response);
            } catch (err) {
                console.error("Error fetching hotel details:", err);
                setError("Có lỗi xảy ra khi tải thông tin khách sạn. Vui lòng thử lại sau.");
            } finally {
                setLoading(false);
            }
        };

        if (hotelId) {
            fetchHotelDetails();
        }
    }, [hotelId]);

    // Filter units based on search term and active tab
    const filteredUnits = hotel?.units?.filter(unit => {
        const matchesSearch = unit.description?.toLowerCase().includes(searchTerm.toLowerCase()) ||
            (unit.unitName?.name || "").toLowerCase().includes(searchTerm.toLowerCase());

        if (activeTab === "all") {
            return matchesSearch;
        } else if (activeTab === "available") {
            return matchesSearch && unit.quantity > 0;
        } else if (activeTab === "unavailable") {
            return matchesSearch && unit.quantity <= 0;
        }

        return matchesSearch;
    }) || [];

    const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setSearchTerm(e.target.value);
    };

    const handleRoomClick = (roomId: number) => {
        router.push(`/hotels/manage/${hotelId}/rooms/${roomId}`);
    };

    return (
        <div className="container mx-auto px-4 py-8">
            <Breadcrumb className="mb-6">
                <BreadcrumbList>
                    <BreadcrumbItem>
                        <BreadcrumbLink href="/dashboard">
                            Trang chủ
                        </BreadcrumbLink>
                    </BreadcrumbItem>
                    <BreadcrumbSeparator />
                    <BreadcrumbItem>
                        <BreadcrumbLink href="/hotels/manage">
                            Quản lý chỗ nghỉ
                        </BreadcrumbLink>
                    </BreadcrumbItem>
                    <BreadcrumbSeparator />
                    <BreadcrumbItem>
                        <BreadcrumbPage>
                            {loading ? "Đang tải..." : hotel?.name || "Chi tiết chỗ nghỉ"}
                        </BreadcrumbPage>
                    </BreadcrumbItem>
                </BreadcrumbList>
            </Breadcrumb>

            <div className="flex justify-between items-center mb-8">
                <div>
                    <h1 className="text-3xl font-bold">
                        {loading ? <Skeleton className="h-9 w-64" /> : hotel?.name}
                    </h1>
                    {!loading && hotel && (
                        <p className="text-gray-500">{hotel.location?.detailAddress || "Chưa có địa chỉ"}</p>
                    )}
                </div>
                <Link href={`/hotels/manage/${hotelId}/rooms/new`}>
                    <Button className="flex items-center gap-2">
                        <PlusCircle className="h-4 w-4" />
                        Thêm phòng mới
                    </Button>
                </Link>
            </div>

            {error && (
                <div className="bg-red-50 text-red-600 p-4 rounded-md mb-6">
                    {error}
                </div>
            )}

            <div className="bg-white rounded-lg border overflow-hidden mb-8">
                <div className="p-4 border-b">
                    <div className="flex flex-col md:flex-row justify-between gap-4">
                        <div className="relative flex-grow">
                            <Search className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                            <Input
                                placeholder="Tìm kiếm phòng..."
                                className="pl-10"
                                value={searchTerm}
                                onChange={handleSearchChange}
                            />
                        </div>
                        <div>
                            <Tabs defaultValue="all" className="w-full" value={activeTab} onValueChange={setActiveTab}>
                                <TabsList>
                                    <TabsTrigger value="all">Tất cả</TabsTrigger>
                                    <TabsTrigger value="available">Có sẵn</TabsTrigger>
                                    <TabsTrigger value="unavailable">Hết phòng</TabsTrigger>
                                </TabsList>
                            </Tabs>
                        </div>
                    </div>
                </div>

                {loading ? (
                    <div className="p-8">
                        {Array.from({ length: 3 }).map((_, index) => (
                            <div key={index} className="mb-4 p-4 border rounded-md">
                                <Skeleton className="h-7 w-48 mb-3" />
                                <div className="flex gap-4 mb-3">
                                    <Skeleton className="h-5 w-24" />
                                    <Skeleton className="h-5 w-32" />
                                </div>
                                <Skeleton className="h-4 w-full max-w-md mb-4" />
                            </div>
                        ))}
                    </div>
                ) : filteredUnits.length === 0 ? (
                    <div className="text-center py-12">
                        <BedDouble className="mx-auto h-12 w-12 text-gray-400 mb-4" />
                        <h3 className="text-lg font-medium mb-2">
                            {searchTerm
                                ? `Không tìm thấy phòng nào với từ khóa "${searchTerm}"`
                                : "Chưa có phòng nào"}
                        </h3>
                        <p className="text-gray-500 mb-6 max-w-md mx-auto">
                            {searchTerm
                                ? "Vui lòng thử tìm kiếm với từ khóa khác."
                                : "Hãy thêm phòng mới để bắt đầu quản lý và cho thuê phòng."}
                        </p>
                        <Link href={`/hotels/manage/${hotelId}/rooms/new`}>
                            <Button>
                                Thêm phòng mới
                            </Button>
                        </Link>
                    </div>
                ) : (
                    <div className="divide-y">
                        {filteredUnits.map((unit) => (
                            <div
                                key={unit.id}
                                className="p-4 hover:bg-gray-50 cursor-pointer transition-colors"
                                onClick={() => handleRoomClick(unit.id!)}
                            >
                                <div className="flex flex-col md:flex-row md:justify-between md:items-center gap-4">
                                    <div className="flex-grow">
                                        <div className="flex items-center gap-2 mb-1">
                                            <h3 className="font-medium text-lg">
                                                {unit.unitName?.name || "Phòng không tên"}
                                            </h3>
                                            {unit.quantity > 0 ? (
                                                <Badge variant="outline" className="bg-green-50 text-green-700 border-green-200">
                                                    <BadgeCheck className="h-3 w-3 mr-1" />
                                                    Còn {unit.quantity} phòng
                                                </Badge>
                                            ) : (
                                                <Badge variant="outline" className="bg-red-50 text-red-700 border-red-200">
                                                    Hết phòng
                                                </Badge>
                                            )}
                                        </div>

                                        <div className="flex flex-wrap gap-3 text-sm text-gray-500 mb-2">
                                            <span className="flex items-center">
                                                <BedDouble className="h-4 w-4 mr-1" />
                                                {(unit.maxAdults || 0) + (unit.maxChildren || 0)} người
                                            </span>
                                            <span>{unit.quantity || 0} phòng</span>
                                            <span>{unit.pricePerNight?.toLocaleString('vi-VN')}đ/đêm</span>
                                        </div>

                                        <p className="text-sm text-gray-600 line-clamp-2">
                                            {unit.description || "Chưa có mô tả"}
                                        </p>
                                    </div>

                                    <div className="flex items-center gap-2">
                                        {unit.images && unit.images.length > 0 ? (
                                            <div className="w-24 h-24 rounded overflow-hidden">
                                                <img
                                                    src={unit.images[0].url}
                                                    alt={unit.unitName?.name || "Room image"}
                                                    className="w-full h-full object-cover"
                                                />
                                            </div>
                                        ) : (
                                            <div className="w-24 h-24 bg-gray-100 rounded flex items-center justify-center">
                                                <BedDouble className="h-8 w-8 text-gray-400" />
                                            </div>
                                        )}
                                        <Button variant="ghost" size="sm">
                                            Chỉnh sửa
                                            <ChevronRight className="ml-1 h-4 w-4" />
                                        </Button>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}