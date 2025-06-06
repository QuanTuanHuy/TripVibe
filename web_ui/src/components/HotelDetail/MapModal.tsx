"use client";

import React, { useEffect, useRef, useState } from 'react';
import { X, MapPin, Navigation, ExternalLink, ZoomIn, ZoomOut, RotateCcw, Maximize2 } from 'lucide-react';
import { Location } from '@/types/location';

interface MapModalProps {
    isOpen: boolean;
    onClose: () => void;
    location?: Location;
    hotelName: string;
    address: string;
}

declare global {
    interface Window {
        L: any;
    }
}

const MapModal: React.FC<MapModalProps> = ({ isOpen, onClose, location, hotelName, address }) => {
    const mapRef = useRef<HTMLDivElement>(null);
    const mapInstanceRef = useRef<any>(null);
    const [isFullscreen, setIsFullscreen] = useState(false);
    
    useEffect(() => {
        if (isOpen && location && location.latitude && location.longitude) {
            // Add small delay to ensure modal is rendered and previous map instance is cleaned up
            setTimeout(() => {
                initializeMap();
            }, 200);
        }

        return () => {
            if (mapInstanceRef.current) {
                mapInstanceRef.current.remove();
                mapInstanceRef.current = null;
            }
        };
    }, [isOpen, location]);

    useEffect(() => {
        const handleEscape = (e: KeyboardEvent) => {
            if (e.key === 'Escape') {
                onClose();
            }
        };

        if (isOpen) {
            document.addEventListener('keydown', handleEscape);
            document.body.style.overflow = 'hidden';
        }

        return () => {
            document.removeEventListener('keydown', handleEscape);
            document.body.style.overflow = 'unset';
        };
    }, [isOpen, onClose]);

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

    const initializeMap = async () => {
        if (!mapRef.current || !location || mapInstanceRef.current) return;

        try {
            // Check if Leaflet is loaded
            if (typeof window !== 'undefined' && window.L) {
                createMap();
            } else {
                await loadLeaflet();
                createMap();
            }
        } catch (error) {
            console.error('Error initializing map:', error);
        }
    };

    const createMap = () => {
        if (!mapRef.current || !location) return;

        // Initialize map
        const map = window.L.map(mapRef.current, {
            center: [location.latitude, location.longitude],
            zoom: 16,
            zoomControl: false, // We'll add custom controls
            scrollWheelZoom: true,
            doubleClickZoom: true,
            dragging: true,
            touchZoom: true,
            boxZoom: true,
        });

        // Add multiple tile layers
        const osmLayer = window.L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '© OpenStreetMap contributors',
            maxZoom: 19,
        });

        const satelliteLayer = window.L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
            attribution: '© Esri',
            maxZoom: 18,
        });

        // Add default layer
        osmLayer.addTo(map);

        // Create layer control
        const baseLayers = {
            "Bản đồ đường": osmLayer,
            "Vệ tinh": satelliteLayer
        };
        window.L.control.layers(baseLayers).addTo(map);

        // Custom hotel icon
        const hotelIcon = window.L.divIcon({
            html: `
                <div class="hotel-marker-large">
                    <div class="marker-pin-large"></div>
                    <div class="marker-content-large">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
                            <circle cx="12" cy="10" r="3"/>
                        </svg>
                    </div>
                </div>
            `,
            className: 'custom-div-icon-large',
            iconSize: [50, 60],
            iconAnchor: [25, 60],
            popupAnchor: [0, -60],
        });

        // Add marker
        const marker = window.L.marker([location.latitude, location.longitude], {
            icon: hotelIcon
        }).addTo(map);

        // Add popup
        marker.bindPopup(`
            <div class="hotel-popup-large">
                <h3 class="font-bold text-xl mb-3">${hotelName}</h3>
                <p class="text-gray-600 mb-4">${address}</p>
                <div class="flex items-center gap-2 text-blue-600 font-medium">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
                        <circle cx="12" cy="10" r="3"/>
                    </svg>
                    <span>${hotelName}</span>
                </div>
            </div>
        `).openPopup();

        // Add circle to show area
        window.L.circle([location.latitude, location.longitude], {
            color: '#3b82f6',
            fillColor: '#3b82f6',
            fillOpacity: 0.1,
            radius: 200
        }).addTo(map);

        mapInstanceRef.current = map;

        // Add custom styles
        addCustomStyles();
    };

    const addCustomStyles = () => {
        const style = document.createElement('style');
        style.textContent = `
            .hotel-marker-large {
                position: relative;
                display: flex;
                align-items: center;
                justify-content: center;
            }
            
            .marker-pin-large {
                position: absolute;
                width: 50px;
                height: 50px;
                background: linear-gradient(135deg, #3b82f6, #1d4ed8);
                border: 4px solid white;
                border-radius: 50% 50% 50% 0;
                transform: rotate(-45deg);
                box-shadow: 0 6px 12px rgba(0,0,0,0.3);
            }
            
            .marker-content-large {
                position: relative;
                z-index: 1;
                color: white;
                transform: rotate(45deg);
                margin-top: -5px;
            }
            
            .hotel-popup-large {
                min-width: 250px;
            }
            
            .hotel-popup-large h3 {
                margin: 0 0 12px 0;
                color: #1f2937;
            }
            
            .hotel-popup-large p {
                margin: 0 0 16px 0;
                line-height: 1.5;
            }
            
            .leaflet-popup-content-wrapper {
                border-radius: 12px;
                box-shadow: 0 20px 25px rgba(0,0,0,0.15);
            }
            
            .leaflet-popup-tip {
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            }
        `;
        document.head.appendChild(style);
    };

    const openInGoogleMaps = () => {
        if (location) {
            const url = `https://www.google.com/maps?q=${location.latitude},${location.longitude}`;
            window.open(url, '_blank');
        }
    };

    const openDirections = () => {
        if (location) {
            const url = `https://www.google.com/maps/dir/?api=1&destination=${location.latitude},${location.longitude}`;
            window.open(url, '_blank');
        }
    };

    const zoomIn = () => {
        if (mapInstanceRef.current) {
            mapInstanceRef.current.zoomIn();
        }
    };

    const zoomOut = () => {
        if (mapInstanceRef.current) {
            mapInstanceRef.current.zoomOut();
        }
    };

    const resetView = () => {
        if (mapInstanceRef.current && location) {
            mapInstanceRef.current.setView([location.latitude, location.longitude], 16);
        }
    };

    const toggleFullscreen = () => {
        setIsFullscreen(!isFullscreen);
        // Trigger map resize after fullscreen change
        setTimeout(() => {
            if (mapInstanceRef.current) {
                mapInstanceRef.current.invalidateSize();
            }
        }, 100);
    }; if (!isOpen) return null;

    return (
        <div className="fixed inset-0 z-[9999] flex items-center justify-center">
            {/* Backdrop */}
            <div
                className="absolute inset-0 bg-black bg-opacity-60 backdrop-blur-sm z-[9998]"
                onClick={onClose}
            />
            {/* Modal */}
            <div className={`relative bg-white rounded-xl shadow-2xl overflow-hidden transition-all duration-300 z-[9999] ${isFullscreen
                ? 'w-full h-full m-0 rounded-none'
                : 'w-[90vw] h-[90vh] max-w-6xl max-h-[800px] m-4'
                }`}>
                {/* Header */}
                <div className="flex items-center justify-between p-4 bg-gradient-to-r from-blue-50 to-indigo-50 border-b border-gray-200">
                    <div className="flex items-center gap-3">
                        <div className="bg-blue-600 p-2 rounded-lg">
                            <MapPin className="text-white" size={20} />
                        </div>
                        <div>
                            <h2 className="text-xl font-bold text-gray-800">{hotelName}</h2>
                            <p className="text-sm text-gray-600">{address}</p>
                        </div>
                    </div>

                    <div className="flex items-center gap-2">
                        <button
                            onClick={toggleFullscreen}
                            className="p-2 hover:bg-white/60 rounded-lg transition-colors"
                            title={isFullscreen ? "Thu nhỏ" : "Toàn màn hình"}
                        >
                            <Maximize2 size={18} />
                        </button>
                        <button
                            onClick={onClose}
                            className="p-2 hover:bg-white/60 rounded-lg transition-colors"
                            title="Đóng"
                        >
                            <X size={18} />
                        </button>
                    </div>
                </div>                {/* Map Container */}
                <div className="relative flex-1 h-full">
                    <div
                        ref={mapRef}
                        id="modal-map-container"
                        className="w-full h-full bg-gray-100"
                        style={{ minHeight: isFullscreen ? 'calc(100vh - 140px)' : '600px' }}
                    />

                    {/* Custom Controls */}
                    <div className="absolute top-4 right-4 flex flex-col gap-2 z-[1000]">
                        <button
                            onClick={zoomIn}
                            className="bg-white hover:bg-gray-50 p-2 rounded-lg shadow-lg border border-gray-200 transition-colors"
                            title="Phóng to"
                        >
                            <ZoomIn size={18} />
                        </button>
                        <button
                            onClick={zoomOut}
                            className="bg-white hover:bg-gray-50 p-2 rounded-lg shadow-lg border border-gray-200 transition-colors"
                            title="Thu nhỏ"
                        >
                            <ZoomOut size={18} />
                        </button>
                        <button
                            onClick={resetView}
                            className="bg-white hover:bg-gray-50 p-2 rounded-lg shadow-lg border border-gray-200 transition-colors"
                            title="Về vị trí ban đầu"
                        >
                            <RotateCcw size={18} />
                        </button>
                    </div>

                    {/* Info Panel */}
                    <div className="absolute bottom-4 left-4 bg-white/95 backdrop-blur-sm rounded-xl p-4 shadow-lg border border-gray-200 max-w-xs z-[1000]">
                        <div className="flex items-center gap-3 mb-3">
                            <div className="bg-blue-600 p-2 rounded-lg">
                                <MapPin className="text-white" size={16} />
                            </div>
                            <div>
                                <h3 className="font-bold text-gray-800">{hotelName}</h3>
                                <p className="text-xs text-gray-600">Vị trí chính xác</p>
                            </div>
                        </div>

                        {location && (
                            <div className="text-xs text-gray-600 mb-3 space-y-1">
                                <div>Vĩ độ: {location.latitude.toFixed(6)}</div>
                                <div>Kinh độ: {location.longitude.toFixed(6)}</div>
                            </div>
                        )}

                        <div className="flex gap-2">
                            <button
                                onClick={openDirections}
                                className="flex-1 flex items-center justify-center gap-1 px-3 py-2 bg-green-600 text-white text-xs font-medium rounded-lg hover:bg-green-700 transition-colors"
                            >
                                <Navigation size={14} />
                                Chỉ đường
                            </button>
                            <button
                                onClick={openInGoogleMaps}
                                className="flex-1 flex items-center justify-center gap-1 px-3 py-2 bg-blue-600 text-white text-xs font-medium rounded-lg hover:bg-blue-700 transition-colors"
                            >
                                <ExternalLink size={14} />
                                Google Maps
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default MapModal;
