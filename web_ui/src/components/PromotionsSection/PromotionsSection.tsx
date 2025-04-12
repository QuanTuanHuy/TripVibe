"use client";

const PromotionsSection: React.FC = () => {
  return (
    <section className="py-6 md:py-8 text-gray-800">
      <div className="container mx-auto px-4">
        <div className="mb-6 md:mb-8">
          <h2 className="text-xl md:text-2xl font-bold mb-2">Ưu đãi</h2>
          <p className="text-gray-600">Khuyến mãi, giảm giá và ưu đãi đặc biệt dành riêng cho bạn</p>
        </div>

        <div className="bg-white shadow-md rounded-lg p-4 md:p-6 mb-8 border border-gray-200">
          <div className="flex flex-col md:flex-row">
            <div className="md:w-2/3">
              <h3 className="text-lg md:text-xl font-bold mb-2">Kỳ nghỉ ngắn ngày chất lượng</h3>
              <p className="text-gray-600 mb-4">Tiết kiệm đến 20% với Ưu Đãi Mùa Du Lịch</p>
              <button className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-md transition duration-200">
                Săn ưu đãi
              </button>
            </div>
            <div className="md:w-1/3 mt-4 md:mt-0">
              <div className="h-32 bg-gray-200 rounded-md overflow-hidden relative">
                <img
                  src="https://q-xx.bstatic.com/xdata/images/xphoto/248x248/468828542.jpeg?k=b51cb74f05db0ebc1a1cbcca384fa2ee8c4d6c0b5fd089a15b1fd14a107ccab4&o="
                  alt="placeholder"
                  className="h-full object-cover"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default PromotionsSection;