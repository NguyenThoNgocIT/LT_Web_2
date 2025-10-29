import { useState, useEffect } from 'react';
import { Button } from '../../components/ui/button';
import { CalendarIcon, Clock, Users } from 'lucide-react';
import { toast } from 'sonner';
import { getAvailableTables, reserveTable } from '../../api/table.api';

export default function TableReservation() {
    const [availableTables, setAvailableTables] = useState([]);
    const [selectedTable, setSelectedTable] = useState(null);
    const [reservationForm, setReservationForm] = useState({
        date: '',
        time: '',
        numberOfGuests: '',
        notes: ''
    });

    useEffect(() => {
        fetchAvailableTables();
    }, []);

    const fetchAvailableTables = async () => {
        try {
            const data = await getAvailableTables();
            setAvailableTables(data);
        } catch (error) {
            console.error('Error fetching tables:', error);
            toast.error('Không thể tải danh sách bàn trống');
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!selectedTable) {
            toast.error('Vui lòng chọn bàn');
            return;
        }

        try {
            const reservationData = {
                ...reservationForm,
                reservationTime: `${reservationForm.date}T${reservationForm.time}`
            };
            
            await reserveTable(selectedTable.id, reservationData);
            toast.success('Đặt bàn thành công');
            fetchAvailableTables(); // Refresh list
            setSelectedTable(null);
            setReservationForm({
                date: '',
                time: '',
                numberOfGuests: '',
                notes: ''
            });
        } catch (error) {
            console.error('Error reserving table:', error);
            toast.error('Không thể đặt bàn: ' + (error.response?.data || 'Đã có lỗi xảy ra'));
        }
    };

    const today = new Date().toISOString().split('T')[0];

    return (
        <div className="min-h-screen bg-[#fcf7f2] p-6">
            <div className="max-w-4xl mx-auto">
                <div className="bg-white rounded-lg shadow-sm p-6">
                    <h1 className="text-2xl font-bold mb-6">Đặt bàn</h1>

                    <form onSubmit={handleSubmit} className="space-y-6">
                        {/* Reservation Details */}
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <div>
                                <label className="block text-sm font-medium mb-2">
                                    Ngày đặt
                                </label>
                                <div className="relative">
                                    <CalendarIcon className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                                    <input
                                        type="date"
                                        min={today}
                                        required
                                        className="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
                                        value={reservationForm.date}
                                        onChange={(e) => setReservationForm({...reservationForm, date: e.target.value})}
                                    />
                                </div>
                            </div>

                            <div>
                                <label className="block text-sm font-medium mb-2">
                                    Thời gian
                                </label>
                                <div className="relative">
                                    <Clock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                                    <input
                                        type="time"
                                        required
                                        className="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
                                        value={reservationForm.time}
                                        onChange={(e) => setReservationForm({...reservationForm, time: e.target.value})}
                                    />
                                </div>
                            </div>

                            <div>
                                <label className="block text-sm font-medium mb-2">
                                    Số lượng khách
                                </label>
                                <div className="relative">
                                    <Users className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                                    <input
                                        type="number"
                                        min="1"
                                        required
                                        className="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
                                        value={reservationForm.numberOfGuests}
                                        onChange={(e) => setReservationForm({...reservationForm, numberOfGuests: e.target.value})}
                                    />
                                </div>
                            </div>

                            <div>
                                <label className="block text-sm font-medium mb-2">
                                    Ghi chú
                                </label>
                                <textarea
                                    className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
                                    rows="2"
                                    value={reservationForm.notes}
                                    onChange={(e) => setReservationForm({...reservationForm, notes: e.target.value})}
                                />
                            </div>
                        </div>

                        {/* Available Tables */}
                        <div>
                            <h3 className="text-lg font-semibold mb-4">Chọn bàn trống</h3>
                            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                                {availableTables.map(table => (
                                    <div
                                        key={table.id}
                                        onClick={() => setSelectedTable(table)}
                                        className={`p-4 border rounded-lg cursor-pointer transition-colors
                                            ${selectedTable?.id === table.id 
                                                ? 'border-primary bg-primary/5' 
                                                : 'hover:border-gray-400'}`}
                                    >
                                        <div className="text-center">
                                            <div className="font-bold">Bàn {table.tableNumber}</div>
                                            <div className="text-sm text-gray-600">{table.capacity} người</div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>

                        {/* Submit Button */}
                        <div className="flex justify-end">
                            <Button 
                                type="submit"
                                className="w-full md:w-auto"
                            >
                                Đặt bàn
                            </Button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}