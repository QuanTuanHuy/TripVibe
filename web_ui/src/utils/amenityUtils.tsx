// Amenity utility functions for icon mapping and categorization
import React from 'react';
import { AccommodationAmenity } from '@/types/accommodation';
import { UnitAmenity } from '@/types/accommodation/accommodation/unit.types';
import {
    Wifi, Car, Utensils, GlassWater, Users, Clock, Sunrise, Check,
    Tv, AirVent, Shield, Building2, Coffee, Bath, Dumbbell,
    Briefcase, Accessibility, MapPin, Star, CreditCard,
    Zap, Waves, TreePine, Baby, Bike, Scissors, Printer, Plane,
    ShoppingCart, Banknote, Home, Sun, ChefHat
} from 'lucide-react';

// Amenity icon mapping based on backend data structure
export const getAmenityIcon = (amenityName: string, amenityIcon?: string): React.ReactNode => {
    // If backend provides emoji icon, convert it to appropriate Lucide icon
    if (amenityIcon) {
        switch (amenityIcon) {
            case '📶': return <Wifi className="text-green-600" size={20} />;
            case '❄️': return <AirVent className="text-green-600" size={20} />;
            case '🚗': return <Car className="text-green-600" size={20} />;
            case '🛎️': return <Users className="text-green-600" size={20} />;
            case '🥐': return <Sunrise className="text-green-600" size={20} />;
            case '🏊': return <Waves className="text-green-600" size={20} />;
            case '🛗': return <Building2 className="text-green-600" size={20} />;
            case '🏨': return <Clock className="text-green-600" size={20} />;
            case '🔒': return <Shield className="text-green-600" size={20} />;
            case '🌳': return <TreePine className="text-green-600" size={20} />;
            case '🏢': return <Building2 className="text-green-600" size={20} />;
            case '🍸': return <GlassWater className="text-green-600" size={20} />;
            case '🍽️': return <Utensils className="text-green-600" size={20} />;
            case '🏪': return <ShoppingCart className="text-green-600" size={20} />;
            case '🏧': return <Banknote className="text-green-600" size={20} />;
            case '🎠': return <Baby className="text-green-600" size={20} />;
            case '📺': return <Tv className="text-green-600" size={20} />;
            case '🧊': return <AirVent className="text-green-600" size={20} />;
            case '🔐': return <Shield className="text-green-600" size={20} />;
            case '💻': return <Briefcase className="text-green-600" size={20} />;
            case '🛋️': return <Home className="text-green-600" size={20} />;
            case '🌅': return <Sun className="text-green-600" size={20} />;
            case '☕': return <Coffee className="text-green-600" size={20} />;
            case '🫖': return <Coffee className="text-green-600" size={20} />;
            case '💨': return <AirVent className="text-green-600" size={20} />;
            case '🥿': return <Home className="text-green-600" size={20} />;
            case '🥼': return <Home className="text-green-600" size={20} />;
            case '🛁': return <Bath className="text-green-600" size={20} />;
            case '🚿': return <Bath className="text-green-600" size={20} />;
            case '🧴': return <Bath className="text-green-600" size={20} />;
            case '🏖️': return <Bath className="text-green-600" size={20} />;
            case '🪞': return <Home className="text-green-600" size={20} />;
            case '🚽': return <Bath className="text-green-600" size={20} />;
            case '🧻': return <Bath className="text-green-600" size={20} />;
            case '🍳': return <ChefHat className="text-green-600" size={20} />;
            case '📱': return <Zap className="text-green-600" size={20} />;
            case '🍚': return <ChefHat className="text-green-600" size={20} />;
            case '🔪': return <ChefHat className="text-green-600" size={20} />;
            case '🪑': return <Home className="text-green-600" size={20} />;
            case '🔊': return <Tv className="text-green-600" size={20} />;
            case '🧶': return <Home className="text-green-600" size={20} />;
            case '💡': return <Zap className="text-green-600" size={20} />;
            case '👔': return <Scissors className="text-green-600" size={20} />;
            case '✈️': return <Plane className="text-green-600" size={20} />;
            case '🚲': return <Bike className="text-green-600" size={20} />;
            case '💆': return <Users className="text-green-600" size={20} />;
            case '🗺️': return <MapPin className="text-green-600" size={20} />;
            case '🎫': return <CreditCard className="text-green-600" size={20} />;
            case '📠': return <Printer className="text-green-600" size={20} />;
            case '🔌': return <Zap className="text-green-600" size={20} />;
            case '🌞': return <Sun className="text-green-600" size={20} />;
            case '🧖': return <Users className="text-green-600" size={20} />;
            case '💆‍♀️': return <Users className="text-green-600" size={20} />;
            case '👶': return <Baby className="text-green-600" size={20} />;
            case '💪': return <Dumbbell className="text-green-600" size={20} />;
            case '🎾': return <Dumbbell className="text-green-600" size={20} />;
            case '⚽': return <Dumbbell className="text-green-600" size={20} />;
            case '🧘': return <Users className="text-green-600" size={20} />;
            case '🏸': return <Dumbbell className="text-green-600" size={20} />;
            case '💼': return <Briefcase className="text-green-600" size={20} />;
            case '🖨️': return <Printer className="text-green-600" size={20} />;
            case '👥': return <Users className="text-green-600" size={20} />;
            case '♿': return <Accessibility className="text-green-600" size={20} />;
            case '🦽': return <Accessibility className="text-green-600" size={20} />;
            case '👁️': return <Accessibility className="text-green-600" size={20} />;
            case '👂': return <Accessibility className="text-green-600" size={20} />;
            default: break;
        }
    }

    // Fallback to name-based icon mapping
    const name = amenityName.toLowerCase();

    // WiFi and Internet
    if (name.includes('wifi') || name.includes('internet') || name.includes('mạng')) {
        return <Wifi className="text-green-600" size={20} />;
    }

    // Transportation and Parking
    if (name.includes('xe') || name.includes('đưa đón') || name.includes('shuttle') ||
        name.includes('car') || name.includes('parking') || name.includes('đậu')) {
        return <Car className="text-green-600" size={20} />;
    }

    // Airport shuttle
    if (name.includes('sân bay') || name.includes('airport')) {
        return <Plane className="text-green-600" size={20} />;
    }

    // Food and Dining
    if (name.includes('nhà hàng') || name.includes('restaurant') || name.includes('ăn') ||
        name.includes('bữa sáng') || name.includes('breakfast') || name.includes('kitchen') ||
        name.includes('bếp') || name.includes('nấu ăn')) {
        return <Utensils className="text-green-600" size={20} />;
    }

    // Bar and Drinks
    if (name.includes('bar') || name.includes('quầy') || name.includes('đồ uống') ||
        name.includes('rượu') || name.includes('wine')) {
        return <GlassWater className="text-green-600" size={20} />;
    }

    // Coffee
    if (name.includes('cà phê') || name.includes('coffee')) {
        return <Coffee className="text-green-600" size={20} />;
    }

    // Pool and Water
    if (name.includes('hồ bơi') || name.includes('pool') || name.includes('bơi')) {
        return <Waves className="text-green-600" size={20} />;
    }

    // Bathroom and Bath
    if (name.includes('phòng tắm') || name.includes('bathroom') || name.includes('tắm') ||
        name.includes('bath') || name.includes('toilet') || name.includes('vòi sen') ||
        name.includes('shower') || name.includes('jacuzzi')) {
        return <Bath className="text-green-600" size={20} />;
    }

    // Spa and Wellness
    if (name.includes('spa') || name.includes('massage') || name.includes('sauna') ||
        name.includes('wellness') || name.includes('chăm sóc')) {
        return <Users className="text-green-600" size={20} />;
    }

    // Gym and Fitness
    if (name.includes('gym') || name.includes('fitness') || name.includes('tập') ||
        name.includes('thể thao') || name.includes('tennis') || name.includes('bóng') ||
        name.includes('cầu lông') || name.includes('yoga')) {
        return <Dumbbell className="text-green-600" size={20} />;
    }

    // Air Conditioning
    if (name.includes('điều hòa') || name.includes('air') || name.includes('ac') ||
        name.includes('conditioning') || name.includes('nhiệt độ')) {
        return <AirVent className="text-green-600" size={20} />;
    }

    // TV and Entertainment
    if (name.includes('tv') || name.includes('television') || name.includes('tivi') ||
        name.includes('smart') || name.includes('netflix') || name.includes('youtube')) {
        return <Tv className="text-green-600" size={20} />;
    }

    // Reception and Service
    if (name.includes('24') || name.includes('lễ tân') || name.includes('reception') ||
        name.includes('service') || name.includes('dịch vụ')) {
        return <Clock className="text-green-600" size={20} />;
    }

    // Security
    if (name.includes('bảo vệ') || name.includes('security') || name.includes('an ninh') ||
        name.includes('két') || name.includes('safe') || name.includes('khóa')) {
        return <Shield className="text-green-600" size={20} />;
    }

    // Business and Work
    if (name.includes('phòng họp') || name.includes('meeting') || name.includes('business') ||
        name.includes('kinh doanh') || name.includes('coworking') || name.includes('làm việc') ||
        name.includes('bàn') || name.includes('desk') || name.includes('in') || name.includes('print')) {
        return <Briefcase className="text-green-600" size={20} />;
    }

    // Accessibility
    if (name.includes('khuyết tật') || name.includes('wheelchair') || name.includes('xe lăn') ||
        name.includes('accessibility') || name.includes('disabled') || name.includes('thị giác') ||
        name.includes('thính giác')) {
        return <Accessibility className="text-green-600" size={20} />;
    }

    // Elevator
    if (name.includes('thang máy') || name.includes('elevator') || name.includes('lift')) {
        return <Building2 className="text-green-600" size={20} />;
    }

    // Garden and Outdoor
    if (name.includes('vườn') || name.includes('garden') || name.includes('sân thượng') ||
        name.includes('terrace') || name.includes('ban công') || name.includes('balcony') ||
        name.includes('ngoài trời') || name.includes('outdoor')) {
        return <TreePine className="text-green-600" size={20} />;
    }

    // Kids and Family
    if (name.includes('trẻ em') || name.includes('kids') || name.includes('children') ||
        name.includes('family') || name.includes('gia đình') || name.includes('baby')) {
        return <Baby className="text-green-600" size={20} />;
    }

    // Laundry
    if (name.includes('giặt') || name.includes('laundry') || name.includes('ủi') ||
        name.includes('wash')) {
        return <Scissors className="text-green-600" size={20} />;
    }

    // Electric and Power
    if (name.includes('điện') || name.includes('electric') || name.includes('sạc') ||
        name.includes('charge') || name.includes('power')) {
        return <Zap className="text-green-600" size={20} />;
    }

    // Shopping
    if (name.includes('cửa hàng') || name.includes('shop') || name.includes('store') ||
        name.includes('atm') || name.includes('mua sắm')) {
        return <ShoppingCart className="text-green-600" size={20} />;
    }

    // Default icon
    return <Check className="text-green-600" size={20} />;
};

// Enhanced amenity categorization based on backend groups
export const categorizeAmenitiesByGroup = (accommodationAmenities: any[]): any[] => {
    const groupedAmenities: { [key: string]: any } = {};

    accommodationAmenities.forEach(amenity => {
        if (!amenity.amenity?.name) return;

        const groupName = getAmenityGroupName(amenity.amenity);
        const groupIcon = amenity.amenity.icon;

        if (!groupedAmenities[groupName]) {
            groupedAmenities[groupName] = {
                title: groupName,
                icon: getGroupIcon(groupName, groupIcon),
                items: []
            };
        }

        groupedAmenities[groupName].items.push({
            name: amenity.amenity.name,
            available: true,
            hasCharge: amenity.fee ? amenity.fee > 0 : false,
            description: amenity.amenity.description,
            availableTime: amenity.amenity.availableTime,
            icon: getAmenityIcon(amenity.amenity.name, amenity.amenity.icon)
        });
    });

    return Object.values(groupedAmenities);
};

// Get group icon based on group name and emoji
export const getGroupIcon = (groupName: string, groupEmoji?: string): React.ReactNode => {
    if (groupEmoji) {
        switch (groupEmoji) {
            case '🌟': return <Star size={20} className="text-gray-600" />;
            case '🏢': return <Building2 size={20} className="text-gray-600" />;
            case '🏠': return <Home size={20} className="text-gray-600" />;
            case '🚿': return <Bath size={20} className="text-gray-600" />;
            case '🍳': return <ChefHat size={20} className="text-gray-600" />;
            case '🛋️': return <Home size={20} className="text-gray-600" />;
            case '🛎️': return <Users size={20} className="text-gray-600" />;
            case '🚗': return <Car size={20} className="text-gray-600" />;
            case '🏊': return <Waves size={20} className="text-gray-600" />;
            case '💪': return <Dumbbell size={20} className="text-gray-600" />;
            case '💼': return <Briefcase size={20} className="text-gray-600" />;
            case '♿': return <Accessibility size={20} className="text-gray-600" />;
            default: break;
        }
    }

    // Fallback based on group name
    const name = groupName.toLowerCase();
    if (name.includes('phổ biến') || name.includes('popular')) return <Star size={20} className="text-gray-600" />;
    if (name.includes('khu vực') || name.includes('property')) return <Building2 size={20} className="text-gray-600" />;
    if (name.includes('phòng') && !name.includes('tắm')) return <Home size={20} className="text-gray-600" />;
    if (name.includes('tắm') || name.includes('bathroom')) return <Bath size={20} className="text-gray-600" />;
    if (name.includes('bếp') || name.includes('kitchen')) return <ChefHat size={20} className="text-gray-600" />;
    if (name.includes('khách') || name.includes('living')) return <Home size={20} className="text-gray-600" />;
    if (name.includes('dịch vụ') || name.includes('service')) return <Users size={20} className="text-gray-600" />;
    if (name.includes('xe') || name.includes('parking')) return <Car size={20} className="text-gray-600" />;
    if (name.includes('hồ bơi') || name.includes('pool') || name.includes('sức khỏe')) return <Waves size={20} className="text-gray-600" />;
    if (name.includes('gym') || name.includes('thể thao')) return <Dumbbell size={20} className="text-gray-600" />;
    if (name.includes('kinh doanh') || name.includes('business')) return <Briefcase size={20} className="text-gray-600" />;
    if (name.includes('khuyết tật') || name.includes('accessibility')) return <Accessibility size={20} className="text-gray-600" />;

    return <Check size={20} className="text-gray-600" />;
};

// Get popular amenities 
export const getPopularAmenities = (accommodationAmenities: any[]): any[] => {
    return accommodationAmenities
        .filter(amenity => amenity.amenity?.isHighlighted && amenity.amenity?.name)
        .slice(0, 10)
        .map(amenity => ({
            name: amenity.amenity.name,
            icon: getAmenityIcon(amenity.amenity.name, amenity.amenity.icon),
            hasCharge: amenity.fee ? amenity.fee > 0 : false,
            availableTime: amenity.amenity.availableTime
        }));
};

// Check if hotel has airport shuttle
export const hasAirportShuttle = (accommodationAmenities: any[]): boolean => {
    return accommodationAmenities.some(amenity => {
        const name = amenity.amenity?.name?.toLowerCase() || '';
        return name.includes('sân bay') ||
            name.includes('airport') ||
            name.includes('đưa đón sân bay') ||
            name.includes('airport shuttle');
    });
};

// Format amenity availability time
export const formatAvailableTime = (availableTime?: string): string => {
    if (!availableTime || availableTime === '24/7') return '24/7';
    return availableTime;
};

// Check if amenity is paid
export const isAmenityPaid = (amenity: any): boolean => {
    return amenity.fee ? amenity.fee > 0 : false;
};

// Deduplicate unit amenities across multiple units
export const deduplicateUnitAmenities = (units: any[]): UnitAmenity[] => {
    const uniqueAmenities = new Map<number, UnitAmenity>();

    units.forEach(unit => {
        if (unit.amenities && Array.isArray(unit.amenities)) {
            unit.amenities.forEach((unitAmenity: UnitAmenity) => {
                if (unitAmenity.amenity?.id && !uniqueAmenities.has(unitAmenity.amenity.id)) {
                    uniqueAmenities.set(unitAmenity.amenity.id, unitAmenity);
                }
            });
        }
    });

    return Array.from(uniqueAmenities.values());
};

// Extract unit amenities from all units and deduplicate them
export const extractUnitAmenities = (units: any[]): UnitAmenity[] => {
    if (!units || !Array.isArray(units)) return [];
    return deduplicateUnitAmenities(units);
};

// Categorize unit amenities by group (similar to accommodation amenities)
export const categorizeUnitAmenitiesByGroup = (unitAmenities: UnitAmenity[]): any[] => {
    const groupedAmenities: { [key: string]: any } = {};

    unitAmenities.forEach(unitAmenity => {
        if (!unitAmenity.amenity?.name) return;

        // Use groupId to determine category, fallback to default grouping
        const groupName = getAmenityGroupName(unitAmenity.amenity);
        const groupIcon = unitAmenity.amenity.icon;

        if (!groupedAmenities[groupName]) {
            groupedAmenities[groupName] = {
                title: groupName,
                icon: getGroupIcon(groupName, groupIcon),
                items: []
            };
        }

        groupedAmenities[groupName].items.push({
            name: unitAmenity.amenity.name,
            available: true,
            hasCharge: unitAmenity.fee ? unitAmenity.fee > 0 : false,
            description: unitAmenity.amenity.description,
            availableTime: unitAmenity.amenity.availableTime,
            icon: getAmenityIcon(unitAmenity.amenity.name, unitAmenity.amenity.icon)
        });
    });

    return Object.values(groupedAmenities);
};

// Combine accommodation and unit amenities into the original 2-section structure
export const combineAmenitiesData = (
    accommodationAmenities: AccommodationAmenity[] = [],
    unitAmenities: UnitAmenity[] = []
) => {
    // Popular amenities section - get from accommodation amenities (property-level)
    const popularAmenities = getPopularAmenities(accommodationAmenities);

    // Amenity categories section - get from unit amenities (room-level) 
    const amenityCategories = categorizeUnitAmenitiesByGroup(unitAmenities);

    return {
        popularAmenities,
        amenityCategories
    };
};

// Get popular amenities specifically for accommodation level 
export const getAccommodationPopularAmenities = (accommodationAmenities: AccommodationAmenity[]): any[] => {
    return accommodationAmenities
        .filter(amenity => amenity.amenity?.isHighlighted && amenity.amenity?.name)
        .slice(0, 10)
        .map(amenity => ({
            name: amenity.amenity!.name,
            icon: getAmenityIcon(amenity.amenity!.name, amenity.amenity!.icon),
            hasCharge: amenity.fee ? amenity.fee > 0 : false,
            availableTime: amenity.amenity!.availableTime
        }));
};

// Check if accommodation has specific amenity type
export const hasAccommodationAmenity = (accommodationAmenities: AccommodationAmenity[], amenityName: string): boolean => {
    return accommodationAmenities.some(amenity => {
        const name = amenity.amenity?.name?.toLowerCase() || '';
        return name.includes(amenityName.toLowerCase());
    });
};

// Check if units have specific amenity type  
export const hasUnitAmenity = (unitAmenities: UnitAmenity[], amenityName: string): boolean => {
    return unitAmenities.some(amenity => {
        const name = amenity.amenity?.name?.toLowerCase() || '';
        return name.includes(amenityName.toLowerCase());
    });
};

// Helper function to determine amenity group name based on groupId or amenity name
export const getAmenityGroupName = (amenity: any): string => {
    // Map groupId to group names based on backend structure
    if (amenity.groupId) {
        switch (amenity.groupId) {
            case 1: return 'Tiện nghi phổ biến';
            case 2: return 'Khu vực chung';
            case 3: return 'Phòng/Unit';
            case 4: return 'Phòng tắm';
            case 5: return 'Nhà bếp';
            case 6: return 'Phòng khách';
            case 7: return 'Dịch vụ';
            case 8: return 'Đỗ xe';
            case 9: return 'Hồ bơi & Sức khỏe';
            case 10: return 'Gym & Thể thao';
            case 11: return 'Kinh doanh';
            case 12: return 'Tiện nghi cho khuyết tật';
            default: break;
        }
    }

    // Fallback to name-based categorization
    const name = amenity.name?.toLowerCase() || '';

    if (name.includes('wifi') || name.includes('internet') || name.includes('điều hòa') || name.includes('tv')) {
        return 'Tiện nghi phổ biến';
    }
    if (name.includes('tắm') || name.includes('shower') || name.includes('toilet') || name.includes('bathroom')) {
        return 'Phòng tắm';
    }
    if (name.includes('bếp') || name.includes('kitchen') || name.includes('nấu') || name.includes('microwave')) {
        return 'Nhà bếp';
    }
    if (name.includes('xe') || name.includes('parking') || name.includes('đậu')) {
        return 'Đỗ xe';
    }
    if (name.includes('gym') || name.includes('fitness') || name.includes('thể thao')) {
        return 'Gym & Thể thao';
    }
    if (name.includes('hồ bơi') || name.includes('pool') || name.includes('spa') || name.includes('massage')) {
        return 'Hồ bơi & Sức khỏe';
    }
    if (name.includes('business') || name.includes('meeting') || name.includes('kinh doanh')) {
        return 'Kinh doanh';
    }
    if (name.includes('khuyết tật') || name.includes('wheelchair') || name.includes('accessibility')) {
        return 'Tiện nghi cho khuyết tật';
    }
    if (name.includes('dịch vụ') || name.includes('service') || name.includes('lễ tân') || name.includes('reception')) {
        return 'Dịch vụ';
    }

    return 'Tiện nghi khác';
};
