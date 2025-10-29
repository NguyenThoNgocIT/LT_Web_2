import { Coffee, CoffeeIcon, Beer, MenuIcon } from 'lucide-react';

import Header from '../../components/Header';

export default function HomePage() {
  return (
    <div className="min-h-screen bg-[#1a1a1a]">
      {/* Hero Section */}
      <header className="relative h-[600px]">
        <div className="absolute inset-0 bg-black/60 z-10" />
        <div 
          className="absolute inset-0 bg-cover bg-center"
          style={{
            backgroundImage: "url('https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?ixlib=rb-4.0.3')",
          }}
        />
        <div className="relative z-20 container mx-auto px-4 h-full flex flex-col justify-center">
          <h1 className="text-5xl md:text-7xl font-bold text-white mb-4">
            Thưởng thức hương vị
            <br />
            tuyệt hảo
          </h1>
          <p className="text-xl text-gray-200 max-w-xl">
            Nơi hòa quyện giữa không gian hiện đại và hương vị cà phê truyền thống
          </p>
        </div>
      </header>

      {/* Counter Section */}
      <section className="py-16 bg-[#222222]">
        <div className="container mx-auto px-4 grid grid-cols-1 md:grid-cols-3 gap-8">
          {/* Counter Bar */}
          <div className="bg-[#2a2a2a] p-8 rounded-lg text-center">
            <div className="mb-4">
              <Coffee className="w-12 h-12 text-amber-500 mx-auto" />
            </div>
            <h3 className="text-2xl font-bold text-white mb-2">Quầy pha chế</h3>
            <p className="text-gray-400">
              Trang bị máy pha cà phê chuyên nghiệp cùng đội ngũ barista tài năng
            </p>
          </div>

          {/* Counter Cold Brew */}
          <div className="bg-[#2a2a2a] p-8 rounded-lg text-center">
            <div className="mb-4">
              <Beer className="w-12 h-12 text-amber-500 mx-auto" />
            </div>
            <h3 className="text-2xl font-bold text-white mb-2">Cold Brew Bar</h3>
            <p className="text-gray-400">
              Trải nghiệm cold brew độc đáo với hệ thống vòi rót hiện đại
            </p>
          </div>

          {/* Counter Menu */}
          <div className="bg-[#2a2a2a] p-8 rounded-lg text-center">
            <div className="mb-4">
              <MenuIcon className="w-12 h-12 text-amber-500 mx-auto" />
            </div>
            <h3 className="text-2xl font-bold text-white mb-2">Menu Đa dạng</h3>
            <p className="text-gray-400">
              Thực đơn phong phú từ cà phê truyền thống đến đồ uống sáng tạo
            </p>
          </div>
        </div>
      </section>

      {/* Menu Preview Section */}
      <section className="py-16 bg-[#1a1a1a]">
        <div className="container mx-auto px-4">
          <h2 className="text-4xl font-bold text-white text-center mb-12">Menu của chúng tôi</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {/* Menu Board 1 */}
            <div className="bg-[#2a2a2a] p-6 rounded-lg">
              <h3 className="text-xl font-bold text-amber-500 mb-4">Cà phê truyền thống</h3>
              <ul className="space-y-4">
                <MenuItem name="Espresso" price="35.000đ" />
                <MenuItem name="Americano" price="45.000đ" />
                <MenuItem name="Cappuccino" price="55.000đ" />
                <MenuItem name="Latte" price="55.000đ" />
              </ul>
            </div>

            {/* Menu Board 2 */}
            <div className="bg-[#2a2a2a] p-6 rounded-lg">
              <h3 className="text-xl font-bold text-amber-500 mb-4">Đặc biệt</h3>
              <ul className="space-y-4">
                <MenuItem name="Cold Brew" price="49.000đ" />
                <MenuItem name="Vietnamese Coffee" price="39.000đ" />
                <MenuItem name="Mocha" price="59.000đ" />
                <MenuItem name="Caramel Macchiato" price="65.000đ" />
              </ul>
            </div>

            {/* Menu Board 3 */}
            <div className="bg-[#2a2a2a] p-6 rounded-lg">
              <h3 className="text-xl font-bold text-amber-500 mb-4">Trà & Khác</h3>
              <ul className="space-y-4">
                <MenuItem name="Trà Sen" price="45.000đ" />
                <MenuItem name="Trà Đào" price="45.000đ" />
                <MenuItem name="Matcha Latte" price="59.000đ" />
                <MenuItem name="Hot Chocolate" price="55.000đ" />
              </ul>
            </div>
          </div>
        </div>
      </section>

      {/* Equipment Section */}
      <section className="py-16 bg-[#222222]">
        <div className="container mx-auto px-4">
          <h2 className="text-4xl font-bold text-white text-center mb-12">Thiết bị & Không gian</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            <EquipmentCard 
              title="Máy pha cà phê chuyên nghiệp"
              description="Máy pha cà phê Espresso cao cấp, đảm bảo chất lượng cho từng tách cà phê"
              imageUrl="https://images.unsplash.com/photo-1592663527359-cf6642f54cff?ixlib=rb-4.0.3"
            />
            <EquipmentCard 
              title="Hệ thống Cold Brew"
              description="Hệ thống pha chế cold brew hiện đại với vòi rót chuyên dụng"
              imageUrl="https://images.unsplash.com/photo-1578874691223-64558a3ca096?ixlib=rb-4.0.3"
            />
            <EquipmentCard 
              title="Không gian trưng bày"
              description="Kệ trưng bày với các sản phẩm cà phê, trà và phụ kiện độc đáo"
              imageUrl="https://images.unsplash.com/photo-1521017432531-fbd92d768814?ixlib=rb-4.0.3"
            />
          </div>
        </div>
      </section>

      {/* Staff Section */}
      <section className="py-16 bg-[#1a1a1a]">
        <div className="container mx-auto px-4">
          <h2 className="text-4xl font-bold text-white text-center mb-12">Đội ngũ của chúng tôi</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            <StaffCard 
              name="Ngọc Anh"
              role="Head Barista"
              imageUrl="https://images.unsplash.com/photo-1517258471123-7d3669e05654?ixlib=rb-4.0.3"
            />
            <StaffCard 
              name="Minh Tuấn"
              role="Coffee Roaster"
              imageUrl="https://images.unsplash.com/photo-1516224498413-5f1f893f059a?ixlib=rb-4.0.3"
            />
            <StaffCard 
              name="Thu Hà"
              role="Service Manager"
              imageUrl="https://images.unsplash.com/photo-1528297506728-9533d2ac3fa4?ixlib=rb-4.0.3"
            />
          </div>
        </div>
      </section>
    </div>
  );
}

// Helper Components
const MenuItem = ({ name, price }) => (
  <div className="flex justify-between items-center">
    <span className="text-gray-300">{name}</span>
    <span className="text-amber-500 font-medium">{price}</span>
  </div>
);

const EquipmentCard = ({ title, description, imageUrl }) => (
  <div className="bg-[#2a2a2a] rounded-lg overflow-hidden">
    <div className="h-48 overflow-hidden">
      <img 
        src={imageUrl} 
        alt={title}
        className="w-full h-full object-cover hover:scale-110 transition-transform duration-300"
      />
    </div>
    <div className="p-6">
      <h3 className="text-xl font-bold text-white mb-2">{title}</h3>
      <p className="text-gray-400">{description}</p>
    </div>
  </div>
);

const StaffCard = ({ name, role, imageUrl }) => (
  <div className="bg-[#2a2a2a] rounded-lg overflow-hidden">
    <div className="h-64 overflow-hidden">
      <img 
        src={imageUrl} 
        alt={name}
        className="w-full h-full object-cover hover:scale-110 transition-transform duration-300"
      />
    </div>
    <div className="p-6 text-center">
      <h3 className="text-xl font-bold text-white mb-1">{name}</h3>
      <p className="text-amber-500">{role}</p>
    </div>
  </div>
);