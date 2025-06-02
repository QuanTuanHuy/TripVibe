// Test data for AvailableRooms component
export const sampleRooms = [
    {
        id: 1,
        name: "Deluxe Room",
        size: "25m²",
        beds: [
            { type: "giường đôi", count: 1 }
        ],
        occupancy: {
            adults: 2,
            children: 1
        },
        amenities: [
            "WiFi miễn phí",
            "Điều hòa",
            "Tủ lạnh mini",
            "TV màn hình phẳng",
            "Phòng tắm riêng",
            "Máy sấy tóc",
            "Két an toàn",
            "Dịch vụ phòng 24/7"
        ],
        breakfast: true,
        freeCancellation: true,
        prepayment: false,
        price: 1200000,
        remainingRooms: 3,
        images: [
            "https://cf.bstatic.com/xdata/images/hotel/max1024x768/587368962.jpg?k=d0c0ad579ea42f87f719ce513507df22c228ce8b6619d3691ff13a6936f17487&o=&hp=1",
            "https://cf.bstatic.com/xdata/images/hotel/max1024x768/414199546.jpg?k=7b9b9e04139210f5ae231c67ecdbac1c6b0fe784a0527e1d0867b36ab813c960&o=",
            "https://cf.bstatic.com/xdata/images/hotel/max1024x768/414203667.jpg?k=c068ca50ec803e86d211a0c10c3601cf8933749ad818bbe4814ad414d7d40fd6&o="
        ],
        specialOffers: [
            "Giảm 15% cho đặt phòng từ 3 đêm",
            "Miễn phí đưa đón sân bay"
        ]
    },
    {
        id: 2,
        name: "Superior Twin Room",
        size: "22m²",
        beds: [
            { type: "giường đơn", count: 2 }
        ],
        occupancy: {
            adults: 2,
            children: 0
        },
        amenities: [
            "WiFi miễn phí",
            "Điều hòa",
            "Tủ lạnh mini",
            "TV màn hình phẳng",
            "Phòng tắm riêng",
            "Máy sấy tóc"
        ],
        breakfast: false,
        freeCancellation: true,
        prepayment: true,
        price: 950000,
        remainingRooms: 5,
        images: [
            "https://cf.bstatic.com/xdata/images/hotel/max1024x768/414203665.jpg?k=9849b5702ce408d1b1075fb84f49e1dc2a1de30a191c8e5ce64fb051e87711bc&o=",
            "https://cf.bstatic.com/xdata/images/hotel/max1024x768/414203669.jpg?k=6e54fafedcfd46d60e23c2522e3a83a8e24831f48de325faa988345ae89f7684&o="
        ]
    },
    {
        id: 3,
        name: "Executive Suite",
        size: "45m²",
        beds: [
            { type: "giường king", count: 1 },
            { type: "sofa bed", count: 1 }
        ],
        occupancy: {
            adults: 3,
            children: 2
        },
        amenities: [
            "WiFi miễn phí",
            "Điều hòa",
            "Tủ lạnh mini",
            "TV màn hình phẳng",
            "Phòng tắm riêng",
            "Máy sấy tóc",
            "Két an toàn",
            "Dịch vụ phòng 24/7",
            "Ban công riêng",
            "Khu vực tiếp khách",
            "Máy pha cà phê",
            "Bồn tắm"
        ],
        breakfast: true,
        freeCancellation: true,
        prepayment: false,
        price: 2500000,
        remainingRooms: 2,
        images: [
            "https://cf.bstatic.com/xdata/images/hotel/max1024x768/414203664.jpg?k=6442947d520e09eb6a9cd8394e4f676334fb3396555970fe6ff3b652e0162620&o=",
            "https://cf.bstatic.com/xdata/images/hotel/max1024x768/303034839.jpg?k=24c08beed8f2e8f6b37e9ef4267635564424db28f25aa6d0b8b32abb7c13a185&o="
        ],
        specialOffers: [
            "Bao gồm spa credit 500,000 VND",
            "Late check-out miễn phí đến 2PM"
        ]
    }
];
