"use client";

import { useState } from 'react';
import Image from 'next/image';
import Link from 'next/link';
import { Heart, MapPin, Check } from 'lucide-react';

interface Amenity {
  id: string;
  name: string;
}

interface SearchResultItemProps {
  id: string;
  name: string;
  imageUrl: string;
  rating?: number;
  reviewCount?: number;
  location: string;
  distance?: string;
  description?: string;
  price: number;
  discountPrice?: number;
  amenities?: Amenity[];
  type?: string;
  beds?: number;
  rooms?: number;
  isFavorite?: boolean;
  hasPromotion?: boolean;
  geniusLevel?: number;
}

const SearchResultItem: React.FC<SearchResultItemProps> = ({
  id,
  name,
  imageUrl,
  rating,
  reviewCount,
  location,
  distance,
  description,
  price,
  discountPrice,
  amenities,
  type,
  beds,
  rooms,
  isFavorite = false,
  hasPromotion = false,
  geniusLevel
}) => {
  const [favorite, setFavorite] = useState(isFavorite);
  
  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('vi-VN', { 
      style: 'currency', 
      currency: 'VND' 
    }).format(price);
  };
  
  const handleFavoriteClick = (e: React.MouseEvent) => {
    e.preventDefault();
    setFavorite(!favorite);
  };
  
  const renderRating = () => {
    if (!rating) return null;
    
    // Màu sắc dựa trên điểm đánh giá - giống với Booking.com
    let bgColor = 'bg-red-600';
    let textColor = 'text-white';
    let ratingText = 'Kém';
    
    if (rating < 6) {
      bgColor = 'bg-red-600';
      ratingText = 'Kém';
    } else if (rating >= 6 && rating < 7) {
      bgColor = 'bg-orange-500'; 
      ratingText = 'Tạm được';
    } else if (rating >= 7 && rating < 8) {
      bgColor = 'bg-[#febb02]';
      textColor = 'text-[#262626]';
      ratingText = 'Tốt';
    } else if (rating >= 8 && rating < 9) {
      bgColor = 'bg-[#00800a]';
      ratingText = 'Rất tốt';
    } else if (rating >= 9) {
      bgColor = 'bg-[#005f00]';
      ratingText = 'Tuyệt hảo';
    }

    return (
      <div className="flex items-center gap-2">
        <div className={`${bgColor} ${textColor} py-1 px-2 rounded font-medium`}>
          {rating.toFixed(1)}
        </div>
        <span className="font-medium">{ratingText}</span>
        {reviewCount && (
          <span className="text-gray-500">({reviewCount} đánh giá)</span>
        )}
      </div>
    );
  };
  
  return (
    <div className="bg-white rounded-md shadow-md hover:shadow-lg transition-shadow duration-300 overflow-hidden border border-gray-200">
      <Link href={`/hotel/${id}`}>
        <div className="flex flex-col md:flex-row">
          {/* Phần ảnh */}
          <div className="relative md:w-1/3 h-48 md:h-64">
            <Image
              src={imageUrl}
              alt={name}
              layout="fill"
              objectFit="cover"
            />
            <button
              onClick={handleFavoriteClick}
              className="absolute top-3 right-3 p-2 rounded-full bg-white/70 hover:bg-white"
            >
              <Heart className={`h-5 w-5 ${favorite ? 'fill-red-500 text-red-500' : 'text-gray-600'}`} />
            </button>
            
            {/* Genius Level tag */}
            {geniusLevel && (
              <div className="absolute top-3 left-3 bg-blue-800 text-white text-xs px-2 py-1 rounded font-medium">
                Genius Level {geniusLevel}
              </div>
            )}
          </div>
          
          {/* Phần nội dung */}
          <div className="flex-1 p-5">
            <div className="flex flex-col md:flex-row md:justify-between">
              <div className="flex-1">
                <h2 className="font-bold text-xl text-blue-800 hover:text-blue-600">{name}</h2>
                <div className="flex items-center text-sm text-gray-600 mt-1 mb-3">
                  <MapPin size={14} className="inline mr-1" />
                  <span className="underline">{location}</span>
                  {distance && <span className="ml-2 text-gray-500">· Cách trung tâm {distance}</span>}
                </div>
                
                {/* Loại phòng */}
                {type && (
                  <div className="font-semibold mb-3 text-gray-800">
                    {type} {rooms && `· ${rooms} phòng`} {beds && `· ${beds} giường`}
                  </div>
                )}
                
                {/* Mô tả ngắn */}
                <p className="text-sm text-gray-600 mb-4">{description}</p>
                
                {/* Các tiện ích */}
                {amenities && amenities.length > 0 && (
                  <div className="flex flex-wrap gap-3 mb-4">
                    {amenities.map((amenity) => (
                      <div key={amenity.id} className="flex items-center text-xs bg-green-50 text-green-700 px-2 py-1 rounded">
                        <Check size={12} className="mr-1" />
                        {amenity.name}
                      </div>
                    ))}
                  </div>
                )}
                
                {/* Promotions */}
                {hasPromotion && (
                  <div className="mt-2 text-sm text-red-600 font-medium flex items-center">
                    <Check size={14} className="mr-1" />
                    Miễn phí hủy phòng
                  </div>
                )}
              </div>
              
              {/* Phần đánh giá và giá */}
              <div className="md:text-right mt-4 md:mt-0 flex flex-col justify-between h-full">
                <div>
                  {renderRating()}
                </div>
                
                <div className="mt-8">
                  {discountPrice && (
                    <div className="text-gray-500 line-through text-sm">{formatPrice(discountPrice)}</div>
                  )}
                  <div className="font-bold text-2xl text-gray-900">{formatPrice(price)}</div>
                  <div className="text-gray-500 text-xs">Đã bao gồm thuế và phí</div>
                  
                  <button className="mt-3 bg-blue-600 hover:bg-blue-700 text-white py-3 px-5 rounded font-semibold w-full md:w-auto">
                    Xem chỗ trống
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </Link>
    </div>
  );
};

export default SearchResultItem;