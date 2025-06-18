"use client";

import { useState, useRef } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { PlusCircle, Trash2, Upload, X, AlertCircle } from 'lucide-react';
import { Image } from '@/types/common';

interface ImageUploadManagerProps {
    images: Image[];
    onImagesUpdate: (newImages: Image[]) => void;
    onUpload: (files: File[]) => Promise<void>;
    onDelete: (imageIds: number[]) => Promise<void>;
    maxImages?: number;
    acceptedTypes?: string[];
    maxFileSize?: number; // in MB
    loading?: boolean;
}

export default function ImageUploadManager({
    images,
    onImagesUpdate,
    onUpload,
    onDelete,
    maxImages = 10,
    acceptedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp'],
    maxFileSize = 5,
    loading = false
}: ImageUploadManagerProps) {
    const [selectedImages, setSelectedImages] = useState<number[]>([]);
    const [dragActive, setDragActive] = useState(false);
    const [uploading, setUploading] = useState(false);
    const [deleting, setDeleting] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const fileInputRef = useRef<HTMLInputElement>(null);

    const handleFileSelect = (files: FileList | null) => {
        if (!files) return;

        const fileArray = Array.from(files);
        const validFiles: File[] = [];
        const errors: string[] = [];

        // Validate files
        fileArray.forEach((file) => {
            if (!acceptedTypes.includes(file.type)) {
                errors.push(`${file.name}: Định dạng file không được hỗ trợ`);
                return;
            }

            if (file.size > maxFileSize * 1024 * 1024) {
                errors.push(`${file.name}: Kích thước file vượt quá ${maxFileSize}MB`);
                return;
            }

            validFiles.push(file);
        });

        // Check total images limit
        if (images.length + validFiles.length > maxImages) {
            errors.push(`Chỉ có thể tải lên tối đa ${maxImages} ảnh. Hiện tại đã có ${images.length} ảnh.`);
        }

        if (errors.length > 0) {
            setError(errors.join('; '));
            return;
        }

        setError(null);
        handleUpload(validFiles);
    };

    const handleUpload = async (files: File[]) => {
        if (files.length === 0) return;

        try {
            setUploading(true);
            setError(null);
            await onUpload(files);
        } catch (err) {
            console.error('Error uploading images:', err);
            setError('Có lỗi xảy ra khi tải lên ảnh. Vui lòng thử lại.');
        } finally {
            setUploading(false);
        }
    };

    const handleDelete = async () => {
        if (selectedImages.length === 0) return;

        try {
            setDeleting(true);
            setError(null);
            await onDelete(selectedImages);
            setSelectedImages([]);
        } catch (err) {
            console.error('Error deleting images:', err);
            setError('Có lỗi xảy ra khi xóa ảnh. Vui lòng thử lại.');
        } finally {
            setDeleting(false);
        }
    };

    const toggleImageSelection = (imageId: number) => {
        setSelectedImages(prev =>
            prev.includes(imageId)
                ? prev.filter(id => id !== imageId)
                : [...prev, imageId]
        );
    };

    const handleDrag = (e: React.DragEvent) => {
        e.preventDefault();
        e.stopPropagation();
        if (e.type === "dragenter" || e.type === "dragover") {
            setDragActive(true);
        } else if (e.type === "dragleave") {
            setDragActive(false);
        }
    };

    const handleDrop = (e: React.DragEvent) => {
        e.preventDefault();
        e.stopPropagation();
        setDragActive(false);

        if (e.dataTransfer.files && e.dataTransfer.files[0]) {
            handleFileSelect(e.dataTransfer.files);
        }
    };

    const canUpload = images.length < maxImages && !loading && !uploading;
    const canDelete = selectedImages.length > 0 && !loading && !deleting;

    return (
        <Card>
            <CardHeader>
                <CardTitle className="flex items-center justify-between">
                    <span>Hình ảnh phòng</span>
                    <div className="flex gap-2">
                        {canDelete && (
                            <Button
                                variant="outline"
                                size="sm"
                                onClick={handleDelete}
                                disabled={deleting}
                                className="text-red-600 border-red-200 hover:bg-red-50"
                            >
                                <Trash2 className="h-4 w-4 mr-2" />
                                {deleting ? 'Đang xóa...' : `Xóa (${selectedImages.length})`}
                            </Button>
                        )}
                        {canUpload && (
                            <Button
                                variant="outline"
                                size="sm"
                                onClick={() => fileInputRef.current?.click()}
                                disabled={uploading}
                            >
                                <Upload className="h-4 w-4 mr-2" />
                                {uploading ? 'Đang tải...' : 'Tải ảnh lên'}
                            </Button>
                        )}
                    </div>
                </CardTitle>
            </CardHeader>
            <CardContent>
                <div className="space-y-4">
                    {error && (
                        <Alert variant="destructive">
                            <AlertCircle className="h-4 w-4" />
                            <AlertDescription>{error}</AlertDescription>
                        </Alert>
                    )}

                    <p className="text-sm text-gray-500">
                        Thêm hình ảnh phòng để giúp khách hàng có cái nhìn trực quan hơn về chỗ nghỉ của bạn.
                        Bạn nên thêm ít nhất 3 hình ảnh chất lượng cao cho mỗi loại phòng.
                        ({images.length}/{maxImages} ảnh)
                    </p>

                    {/* Image Grid */}
                    <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
                        {images.map((image) => (
                            <div
                                key={image.id}
                                className={`relative h-40 rounded-md overflow-hidden border-2 cursor-pointer transition-all ${selectedImages.includes(image.id)
                                        ? 'border-red-500 ring-2 ring-red-200'
                                        : 'border-gray-200 hover:border-gray-300'
                                    }`}
                                onClick={() => toggleImageSelection(image.id)}
                            >
                                <img
                                    src={image.url}
                                    alt={`Room image ${image.id}`}
                                    className="w-full h-full object-cover"
                                />
                                {selectedImages.includes(image.id) && (
                                    <div className="absolute inset-0 bg-red-500/20 flex items-center justify-center">
                                        <div className="bg-red-500 text-white rounded-full p-1">
                                            <X className="h-4 w-4" />
                                        </div>
                                    </div>
                                )}
                                {/* Image loading overlay */}
                                {loading && (
                                    <div className="absolute inset-0 bg-black/40 flex items-center justify-center">
                                        <div className="w-6 h-6 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                                    </div>
                                )}
                            </div>
                        ))}

                        {/* Upload Area */}
                        {canUpload && (
                            <div
                                className={`h-40 border-2 border-dashed rounded-md flex flex-col items-center justify-center text-gray-500 cursor-pointer transition-all ${dragActive
                                        ? 'border-blue-500 bg-blue-50 text-blue-600'
                                        : 'border-gray-300 hover:border-gray-400 hover:bg-gray-50'
                                    }`}
                                onClick={() => fileInputRef.current?.click()}
                                onDragEnter={handleDrag}
                                onDragLeave={handleDrag}
                                onDragOver={handleDrag}
                                onDrop={handleDrop}
                            >
                                <PlusCircle className="h-8 w-8 mb-2" />
                                <p className="text-sm text-center">
                                    {dragActive ? 'Thả ảnh vào đây' : 'Thêm ảnh mới'}
                                </p>
                                <p className="text-xs text-gray-400 mt-1">
                                    Kéo thả hoặc click để chọn
                                </p>
                            </div>
                        )}
                    </div>

                    {/* Hidden file input */}
                    <input
                        ref={fileInputRef}
                        type="file"
                        multiple
                        accept={acceptedTypes.join(',')}
                        onChange={(e) => handleFileSelect(e.target.files)}
                        className="hidden"
                    />

                    {/* Usage instructions */}
                    <div className="text-xs text-gray-500 space-y-1">
                        <p>• Định dạng hỗ trợ: JPEG, PNG, WebP</p>
                        <p>• Kích thước tối đa: {maxFileSize}MB mỗi ảnh</p>
                        <p>• Số lượng tối đa: {maxImages} ảnh</p>
                    </div>
                </div>
            </CardContent>
        </Card>
    );
}
