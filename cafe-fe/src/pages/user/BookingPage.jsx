import { useState, useEffect } from 'react';
import TableMap from '../../components/TableMap';
import { Button } from '../../components/ui/button';
import { motion } from 'framer-motion';
import { Calendar, ChefHat, Clock, MapPin, Phone, Users } from 'lucide-react';

const BookingPage = () => {
    return (
        <div className="min-h-screen bg-[#fcf7f2]">
            {/* Hero Section */}
            <div className="relative h-[300px] bg-gradient-to-r from-brown-800 to-brown-900 text-white">
                <div className="absolute inset-0 bg-black/40"></div>
                <div className="relative h-full max-w-7xl mx-auto px-6 flex flex-col justify-center">
                    <motion.h1 
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        className="text-4xl md:text-5xl font-bold mb-4"
                    >
                        Đặt bàn
                    </motion.h1>
                    <motion.p 
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.1 }}
                        className="text-lg md:text-xl text-gray-200 max-w-2xl"
                    >
                        Hãy để chúng tôi chuẩn bị không gian tốt nhất cho bạn và những người thân yêu
                    </motion.p>
                </div>
            </div>

            {/* Content */}
            <div className="max-w-7xl mx-auto px-6 py-12">
                <div className="grid grid-cols-1 lg:grid-cols-3 gap-12">
                    {/* Left Column - Info */}
                    <div className="space-y-8">
                        <div>
                            <h2 className="text-2xl font-bold mb-4">Thông tin đặt bàn</h2>
                            <div className="space-y-4">
                                <div className="flex items-start space-x-3">
                                    <Clock className="w-5 h-5 mt-0.5 text-brown-600" />
                                    <div>
                                        <h3 className="font-semibold">Giờ mở cửa</h3>
                                        <p className="text-gray-600">Thứ 2 - Chủ nhật: 7:00 - 22:00</p>
                                    </div>
                                </div>
                                
                                <div className="flex items-start space-x-3">
                                    <Phone className="w-5 h-5 mt-0.5 text-brown-600" />
                                    <div>
                                        <h3 className="font-semibold">Liên hệ</h3>
                                        <p className="text-gray-600">+84 123 456 789</p>
                                        <p className="text-gray-600">cafe@example.com</p>
                                    </div>
                                </div>
                                
                                <div className="flex items-start space-x-3">
                                    <MapPin className="w-5 h-5 mt-0.5 text-brown-600" />
                                    <div>
                                        <h3 className="font-semibold">Địa chỉ</h3>
                                        <p className="text-gray-600">123 Đường ABC, Quận XYZ, TP.HCM</p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div>
                            <h2 className="text-2xl font-bold mb-4">Lưu ý khi đặt bàn</h2>
                            <ul className="space-y-3 text-gray-600">
                                <li className="flex items-start space-x-2">
                                    <Calendar className="w-5 h-5 mt-0.5 text-brown-600 flex-shrink-0" />
                                    <span>Đặt bàn trước ít nhất 2 tiếng</span>
                                </li>
                                <li className="flex items-start space-x-2">
                                    <Users className="w-5 h-5 mt-0.5 text-brown-600 flex-shrink-0" />
                                    <span>Vui lòng đến đúng số lượng người đã đặt</span>
                                </li>
                                <li className="flex items-start space-x-2">
                                    <Clock className="w-5 h-5 mt-0.5 text-brown-600 flex-shrink-0" />
                                    <span>Giữ bàn tối đa 15 phút sau giờ đặt</span>
                                </li>
                                <li className="flex items-start space-x-2">
                                    <ChefHat className="w-5 h-5 mt-0.5 text-brown-600 flex-shrink-0" />
                                    <span>Có thể đặt trước món ăn khi đặt bàn</span>
                                </li>
                            </ul>
                        </div>

                        <div className="bg-brown-50 border border-brown-100 rounded-lg p-6">
                            <h3 className="font-semibold mb-2">Cần hỗ trợ?</h3>
                            <p className="text-gray-600 mb-4">Liên hệ với chúng tôi nếu bạn cần đặt bàn cho nhóm lớn hoặc có yêu cầu đặc biệt</p>
                            <Button className="w-full">
                                Gọi ngay
                            </Button>
                        </div>
                    </div>

                    {/* Right Column - Table Map */}
                    <div className="lg:col-span-2">
                        <TableMap />
                    </div>
                </div>
            </div>
        </div>
    );
};

export default BookingPage;