import { useState, useEffect } from 'react';
import { Button } from './ui/button';
import { Users, Coffee, Clock } from 'lucide-react';
import { motion } from 'framer-motion';
import { toast } from 'sonner';
import { getAvailableTables, createReservation } from '../api/table.api';

export default function TableMap() {
    const [tables, setTables] = useState([]);
    const [selectedTable, setSelectedTable] = useState(null);
    const [showReservationModal, setShowReservationModal] = useState(false);
    const [reservation, setReservation] = useState({
        date: '',
        time: '',
        numberOfGuests: '',
        notes: ''
    });

    useEffect(() => {
        fetchTables();
    }, []);

    const fetchTables = async () => {
        try {
            const data = await getAvailableTables();
            setTables(data);
        } catch (error) {
            console.error('Error fetching tables:', error);
            toast.error('Không thể tải thông tin bàn');
        }
    };

    const handleReserve = async (e) => {
        e.preventDefault();
        try {
            // Backend expects: { tableId, reservationTime (ISO), note }
            const reservationData = {
                tableId: selectedTable.id,
                reservationTime: `${reservation.date}T${reservation.time}:00`,
                note: reservation.notes || null
            };
            
            await createReservation(reservationData);
            toast.success('Đặt bàn thành công');
            setShowReservationModal(false);
            fetchTables();
            setReservation({
                date: '',
                time: '',
                numberOfGuests: '',
                notes: ''
            });
        } catch (error) {
            console.error('Error reserving table:', error);
            const errorMsg = error.response?.data?.message || error.response?.data || 'Đã có lỗi xảy ra';
            toast.error('Không thể đặt bàn: ' + errorMsg);
        }
    };

    const getStatusColor = (status) => {
        switch (status) {
            case 'AVAILABLE':
                return 'bg-green-100 border-green-500 hover:bg-green-200';
            case 'OCCUPIED':
                return 'bg-red-100 border-red-500 cursor-not-allowed';
            case 'RESERVED':
                return 'bg-yellow-100 border-yellow-500 cursor-not-allowed';
            default:
                return 'bg-gray-100 border-gray-500';
        }
    };

    const getStatusText = (status) => {
        switch (status) {
            case 'AVAILABLE':
                return 'Trống';
            case 'OCCUPIED':
                return 'Có khách';
            case 'RESERVED':
                return 'Đã đặt';
            default:
                return status;
        }
    };

    const today = new Date().toISOString().split('T')[0];

    return (
        <div className="min-h-screen bg-[#fcf7f2] p-6">
            <div className="max-w-7xl mx-auto">
                <div className="bg-white rounded-lg shadow-sm p-6">
                    <h1 className="text-2xl font-bold mb-6">Sơ đồ bàn</h1>

                    {/* Table Map */}
                    <div className="relative">
                        {/* Legend */}
                        <div className="flex gap-4 mb-6">
                            <div className="flex items-center gap-2">
                                <div className="w-4 h-4 bg-green-100 border border-green-500 rounded"></div>
                                <span>Trống</span>
                            </div>
                            <div className="flex items-center gap-2">
                                <div className="w-4 h-4 bg-yellow-100 border border-yellow-500 rounded"></div>
                                <span>Đã đặt</span>
                            </div>
                            <div className="flex items-center gap-2">
                                <div className="w-4 h-4 bg-red-100 border border-red-500 rounded"></div>
                                <span>Có khách</span>
                            </div>
                        </div>

                        {/* Tables Grid */}
                        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-6">
                            {tables.map(table => (
                                <motion.div
                                    key={table.id}
                                    whileHover={{ scale: table.status === 'AVAILABLE' ? 1.05 : 1 }}
                                    className={`relative p-4 border-2 rounded-lg shadow-sm 
                                        ${getStatusColor(table.status)}
                                        transition-colors`}
                                    onClick={() => {
                                        if (table.status === 'AVAILABLE') {
                                            setSelectedTable(table);
                                            setShowReservationModal(true);
                                        }
                                    }}
                                >
                                    <div className="text-center">
                                        <div className="font-bold text-lg mb-2">{table.name}</div>
                                        {table.location && (
                                            <div className="flex items-center justify-center gap-1 text-sm text-gray-600 mb-2">
                                                <span>{table.location}</span>
                                            </div>
                                        )}
                                        <div className="inline-block px-3 py-1 rounded-full text-xs font-medium 
                                            bg-opacity-50 text-gray-800">
                                            {getStatusText(table.status)}
                                        </div>
                                    </div>

                                    {table.status === 'AVAILABLE' && (
                                        <div className="absolute inset-0 flex items-center justify-center opacity-0 hover:opacity-100 bg-black/5 rounded-lg transition-opacity">
                                            <Button size="sm">Đặt bàn</Button>
                                        </div>
                                    )}
                                </motion.div>
                            ))}
                        </div>
                    </div>
                </div>
            </div>

            {/* Reservation Modal */}
            {showReservationModal && selectedTable && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4">
                    <div className="bg-white rounded-lg max-w-md w-full p-6">
                        <h2 className="text-xl font-bold mb-4">Đặt {selectedTable.name}</h2>
                        <form onSubmit={handleReserve} className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium mb-1">Ngày đặt</label>
                                <input
                                    type="date"
                                    min={today}
                                    required
                                    className="w-full p-2 border rounded-md"
                                    value={reservation.date}
                                    onChange={(e) => setReservation({...reservation, date: e.target.value})}
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium mb-1">Thời gian</label>
                                <input
                                    type="time"
                                    required
                                    className="w-full p-2 border rounded-md"
                                    value={reservation.time}
                                    onChange={(e) => setReservation({...reservation, time: e.target.value})}
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium mb-1">Số lượng khách</label>
                                <input
                                    type="number"
                                    required
                                    min="1"
                                    className="w-full p-2 border rounded-md"
                                    value={reservation.numberOfGuests}
                                    onChange={(e) => setReservation({...reservation, numberOfGuests: e.target.value})}
                                    placeholder="Số người"
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium mb-1">Ghi chú</label>
                                <textarea
                                    className="w-full p-2 border rounded-md"
                                    rows="2"
                                    value={reservation.notes}
                                    onChange={(e) => setReservation({...reservation, notes: e.target.value})}
                                />
                            </div>

                            <div className="flex justify-end space-x-4 mt-6">
                                <Button 
                                    type="button"
                                    variant="outline"
                                    onClick={() => setShowReservationModal(false)}
                                >
                                    Hủy
                                </Button>
                                <Button type="submit">
                                    Xác nhận đặt bàn
                                </Button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

