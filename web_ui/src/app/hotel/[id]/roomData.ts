// Dữ liệu mẫu cho phần Phòng trống
export const availableRooms = [
  {
    id: 1,
    name: "Phòng Deluxe",
    size: "25m²",
    beds: [
      { type: "Giường đôi", count: 1 }
    ],
    occupancy: {
      adults: 2,
      children: 1
    },
    amenities: [
      "WiFi miễn phí",
      "Điều hòa nhiệt độ",
      "TV màn hình phẳng",
      "Minibar",
      "Két an toàn"
    ],
    images: [
      "https://cf.bstatic.com/xdata/images/hotel/max1024x768/435757565.jpg?k=d9435da26c0572a05233e820ab907f4a21f322a3133e8a04ca6829f3c241145e&o=",
      "https://cf.bstatic.com/xdata/images/hotel/max1024x768/414199546.jpg?k=7b9b9e04139210f5ae231c67ecdbac1c6b0fe784a0527e1d0867b36ab813c960&o=",
      "https://cf.bstatic.com/xdata/images/hotel/max1024x768/414203667.jpg?k=c068ca50ec803e86d211a0c10c3601cf8933749ad818bbe4814ad414d7d40fd6&o=",
      "https://cf.bstatic.com/xdata/images/hotel/max1024x768/414203665.jpg?k=9849b5702ce408d1b1075fb84f49e1dc2a1de30a191c8e5ce64fb051e87711bc&o=",
      "https://cf.bstatic.com/xdata/images/hotel/max1024x768/414203669.jpg?k=6e54fafedcfd46d60e23c2522e3a83a8e24831f48de325faa988345ae89f7684&o=",
      "https://cf.bstatic.com/xdata/images/hotel/max1024x768/414203664.jpg?k=6442947d520e09eb6a9cd8394e4f676334fb3396555970fe6ff3b652e0162620&o=",
      "https://cf.bstatic.com/xdata/images/hotel/max1024x768/303034839.jpg?k=24c08beed8f2e8f6b37e9ef4267635564424db28f25aa6d0b8b32abb7c13a185&o=",

    ],
    price: 1250000,
    specialOffers: ["Giảm 10% cho đặt phòng sớm"],
    freeCancellation: true,
    prepayment: false,
    breakfast: true,
    remainingRooms: 3
  },
  {
    id: 2,
    name: "Phòng Superior",
    size: "30m²",
    beds: [
      { type: "Giường đôi", count: 1 }
    ],
    occupancy: {
      adults: 2,
      children: 0
    },
    amenities: [
      "WiFi miễn phí",
      "Điều hòa nhiệt độ",
      "TV màn hình phẳng",
      "Minibar",
      "Két an toàn",
      "Phòng tắm riêng lớn",
      "Bồn tắm"
    ],
    images: [
      "https://cf.bstatic.com/xdata/images/hotel/max1024x768/435757565.jpg?k=d9435da26c0572a05233e820ab907f4a21f322a3133e8a04ca6829f3c241145e&o=",
      "https://cf.bstatic.com/xdata/images/hotel/max1024x768/414199546.jpg?k=7b9b9e04139210f5ae231c67ecdbac1c6b0fe784a0527e1d0867b36ab813c960&o=",
      "https://cf.bstatic.com/xdata/images/hotel/max1024x768/414203667.jpg?k=c068ca50ec803e86d211a0c10c3601cf8933749ad818bbe4814ad414d7d40fd6&o=",
      "https://cf.bstatic.com/xdata/images/hotel/max1024x768/414203665.jpg?k=9849b5702ce408d1b1075fb84f49e1dc2a1de30a191c8e5ce64fb051e87711bc&o=",
      "https://cf.bstatic.com/xdata/images/hotel/max1024x768/414203669.jpg?k=6e54fafedcfd46d60e23c2522e3a83a8e24831f48de325faa988345ae89f7684&o=",
      "https://cf.bstatic.com/xdata/images/hotel/max1024x768/414203664.jpg?k=6442947d520e09eb6a9cd8394e4f676334fb3396555970fe6ff3b652e0162620&o=",
      "https://cf.bstatic.com/xdata/images/hotel/max1024x768/303034839.jpg?k=24c08beed8f2e8f6b37e9ef4267635564424db28f25aa6d0b8b32abb7c13a185&o=",
    ],
    price: 1500000,
    specialOffers: ["Bữa tối miễn phí", "Đưa đón sân bay"],
    freeCancellation: true,
    prepayment: false,
    breakfast: true,
    remainingRooms: 2
  },
  {
    id: 3,
    name: "Phòng Suite Executive",
    size: "45m²",
    beds: [
      { type: "Giường King", count: 1 }
    ],
    occupancy: {
      adults: 2,
      children: 2
    },
    amenities: [
      "WiFi miễn phí tốc độ cao",
      "Điều hòa nhiệt độ",
      "TV màn hình phẳng 4K",
      "Minibar miễn phí",
      "Két an toàn",
      "Phòng tắm sang trọng",
      "Bồn tắm spa",
      "Khu vực phòng khách riêng"
    ],
    images: [
      "https://cf.bstatic.com/xdata/images/hotel/max1024x768/435757565.jpg?k=d9435da26c0572a05233e820ab907f4a21f322a3133e8a04ca6829f3c241145e&o=",
      "https://cf.bstatic.com/xdata/images/hotel/max1024x768/414199546.jpg?k=7b9b9e04139210f5ae231c67ecdbac1c6b0fe784a0527e1d0867b36ab813c960&o=",
      "https://cf.bstatic.com/xdata/images/hotel/max1024x768/414203667.jpg?k=c068ca50ec803e86d211a0c10c3601cf8933749ad818bbe4814ad414d7d40fd6&o=",
      "https://cf.bstatic.com/xdata/images/hotel/max1024x768/414203665.jpg?k=9849b5702ce408d1b1075fb84f49e1dc2a1de30a191c8e5ce64fb051e87711bc&o=",
      "https://cf.bstatic.com/xdata/images/hotel/max1024x768/414203669.jpg?k=6e54fafedcfd46d60e23c2522e3a83a8e24831f48de325faa988345ae89f7684&o=",
      "https://cf.bstatic.com/xdata/images/hotel/max1024x768/414203664.jpg?k=6442947d520e09eb6a9cd8394e4f676334fb3396555970fe6ff3b652e0162620&o=",
      "https://cf.bstatic.com/xdata/images/hotel/max1024x768/303034839.jpg?k=24c08beed8f2e8f6b37e9ef4267635564424db28f25aa6d0b8b32abb7c13a185&o=",

    ],
    price: 2500000,
    specialOffers: ["Trải nghiệm spa miễn phí", "Đồ uống chào mừng"],
    freeCancellation: true,
    prepayment: false,
    breakfast: true,
    remainingRooms: 1
  }
];

// Chuyển đổi giá tiền thành định dạng VND
export const formatPrice = (price: number): string => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price);
};
