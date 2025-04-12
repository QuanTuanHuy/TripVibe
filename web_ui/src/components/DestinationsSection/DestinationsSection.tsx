"use client";

interface DestinationCardProps {
  name: string;
  imageUrl: string;
}

const DestinationCard: React.FC<DestinationCardProps> = ({ name, imageUrl }) => {
  return (
    <div className="rounded-lg overflow-hidden shadow-md relative h-48 md:h-64">
      <img
        src={imageUrl}
        alt={name}
        className="w-full h-full object-cover"
      />
      <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent flex items-end p-4">
        <div>
          <div className="flex items-center">
            <h3 className="text-white text-lg md:text-xl font-bold">{name}</h3>
            <div className="ml-2 h-4 w-6 bg-red-500 relative rounded-sm">
              <div className="absolute inset-0 flex items-center justify-center text-yellow-300 text-xs">★</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

const DestinationsSection: React.FC = () => {
  const destinations = [
    { name: "TP. Hồ Chí Minh", imageUrl: "https://cf.bstatic.com/xdata/images/city/600x600/688893.jpg?k=d32ef7ff94e5d02b90908214fb2476185b62339549a1bd7544612bdac51fda31&o=" },
    { name: "Hà Nội", imageUrl: "https://cf.bstatic.com/xdata/images/city/600x600/981517.jpg?k=2268f51ad34ab94115ea9e42155bc593aa8d48ffaa6fc58432a8760467dc4ea6&o=" },
    { name: "Đà Nẵng", imageUrl: "https://cf.bstatic.com/xdata/images/city/600x600/688844.jpg?k=02892d4252c5e4272ca29db5faf12104004f81d13ff9db724371de0c526e1e15&o=" },
    { name: "Vũng Tàu", imageUrl: "https://cf.bstatic.com/xdata/images/city/600x600/688956.jpg?k=fc88c6ab5434042ebe73d94991e011866b18ee486476e475a9ac596c79dce818&o=" },
    { name: "Đà Lạt", imageUrl: "https://cf.bstatic.com/xdata/images/city/600x600/688831.jpg?k=7b999c7babe3487598fc4dd89365db2c4778827eac8cb2a47d48505c97959a78&o=" }
  ];

  return (
    <section className="py-6 md:py-8 bg-gray-50 text-gray-800">
      <div className="container mx-auto px-4">
        <h2 className="text-xl md:text-2xl font-bold mb-2">Điểm đến đang thịnh hành</h2>
        <p className="text-gray-600 mb-4 md:mb-6">Các lựa chọn phổ biến nhất cho du khách từ Việt Nam</p>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {destinations.map((destination, index) => (
            <DestinationCard 
              key={index}
              name={destination.name}
              imageUrl={destination.imageUrl}
            />
          ))}
        </div>
      </div>
    </section>
  );
};

export default DestinationsSection;