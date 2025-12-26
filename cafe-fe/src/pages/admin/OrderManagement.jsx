import { useState, useEffect, useMemo } from 'react';
import {  Search } from 'lucide-react';
import { Button } from '../../components/ui/button';
import { getOrders, updateOrderStatus } from '../../api/order.api';
import { toast } from 'sonner';

export default function OrderManagement() {
  const [orders, setOrders] = useState([]);
  const [q, setQ] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [page, setPage] = useState(1);
  const pageSize = 10;

  const fetchOrders = async () => {
    try {
      const data = await getOrders();
      setOrders(data.reverse());
    } catch (error) {
      console.error('Error fetching orders:', error);
      toast.error('Không thể tải danh sách đơn hàng');
    }
  };

  useEffect(() => { fetchOrders(); }, []);

  const handleStatusChange = async (orderId, newStatus) => {
    console.log('🔄 Updating order status:', { orderId, newStatus });
    try {
      const result = await updateOrderStatus(orderId, newStatus);
      console.log('✅ Status update result:', result);
      toast.success('Cập nhật trạng thái thành công');
      fetchOrders();
    } catch (error) {
      console.error('❌ Error updating order status:', error);
      console.error('Error response:', error.response);
      const errorMsg = error.response?.data?.message || error.response?.data || 'Không thể cập nhật trạng thái';
      toast.error('Lỗi: ' + errorMsg);
    }
  };

  const filtered = useMemo(() => {
    return orders.filter(o => {
      if (statusFilter !== 'ALL' && o.status !== statusFilter) return false;
      if (!q) return true;
      const s = q.toLowerCase();
      const tableNumber = (o.table && o.table.tableNumber) ? String(o.table.tableNumber) : '';
      const customer = o.customer ? (o.customer.fullName || o.customer.username || '') : '';
      return tableNumber.includes(s) || customer.toLowerCase().includes(s) || String(o.id).includes(s);
    });
  }, [orders, statusFilter, q]);

  const totalPages = Math.max(1, Math.ceil(filtered.length / pageSize));
  const pageData = filtered.slice((page - 1) * pageSize, page * pageSize);

  const statusOptions = ['ALL', 'PENDING', 'PREPARING', 'SERVED', 'COMPLETED', 'CANCELLED'];

  const getStatusColor = (status) => {
    switch (status) {
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      case 'PREPARING':
        return 'bg-blue-100 text-blue-800';
      case 'SERVED':
        return 'bg-purple-100 text-purple-800';
      case 'COMPLETED':
        return 'bg-green-100 text-green-800';
      case 'CANCELLED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusText = (status) => {
    switch (status) {
      case 'PENDING':
        return 'Chờ xử lý';
      case 'PREPARING':
        return 'Đang chế biến';
      case 'SERVED':
        return 'Đã phục vụ';
      case 'COMPLETED':
        return 'Hoàn thành';
      case 'CANCELLED':
        return 'Đã hủy';
      default:
        return status;
    }
  };

  const getAvailableActions = (currentStatus) => {
    switch (currentStatus) {
      case 'PENDING':
        return [
          { status: 'PREPARING', label: 'Bắt đầu chế biến', color: 'bg-blue-600 hover:bg-blue-700' },
          { status: 'CANCELLED', label: 'Hủy', color: 'bg-red-600 hover:bg-red-700' }
        ];
      case 'PREPARING':
        return [
          { status: 'SERVED', label: 'Đã phục vụ', color: 'bg-purple-600 hover:bg-purple-700' },
          { status: 'CANCELLED', label: 'Hủy', color: 'bg-red-600 hover:bg-red-700' }
        ];
      case 'SERVED':
        return [
          { status: 'COMPLETED', label: 'Hoàn thành', color: 'bg-green-600 hover:bg-green-700' }
        ];
      case 'COMPLETED':
      case 'CANCELLED':
        return [];
      default:
        return [];
    }
  };

  return (
    <div className="p-6">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">Quản lý đơn hàng</h1>

        <div className="flex items-center space-x-3">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              className="pl-10 pr-3 py-2 border rounded-lg w-72"
              placeholder="Tìm theo bàn, khách hoặc ID"
              value={q}
              onChange={(e) => { setQ(e.target.value); setPage(1); }}
            />
          </div>

          <select className="border rounded-lg p-2" value={statusFilter} onChange={(e) => { setStatusFilter(e.target.value); setPage(1); }}>
            {statusOptions.map(s => <option key={s} value={s}>{s === 'ALL' ? 'Tất cả' : getStatusText(s)}</option>)}
          </select>

          <Button onClick={() => fetchOrders()}>Làm mới</Button>
        </div>
      </div>

      <div className="bg-white rounded-lg shadow overflow-auto">
        <table className="min-w-full text-sm">
          <thead className="bg-gray-50">
            <tr>
              <th className="p-3 text-left">ID</th>
              <th className="p-3 text-left">Bàn</th>
              <th className="p-3 text-left">Khách</th>
              <th className="p-3 text-left">Món</th>
              <th className="p-3 text-right">Tổng</th>
              <th className="p-3 text-left">Trạng thái</th>
              <th className="p-3 text-left">Thời gian</th>
              <th className="p-3 text-right">Hành động</th>
            </tr>
          </thead>
          <tbody>
            {pageData.map(order => (
              <tr key={order.id} className="border-t hover:bg-gray-50">
                <td className="p-3 font-medium">#{order.id}</td>
                <td className="p-3">{order.tableName || 'Mang đi'}</td>
                <td className="p-3">{order.customerName || 'Khách vãng lai'}</td>
                <td className="p-3">
                  <span className="text-sm text-gray-600">{order.items?.length || 0} món</span>
                </td>
                <td className="p-3 text-right font-semibold">{order.totalAmount?.toLocaleString() || 0}₫</td>
                <td className="p-3">
                  <span className={`px-3 py-1 rounded-full text-xs font-medium ${getStatusColor(order.status)}`}>
                    {getStatusText(order.status)}
                  </span>
                </td>
                <td className="p-3 text-sm text-gray-600">{new Date(order.createdAt).toLocaleString('vi-VN')}</td>
                <td className="p-3 text-right">
                  <div className="flex justify-end gap-2">
                    {getAvailableActions(order.status).map((action) => (
                      <Button 
                        key={action.status}
                        size="sm"
                        className={`text-white ${action.color}`}
                        onClick={() => handleStatusChange(order.id, action.status)}
                      >
                        {action.label}
                      </Button>
                    ))}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="flex items-center justify-between mt-4">
        <div>Hiển thị { (page - 1) * pageSize + 1 } - { Math.min(page * pageSize, filtered.length) } / { filtered.length }</div>
        <div className="space-x-2">
          <Button onClick={() => setPage(p => Math.max(1, p - 1))} disabled={page === 1}>Trước</Button>
          <span>Trang {page} / {totalPages}</span>
          <Button onClick={() => setPage(p => Math.min(totalPages, p + 1))} disabled={page === totalPages}>Tiếp</Button>
        </div>
      </div>
    </div>
  );
}