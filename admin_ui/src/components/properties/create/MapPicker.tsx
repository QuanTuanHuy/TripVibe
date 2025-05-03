"use client";

import { useState, useEffect, useRef } from 'react';
// Import không chỉ định kiểu để tránh lỗi TypeScript
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { Search, MapPin, X, Loader2, MoveVertical } from 'lucide-react';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';

interface MapPickerProps {
  initialLatitude: number;
  initialLongitude: number;
  onCoordinatesChange: (latitude: number, longitude: number) => void;
}

interface SearchResult {
  displayName: string;
  lat: number;
  lon: number;
}

export default function MapPicker({
  initialLatitude,
  initialLongitude,
  onCoordinatesChange
}: MapPickerProps) {
  const [loaded, setLoaded] = useState(false);
  const [searching, setSearching] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<SearchResult[]>([]);
  const [selectedLocation, setSelectedLocation] = useState<{lat: number, lng: number, name: string | null}>({
    lat: initialLatitude || 21.008854,
    lng: initialLongitude || 105.850782,
    name: null
  });
  const [showHelp, setShowHelp] = useState(true);
  
  const mapRef = useRef<HTMLDivElement>(null);
  // Sử dụng any để tránh lỗi TypeScript
  const leafletMapRef = useRef<any>(null);
  const markerRef = useRef<any>(null);

  useEffect(() => {
    // Khởi tạo bản đồ Leaflet
    const initializeMap = () => {
      if (!mapRef.current) return;

      // Đảm bảo chỉ khởi tạo map một lần
      if (leafletMapRef.current) return;

      // Fix các icon của Leaflet trong Next.js
      const fixLeafletIcon = () => {
        // @ts-ignore
        delete L.Icon.Default.prototype._getIconUrl;
        
        // @ts-ignore
        L.Icon.Default.mergeOptions({
          // Đường dẫn tuyệt đối đến icon thay vì import
          iconUrl: '/leaflet/marker-icon.png',
          iconRetinaUrl: '/leaflet/marker-icon-2x.png',
          shadowUrl: '/leaflet/marker-shadow.png',
          iconSize: [25, 41],
          iconAnchor: [12, 41],
          popupAnchor: [1, -34],
          shadowSize: [41, 41]
        });
      };
      
      // Phải gọi fixLeafletIcon trước khi khởi tạo map
      fixLeafletIcon();

      const position = [
        initialLatitude || 10.762622, // TP.HCM mặc định
        initialLongitude || 106.660172
      ];

      try {
        // Tạo bản đồ Leaflet
        // @ts-ignore
        leafletMapRef.current = L.map(mapRef.current, {
          zoomControl: false, // Tắt zoom control mặc định
        }).setView(position, 15); // Tăng zoom level lên 15 để nhìn rõ hơn

        // Thêm tile layer từ OpenStreetMap (miễn phí)
        // @ts-ignore
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
          attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
          maxZoom: 19,
        }).addTo(leafletMapRef.current);

        // Thêm zoom control vào góc phải dưới
        // @ts-ignore
        L.control.zoom({
          position: 'bottomright'
        }).addTo(leafletMapRef.current);

        // Thêm các controls điều khiển bản đồ
        // @ts-ignore
        L.control.scale({imperial: false, position: 'bottomleft'}).addTo(leafletMapRef.current);

        // Tạo custom marker icon với màu đặc trưng của booking.com
        // @ts-ignore
        const customIcon = L.divIcon({
          html: `
            <div style="background-color: #003580; width: 24px; height: 24px; border-radius: 50%; display: flex; justify-content: center; align-items: center; box-shadow: 0 0 0 2px white, 0 0 10px rgba(0,0,0,0.35);">
              <div style="color: white; font-size: 16px; font-weight: bold;">+</div>
            </div>
          `,
          className: 'custom-div-icon',
          iconSize: [24, 24],
          iconAnchor: [12, 12],
        });

        // Thêm marker vào bản đồ
        // @ts-ignore
        markerRef.current = L.marker(position, { 
          draggable: true,
          icon: customIcon,
          title: 'Vị trí chỗ nghỉ của bạn',
        }).addTo(leafletMapRef.current);

        // Popup cho marker với thông tin "Kéo để di chuyển"
        markerRef.current.bindTooltip('Kéo để di chuyển vị trí', {
          permanent: false, 
          direction: 'top',
          offset: [0, -15]
        });

        // Sự kiện khi kéo marker
        markerRef.current.on('dragend', () => {
          const newPosition = markerRef.current?.getLatLng();
          if (newPosition) {
            onCoordinatesChange(newPosition.lat, newPosition.lng);
            setSelectedLocation({
              lat: newPosition.lat,
              lng: newPosition.lng,
              name: selectedLocation.name
            });
            setShowHelp(false);
            
            // Reverse geocoding để lấy tên địa điểm
            fetchLocationName(newPosition.lat, newPosition.lng);
          }
        });

        // Sự kiện khi click vào bản đồ
        leafletMapRef.current.on('click', (event: any) => {
          const { lat, lng } = event.latlng;
          markerRef.current?.setLatLng([lat, lng]);
          onCoordinatesChange(lat, lng);
          setSelectedLocation({
            lat: lat,
            lng: lng,
            name: null
          });
          setShowHelp(false);
          
          // Hiện tooltip "Kéo để di chuyển" khi click
          markerRef.current.openTooltip();
          
          // Reverse geocoding để lấy tên địa điểm
          fetchLocationName(lat, lng);
        });

        setLoaded(true);

        // Fetch tên vị trí ban đầu nếu có tọa độ
        if (initialLatitude && initialLongitude) {
          fetchLocationName(initialLatitude, initialLongitude);
          setShowHelp(false);
        }
      } catch (error) {
        console.error('Error initializing map:', error);
      }
    };

    // Đảm bảo code chỉ chạy ở client side
    if (typeof window !== 'undefined') {
      // Đảm bảo Leaflet đã load hoàn toàn trước khi khởi tạo map
      setTimeout(initializeMap, 100);
    }

    // Cleanup function
    return () => {
      if (leafletMapRef.current) {
        try {
          leafletMapRef.current.remove();
        } catch (error) {
          console.error('Error removing map:', error);
        }
        leafletMapRef.current = null;
      }
    };
  }, [initialLatitude, initialLongitude, onCoordinatesChange]);

  // Cập nhật marker khi latitude/longitude thay đổi từ bên ngoài
  useEffect(() => {
    if (loaded && markerRef.current && leafletMapRef.current && initialLatitude && initialLongitude) {
      try {
        markerRef.current.setLatLng([initialLatitude, initialLongitude]);
        
        // Đảm bảo map được centered vào vị trí mới
        leafletMapRef.current?.setView([initialLatitude, initialLongitude], 
          leafletMapRef.current?.getZoom() || 15);
          
        setSelectedLocation({
          lat: initialLatitude,
          lng: initialLongitude,
          name: selectedLocation.name
        });
        
        // Tắt thông báo trợ giúp khi có tọa độ
        if (initialLatitude && initialLongitude) {
          setShowHelp(false);
        }
      } catch (error) {
        console.error('Error updating marker position:', error);
      }
    }
  }, [initialLatitude, initialLongitude, loaded]);

  // Xử lý resize khi window thay đổi kích thước
  useEffect(() => {
    const handleResize = () => {
      if (leafletMapRef.current) {
        try {
          leafletMapRef.current.invalidateSize();
        } catch (error) {
          console.error('Error resizing map:', error);
        }
      }
    };

    window.addEventListener('resize', handleResize);
    
    return () => {
      window.removeEventListener('resize', handleResize);
    };
  }, []);

  // Hàm tìm kiếm địa điểm dựa trên từ khóa sử dụng Nominatim OpenStreetMap API
  const searchLocation = async (query: string) => {
    if (!query.trim()) return;
    
    setSearching(true);
    
    try {
      const response = await fetch(
        `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(query)}&limit=5&countrycodes=vn`
      );
      
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      
      const data = await response.json();
      
      const results: SearchResult[] = data.map((item: any) => ({
        displayName: item.display_name,
        lat: parseFloat(item.lat),
        lon: parseFloat(item.lon)
      }));
      
      setSearchResults(results);
    } catch (error) {
      console.error('Error searching location:', error);
      setSearchResults([]);
    } finally {
      setSearching(false);
    }
  };

  // Hàm lấy tên địa điểm từ tọa độ (reverse geocoding)
  const fetchLocationName = async (lat: number, lng: number) => {
    try {
      const response = await fetch(
        `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&zoom=18&addressdetails=1`
      );
      
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      
      const data = await response.json();
      
      setSelectedLocation(prev => ({
        ...prev,
        name: data.display_name
      }));
    } catch (error) {
      console.error('Error fetching location name:', error);
    }
  };

  // Xử lý khi chọn một kết quả tìm kiếm
  const handleSelectResult = (result: SearchResult) => {
    if (leafletMapRef.current && markerRef.current) {
      try {
        // Cập nhật vị trí trên bản đồ
        leafletMapRef.current.setView([result.lat, result.lon], 16);
        markerRef.current.setLatLng([result.lat, result.lon]);
        
        // Hiện tooltip "Kéo để di chuyển" khi chọn kết quả
        markerRef.current.openTooltip();
        
        // Gọi callback để cập nhật tọa độ
        onCoordinatesChange(result.lat, result.lon);
        
        // Cập nhật trạng thái nội bộ
        setSelectedLocation({
          lat: result.lat,
          lng: result.lon,
          name: result.displayName
        });
        
        // Xóa kết quả tìm kiếm sau khi chọn
        setSearchResults([]);
        setSearchQuery('');
        setShowHelp(false);
      } catch (error) {
        console.error('Error selecting search result:', error);
      }
    }
  };

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    searchLocation(searchQuery);
  };
  
  const clearSearch = () => {
    setSearchQuery('');
    setSearchResults([]);
  };

  return (
    <div className="flex flex-col h-full w-full relative">
      {/* Search bar nổi trên bản đồ */}
      <div className="absolute top-4 left-0 right-0 z-10 px-4">
        <Card className="p-3 shadow-md bg-white border-0">
          <form onSubmit={handleSearchSubmit} className="flex gap-2">
            <div className="relative flex-grow">
              <div className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400">
                <Search size={18} />
              </div>
              <Input
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Tìm kiếm địa điểm, tên đường, địa danh..."
                className="pl-10 pr-10 py-2 h-10 border-gray-300 focus:border-blue-600"
              />
              {searchQuery && (
                <div 
                  className="absolute right-3 top-1/2 transform -translate-y-1/2 cursor-pointer text-gray-400 hover:text-gray-600"
                  onClick={clearSearch}
                >
                  <X size={18} />
                </div>
              )}
              {searching && (
                <div className="absolute right-3 top-1/2 transform -translate-y-1/2">
                  <Loader2 size={18} className="animate-spin text-blue-600" />
                </div>
              )}
            </div>
            <Button 
              type="submit" 
              className="bg-blue-600 hover:bg-blue-700 text-white font-medium"
            >
              Tìm
            </Button>
          </form>
          
          {/* Kết quả tìm kiếm */}
          {searchResults.length > 0 && (
            <div className="mt-2 bg-white rounded-md shadow-inner max-h-60 overflow-y-auto">
              <ul className="divide-y divide-gray-100">
                {searchResults.map((result, index) => (
                  <li 
                    key={index}
                    className="p-3 hover:bg-blue-50 cursor-pointer flex items-start transition-colors"
                    onClick={() => handleSelectResult(result)}
                  >
                    <MapPin size={16} className="mr-2 mt-1 flex-shrink-0 text-blue-600" />
                    <span className="text-sm">{result.displayName}</span>
                  </li>
                ))}
              </ul>
            </div>
          )}
          
          {/* Hiển thị vị trí đã chọn ngay dưới search box */}
          {selectedLocation.name && !searchResults.length && (
            <div className="mt-2 px-2 py-1 bg-blue-50 rounded-md text-sm border border-blue-100">
              <div className="flex items-start gap-2">
                <Badge variant="outline" className="bg-blue-100 text-blue-800 border-blue-200">
                  Vị trí đã chọn
                </Badge>
                <span className="text-blue-800 text-xs line-clamp-1">
                  {selectedLocation.name}
                </span>
              </div>
            </div>
          )}
        </Card>
      </div>
      
      {/* Hiển thị bản đồ */}
      <div className="flex-grow relative">
        {!loaded && (
          <div className="absolute inset-0 flex items-center justify-center z-10 bg-gray-100 bg-opacity-75">
            <div className="text-center">
              <div className="w-10 h-10 border-4 border-blue-500 border-t-transparent rounded-full animate-spin mx-auto"></div>
              <p className="mt-2">Đang tải bản đồ...</p>
            </div>
          </div>
        )}
        <div
          ref={mapRef}
          className="h-full w-full z-0 rounded-md overflow-hidden"
          style={{ opacity: loaded ? 1 : 0.3 }}
        ></div>
        
        {/* Hướng dẫn chọn vị trí */}
        {showHelp && loaded && (
          <div className="absolute bottom-10 left-1/2 transform -translate-x-1/2 z-10 bg-white p-3 rounded-lg shadow-lg max-w-xs text-center">
            <div className="flex flex-col items-center gap-2">
              <MoveVertical size={20} className="text-blue-600" />
              <p className="text-sm font-medium">Click vào bản đồ để chọn vị trí chỗ nghỉ của bạn</p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}