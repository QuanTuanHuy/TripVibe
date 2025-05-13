"use client";

const Footer: React.FC = () => {
  return (
    <footer className="bg-gray-100 py-6 md:py-8">
      <div className="container mx-auto px-4">
        <div className="text-center text-gray-600 text-sm">
        <p>© {new Date().getFullYear()} TripVibe. Tất cả các quyền được bảo lưu.</p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;