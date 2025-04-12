"use client";

interface PropertyCardProps {
  name: string;
  imageUrl: string;
}

const PropertyCard: React.FC<PropertyCardProps> = ({ name, imageUrl }) => {
  return (
    <div className="rounded-lg overflow-hidden shadow-md">
      <div className="h-32 md:h-40 bg-gray-200">
        <img
          src={imageUrl}
          alt={name}
          className="w-full h-full object-cover"
        />
      </div>
      <div className="p-3">
        <h3 className="font-medium">{name}</h3>
      </div>
    </div>
  );
};

const PropertyTypesSection: React.FC = () => {
  const propertyTypes = [
    { name: "Khách sạn", imageUrl: "https://r-xx.bstatic.com/xdata/images/hotel/263x210/595550862.jpeg?k=3514aa4abb76a6d19df104cb307b78b841ac0676967f24f4b860d289d55d3964&o=" },
    { name: "Căn hộ", imageUrl: "https://q-xx.bstatic.com/xdata/images/hotel/263x210/595548591.jpeg?k=01741bc3aef1a5233dd33794dda397083092c0215b153915f27ea489468e57a2&o=" },
    { name: "Khu nghỉ dưỡng", imageUrl: "https://r-xx.bstatic.com/xdata/images/hotel/263x210/595551044.jpeg?k=262826efe8e21a0868105c01bf7113ed94de28492ee370f4225f00d1de0c6c44&o=" },
    { name: "Biệt thự", imageUrl: "https://q-xx.bstatic.com/xdata/images/hotel/263x210/620168315.jpeg?k=300d8d8059c8c5426ea81f65a30a7f93af09d377d4d8570bda1bd1f0c8f0767f&o=" }
  ];

  return (
    <section className="py-6 md:py-8 text-gray-800">
      <div className="container mx-auto px-4">
        <h2 className="text-xl md:text-2xl font-bold mb-4 md:mb-6">Tìm theo loại chỗ nghỉ</h2>

        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4">
          {propertyTypes.map((property, index) => (
            <PropertyCard
              key={index}
              name={property.name}
              imageUrl={property.imageUrl}
            />
          ))}
        </div>
      </div>
    </section>
  );
};

export default PropertyTypesSection;