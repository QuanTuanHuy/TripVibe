"use client";

import { useState, useEffect } from "react";
import { useParams, useRouter } from "next/navigation";
import { Home, Save, Trash2, ArrowLeft, PlusCircle } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { accommodationService, bedTypeService } from "@/services";
import { Skeleton } from "@/components/ui/skeleton";
import {
    Form,
    FormControl,
    FormDescription,
    FormField,
    FormItem,
    FormLabel,
    FormMessage
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import * as z from "zod";
import { Checkbox } from "@/components/ui/checkbox";
import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
    AlertDialogTrigger,
} from "@/components/ui/alert-dialog";
import {
    Breadcrumb,
    BreadcrumbItem,
    BreadcrumbLink,
    BreadcrumbList,
    BreadcrumbSeparator,
    BreadcrumbPage
} from "@/components/ui/breadcrumb";
import { Accommodation, Amenity, Unit } from "@/types/accommodation/accommodation";
import { BedType } from "@/types/accommodation";

interface RoomFormValues {
    unitName: string;
    description: string;
    quantity: number;
    pricePerNight: number;
    maxAdults: number;
    maxChildren: number;
    area: number;
    bedrooms: {
        id?: number;
        quantity: number;
        beds: {
            bedTypeId: number;
            quantity: number;
        }[];
    }[];
    amenities: string[];
}

// Define form validation schema
const roomFormSchema = z.object({
    unitName: z.string().min(2, {
        message: "Tên phòng phải có ít nhất 2 ký tự.",
    }),
    description: z.string().min(10, {
        message: "Mô tả phòng phải có ít nhất 10 ký tự.",
    }),
    quantity: z.coerce.number().int().min(0, {
        message: "Số lượng phòng không được là số âm.",
    }),
    pricePerNight: z.coerce.number().min(0, {
        message: "Giá phòng không được là số âm.",
    }),
    maxAdults: z.coerce.number().int().min(1, {
        message: "Phòng phải có chỗ cho ít nhất 1 người lớn.",
    }),
    maxChildren: z.coerce.number().int().min(0, {
        message: "Số trẻ em không được là số âm.",
    }),
    area: z.coerce.number().min(0, {
        message: "Diện tích phòng không được là số âm.",
    }),
    bedrooms: z.array(
        z.object({
            id: z.number().optional(),
            quantity: z.coerce.number().int().min(1, {
                message: "Số lượng phòng ngủ phải ít nhất là 1.",
            }),
            beds: z.array(
                z.object({
                    bedTypeId: z.coerce.number().int().min(1, {
                        message: "Phải chọn loại giường.",
                    }),
                    quantity: z.coerce.number().int().min(1, {
                        message: "Số lượng giường phải ít nhất là 1.",
                    }),
                })
            ).min(1, {
                message: "Mỗi phòng ngủ phải có ít nhất một giường.",
            }),
        })
    ).min(1, {
        message: "Phòng phải có ít nhất một phòng ngủ.",
    }),
    amenities: z.array(z.string()),
});

export default function RoomDetailPage() {
    const params = useParams();
    const router = useRouter();
    const { hotelId, roomId } = params;

    const [room, setRoom] = useState<Unit | null>(null);
    const [hotel, setHotel] = useState<Accommodation | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [saving, setSaving] = useState(false);
    const [bedTypes, setBedTypes] = useState<BedType[]>([]);

    // Initialize the form with updated structure
    const form = useForm<RoomFormValues>({
        resolver: zodResolver(roomFormSchema),
        defaultValues: {
            unitName: "",
            description: "",
            quantity: 1,
            pricePerNight: 0,
            maxAdults: 1,
            maxChildren: 0,
            area: 0,
            bedrooms: [
                {
                    quantity: 1,
                    beds: [{ bedTypeId: 0, quantity: 1 }]
                }
            ],
            amenities: [],
        }
    });

    useEffect(() => {
        const fetchBedTypes = async () => {
            setLoading(true);
            setError(null);

            try {
                const bedTypeResponse = await bedTypeService.getBedTypes({
                    page: 0,
                    pageSize: 100
                });
                setBedTypes(bedTypeResponse.data || []);
            } catch (error) {
                console.error("Error fetching bed types:", error);
                setError("Có lỗi xảy ra khi tải danh sách loại giường. Vui lòng thử lại sau.");
            } finally {
                setLoading(false);
            }
        };

        fetchBedTypes();
    }, [])

    useEffect(() => {
        const fetchRoom = async () => {
            try {
                setLoading(true);
                setError(null);

                // Fetch hotel details
                const hotelResponse = await accommodationService.getAccommodationById(Number(hotelId));
                setHotel(hotelResponse);

                // Fetch room details if not a new room
                if (roomId !== "new") {
                    const roomData = hotelResponse?.units?.find(unit => unit.id === Number(roomId));
                    if (roomData) {
                        setRoom(roomData);

                        // Update form values with room data
                        form.reset({
                            unitName: roomData.unitName?.name || "",
                            description: roomData.description || "",
                            quantity: roomData.quantity || 1,
                            pricePerNight: roomData.pricePerNight || 0,
                            maxAdults: roomData.maxAdults || 1,
                            maxChildren: roomData.maxChildren || 0,
                            area: 0, // Since area doesn't exist in Unit type, default to 0
                            bedrooms: roomData.bedrooms?.map(bedroom => ({
                                id: bedroom.id,
                                quantity: bedroom.quantity || 1,
                                beds: bedroom.beds?.map(bed => ({
                                    bedTypeId: bed.bedTypeId,
                                    quantity: bed.quantity
                                })) || [{ bedTypeId: 1, quantity: 1 }]
                            })) || [
                                    {
                                        quantity: 1,
                                        beds: [{ bedTypeId: 1, quantity: 1 }]
                                    }
                                ],
                            amenities: roomData.amenities?.map(amenity =>
                                amenity.amenityId?.toString() || ""
                            ) || [],
                        });
                    }
                }

            } catch (err) {
                console.error("Error fetching room details:", err);
                setError("Có lỗi xảy ra khi tải thông tin phòng. Vui lòng thử lại sau.");
            } finally {
                setLoading(false);
            }
        };

        fetchRoom();
    }, [hotelId, roomId, form]);

    // Add a function to add a new bedroom
    const addBedroom = () => {
        const currentBedrooms = form.getValues("bedrooms") || [];
        form.setValue("bedrooms", [
            ...currentBedrooms,
            {
                quantity: 1,
                beds: [{ bedTypeId: 1, quantity: 1 }]
            }
        ]);
    };

    // Add a function to remove a bedroom
    const removeBedroom = (index: number) => {
        const currentBedrooms = form.getValues("bedrooms") || [];
        if (currentBedrooms.length > 1) {
            form.setValue("bedrooms", currentBedrooms.filter((_, i) => i !== index));
        }
    };

    // Add a function to add a bed to a bedroom
    const addBed = (bedroomIndex: number) => {
        const currentBedrooms = form.getValues("bedrooms") || [];
        const updatedBedrooms = [...currentBedrooms];

        if (updatedBedrooms[bedroomIndex]) {
            updatedBedrooms[bedroomIndex] = {
                ...updatedBedrooms[bedroomIndex],
                beds: [
                    ...(updatedBedrooms[bedroomIndex].beds || []),
                    { bedTypeId: 1, quantity: 1 }
                ]
            };
            form.setValue("bedrooms", updatedBedrooms);
        }
    };

    // Add a function to remove a bed from a bedroom
    const removeBed = (bedroomIndex: number, bedIndex: number) => {
        const currentBedrooms = form.getValues("bedrooms") || [];
        const updatedBedrooms = [...currentBedrooms];

        if (updatedBedrooms[bedroomIndex] && updatedBedrooms[bedroomIndex].beds) {
            const bedroomBeds = updatedBedrooms[bedroomIndex].beds;

            if (bedroomBeds.length > 1) {
                updatedBedrooms[bedroomIndex] = {
                    ...updatedBedrooms[bedroomIndex],
                    beds: bedroomBeds.filter((_, i) => i !== bedIndex)
                };
                form.setValue("bedrooms", updatedBedrooms);
            }
        }
    };

    const onSubmit = async (data: RoomFormValues) => {
        try {
            setSaving(true);

            // Convert form data to the API format
            const roomData: Partial<Unit> = {
                id: roomId !== "new" ? Number(roomId) : undefined,
                accommodationId: Number(hotelId),
                description: data.description,
                quantity: data.quantity,
                pricePerNight: data.pricePerNight,
                maxAdults: data.maxAdults,
                maxChildren: data.maxChildren,
                useSharedBathroom: false, // Default value as it's required by the Unit type
                unitNameId: room?.unitName?.id || 1, // Use existing ID if available
                // Convert bedrooms data to the correct format for the API
                bedrooms: data.bedrooms.map(bedroom => ({
                    id: bedroom.id || 0, // Set ID if existing, otherwise 0 for new
                    unitId: roomId !== "new" ? Number(roomId) : 0,
                    quantity: bedroom.quantity,
                    beds: bedroom.beds.map(bed => ({
                        id: 0, // New bed will get ID from server
                        bedroomId: bedroom.id || 0,
                        bedTypeId: bed.bedTypeId,
                        quantity: bed.quantity
                    }))
                })),
                // amenities: data.amenities.map(id => ({
                //     unitId: roomId !== "new" ? Number(roomId) : 0,
                //     amenityId: Number(id)
                // })),
            };

            // These methods need to be implemented in your accommodationService
            // or you should use your actual API calls to create/update units
            if (roomId === "new") {
                // Create new room - using an appropriate API method
                await fetch(`/api/accommodations/${hotelId}/units`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(roomData)
                });
            } else {
                // Update existing room - using an appropriate API method
                await fetch(`/api/units/${roomId}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(roomData)
                });
            }

            router.push(`/hotels/manage/${hotelId}`);
        } catch (err) {
            console.error("Error saving room:", err);
            setError("Có lỗi xảy ra khi lưu thông tin phòng. Vui lòng thử lại.");
        } finally {
            setSaving(false);
        }
    };

    const handleDelete = async () => {
        try {
            // This method needs to be implemented or replaced with your actual API call
            await fetch(`/api/units/${roomId}`, { method: 'DELETE' });
            router.push(`/hotels/manage/${hotelId}`);
        } catch (err) {
            console.error("Error deleting room:", err);
            setError("Có lỗi xảy ra khi xóa phòng. Vui lòng thử lại.");
        }
    };

    const isNewRoom = roomId === "new";

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
                        <BreadcrumbLink href={`/hotels/manage/${hotelId}`}>
                            {loading ? "Đang tải..." : hotel?.name}
                        </BreadcrumbLink>
                    </BreadcrumbItem>
                    <BreadcrumbSeparator />
                    <BreadcrumbItem>
                        <BreadcrumbPage>
                            {isNewRoom ? "Thêm phòng mới" : (loading ? "Đang tải..." : room?.unitName?.name || "Chi tiết phòng")}
                        </BreadcrumbPage>
                    </BreadcrumbItem>
                </BreadcrumbList>
            </Breadcrumb>

            <div className="flex justify-between items-center mb-8">
                <div className="flex items-center gap-3">
                    <Button
                        variant="outline"
                        size="icon"
                        onClick={() => router.push(`/hotels/manage/${hotelId}`)}
                    >
                        <ArrowLeft className="h-4 w-4" />
                    </Button>
                    <h1 className="text-3xl font-bold">
                        {isNewRoom ? "Thêm phòng mới" : (loading ? <Skeleton className="h-9 w-64" /> : room?.unitName?.name)}
                    </h1>
                </div>

                <div className="flex gap-2">
                    {!isNewRoom && (
                        <AlertDialog>
                            <AlertDialogTrigger asChild>
                                <Button variant="outline" className="text-red-500 border-red-200 hover:bg-red-50">
                                    <Trash2 className="h-4 w-4 mr-2" />
                                    Xóa phòng
                                </Button>
                            </AlertDialogTrigger>
                            <AlertDialogContent>
                                <AlertDialogHeader>
                                    <AlertDialogTitle>Bạn có chắc chắn muốn xóa?</AlertDialogTitle>
                                    <AlertDialogDescription>
                                        Hành động này không thể hoàn tác. Phòng này sẽ bị xóa vĩnh viễn khỏi hệ thống.
                                    </AlertDialogDescription>
                                </AlertDialogHeader>
                                <AlertDialogFooter>
                                    <AlertDialogCancel>Hủy</AlertDialogCancel>
                                    <AlertDialogAction onClick={handleDelete} className="bg-red-600 hover:bg-red-700">
                                        Xóa
                                    </AlertDialogAction>
                                </AlertDialogFooter>
                            </AlertDialogContent>
                        </AlertDialog>
                    )}

                    <Button
                        type="submit"
                        form="room-form"
                        disabled={saving}
                    >
                        <Save className="h-4 w-4 mr-2" />
                        {saving ? "Đang lưu..." : "Lưu thay đổi"}
                    </Button>
                </div>
            </div>

            {error && (
                <div className="bg-red-50 text-red-600 p-4 rounded-md mb-6">
                    {error}
                </div>
            )}

            {loading ? (
                <div className="space-y-6">
                    <Card>
                        <CardHeader>
                            <Skeleton className="h-7 w-32" />
                        </CardHeader>
                        <CardContent>
                            <div className="space-y-4">
                                <Skeleton className="h-10 w-full" />
                                <Skeleton className="h-20 w-full" />
                                <Skeleton className="h-10 w-full" />
                            </div>
                        </CardContent>
                    </Card>
                </div>
            ) : (
                <Form {...form}>
                    <form id="room-form" onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                        <Tabs defaultValue="basic" className="w-full">
                            <TabsList className="grid w-full grid-cols-3 mb-8">
                                <TabsTrigger value="basic">Thông tin cơ bản</TabsTrigger>
                                <TabsTrigger value="amenities">Tiện nghi</TabsTrigger>
                                <TabsTrigger value="images">Hình ảnh</TabsTrigger>
                            </TabsList>

                            {/* Basic Info Tab */}
                            <TabsContent value="basic" className="space-y-4">
                                <Card>
                                    <CardHeader>
                                        <CardTitle>Thông tin cơ bản</CardTitle>
                                    </CardHeader>
                                    <CardContent className="space-y-4">
                                        <FormField
                                            control={form.control}
                                            name="unitName"
                                            render={({ field }) => (
                                                <FormItem>
                                                    <FormLabel>Tên phòng</FormLabel>
                                                    <FormControl>
                                                        <Input placeholder="Nhập tên phòng" {...field} />
                                                    </FormControl>
                                                    <FormDescription>
                                                        Tên này sẽ hiển thị cho khách hàng khi đặt phòng.
                                                    </FormDescription>
                                                    <FormMessage />
                                                </FormItem>
                                            )}
                                        />

                                        <FormField
                                            control={form.control}
                                            name="description"
                                            render={({ field }) => (
                                                <FormItem>
                                                    <FormLabel>Mô tả phòng</FormLabel>
                                                    <FormControl>
                                                        <Textarea
                                                            placeholder="Mô tả chi tiết về phòng và các tiện nghi..."
                                                            className="min-h-[120px]"
                                                            {...field}
                                                        />
                                                    </FormControl>
                                                    <FormDescription>
                                                        Mô tả đầy đủ sẽ giúp khách hàng hiểu rõ hơn về phòng của bạn.
                                                    </FormDescription>
                                                    <FormMessage />
                                                </FormItem>
                                            )}
                                        />
                                    </CardContent>
                                </Card>

                                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                    <Card>
                                        <CardHeader>
                                            <CardTitle>Chi tiết phòng</CardTitle>
                                        </CardHeader>
                                        <CardContent className="space-y-4">
                                            <FormField
                                                control={form.control}
                                                name="quantity"
                                                render={({ field }) => (
                                                    <FormItem>
                                                        <FormLabel>Số lượng phòng</FormLabel>
                                                        <FormControl>
                                                            <Input type="number" min="0" {...field} />
                                                        </FormControl>
                                                        <FormMessage />
                                                    </FormItem>
                                                )}
                                            />

                                            <FormField
                                                control={form.control}
                                                name="area"
                                                render={({ field }) => (
                                                    <FormItem>
                                                        <FormLabel>Diện tích (m²)</FormLabel>
                                                        <FormControl>
                                                            <Input type="number" min="0" {...field} />
                                                        </FormControl>
                                                        <FormMessage />
                                                    </FormItem>
                                                )}
                                            />

                                            <div className="grid grid-cols-2 gap-4">
                                                <FormField
                                                    control={form.control}
                                                    name="maxAdults"
                                                    render={({ field }) => (
                                                        <FormItem>
                                                            <FormLabel>Số người lớn tối đa</FormLabel>
                                                            <FormControl>
                                                                <Input type="number" min="1" {...field} />
                                                            </FormControl>
                                                            <FormMessage />
                                                        </FormItem>
                                                    )}
                                                />

                                                <FormField
                                                    control={form.control}
                                                    name="maxChildren"
                                                    render={({ field }) => (
                                                        <FormItem>
                                                            <FormLabel>Số trẻ em tối đa</FormLabel>
                                                            <FormControl>
                                                                <Input type="number" min="0" {...field} />
                                                            </FormControl>
                                                            <FormMessage />
                                                        </FormItem>
                                                    )}
                                                />
                                            </div>
                                        </CardContent>
                                    </Card>

                                    <Card>
                                        <CardHeader>
                                            <CardTitle>Phòng ngủ và giường</CardTitle>
                                        </CardHeader>
                                        <CardContent className="space-y-4">
                                            <FormField
                                                control={form.control}
                                                name="bedrooms"
                                                render={() => (
                                                    <FormItem>
                                                        <div className="mb-2">
                                                            <FormLabel>Phòng ngủ</FormLabel>
                                                            <FormDescription>Quản lý các phòng ngủ và giường trong phòng</FormDescription>
                                                        </div>
                                                        {form.getValues("bedrooms").map((bedroom, bedroomIndex) => (
                                                            <div key={bedroomIndex} className="border p-4 rounded-md mb-4">
                                                                <div className="flex justify-between items-center mb-4">
                                                                    <h3 className="text-lg font-semibold">Phòng ngủ {bedroomIndex + 1}</h3>
                                                                    <Button
                                                                        variant="outline"
                                                                        size="sm"
                                                                        onClick={() => removeBedroom(bedroomIndex)}
                                                                    >
                                                                        Xóa phòng ngủ
                                                                    </Button>
                                                                </div>
                                                                <FormField
                                                                    control={form.control}
                                                                    name={`bedrooms.${bedroomIndex}.quantity`}
                                                                    render={({ field }) => (
                                                                        <FormItem>
                                                                            <FormLabel>Số lượng phòng ngủ</FormLabel>
                                                                            <FormControl>
                                                                                <Input type="number" min="1" {...field} />
                                                                            </FormControl>
                                                                            <FormMessage />
                                                                        </FormItem>
                                                                    )}
                                                                />
                                                                <div className="mt-4">
                                                                    <FormLabel className="mb-2">Giường</FormLabel>
                                                                    {bedroom.beds.map((bed, bedIndex) => (
                                                                        <div key={bedIndex} className="flex items-center gap-4 mb-2">
                                                                            <FormField
                                                                                control={form.control}
                                                                                name={`bedrooms.${bedroomIndex}.beds.${bedIndex}.bedTypeId`}
                                                                                render={({ field }) => (
                                                                                    <FormItem>
                                                                                        <FormControl>
                                                                                            <select {...field} className="border rounded-md p-2">
                                                                                                {bedTypes.map((type) => (
                                                                                                    <option key={type.id} value={type.id}>
                                                                                                        {type.name}
                                                                                                    </option>
                                                                                                ))}
                                                                                            </select>
                                                                                        </FormControl>
                                                                                        <FormMessage />
                                                                                    </FormItem>
                                                                                )}
                                                                            />
                                                                            <FormField
                                                                                control={form.control}
                                                                                name={`bedrooms.${bedroomIndex}.beds.${bedIndex}.quantity`}
                                                                                render={({ field }) => (
                                                                                    <FormItem>
                                                                                        <FormControl>
                                                                                            <Input type="number" min="1" {...field} />
                                                                                        </FormControl>
                                                                                        <FormMessage />
                                                                                    </FormItem>
                                                                                )}
                                                                            />
                                                                            <Button
                                                                                variant="outline"
                                                                                size="sm"
                                                                                onClick={() => removeBed(bedroomIndex, bedIndex)}
                                                                            >
                                                                                Xóa giường
                                                                            </Button>
                                                                        </div>
                                                                    ))}
                                                                    <Button
                                                                        variant="outline"
                                                                        size="sm"
                                                                        onClick={() => addBed(bedroomIndex)}
                                                                    >
                                                                        Thêm giường
                                                                    </Button>
                                                                </div>
                                                            </div>
                                                        ))}
                                                        <Button
                                                            variant="outline"
                                                            size="sm"
                                                            onClick={addBedroom}
                                                        >
                                                            Thêm phòng ngủ
                                                        </Button>
                                                        <FormMessage />
                                                    </FormItem>
                                                )}
                                            />
                                        </CardContent>
                                    </Card>
                                </div>
                            </TabsContent>

                            {/* Amenities Tab */}
                            <TabsContent value="amenities">
                                <Card>
                                    <CardHeader>
                                        <CardTitle>Tiện nghi phòng</CardTitle>
                                    </CardHeader>
                                    <CardContent>
                                        <FormField
                                            control={form.control}
                                            name="amenities"
                                            render={() => (
                                                <FormItem>
                                                    <div className="mb-4">
                                                        <FormDescription>
                                                            Chọn các tiện nghi có sẵn trong phòng. Tiện nghi đầy đủ sẽ thu hút khách hàng hơn.
                                                        </FormDescription>
                                                    </div>

                                                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-2">
                                                        {room?.amenities?.map((amenity) => (
                                                            <FormField
                                                                key={amenity.id}
                                                                control={form.control}
                                                                name="amenities"
                                                                render={({ field }) => {
                                                                    return (
                                                                        <FormItem
                                                                            key={amenity.id}
                                                                            className="flex flex-row items-start space-x-3 space-y-0 rounded-md border p-3"
                                                                        >
                                                                            <FormControl>
                                                                                <Checkbox
                                                                                    checked={field.value?.includes(amenity.amenityId.toString())}
                                                                                    onCheckedChange={(checked) => {
                                                                                        return checked
                                                                                            ? field.onChange([...field.value, amenity.amenityId.toString()])
                                                                                            : field.onChange(
                                                                                                field.value?.filter(
                                                                                                    (value) => value !== amenity.amenityId.toString()
                                                                                                )
                                                                                            )
                                                                                    }}
                                                                                />
                                                                            </FormControl>
                                                                            <FormLabel className="text-sm font-normal">
                                                                                {amenity?.amenity?.name || "Tiện nghi không xác định"}
                                                                            </FormLabel>
                                                                        </FormItem>
                                                                    )
                                                                }}
                                                            />
                                                        ))}
                                                    </div>
                                                    <FormMessage />
                                                </FormItem>
                                            )}
                                        />
                                    </CardContent>
                                </Card>
                            </TabsContent>

                            {/* Images Tab */}
                            <TabsContent value="images">
                                <Card>
                                    <CardHeader>
                                        <CardTitle>Hình ảnh phòng</CardTitle>
                                    </CardHeader>
                                    <CardContent>
                                        <div className="space-y-4">
                                            <p className="text-sm text-gray-500">
                                                Thêm hình ảnh phòng để giúp khách hàng có cái nhìn trực quan hơn về chỗ nghỉ của bạn.
                                                Bạn nên thêm ít nhất 3 hình ảnh chất lượng cao cho mỗi loại phòng.
                                            </p>

                                            <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
                                                {room?.images?.map((image, index) => (
                                                    <div key={index} className="relative h-40 rounded-md overflow-hidden border">
                                                        <img
                                                            src={image.url}
                                                            alt={`Room image ${index + 1}`}
                                                            className="w-full h-full object-cover"
                                                        />
                                                        <div className="absolute inset-0 bg-black/40 opacity-0 hover:opacity-100 transition-opacity flex items-center justify-center">
                                                            <Button variant="destructive" size="sm">Xóa</Button>
                                                        </div>
                                                    </div>
                                                ))}

                                                <div className="h-40 border border-dashed rounded-md flex flex-col items-center justify-center text-gray-500 hover:bg-gray-50 cursor-pointer">
                                                    <PlusCircle className="h-8 w-8 mb-2" />
                                                    <p className="text-sm">Thêm ảnh mới</p>
                                                </div>
                                            </div>
                                        </div>
                                    </CardContent>
                                </Card>
                            </TabsContent>
                        </Tabs>
                    </form>
                </Form>
            )}
        </div>
    );
}