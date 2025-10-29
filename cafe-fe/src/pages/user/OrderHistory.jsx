import { useEffect, useState } from 'react';
import { getMyOrders } from '../../api/order.api';
import { toast } from 'sonner';

export default function OrderHistory() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    setLoading(true);
    try {
      const data = await getMyOrders();
      setOrders(data);
    } catch (error) {
      toast.error('Không thể tải lịch sử đơn hàng');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#fcf7f2] p-6">
      <div className="max-w-4xl mx-auto">
        <div className="bg-white rounded-lg shadow-sm p-6">
          <h1 className="text-2xl font-bold mb-6">Lịch sử đơn hàng</h1>
          {loading ? (
            <div>Đang tải...</div>
          ) : orders.length === 0 ? (
            <div className="text-gray-500">Bạn chưa có đơn hàng nào.</div>
          ) : (
            <table className="w-full border">
              <thead>
                <tr className="bg-brown-50">
                  <th className="p-2">Mã đơn</th>
                  <th className="p-2">Thời gian</th>
                  <th className="p-2">Tổng tiền</th>
                  <th className="p-2">Trạng thái</th>
                </tr>
              </thead>
              <tbody>
                {orders.map(order => (
                  <tr key={order.id} className="border-t">
                    <td className="p-2">{order.id}</td>
                    <td className="p-2">{order.createdAt ? new Date(order.createdAt).toLocaleString() : '-'}</td>
                    <td className="p-2">{order.total ? order.total.toLocaleString() + '₫' : '-'}</td>
                    <td className="p-2">{order.status}</td>
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
