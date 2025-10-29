import { useEffect, useState } from 'react';
import { getMyReservations } from '../../api/table.api';
import { toast } from 'sonner';

export default function ReservationHistory() {
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchReservations();
  }, []);

  const fetchReservations = async () => {
    setLoading(true);
    try {
      const data = await getMyReservations();
      setReservations(data);
    } catch (error) {
      toast.error('Không thể tải lịch sử đặt bàn');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#fcf7f2] p-6">
      <div className="max-w-4xl mx-auto">
        <div className="bg-white rounded-lg shadow-sm p-6">
          <h1 className="text-2xl font-bold mb-6">Lịch sử đặt bàn</h1>
          {loading ? (
            <div>Đang tải...</div>
          ) : reservations.length === 0 ? (
            <div className="text-gray-500">Bạn chưa có lịch sử đặt bàn nào.</div>
          ) : (
            <table className="w-full border">
              <thead>
                <tr className="bg-brown-50">
                  <th className="p-2">Bàn</th>
                  <th className="p-2">Thời gian</th>
                  <th className="p-2">Ghi chú</th>
                  <th className="p-2">Trạng thái</th>
                </tr>
              </thead>
              <tbody>
                {reservations.map(rsv => (
                  <tr key={rsv.id} className="border-t">
                    <td className="p-2">{rsv.table?.tableNumber || '-'}</td>
                    <td className="p-2">{rsv.reservationTime ? new Date(rsv.reservationTime).toLocaleString() : '-'}</td>
                    <td className="p-2">{rsv.note}</td>
                    <td className="p-2">{rsv.status}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  );
}
