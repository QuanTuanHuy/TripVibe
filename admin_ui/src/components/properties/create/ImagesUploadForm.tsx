"use client";

import { useState } from 'react';
import { Label } from '@/components/ui/label';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { X, Upload, Image as ImageIcon } from 'lucide-react';
import Image from 'next/image';

interface ImagesUploadFormProps {
  images: File[];
  setImages: (images: File[]) => void;
}

export default function ImagesUploadForm({ images, setImages }: ImagesUploadFormProps) {
  const [dragOver, setDragOver] = useState(false);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      const newFiles = Array.from(e.target.files);
      setImages([...images, ...newFiles]);
    }
  };

  const handleDrop = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setDragOver(false);

    if (e.dataTransfer.files) {
      const newFiles = Array.from(e.dataTransfer.files);
      setImages([...images, ...newFiles]);
    }
  };

  const handleDragOver = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setDragOver(true);
  };

  const handleDragLeave = () => {
    setDragOver(false);
  };

  const removeImage = (index: number) => {
    const newImages = [...images];
    newImages.splice(index, 1);
    setImages(newImages);
  };

  return (
    <div className="space-y-6">
      <h2 className="text-xl font-semibold">Hình ảnh chỗ nghỉ</h2>
      <p className="text-gray-600 mb-4">
        Tải lên hình ảnh chất lượng cao để giới thiệu chỗ nghỉ của bạn đến khách hàng.
        Hình ảnh đẹp sẽ giúp tăng tỷ lệ đặt phòng.
      </p>

      <div
        className={`border-2 border-dashed rounded-lg p-6 text-center ${dragOver ? 'border-blue-500 bg-blue-50' : 'border-gray-300'
          }`}
        onDrop={handleDrop}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
      >
        <div className="flex flex-col items-center justify-center space-y-4">
          <div className="p-3 bg-gray-100 rounded-full">
            <Upload className="h-8 w-8 text-gray-500" />
          </div>
          <div>
            <Label htmlFor="images" className="cursor-pointer">
              <span className="font-medium text-blue-600 hover:underline">
                Chọn hình ảnh
              </span>
              <span className="text-gray-500"> hoặc kéo thả vào đây</span>
            </Label>
            <input
              id="images"
              type="file"
              accept="image/*"
              multiple
              className="hidden"
              onChange={handleFileChange}
            />
          </div>
          <p className="text-xs text-gray-500">
            PNG, JPG, WEBP hoặc GIF (tối đa 5MB mỗi ảnh)
          </p>
        </div>
      </div>

      {images.length > 0 && (
        <div className="mt-6">
          <h3 className="text-lg font-medium mb-4">Hình ảnh đã tải lên ({images.length})</h3>
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4">
            {images.map((image, index) => (
              <Card key={index} className="relative group">
                <div className="aspect-[4/3] relative">
                  <div className="absolute inset-0 flex items-center justify-center">
                    {/* Hiển thị hình ảnh preview */}
                    <div className="relative w-full h-full">
                      <Image
                        src={URL.createObjectURL(image)}
                        alt={`Preview ${index}`}
                        fill
                        className="object-cover rounded-t-lg"
                      />
                    </div>
                  </div>
                </div>
                <div className="p-2 text-xs truncate">{image.name}</div>
                <Button
                  type="button"
                  variant="destructive"
                  size="icon"
                  className="absolute top-1 right-1 h-6 w-6 opacity-0 group-hover:opacity-100 transition-opacity"
                  onClick={() => removeImage(index)}
                >
                  <X className="h-4 w-4" />
                </Button>
              </Card>
            ))}
          </div>
        </div>
      )}

      <div className="mt-4">
        <p className="text-sm text-gray-500">
          Hình ảnh đầu tiên sẽ được sử dụng làm ảnh đại diện cho chỗ nghỉ của bạn.
          Bạn có thể tải lên tối đa 20 hình ảnh.
        </p>
      </div>
    </div>
  );
}