"use client";

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/context/AuthContext';
import Header from '@/components/Header';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";
import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog";
import {
    Form,
    FormControl,
    FormDescription,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from "@/components/ui/form";
import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import { format } from "date-fns";
import { vi } from 'date-fns/locale';
import { ArrowLeft, Loader2, UserPlus, Trash2, Edit2, Calendar as CalendarIcon, User } from 'lucide-react';
import { Calendar } from "@/components/ui/calendar";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import * as z from "zod";
import { Friend, CreateFriendDto } from '@/types/profile/profile.types';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';

const genderOptions = [
    { label: "Nam", value: "MALE" },
    { label: "Nữ", value: "FEMALE" },
    { label: "Khác", value: "OTHER" },
    { label: "Không muốn tiết lộ", value: "PREFER_NOT_TO_SAY" }
];

const friendFormSchema = z.object({
    firstName: z.string().min(1, "Tên không được để trống").max(50, "Tên không được vượt quá 50 ký tự"),
    lastName: z.string().min(1, "Họ không được để trống").max(50, "Họ không được vượt quá 50 ký tự"),
    gender: z.string().optional(),
    dateOfBirth: z.date().optional(),
});

export default function TravelCompanionsPage() {
    const router = useRouter();
    const { isAuthenticated } = useAuth();
    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);
    const [companions, setCompanions] = useState<Friend[]>([]);

    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
    const [selectedCompanion, setSelectedCompanion] = useState<Friend | null>(null);
    const [isEditing, setIsEditing] = useState(false);

    // Initialize form with zod schema
    const form = useForm<z.infer<typeof friendFormSchema>>({
        resolver: zodResolver(friendFormSchema),
        defaultValues: {
            firstName: "",
            lastName: "",
            gender: "",
            dateOfBirth: undefined,
        },
    }); useEffect(() => {
        if (!isAuthenticated) {
            router.push('/login');
            return;
        }

        fetchCompanions();
    }, [isAuthenticated, router]);

    // Handle dialog open/close to ensure proper form reset
    useEffect(() => {
        if (!isDialogOpen) {
            // Reset form when closing to ensure clean state next time it opens
            form.reset({
                firstName: "",
                lastName: "",
                gender: "",
                dateOfBirth: undefined,
            });
        }
    }, [isDialogOpen, form]);

    const fetchCompanions = async () => {
        setIsLoading(true);
        setError(null);

        try {
            // In production, uncomment:
            /*
            const data = await ProfileService.getFriends();
            setCompanions(data);
            */

            // Mock data for development:
            setTimeout(() => {
                const mockCompanions: Friend[] = [
                    {
                        id: 1,
                        touristId: 1,
                        firstName: 'Minh',
                        lastName: 'Nguyễn',
                        dateOfBirth: '1990-04-15', // ISO format YYYY-MM-DD
                        gender: 'MALE',
                    },
                    {
                        id: 2,
                        touristId: 1,
                        firstName: 'Hương',
                        lastName: 'Trần',
                        dateOfBirth: '1992-07-22', // ISO format YYYY-MM-DD
                        gender: 'FEMALE',
                    },
                    {
                        id: 3,
                        touristId: 1,
                        firstName: 'Linh',
                        lastName: 'Đặng',
                        gender: 'FEMALE',
                    }
                ];

                setCompanions(mockCompanions);
                setIsLoading(false);
            }, 800);
        } catch (error) {
            console.error('Error fetching companions:', error);
            setError('Không thể tải danh sách người đi cùng. Vui lòng thử lại sau.');
            setIsLoading(false);
        }
    }; const openAddDialog = () => {
        // Reset form và xóa toàn bộ lỗi
        form.reset({
            firstName: "",
            lastName: "",
            gender: "",
            dateOfBirth: undefined,
        });

        // Reset lại các trường dữ liệu, đặc biệt là ngày sinh
        form.clearErrors();

        // Đảm bảo component Calendar được reset bằng cách dùng setTimeout
        // để đảm bảo reset được áp dụng sau khi form được mở
        setTimeout(() => {
            form.setValue("dateOfBirth", undefined);
        }, 50);

        setIsEditing(false);
        setIsDialogOpen(true);
    };

    const openEditDialog = (companion: Friend) => {
        setSelectedCompanion(companion);

        // Make sure dateOfBirth is properly parsed to a Date object
        let dateOfBirth: Date | undefined = undefined;
        if (companion.dateOfBirth) {
            try {
                // Convert string date to Date object (ISO format YYYY-MM-DD)
                // For date-fns compatibility, we need to ensure correct parsing
                const [year, month, day] = companion.dateOfBirth.split('-').map(Number);
                if (!isNaN(year) && !isNaN(month) && !isNaN(day)) {
                    // JavaScript months are 0-based (0 = January)
                    dateOfBirth = new Date(year, month - 1, day);

                    // Check if the date is valid
                    if (isNaN(dateOfBirth.getTime())) {
                        console.error('Created invalid date object:', companion.dateOfBirth);
                        dateOfBirth = undefined;
                    } else {
                        console.log('Successfully parsed date:', dateOfBirth);
                    }
                } else {
                    console.error('Invalid date components:', companion.dateOfBirth);
                }
            } catch (error) {
                console.error('Error parsing date:', error);
                dateOfBirth = undefined;
            }
        }

        form.reset({
            firstName: companion.firstName,
            lastName: companion.lastName,
            gender: companion.gender || undefined,
            dateOfBirth: dateOfBirth,
        });
        setIsEditing(true);
        setIsDialogOpen(true);
    };

    const openDeleteDialog = (companion: Friend) => {
        setSelectedCompanion(companion);
        setIsDeleteDialogOpen(true);
    }; const onSubmit = async (values: z.infer<typeof friendFormSchema>) => {
        setIsSubmitting(true);
        setError(null);

        try {            // Format date properly or set as undefined if not present
            let formattedDate: string | undefined = undefined;

            console.log('Raw date value:', values.dateOfBirth);

            // Kiểm tra đặc biệt để đảm bảo dateOfBirth không null/undefined
            if (values.dateOfBirth && !isNaN(values.dateOfBirth.getTime())) {
                // Ensure we're using consistent ISO format YYYY-MM-DD for backend storage
                formattedDate = format(values.dateOfBirth, 'yyyy-MM-dd');
                console.log('Formatted date for submission:', formattedDate);
            } else {
                console.log('Date is invalid or not selected');
            }

            const companionData: CreateFriendDto = {
                firstName: values.firstName,
                lastName: values.lastName,
                gender: values.gender,
                dateOfBirth: formattedDate,
            };

            if (isEditing && selectedCompanion) {
                // In production, would update via API
                // await ProfileService.updateFriend(selectedCompanion.id, companionData);

                // Update locally for development
                setCompanions(companions.map(c =>
                    c.id === selectedCompanion.id
                        ? {
                            ...c,
                            firstName: companionData.firstName,
                            lastName: companionData.lastName,
                            gender: companionData.gender,
                            dateOfBirth: companionData.dateOfBirth
                        }
                        : c
                ));

                console.log('Updated companion with date:', companionData.dateOfBirth);
                setSuccess(`Đã cập nhật thông tin của ${values.firstName} ${values.lastName}`);
            } else {
                // In production, would create via API
                // const newCompanion = await ProfileService.createFriend(companionData);                // Create locally for development
                const newCompanion: Friend = {
                    id: Math.max(0, ...companions.map(c => c.id)) + 1,
                    touristId: 1,
                    firstName: companionData.firstName,
                    lastName: companionData.lastName,
                    gender: companionData.gender,
                    dateOfBirth: companionData.dateOfBirth, // Bảo đảm dateOfBirth đã được định dạng đúng ở trên
                };

                // Lưu ý đặc biệt: kiểm tra và bảo đảm rằng dateOfBirth được lưu đúng cách
                if (companionData.dateOfBirth) {
                    console.log('Saving with date:', companionData.dateOfBirth);
                } else if (values.dateOfBirth) {
                    // Fallback nếu có date nhưng chưa được định dạng
                    newCompanion.dateOfBirth = format(values.dateOfBirth, 'yyyy-MM-dd');
                    console.log('Fallback date used:', newCompanion.dateOfBirth);
                } else {
                    console.log('No date of birth provided');
                }

                // Log để debug trước khi thêm vào state
                console.log('Created new companion:', newCompanion);

                setCompanions([...companions, newCompanion]);
                setSuccess(`Đã thêm ${values.firstName} ${values.lastName} vào danh sách người đi cùng`);
            }

            setIsDialogOpen(false);
            setIsSubmitting(false);

            // Clear success message after delay
            setTimeout(() => setSuccess(null), 3000);

        } catch (error: any) {
            console.error('Error saving companion:', error);
            setError(error instanceof Error ? error.message : 'Có lỗi xảy ra. Vui lòng thử lại sau.');
            setIsSubmitting(false);
        }
    };

    const handleDelete = async () => {
        if (!selectedCompanion) return;

        setIsSubmitting(true);

        try {
            // In production, would delete via API
            // await ProfileService.deleteFriend(selectedCompanion.id);

            // Delete locally for development
            setCompanions(companions.filter(c => c.id !== selectedCompanion.id));
            setSuccess(`Đã xóa ${selectedCompanion.firstName} ${selectedCompanion.lastName} khỏi danh sách`);

            setIsDeleteDialogOpen(false);
            setSelectedCompanion(null);
            setIsSubmitting(false);

            // Clear success message after delay
            setTimeout(() => setSuccess(null), 3000);

        } catch (error: any) {
            console.error('Error deleting companion:', error);
            setError(error instanceof Error ? error.message : 'Có lỗi xảy ra khi xóa. Vui lòng thử lại sau.');
            setIsSubmitting(false);
        }
    };

    if (isLoading) {
        return (
            <div className="min-h-screen bg-gray-50">
                <Header />
                <div className="container max-w-4xl mx-auto py-10 px-4">
                    <div className="flex items-center justify-center min-h-[60vh]">
                        <Loader2 className="h-8 w-8 animate-spin text-primary" />
                        <span className="ml-2">Đang tải thông tin...</span>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <Header />
            <div className="container max-w-4xl mx-auto py-6 px-4">
                <Button
                    variant="ghost"
                    className="mb-6 flex items-center text-gray-500 hover:text-black"
                    onClick={() => router.push('/myaccount')}
                >
                    <ArrowLeft className="mr-2 h-4 w-4" /> Quay lại tài khoản
                </Button>

                <div className="flex justify-between items-center mb-6">
                    <h1 className="text-2xl font-bold">Người đi cùng</h1>
                    <Button onClick={openAddDialog} className="flex items-center">
                        <UserPlus className="mr-2 h-4 w-4" /> Thêm người đi cùng
                    </Button>
                </div>

                {error && (
                    <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-6">
                        {error}
                    </div>
                )}

                {success && (
                    <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded mb-6">
                        {success}
                    </div>
                )}

                <Card className="mb-8">
                    <CardHeader>
                        <CardTitle>Danh sách người đi cùng</CardTitle>
                        <CardDescription>
                            Người đi cùng sẽ được thêm vào đơn đặt phòng của bạn
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        {companions.length > 0 ? (
                            <div className="space-y-4">
                                {companions.map((companion) => (
                                    <div
                                        key={companion.id}
                                        className="flex flex-col md:flex-row justify-between items-start md:items-center p-4 border border-gray-200 rounded-lg"
                                    >
                                        <div className="flex items-center mb-3 md:mb-0">
                                            <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center text-primary mr-3">
                                                <User className="h-5 w-5" />
                                            </div>
                                            <div>
                                                <p className="font-medium">{companion.firstName} {companion.lastName}</p>
                                                <div className="flex items-center text-sm text-gray-500 mt-1">
                                                    {companion.gender && (
                                                        <span className="mr-2">{
                                                            companion.gender === 'MALE' ? 'Nam' :
                                                                companion.gender === 'FEMALE' ? 'Nữ' :
                                                                    companion.gender === 'OTHER' ? 'Khác' : 'Không tiết lộ'
                                                        }</span>
                                                    )}                                                    {companion.dateOfBirth && (
                                                        <span className="flex items-center">
                                                            <CalendarIcon className="h-3.5 w-3.5 mr-1" />
                                                            {(() => {
                                                                try {
                                                                    if (!companion.dateOfBirth) return null;

                                                                    console.log('Rendering dateOfBirth:', companion.dateOfBirth);

                                                                    // Parse date from ISO format (YYYY-MM-DD)
                                                                    const [year, month, day] = companion.dateOfBirth.split('-').map(Number);
                                                                    if (isNaN(year) || isNaN(month) || isNaN(day)) {
                                                                        console.error('Invalid date parts:', companion.dateOfBirth);
                                                                        return 'Ngày không hợp lệ';
                                                                    }

                                                                    const date = new Date(year, month - 1, day);
                                                                    if (isNaN(date.getTime())) {
                                                                        console.error('Invalid date time:', companion.dateOfBirth);
                                                                        return 'Ngày không hợp lệ';
                                                                    }

                                                                    // Format date in Vietnamese style (DD/MM/YYYY)
                                                                    return format(date, 'dd/MM/yyyy');
                                                                } catch (error) {
                                                                    console.error('Error formatting date:', error);
                                                                    return 'Ngày không hợp lệ';
                                                                }
                                                            })()}
                                                        </span>
                                                    )}
                                                </div>
                                            </div>
                                        </div>
                                        <div className="flex space-x-2 w-full md:w-auto">
                                            <Button
                                                variant="outline"
                                                size="sm"
                                                className="flex-1 md:flex-none"
                                                onClick={() => openEditDialog(companion)}
                                            >
                                                <Edit2 className="h-4 w-4 mr-2" />
                                                Sửa
                                            </Button>
                                            <Button
                                                variant="outline"
                                                size="sm"
                                                className="flex-1 md:flex-none text-red-500 border-red-200 hover:bg-red-50 hover:text-red-600"
                                                onClick={() => openDeleteDialog(companion)}
                                            >
                                                <Trash2 className="h-4 w-4 mr-2" />
                                                Xóa
                                            </Button>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <div className="text-center py-12">
                                <div className="bg-gray-100 rounded-full w-16 h-16 mx-auto flex items-center justify-center mb-4">
                                    <UserPlus className="h-8 w-8 text-gray-400" />
                                </div>
                                <h3 className="text-lg font-medium text-gray-800 mb-1">Chưa có người đi cùng nào</h3>
                                <p className="text-gray-500 mb-6">Thêm người đi cùng để đặt phòng và lưu trú dễ dàng hơn</p>
                                <Button onClick={openAddDialog} className="flex items-center mx-auto">
                                    <UserPlus className="mr-2 h-4 w-4" /> Thêm người đi cùng
                                </Button>
                            </div>
                        )}
                    </CardContent>
                </Card>

                <div className="bg-blue-50 p-4 rounded-lg border border-blue-100">
                    <h3 className="font-medium text-blue-800 mb-2">Thông tin hữu ích</h3>
                    <p className="text-sm text-blue-700">
                        Thêm thông tin chi tiết về bạn bè và người thân đi cùng để tiết kiệm thời gian khi đặt phòng.
                        Thông tin này sẽ được sử dụng tự động khi bạn đặt phòng cho nhiều người.
                    </p>
                </div>
            </div>

            {/* Add/Edit Friend Dialog */}
            <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
                <DialogContent className="sm:max-w-[425px]">
                    <DialogHeader>
                        <DialogTitle>{isEditing ? "Cập nhật thông tin" : "Thêm người đi cùng"}</DialogTitle>
                        <DialogDescription>
                            Điền thông tin chi tiết về người đi cùng của bạn
                        </DialogDescription>
                    </DialogHeader>
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4 py-4">
                            <div className="grid grid-cols-2 gap-4">
                                <FormField
                                    control={form.control}
                                    name="lastName"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Họ</FormLabel>
                                            <FormControl>
                                                <Input placeholder="Họ" {...field} />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="firstName"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Tên</FormLabel>
                                            <FormControl>
                                                <Input placeholder="Tên" {...field} />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                            </div>

                            <FormField
                                control={form.control}
                                name="gender"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Giới tính</FormLabel>
                                        <Select
                                            onValueChange={field.onChange}
                                            defaultValue={field.value}
                                            value={field.value}
                                        >
                                            <FormControl>
                                                <SelectTrigger>
                                                    <SelectValue placeholder="Chọn giới tính" />
                                                </SelectTrigger>
                                            </FormControl>
                                            <SelectContent>
                                                {genderOptions.map((option) => (
                                                    <SelectItem key={option.value} value={option.value}>
                                                        {option.label}
                                                    </SelectItem>
                                                ))}
                                            </SelectContent>
                                        </Select>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />

                            <FormField
                                control={form.control}
                                name="dateOfBirth"
                                render={({ field }) => (
                                    <FormItem className="flex flex-col">
                                        <FormLabel>Ngày sinh</FormLabel>
                                        <Popover>
                                            <PopoverTrigger asChild>
                                                <FormControl>
                                                    <Button
                                                        variant={"outline"}
                                                        className={`w-full pl-3 text-left font-normal ${!field.value ? "text-muted-foreground" : ""}`}
                                                    >
                                                        {field.value ? (
                                                            format(field.value, "dd/MM/yyyy", { locale: vi })
                                                        ) : (
                                                            <span>Chọn ngày sinh</span>
                                                        )}
                                                    </Button>
                                                </FormControl>
                                            </PopoverTrigger>
                                            <PopoverContent className="w-auto p-0" align="start">                                                <Calendar
                                                mode="single"
                                                selected={field.value}
                                                onSelect={(date) => {
                                                    console.log("Date selected:", date);
                                                    // Xử lý đặc biệt để đảm bảo date không bị null
                                                    if (date) {
                                                        field.onChange(date);
                                                        // Cập nhật trực tiếp giá trị của form để đảm bảo nó được lưu
                                                        form.setValue("dateOfBirth", date);
                                                    }
                                                }}
                                                disabled={(date) =>
                                                    date > new Date() || date < new Date("1900-01-01")
                                                }
                                                locale={vi}
                                                initialFocus
                                            />
                                            </PopoverContent>
                                        </Popover>
                                        <FormMessage />
                                        <FormDescription>
                                            Tuỳ chọn, dùng cho đặt vé/phòng cho trẻ em hoặc người cao tuổi
                                        </FormDescription>
                                    </FormItem>
                                )}
                            />

                            <DialogFooter className="pt-4">
                                <Button
                                    type="submit"
                                    disabled={isSubmitting}
                                >
                                    {isSubmitting && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                                    {isEditing ? "Cập nhật" : "Thêm"}
                                </Button>
                            </DialogFooter>
                        </form>
                    </Form>
                </DialogContent>
            </Dialog>

            {/* Delete Confirmation Dialog */}
            <AlertDialog open={isDeleteDialogOpen} onOpenChange={setIsDeleteDialogOpen}>
                <AlertDialogContent>
                    <AlertDialogHeader>
                        <AlertDialogTitle>Xóa người đi cùng</AlertDialogTitle>
                        <AlertDialogDescription>
                            Bạn có chắc chắn muốn xóa {selectedCompanion?.firstName} {selectedCompanion?.lastName} khỏi danh sách người đi cùng?
                            Thao tác này không thể hoàn tác.
                        </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                        <AlertDialogCancel>Hủy</AlertDialogCancel>
                        <AlertDialogAction
                            onClick={handleDelete}
                            className="bg-red-500 hover:bg-red-600 text-white"
                        >
                            {isSubmitting && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                            Xác nhận xóa
                        </AlertDialogAction>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialog>
        </div>
    );
}
