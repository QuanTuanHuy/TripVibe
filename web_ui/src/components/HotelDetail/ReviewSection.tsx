import { MapPin, Star, Users } from "lucide-react";

export default function ReviewSection() {
    return (
        <>
            <h2 className="text-2xl font-bold mb-6">Đánh giá của khách</h2>

            <div className="bg-white rounded-lg shadow-sm p-6 mb-8">
                <div className="flex items-center gap-4 mb-6">
                    <div className="bg-blue-700 text-white font-bold text-xl px-3 py-1 rounded">
                        9.1
                    </div>
                    <div>
                        <h3 className="font-bold text-lg">Tuyệt hảo</h3>
                        <p className="text-sm text-gray-600">689 đánh giá thật</p>
                    </div>
                    <div className="ml-auto">
                        <button className="text-blue-600 font-medium">Đọc tất cả đánh giá</button>
                    </div>
                </div>

                <h3 className="font-bold mb-4">Hạng mục:</h3>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-4 mb-8">
                    <div>
                        <div className="flex justify-between items-center mb-2">
                            <div className="text-sm">Nhân viên phục vụ</div>
                            <div className="flex items-center">
                                <div className="bg-blue-700 text-white font-bold text-sm px-2 py-0.5 rounded mr-2">9.5</div>
                                <div className="w-32 h-2 bg-gray-200 rounded-full">
                                    <div className="h-full bg-blue-700 rounded-full" style={{ width: "95%" }}></div>
                                </div>
                            </div>
                        </div>
                        <div className="flex justify-between items-center mb-2">
                            <div className="text-sm">Thoải mái</div>
                            <div className="flex items-center">
                                <div className="bg-blue-700 text-white font-bold text-sm px-2 py-0.5 rounded mr-2">9.2</div>
                                <div className="w-32 h-2 bg-gray-200 rounded-full">
                                    <div className="h-full bg-blue-700 rounded-full" style={{ width: "92%" }}></div>
                                </div>
                            </div>
                        </div>
                        <div className="flex justify-between items-center mb-2">
                            <div className="text-sm">WiFi miễn phí</div>
                            <div className="flex items-center">
                                <div className="bg-blue-700 text-white font-bold text-sm px-2 py-0.5 rounded mr-2">9.0</div>
                                <div className="w-32 h-2 bg-gray-200 rounded-full">
                                    <div className="h-full bg-blue-700 rounded-full" style={{ width: "90%" }}></div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div>
                        <div className="flex justify-between items-center mb-2">
                            <div className="text-sm">Tiện nghi</div>
                            <div className="flex items-center">
                                <div className="bg-blue-700 text-white font-bold text-sm px-2 py-0.5 rounded mr-2">9.1</div>
                                <div className="w-32 h-2 bg-gray-200 rounded-full">
                                    <div className="h-full bg-blue-700 rounded-full" style={{ width: "91%" }}></div>
                                </div>
                            </div>
                        </div>
                        <div className="flex justify-between items-center mb-2">
                            <div className="text-sm">Sạch sẽ</div>
                            <div className="flex items-center">
                                <div className="bg-blue-700 text-white font-bold text-sm px-2 py-0.5 rounded mr-2">9.2</div>
                                <div className="w-32 h-2 bg-gray-200 rounded-full">
                                    <div className="h-full bg-blue-700 rounded-full" style={{ width: "92%" }}></div>
                                </div>
                            </div>
                        </div>
                        <div className="flex justify-between items-center mb-2">
                            <div className="text-sm">Đáng giá tiền</div>
                            <div className="flex items-center">
                                <div className="bg-blue-700 text-white font-bold text-sm px-2 py-0.5 rounded mr-2">9.3</div>
                                <div className="w-32 h-2 bg-gray-200 rounded-full">
                                    <div className="h-full bg-blue-700 rounded-full" style={{ width: "93%" }}></div>
                                </div>
                            </div>
                        </div>
                        <div className="flex justify-between items-center mb-2">
                            <div className="text-sm">Địa điểm</div>
                            <div className="flex items-center">
                                <div className="bg-blue-700 text-white font-bold text-sm px-2 py-0.5 rounded mr-2">9.5</div>
                                <div className="w-32 h-2 bg-gray-200 rounded-full">
                                    <div className="h-full bg-blue-700 rounded-full" style={{ width: "95%" }}></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <h3 className="font-bold mb-4">Chọn chủ đề để đọc đánh giá:</h3>
                <div className="flex flex-wrap gap-2 mb-8">
                    <button className="px-3 py-2 bg-white border border-gray-300 rounded-full text-sm hover:bg-gray-50 flex items-center gap-1">
                        <MapPin size={14} />
                        Vị trí
                    </button>
                    <button className="px-3 py-2 bg-white border border-gray-300 rounded-full text-sm hover:bg-gray-50">
                        Phòng
                    </button>
                    <button className="px-3 py-2 bg-white border border-gray-300 rounded-full text-sm hover:bg-gray-50">
                        Sạch sẽ
                    </button>
                    <button className="px-3 py-2 bg-white border border-gray-300 rounded-full text-sm hover:bg-gray-50">
                        Bữa sáng
                    </button>
                    <button className="px-3 py-2 bg-white border border-gray-300 rounded-full text-sm hover:bg-gray-50">
                        Giường
                    </button>
                </div>
            </div>

            <div className="space-y-6">
                {/* Sample reviews */}
                <div className="border-b pb-6">
                    <div className="flex items-start gap-3 mb-3">
                        <div className="w-10 h-10 rounded-full bg-blue-50 flex items-center justify-center text-blue-600 font-bold">
                            NT
                        </div>
                        <div>
                            <h4 className="font-semibold">Nguyễn Thành</h4>
                            <div className="flex items-center gap-2 text-sm text-gray-600">
                                {/* <div className="flex">
                                                <img src="/images/vietnam-flag.png" alt="Vietnam" width={16} height={12} className="mr-1" />
                                                <span>Việt Nam</span>
                                            </div> */}
                                <span className="text-gray-400">•</span>
                                <div className="flex items-center">
                                    <Users size={14} className="mr-1" />
                                    <span>Gia đình có trẻ nhỏ</span>
                                </div>
                            </div>
                            <div className="mt-2">
                                <div className="flex items-center gap-1">
                                    <div className="bg-blue-700 text-white font-bold text-sm px-2 py-0.5 rounded">
                                        10
                                    </div>
                                    <Star fill="#FFB700" stroke="#FFB700" size={16} />
                                    <Star fill="#FFB700" stroke="#FFB700" size={16} />
                                    <Star fill="#FFB700" stroke="#FFB700" size={16} />
                                    <Star fill="#FFB700" stroke="#FFB700" size={16} />
                                    <Star fill="#FFB700" stroke="#FFB700" size={16} />
                                </div>
                                <p className="text-sm text-gray-500 mt-1">2 tuần trước</p>
                            </div>
                        </div>
                    </div>
                    <p className="text-gray-700">
                        Khách sạn rất tuyệt vời, phòng sạch sẽ và thoải mái. Nhân viên thân thiện và nhiệt tình. Vị trí thuận tiện, gần các điểm tham quan. Tôi sẽ quay lại lần sau!
                    </p>
                </div>

                <div>
                    <div className="flex items-start gap-3 mb-3">
                        <div className="w-10 h-10 rounded-full bg-green-50 flex items-center justify-center text-green-600 font-bold">
                            TL
                        </div>
                        <div>
                            <h4 className="font-semibold">Trần Linh</h4>
                            <div className="flex items-center gap-2 text-sm text-gray-600">
                                {/* <div className="flex">
                                                <img src="/images/vietnam-flag.png" alt="Vietnam" width={16} height={12} className="mr-1" />
                                                <span>Việt Nam</span>
                                            </div> */}
                                <span className="text-gray-400">•</span>
                                <div className="flex items-center">
                                    <Users size={14} className="mr-1" />
                                    <span>Cặp đôi</span>
                                </div>
                            </div>
                            <div className="mt-2">
                                <div className="flex items-center gap-1">
                                    <div className="bg-blue-700 text-white font-bold text-sm px-2 py-0.5 rounded">
                                        8
                                    </div>
                                    <Star fill="#FFB700" stroke="#FFB700" size={16} />
                                    <Star fill="#FFB700" stroke="#FFB700" size={16} />
                                    <Star fill="#FFB700" stroke="#FFB700" size={16} />
                                    <Star fill="#FFB700" stroke="#FFB700" size={16} />
                                    <Star fill="transparent" stroke="#FFB700" size={16} />
                                </div>
                                <p className="text-sm text-gray-500 mt-1">1 tháng trước</p>
                            </div>
                        </div>
                    </div>
                    <p className="text-gray-700">
                        Phòng đẹp, sạch sẽ và đầy đủ tiện nghi. Nhân viên phục vụ tốt. Bữa sáng khá ngon với nhiều lựa chọn. Vị trí thuận tiện để đi lại và tham quan các điểm du lịch gần đó.
                    </p>
                </div>
            </div>
        </>
    );
}