"use client";

import { useCallback, useEffect, useRef } from 'react';
import { MapPin } from 'lucide-react';
import { Location } from '@/types/location';

interface HotelMapProps {
    location?: Location;
    hotelName: string;
    className?: string;
    onClick?: () => void; // Add onClick handler
    isModalOpen?: boolean; // Add prop to know when modal is open
}

declare global {
    interface Window {
        L: any;
    }
}

const HotelMap: React.FC<HotelMapProps> = ({ location, hotelName, className = "", onClick, isModalOpen = false }) => {
    const mapRef = useRef<HTMLDivElement>(null);
    const mapInstanceRef = useRef<any>(null);

    const initializeMap = useCallback(() => {
        if (!mapRef.current || !location || mapInstanceRef.current) return;

        try {
            // Initialize map
            const map = window.L.map(mapRef.current, {
                center: [location.latitude, location.longitude],
                zoom: 15,
                zoomControl: true,
                scrollWheelZoom: false,
                doubleClickZoom: true,
                dragging: true,
                touchZoom: true,
                boxZoom: false,
            });

            // Add tile layer (OpenStreetMap)
            window.L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '© OpenStreetMap contributors',
                maxZoom: 19,
            }).addTo(map);

            // Custom hotel icon
            const hotelIcon = window.L.divIcon({
                html: `
                <div class="hotel-marker">
                    <div class="marker-pin"></div>
                    <div class="marker-content">
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
                            <circle cx="12" cy="10" r="3"/>
                        </svg>
                    </div>
                </div>
            `,
                className: 'custom-div-icon',
                iconSize: [40, 50],
                iconAnchor: [20, 50],
                popupAnchor: [0, -50],
            });

            // Add marker
            const marker = window.L.marker([location.latitude, location.longitude], {
                icon: hotelIcon
            }).addTo(map);

            // Add popup
            marker.bindPopup(`
            <div class="hotel-popup">
                <h3 class="font-bold text-lg mb-2">${hotelName}</h3>
                <p class="text-sm text-gray-600 mb-3">${location.detailAddress}</p>
                <div class="flex items-center gap-2 text-blue-600 text-sm">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
                        <circle cx="12" cy="10" r="3"/>
                    </svg>
                    <span>Xem chi tiết vị trí</span>
                </div>
            </div>
        `);

            mapInstanceRef.current = map;

            // Add custom styles
            const style = document.createElement('style');
            style.textContent = `
            .hotel-marker {
                position: relative;
                display: flex;
                align-items: center;
                justify-content: center;
            }
            
            .marker-pin {
                position: absolute;
                width: 40px;
                height: 40px;
                background: linear-gradient(135deg, #3b82f6, #1d4ed8);
                border: 3px solid white;
                border-radius: 50% 50% 50% 0;
                transform: rotate(-45deg);
                box-shadow: 0 4px 8px rgba(0,0,0,0.3);
            }
            
            .marker-content {
                position: relative;
                z-index: 1;
                color: white;
                transform: rotate(45deg);
                margin-top: -5px;
            }
            
            .hotel-popup {
                min-width: 200px;
            }
            
            .hotel-popup h3 {
                margin: 0 0 8px 0;
                color: #1f2937;
            }
            
            .hotel-popup p {
                margin: 0 0 12px 0;
                line-height: 1.4;
            }
            
            .leaflet-popup-content-wrapper {
                border-radius: 8px;
                box-shadow: 0 10px 25px rgba(0,0,0,0.1);
            }
            
            .leaflet-popup-tip {
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            }
        `;
            document.head.appendChild(style);

        } catch (error) {
            console.error('Error initializing map:', error);
        }
    }, [location, hotelName]);


    useEffect(() => {
        // Don't initialize map if modal is open to avoid conflicts
        if (isModalOpen) {
            if (mapInstanceRef.current) {
                mapInstanceRef.current.remove();
                mapInstanceRef.current = null;
            }
            return;
        }

        // Only initialize map if location data is available
        if (!location || !location.latitude || !location.longitude || !mapRef.current) {
            return;
        }

        // Check if Leaflet is loaded
        if (typeof window !== 'undefined' && window.L) {
            initializeMap();
        } else {
            // Load Leaflet dynamically
            loadLeaflet().then(() => {
                initializeMap();
            });
        }

        return () => {
            // Cleanup map instance
            if (mapInstanceRef.current) {
                mapInstanceRef.current.remove();
                mapInstanceRef.current = null;
            }
        };
    }, [location, hotelName, isModalOpen, initializeMap]);

    const loadLeaflet = async () => {
        if (typeof window === 'undefined' || window.L) return;

        // Load CSS if not already loaded
        if (!document.querySelector('link[href*="leaflet.css"]')) {
            const cssLink = document.createElement('link');
            cssLink.rel = 'stylesheet';
            cssLink.href = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.css';
            cssLink.integrity = 'sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=';
            cssLink.crossOrigin = '';
            document.head.appendChild(cssLink);
        }

        // Load JS
        return new Promise((resolve, reject) => {
            const script = document.createElement('script');
            script.src = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.js';
            script.integrity = 'sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=';
            script.crossOrigin = '';
            script.onload = () => resolve(undefined);
            script.onerror = reject;
            document.head.appendChild(script);
        });
    };

    const openInGoogleMaps = () => {
        if (location) {
            const url = `https://www.google.com/maps?q=${location.latitude},${location.longitude}`;
            window.open(url, '_blank');
        }
    };

    if (!location || !location.latitude || !location.longitude) {
        return (
            <div className={`bg-gray-100 rounded-lg p-8 text-center ${className}`}>
                <MapPin className="mx-auto mb-4 text-gray-400" size={48} />
                <p className="text-gray-600">Thông tin vị trí không có sẵn</p>
            </div>
        );
    }

    return (
        <div className={`bg-white rounded-lg overflow-hidden shadow-lg ${className}`}>
            {/* Map Header */}
            <div className="p-4 bg-gradient-to-r from-blue-50 to-indigo-50 border-b border-blue-100">
                <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                        <div className="bg-blue-600 p-2 rounded-lg">
                            <MapPin className="text-white" size={20} />
                        </div>
                        <div>
                            <h3 className="font-bold text-gray-800">Vị trí khách sạn</h3>
                            <p className="text-sm text-gray-600">{location.detailAddress}</p>
                        </div>
                    </div>
                    <div className="flex gap-2">
                        {/* <button
                            onClick={openDirections}
                            className="flex items-center gap-2 px-3 py-2 bg-green-600 text-white text-sm font-medium rounded-lg hover:bg-green-700 transition-colors"
                            title="Chỉ đường"
                        >
                            <Navigation size={16} />
                            <span className="hidden sm:inline">Chỉ đường</span>
                        </button> */}
                        <button
                            onClick={openInGoogleMaps}
                            className="px-3 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 transition-colors"
                            title="Xem trên Google Maps"
                        >
                            <span className="hidden sm:inline">Google Maps</span>
                            <span className="sm:hidden">Maps</span>
                        </button>
                    </div>
                </div>
            </div>
            {/* Map Container */}
            <div className="relative">
                <div
                    ref={mapRef}
                    id="hotel-map-container"
                    className={`w-full h-80 bg-white ${onClick ? 'cursor-pointer' : ''}`}
                    style={{ minHeight: '320px' }}
                    onClick={onClick}
                />
                {/* Map overlay for better UX */}
                {onClick && (
                    <div
                        className="absolute inset-0 bg-black bg-opacity-0 hover:bg-opacity-10 transition-all duration-300 cursor-pointer rounded-lg flex items-center justify-center"
                        onClick={onClick}
                    >
                        <div className="bg-white/90 backdrop-blur-sm px-4 py-2 rounded-lg shadow-lg opacity-0 hover:opacity-100 transition-opacity duration-300 pointer-events-none">
                            <p className="text-sm font-medium text-gray-700">Nhấp để xem bản đồ chi tiết</p>
                        </div>
                    </div>
                )}
            </div>

            {/* Map Footer */}
            <div className="p-4 bg-gray-50 border-t">
                <div className="flex items-center justify-between text-sm">
                    {/* <div className="text-gray-600">
                        <span className="font-medium">Tọa độ:</span> {location.latitude.toFixed(6)}, {location.longitude.toFixed(6)}
                    </div> */}
                    <div className="flex items-center gap-4">
                        <button
                            onClick={() => {
                                if (mapInstanceRef.current) {
                                    mapInstanceRef.current.setView([location.latitude, location.longitude], 15);
                                }
                            }}
                            className="text-blue-600 hover:text-blue-700 font-medium"
                        >
                            Về trung tâm
                        </button>
                        <button
                            onClick={() => {
                                if (mapInstanceRef.current) {
                                    mapInstanceRef.current.zoomIn();
                                }
                            }}
                            className="text-blue-600 hover:text-blue-700 font-medium"
                        >
                            Phong to
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default HotelMap;
