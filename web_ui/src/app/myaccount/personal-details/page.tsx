"use client";

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/context/AuthContext';
import Header from '@/components/Header';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Textarea } from '@/components/ui/textarea';
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
    CardFooter,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
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
    Tabs,
    TabsContent,
    TabsList,
    TabsTrigger,
} from "@/components/ui/tabs";
import {
    Calendar
} from "@/components/ui/calendar";
import { format } from "date-fns";
import { vi } from 'date-fns/locale';
import { ArrowLeft, Loader2, Camera, User, MapPin } from 'lucide-react';
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import * as z from "zod";
import { UserProfile, UpdateProfileDto } from '@/types/profile/profile.types';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import Image from 'next/image';

const genderOptions = [
    { label: "Nam", value: "MALE" },
    { label: "Nữ", value: "FEMALE" },
    { label: "Khác", value: "OTHER" },
    { label: "Không muốn tiết lộ", value: "PREFER_NOT_TO_SAY" }
];

const formSchema = z.object({
    name: z.string().min(2, "Tên phải có ít nhất 2 ký tự").max(100, "Tên không được vượt quá 100 ký tự"),
    phone: z.string().regex(/^\+?[0-9]{10,15}$/, "Số điện thoại không hợp lệ").optional().or(z.literal('')),
    gender: z.string().optional(),
    dateOfBirth: z.date().optional(),
    address: z.string().max(255, "Địa chỉ không được vượt quá 255 ký tự").optional().or(z.literal('')),
});

export default function PersonalDetailsPage() {
    const router = useRouter();
    const { user, isAuthenticated } = useAuth();
    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState(false);
    const [profileData, setProfileData] = useState<UserProfile | null>(null);
    const [avatar, setAvatar] = useState<string | null>(null);
    const [avatarFile, setAvatarFile] = useState<File | null>(null);

    // Initialize form with zod schema
    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            name: "",
            phone: "",
            gender: "",
            dateOfBirth: undefined,
            address: "",
        },
    });

    useEffect(() => {
        if (!isAuthenticated) {
            router.push('/login');
            return;
        }

        const fetchUserProfile = async () => {
            setIsLoading(true);
            setError(null);

            try {
                // In production, uncomment:
                /*
                const data = await ProfileService.getUserProfile();
                setProfileData(data);
                
                // Set form values from data
                form.reset({
                    name: data.name || '',
                    phone: data.phone || '',
                    gender: data.gender || '',
                    dateOfBirth: data.dateOfBirth ? new Date(data.dateOfBirth) : undefined,
                    address: data.location?.address || '',
                });
                
                setAvatar(data.avatarUrl || null);
                */

                // Mock data for development:
                setTimeout(() => {
                    const mockData: UserProfile = {
                        id: 1,
                        name: user?.name || 'Quản Tuấn Huy',
                        email: 'huy@gmail.com',
                        phone: '+84123456789',
                        gender: 'MALE',
                        dateOfBirth: '1995-05-15',
                        avatarUrl: user?.avatarUrl || '',
                        isActive: true,
                        memberLevel: 1,
                        location: {
                            address: '72 Hàng Trống, Hoàn Kiếm, Hà Nội',
                            countryId: 1,
                            provinceId: 1,
                            countryName: 'Việt Nam',
                            provinceName: 'Hà Nội',
                        },
                        passport: {
                            passportNumber: 'B12345678',
                            firstName: 'Tuấn Huy',
                            lastName: 'Quản',
                            expirationDate: new Date('2030-01-01').getTime(),
                            nationalityId: 1,
                            nationalityName: 'Việt Nam',
                        },
                        userSetting: {
                            preferredCurrency: 'VND',
                            preferredLanguage: 'vi',
                            receiveNotifications: true,
                            receiveNewsletters: false,
                        }
                    };

                    setProfileData(mockData);

                    // Set form values from mock data
                    form.reset({
                        name: mockData.name || '',
                        phone: mockData.phone || '',
                        gender: mockData.gender || '',
                        dateOfBirth: mockData.dateOfBirth ? new Date(mockData.dateOfBirth) : undefined,
                        address: mockData.location?.address || '',
                    });

                    setAvatar(mockData.avatarUrl || null);
                    setIsLoading(false);
                }, 800);

            } catch (error) {
                console.error('Error fetching user profile:', error);
                setError('Không thể tải thông tin hồ sơ. Vui lòng thử lại sau.');
                setIsLoading(false);
            }
        };

        fetchUserProfile();
    }, [form, isAuthenticated, router, user?.avatarUrl, user?.name]);



    const onSubmit = async (values: z.infer<typeof formSchema>) => {
        setIsSubmitting(true);
        setError(null);
        setSuccess(false);

        try {
            const updateData: UpdateProfileDto = {
                name: values.name,
                phone: values.phone || undefined,
                gender: values.gender,
                dateOfBirth: values.dateOfBirth ? format(values.dateOfBirth, 'yyyy-MM-dd') : undefined,
                location: {
                    address: values.address,
                    // Keep existing country/province data
                    countryId: profileData?.location?.countryId,
                    provinceId: profileData?.location?.provinceId,
                }
            };

            // Upload avatar if changed
            if (avatarFile) {
                // In production, uncomment:
                // await ProfileService.updateAvatar(avatarFile);
                console.log('Uploading avatar:', avatarFile.name);
            }

            // Update profile data
            // In production, uncomment:
            // const updatedProfile = await ProfileService.updateUserProfile(updateData);

            // For development:
            console.log('Submitting profile data:', updateData);

            // Simulated success
            setTimeout(() => {
                setSuccess(true);
                setIsSubmitting(false);

                // Update local state to reflect changes
                if (profileData) {
                    setProfileData({
                        ...profileData,
                        name: values.name,
                        phone: values.phone || profileData.phone,
                        gender: values.gender || profileData.gender,
                        dateOfBirth: values.dateOfBirth ? format(values.dateOfBirth, 'yyyy-MM-dd') : profileData.dateOfBirth,
                        location: {
                            ...profileData.location,
                            address: values.address || profileData.location?.address,
                        }
                    });
                }

                // Clear success message after delay
                setTimeout(() => setSuccess(false), 3000);
            }, 800);

        } catch (error: any) {
            console.error('Error updating profile:', error);
            setError(error instanceof Error ? error.message : 'Có lỗi xảy ra khi cập nhật hồ sơ. Vui lòng thử lại sau.');
            setIsSubmitting(false);
        }
    };

    const handleAvatarChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (file) {
            setAvatarFile(file);
            setAvatar(URL.createObjectURL(file));
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

                <h1 className="text-2xl font-bold mb-6">Thông tin cá nhân</h1>

                {error && (
                    <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-6">
                        {error}
                    </div>
                )}

                {success && (
                    <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded mb-6">
                        Thông tin cá nhân đã được cập nhật thành công.
                    </div>
                )}

                <div className="mb-8 flex flex-col md:flex-row items-center md:items-start gap-6">
                    <div className="relative">
                        <div className="h-32 w-32 rounded-full overflow-hidden bg-gray-200 border-2 border-white shadow">
                            {avatar ? (
                                <div className="relative h-full w-full">
                                    <Image
                                        src={avatar}
                                        alt="Avatar"
                                        fill
                                        sizes="128px"
                                        className="object-cover"
                                        priority
                                    />
                                </div>
                            ) : (
                                <div className="h-full w-full flex items-center justify-center">
                                    <User className="h-16 w-16 text-gray-400" />
                                </div>
                            )}
                        </div>
                        <label
                            htmlFor="avatar-upload"
                            className="absolute bottom-0 right-0 bg-primary text-white p-2 rounded-full cursor-pointer shadow-md hover:bg-primary/90 transition-colors"
                        >
                            <Camera className="h-5 w-5" />
                            <input
                                type="file"
                                id="avatar-upload"
                                accept="image/*"
                                className="hidden"
                                onChange={handleAvatarChange}
                            />
                        </label>
                    </div>

                    <div>
                        <h2 className="text-xl font-semibold">{profileData?.name}</h2>
                        <p className="text-gray-500">{profileData?.email}</p>
                        {profileData?.location?.address && (
                            <div className="flex items-center text-gray-500 mt-1">
                                <MapPin className="h-4 w-4 mr-1" />
                                <span className="text-sm">{profileData.location.address}</span>
                            </div>
                        )}
                        <div className="mt-2 flex items-center">
                            <div className="bg-blue-100 text-blue-800 text-xs font-medium px-2.5 py-0.5 rounded-full flex items-center">
                                <span>Genius Cấp {profileData?.memberLevel || 1}</span>
                                <svg viewBox="0 0 28 28" height="14" width="14" aria-hidden="true" className="ml-1"><path d="M14 2.333a.9.9 0 0 0-.9.9v23.434a.9.9 0 0 0 .9.9.9.9 0 0 0 .9-.9V3.233a.9.9 0 0 0-.9-.9zm3.6 2.7a.9.9 0 0 0-.9.9v18.034a.9.9 0 0 0 .9.9.9.9 0 0 0 .9-.9V5.933a.9.9 0 0 0-.9-.9zm-7.2 0a.9.9 0 0 0-.9.9v18.034a.9.9 0 0 0 .9.9.9.9 0 0 0 .9-.9V5.933a.9.9 0 0 0-.9-.9zm10.8 2.7a.9.9 0 0 0-.9.9v12.634a.9.9 0 0 0 .9.9.9.9 0 0 0 .9-.9V8.633a.9.9 0 0 0-.9-.9zm-14.4 0a.9.9 0 0 0-.9.9v12.634a.9.9 0 0 0 .9.9.9.9 0 0 0 .9-.9V8.633a.9.9 0 0 0-.9-.9zm18 2.7a.9.9 0 0 0-.9.9v7.234a.9.9 0 0 0 .9.9.9.9 0 0 0 .9-.9v-7.234a.9.9 0 0 0-.9-.9zm-21.6 0a.9.9 0 0 0-.9.9v7.234a.9.9 0 0 0 .9.9.9.9 0 0 0 .9-.9v-7.234a.9.9 0 0 0-.9-.9z" fill="#0071c2"></path></svg>
                            </div>
                        </div>
                    </div>
                </div>

                <Tabs defaultValue="basic-info" className="mb-8">
                    <TabsList className="grid grid-cols-3 mb-6">
                        <TabsTrigger value="basic-info">Thông tin cơ bản</TabsTrigger>
                        <TabsTrigger value="passport">Hộ chiếu</TabsTrigger>
                        <TabsTrigger value="preferences">Tùy chọn</TabsTrigger>
                    </TabsList>

                    <TabsContent value="basic-info">
                        <Card>
                            <CardHeader>
                                <CardTitle>Thông tin cá nhân</CardTitle>
                                <CardDescription>
                                    Cập nhật thông tin cá nhân của bạn tại đây
                                </CardDescription>
                            </CardHeader>
                            <CardContent>
                                <Form {...form}>
                                    <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
                                        <FormField
                                            control={form.control}
                                            name="name"
                                            render={({ field }) => (
                                                <FormItem>
                                                    <FormLabel>Họ và tên</FormLabel>
                                                    <FormControl>
                                                        <Input placeholder="Nhập họ và tên của bạn" {...field} />
                                                    </FormControl>
                                                    <FormMessage />
                                                </FormItem>
                                            )}
                                        />

                                        <FormField
                                            control={form.control}
                                            name="phone"
                                            render={({ field }) => (
                                                <FormItem>
                                                    <FormLabel>Số điện thoại</FormLabel>
                                                    <FormControl>
                                                        <Input placeholder="+84 xxx xxx xxx" {...field} />
                                                    </FormControl>
                                                    <FormDescription>
                                                        Số điện thoại liên hệ khi cần thiết
                                                    </FormDescription>
                                                    <FormMessage />
                                                </FormItem>
                                            )}
                                        />

                                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
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
                                                            <PopoverContent className="w-auto p-0" align="start">
                                                                <Calendar
                                                                    mode="single"
                                                                    selected={field.value}
                                                                    onSelect={field.onChange}
                                                                    disabled={(date) =>
                                                                        date > new Date() || date < new Date("1900-01-01")
                                                                    }
                                                                    locale={vi}
                                                                    initialFocus
                                                                />
                                                            </PopoverContent>
                                                        </Popover>
                                                        <FormMessage />
                                                    </FormItem>
                                                )}
                                            />
                                        </div>

                                        <FormField
                                            control={form.control}
                                            name="address"
                                            render={({ field }) => (
                                                <FormItem>
                                                    <FormLabel>Địa chỉ</FormLabel>
                                                    <FormControl>
                                                        <Textarea
                                                            placeholder="Nhập địa chỉ của bạn"
                                                            className="resize-none"
                                                            {...field}
                                                        />
                                                    </FormControl>
                                                    <FormMessage />
                                                </FormItem>
                                            )}
                                        />

                                        <CardFooter className="flex justify-between border-t pt-6 pb-0 px-0">
                                            <Button variant="outline" type="button" onClick={() => router.push('/myaccount')}>
                                                Hủy
                                            </Button>
                                            <Button
                                                type="submit"
                                                disabled={isSubmitting}
                                            >
                                                {isSubmitting && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                                                Lưu thay đổi
                                            </Button>
                                        </CardFooter>
                                    </form>
                                </Form>
                            </CardContent>
                        </Card>
                    </TabsContent>

                    <TabsContent value="passport">
                        <Card>
                            <CardHeader>
                                <CardTitle>Thông tin hộ chiếu</CardTitle>
                                <CardDescription>
                                    Thông tin hộ chiếu sẽ được sử dụng khi đặt phòng quốc tế
                                </CardDescription>
                            </CardHeader>
                            <CardContent>
                                <div className="space-y-4">
                                    <div className="grid grid-cols-2 gap-4">
                                        <div>
                                            <p className="text-sm text-gray-500 mb-1">Họ</p>
                                            <p className="font-medium">{profileData?.passport?.lastName || '—'}</p>
                                        </div>
                                        <div>
                                            <p className="text-sm text-gray-500 mb-1">Tên</p>
                                            <p className="font-medium">{profileData?.passport?.firstName || '—'}</p>
                                        </div>
                                    </div>
                                    <div className="grid grid-cols-2 gap-4">
                                        <div>
                                            <p className="text-sm text-gray-500 mb-1">Số hộ chiếu</p>
                                            <p className="font-medium">{profileData?.passport?.passportNumber || '—'}</p>
                                        </div>
                                        <div>
                                            <p className="text-sm text-gray-500 mb-1">Quốc tịch</p>
                                            <p className="font-medium">{profileData?.passport?.nationalityName || '—'}</p>
                                        </div>
                                    </div>
                                    <div>
                                        <p className="text-sm text-gray-500 mb-1">Ngày hết hạn</p>
                                        <p className="font-medium">
                                            {profileData?.passport?.expirationDate
                                                ? format(new Date(profileData.passport.expirationDate), 'dd/MM/yyyy')
                                                : '—'}
                                        </p>
                                    </div>
                                </div>
                            </CardContent>
                            <CardFooter className="border-t pt-6">
                                <Button className="ml-auto">Cập nhật hộ chiếu</Button>
                            </CardFooter>
                        </Card>
                    </TabsContent>

                    <TabsContent value="preferences">
                        <Card>
                            <CardHeader>
                                <CardTitle>Tùy chọn cá nhân</CardTitle>
                                <CardDescription>
                                    Thiết lập tùy chọn của bạn về tiền tệ, ngôn ngữ và thông báo
                                </CardDescription>
                            </CardHeader>
                            <CardContent>
                                <div className="space-y-6">
                                    <div>
                                        <h4 className="text-sm font-medium mb-2">Tiền tệ ưa thích</h4>
                                        <Select defaultValue={profileData?.userSetting?.preferredCurrency || "VND"}>
                                            <SelectTrigger className="w-full md:w-[240px]">
                                                <SelectValue placeholder="Chọn tiền tệ" />
                                            </SelectTrigger>
                                            <SelectContent>
                                                <SelectItem value="VND">VND - Việt Nam Đồng</SelectItem>
                                                <SelectItem value="USD">USD - US Dollar</SelectItem>
                                                <SelectItem value="EUR">EUR - Euro</SelectItem>
                                                <SelectItem value="JPY">JPY - Japanese Yen</SelectItem>
                                                <SelectItem value="KRW">KRW - Korean Won</SelectItem>
                                            </SelectContent>
                                        </Select>
                                    </div>

                                    <div>
                                        <h4 className="text-sm font-medium mb-2">Ngôn ngữ ưa thích</h4>
                                        <Select defaultValue={profileData?.userSetting?.preferredLanguage || "vi"}>
                                            <SelectTrigger className="w-full md:w-[240px]">
                                                <SelectValue placeholder="Chọn ngôn ngữ" />
                                            </SelectTrigger>
                                            <SelectContent>
                                                <SelectItem value="vi">Tiếng Việt</SelectItem>
                                                <SelectItem value="en">English</SelectItem>
                                                <SelectItem value="ja">日本語</SelectItem>
                                                <SelectItem value="ko">한국어</SelectItem>
                                                <SelectItem value="zh">中文</SelectItem>
                                            </SelectContent>
                                        </Select>
                                    </div>

                                    <div className="space-y-3">
                                        <h4 className="text-sm font-medium">Cài đặt thông báo</h4>
                                        <div className="flex items-center justify-between">
                                            <div>
                                                <p className="font-medium">Thông báo ứng dụng</p>
                                                <p className="text-sm text-gray-500">Nhận thông báo về đơn đặt phòng và khuyến mãi</p>
                                            </div>
                                            <div className="relative inline-flex h-6 w-11 items-center rounded-full bg-gray-200 transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:ring-offset-white data-[state=checked]:bg-primary">
                                                <span className="inline-block h-4 w-4 rounded-full bg-white shadow-lg transition-transform data-[state=checked]:translate-x-5" style={{ transform: profileData?.userSetting?.receiveNotifications ? 'translateX(1.25rem)' : 'translateX(0.25rem)' }}></span>
                                            </div>
                                        </div>
                                        <div className="flex items-center justify-between">
                                            <div>
                                                <p className="font-medium">Email bản tin</p>
                                                <p className="text-sm text-gray-500">Nhận thông tin về khuyến mãi và cập nhật mới</p>
                                            </div>
                                            <div className="relative inline-flex h-6 w-11 items-center rounded-full bg-gray-200 transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:ring-offset-white data-[state=checked]:bg-primary">
                                                <span className="inline-block h-4 w-4 rounded-full bg-white shadow-lg transition-transform data-[state=checked]:translate-x-5" style={{ transform: profileData?.userSetting?.receiveNewsletters ? 'translateX(1.25rem)' : 'translateX(0.25rem)' }}></span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </CardContent>
                            <CardFooter className="border-t pt-6">
                                <Button className="ml-auto">Lưu tùy chọn</Button>
                            </CardFooter>
                        </Card>
                    </TabsContent>
                </Tabs>
            </div>
        </div>
    );
}
