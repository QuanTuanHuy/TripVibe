"use client";

import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Home, BedDouble, PlusCircle, Search, BadgeCheck, ChevronRight } from "lucide-react";
import Link from "next/link";
import { accommodationService } from "@/services";
import { useRouter } from "next/navigation";
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
import { useAuth } from "@/context/AuthContext";


export default function HotelRooms() {
    const [hotels, setHotels] = useState<Accommodation[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [searchTerm, setSearchTerm] = useState("");
    const [activeTab, setActiveTab] = useState("all");
    // const { userId } = useAuth();

    const router = useRouter();

    useEffect(() => {
        const fetchHotels = async () => {
            try {
                setLoading(true);
                setError(null);


                try {
                    const response = await accommodationService.getAccommodations({
                        hostId: 3
                    })

                    setHotels(response || []);
                } catch (apiErr) {
                    console.error("Error fetching accommodations:", apiErr);
                    setHotels([]);
                }
            } catch (err) {
                console.error("Error fetching hotels:", err);
                setError("Có lỗi xảy ra khi tải danh sách khách sạn. Vui lòng thử lại sau.");
            } finally {
                setLoading(false);
            }
        };

        fetchHotels();
    }, [searchTerm]);

    const filteredHotels = hotels.filter(hotel => {
        // Karena status tidak ada dalam type Accommodation, kita gunakan properti isVerified sebagai gantinya
        if (activeTab === "all") {
            return true;
        } else if (activeTab === "active") {
            return hotel.isVerified === true;
        } else if (activeTab === "inactive") {
            return hotel.isVerified === false;
        }
        return true;
    });

    const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setSearchTerm(e.target.value);
    };

    const handleHotelClick = (hotelId: number) => {
        router.push(`/hotels/manage/${hotelId}`);
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
                        <BreadcrumbPage>Quản lý chỗ nghỉ</BreadcrumbPage>
                    </BreadcrumbItem>
                </BreadcrumbList>
            </Breadcrumb>

            <div className="flex justify-between items-center mb-8">
                <h1 className="text-3xl font-bold">Quản lý chỗ nghỉ</h1>
                <Link href="/hotels/new">
                    <Button className="flex items-center gap-2">
                        <PlusCircle className="h-4 w-4" />
                        Tạo chỗ nghỉ mới
                    </Button>
                </Link>
            </div>

            <div className="bg-white rounded-lg border overflow-hidden mb-8">
                <div className="p-4 border-b">
                    <div className="flex flex-col md:flex-row justify-between gap-4">
                        <div className="relative flex-grow">
                            <Search className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                            <Input
                                placeholder="Tìm kiếm chỗ nghỉ..."
                                className="pl-10"
                                value={searchTerm}
                                onChange={handleSearchChange}
                            />
                        </div>
                        <div>
                            <Tabs defaultValue="all" className="w-full" value={activeTab} onValueChange={setActiveTab}>
                                <TabsList>
                                    <TabsTrigger value="all">Tất cả</TabsTrigger>
                                    <TabsTrigger value="active">Đang hoạt động</TabsTrigger>
                                    <TabsTrigger value="inactive">Đã tạm ngừng</TabsTrigger>
                                </TabsList>
                            </Tabs>
                        </div>
                    </div>
                </div>

                {error && (
                    <div className="bg-red-50 text-red-600 p-4">
                        {error}
                    </div>
                )}

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
                ) : filteredHotels.length === 0 ? (
                    <div className="text-center py-12">
                        <BedDouble className="mx-auto h-12 w-12 text-gray-400 mb-4" />
                        <h3 className="text-lg font-medium mb-2">
                            {searchTerm
                                ? `Không tìm thấy chỗ nghỉ nào với từ khóa "${searchTerm}"`
                                : "Bạn chưa có chỗ nghỉ nào"}
                        </h3>
                        <p className="text-gray-500 mb-6 max-w-md mx-auto">
                            {searchTerm
                                ? "Vui lòng thử tìm kiếm với từ khóa khác."
                                : "Hãy thêm một chỗ nghỉ mới để bắt đầu quản lý và cho thuê phòng."}
                        </p>
                        <Link href="/hotels/new">
                            <Button>
                                Tạo chỗ nghỉ mới
                            </Button>
                        </Link>
                    </div>
                ) : (
                    <div className="divide-y">
                        {filteredHotels.map((hotel) => (
                            <div
                                key={hotel.id}
                                className="p-4 hover:bg-gray-50 cursor-pointer transition-colors"
                                onClick={() => handleHotelClick(hotel.id)}
                            >
                                <div className="flex flex-col md:flex-row md:justify-between md:items-center gap-4">
                                    <div className="flex-grow">
                                        <div className="flex items-center gap-2 mb-1">
                                            <h3 className="font-medium text-lg">
                                                {hotel.name || "Chỗ nghỉ không tên"}
                                            </h3>
                                            {hotel.isVerified && (
                                                <Badge variant="outline" className="bg-green-50 text-green-700 border-green-200">
                                                    <BadgeCheck className="h-3 w-3 mr-1" />
                                                    Đang hoạt động
                                                </Badge>
                                            )}
                                            {hotel.isVerified === false && (
                                                <Badge variant="outline" className="bg-gray-50 text-gray-700 border-gray-200">
                                                    Tạm ngừng
                                                </Badge>
                                            )}
                                        </div>

                                        <div className="flex flex-wrap gap-3 text-sm text-gray-500 mb-2">
                                            <span>{hotel.location?.provinceId || "Chưa có địa điểm"}</span>
                                            <span>{hotel.type?.name || "Chưa phân loại"}</span>
                                            <span>{hotel.units?.length || 0} phòng</span>
                                        </div>

                                        <p className="text-sm text-gray-600 line-clamp-2">
                                            {hotel.description || "Chưa có mô tả chi tiết"}
                                        </p>
                                    </div>

                                    <div className="flex items-center gap-2">
                                        {hotel.thumbnailUrl ? (
                                            <div className="w-24 h-24 rounded overflow-hidden">
                                                <img
                                                    src={hotel.thumbnailUrl}
                                                    alt={hotel.name}
                                                    className="w-full h-full object-cover"
                                                />
                                            </div>
                                        ) : (
                                            <div className="w-24 h-24 bg-gray-100 rounded flex items-center justify-center">
                                                <BedDouble className="h-8 w-8 text-gray-400" />
                                            </div>
                                        )}
                                        <Button variant="ghost" size="sm">
                                            Quản lý phòng
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