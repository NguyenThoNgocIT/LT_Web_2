import { useState, useEffect } from 'react';
import { Clock, Coffee, Users, DollarSign, TrendingUp } from 'lucide-react';
import { getOrders } from '../../api/order.api';
import { getAllProducts } from '../../api/product.api';

export default function Dashboard() {
  // Revenue report state
  const [revenueType, setRevenueType] = useState('day');
  const [revenueData, setRevenueData] = useState({ labels: [], values: [], total: 0 });
  const fetchRevenue = async (type = revenueType) => {
    try {
      const res = await fetch(`/api/admin/revenue?type=${type}`);
      const data = await res.json();
      setRevenueData(data);
    } catch (err) {
      setRevenueData({ labels: [], values: [], total: 0 });
    }
  };
  useEffect(() => { fetchRevenue(revenueType); }, [revenueType]);
  const [stats, setStats] = useState({
    totalOrders: 0,
    totalRevenue: 0,
    totalCustomers: 0,
    averageOrderValue: 0
  });

  const [recentOrders, setRecentOrders] = useState([]);
  const [topProducts, setTopProducts] = useState([]);

  const fetchStats = async () => {
    try {
      // Derive simple stats from available endpoints
      const orders = await getOrders();
      const totalOrders = orders.length;
      const totalRevenue = orders.reduce((sum, o) => sum + (o.total || 0), 0);
      const customersSet = new Set(
        orders
          .map(o => (o.customer && (o.customer.email || o.customer.username || o.customer.name)))
          .filter(Boolean)
      );
      const totalCustomers = customersSet.size;
      const averageOrderValue = totalOrders > 0 ? Math.round(totalRevenue / totalOrders) : 0;

      setStats({ totalOrders, totalRevenue, totalCustomers, averageOrderValue });
    } catch (error) {
      console.error('Error deriving stats:', error);
    }
  };

  const fetchRecentOrders = async () => {
    try {
      const orders = await getOrders();
      // Sort by createdAt desc if present; fallback to id desc
      const sorted = [...orders].sort((a, b) => {
        const da = a.createdAt ? new Date(a.createdAt).getTime() : 0;
        const db = b.createdAt ? new Date(b.createdAt).getTime() : 0;
        if (db !== da) return db - da;
        return (b.id || 0) - (a.id || 0);
      });
      setRecentOrders(sorted.slice(0, 5));
    } catch (error) {
      console.error('Error fetching recent orders:', error);
    }
  };

  const fetchTopProducts = async () => {
    try {
      // Backend doesn't expose top-products; show first few products as placeholder
      const products = await getAllProducts();
      setTopProducts(products.slice(0, 5).map(p => ({ ...p, soldCount: p.soldCount || 0 })));
    } catch (error) {
      console.error('Error fetching products:', error);
    }
  };

  useEffect(() => {
    fetchStats();
    fetchRecentOrders();
    fetchTopProducts();
  }, []);

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-6">Dashboard</h1>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-500 text-sm">Tổng đơn hàng</p>
              <h3 className="text-2xl font-bold">{stats.totalOrders}</h3>
            </div>
      {/* Revenue Report Section */}
      <div className="bg-white rounded-xl shadow-lg p-8 mt-10 flex flex-col items-center max-w-3xl mx-auto">
        <div className="flex flex-col md:flex-row items-center justify-between w-full mb-6 gap-4">
          <h2 className="text-2xl font-bold text-blue-700">Báo cáo doanh thu</h2>
          <select value={revenueType} onChange={e => setRevenueType(e.target.value)} className="border border-blue-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400">
            <option value="day">Theo ngày</option>
            <option value="week">Theo tuần</option>
            <option value="month">Theo tháng</option>
          </select>
        </div>
        <div className="mb-4 text-lg">Tổng doanh thu: <span className="font-extrabold text-green-600 text-2xl">{revenueData.total?.toLocaleString()}₫</span></div>
        <div className="overflow-x-auto w-full mb-6">
          <table className="min-w-full text-sm border border-gray-200 rounded-lg overflow-hidden">
            <thead>
              <tr className="bg-blue-50">
                <th className="text-left p-3 font-semibold">{revenueType === 'day' ? 'Ngày' : revenueType === 'week' ? 'Tuần' : 'Tháng'}</th>
                <th className="text-right p-3 font-semibold">Doanh thu</th>
              </tr>
            </thead>
            <tbody>
              {revenueData.labels.map((label, idx) => (
                <tr key={label} className={idx % 2 ? 'bg-gray-50' : 'bg-white'}>
                  <td className="p-3">{label}</td>
                  <td className="p-3 text-right text-blue-700 font-bold">{revenueData.values[idx]?.toLocaleString()}₫</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {/* Bar chart - centered, spaced, rounded, soft color */}
        <div className="w-full flex flex-col items-center">
          <div className="flex items-end justify-center h-36 gap-2 w-full">
            {revenueData.values.map((v, idx) => {
              const max = Math.max(...revenueData.values, 1);
              const height = Math.round((v / max) * 120);
              return (
                <div key={idx} className="bg-blue-300 rounded-t-lg transition-all duration-300" style={{height: `${height}px`, width: '24px'}} title={revenueData.labels[idx] + ': ' + v.toLocaleString() + '₫'}></div>
              );
            })}
          </div>
          <div className="flex justify-center text-xs mt-2 w-full gap-2">
            {revenueData.labels.map((label, idx) => (
              <span key={idx} className="w-24 text-center truncate">{label}</span>
            ))}
          </div>
        </div>
      </div>
            <Coffee className="w-8 h-8 text-blue-500" />
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-500 text-sm">Doanh thu</p>
              <h3 className="text-2xl font-bold">{stats.totalRevenue.toLocaleString()}đ</h3>
            </div>
            <DollarSign className="w-8 h-8 text-green-500" />
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-500 text-sm">Khách hàng</p>
              <h3 className="text-2xl font-bold">{stats.totalCustomers}</h3>
            </div>
            <Users className="w-8 h-8 text-purple-500" />
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-500 text-sm">Giá trị TB/đơn</p>
              <h3 className="text-2xl font-bold">{stats.averageOrderValue.toLocaleString()}đ</h3>
            </div>
            <TrendingUp className="w-8 h-8 text-orange-500" />
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Orders */}
        <div className="bg-white rounded-lg shadow">
          <div className="p-6 border-b">
            <h2 className="text-xl font-semibold">Đơn hàng gần đây</h2>
          </div>
          <div className="p-6">
            <div className="space-y-4">
              {recentOrders.map((order) => (
                <div key={order.id} className="flex items-center justify-between">
                  <div>
                    <p className="font-medium">{order.table ? `Bàn ${order.table.tableNumber}` : 'Đơn hàng'}</p>
                    <p className="text-sm text-gray-500">
                      {order.items?.length || 0} món - {(order.total || 0).toLocaleString()}đ
                    </p>
                  </div>
                  <div className="flex items-center text-gray-500">
                    <Clock className="w-4 h-4 mr-2" />
                    <span className="text-sm">
                      {new Date(order.createdAt || order.orderTime || Date.now()).toLocaleTimeString()}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Top Products */}
        <div className="bg-white rounded-lg shadow">
          <div className="p-6 border-b">
            <h2 className="text-xl font-semibold">Sản phẩm bán chạy</h2>
          </div>
          <div className="p-6">
            <div className="space-y-4">
              {topProducts.map((product) => (
                <div key={product.id} className="flex items-center justify-between">
                  <div>
                    <p className="font-medium">{product.name}</p>
                    <p className="text-sm text-gray-500">
                      {product.category} - {product.price.toLocaleString()}đ
                    </p>
                  </div>
                  <div className="text-sm font-medium">
                    Đã bán: {product.soldCount}
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}