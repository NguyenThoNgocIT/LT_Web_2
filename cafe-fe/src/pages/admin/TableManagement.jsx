import { useState, useEffect } from 'react';
import { Plus, Users, Grid3x3, List, MapPin } from 'lucide-react';
import { Button } from '../../components/ui/button';
import { getAllTables, createTable, updateTableStatus } from '../../api/table.api';
import { toast } from 'sonner';

export default function TableManagement() {
  const [tables, setTables] = useState([]);
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [viewMode, setViewMode] = useState('grid'); // 'grid' or 'list'
  const [formData, setFormData] = useState({
    name: '',
    location: '',
    status: 'AVAILABLE'
  });

  const fetchTables = async () => {
    try {
      const data = await getAllTables();
      setTables(data);
    } catch (error) {
      console.error('Error fetching tables:', error);
      toast.error('Lỗi tải danh sách bàn');
    }
  };

  useEffect(() => {
    fetchTables();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await createTable(formData);
      toast.success('Thêm bàn thành công');
      setIsAddModalOpen(false);
      fetchTables();
    } catch (error) {
      console.error('Error adding table:', error);
      toast.error('Lỗi thêm bàn: ' + (error.response?.data || 'Đã có lỗi xảy ra'));
    }
  };

  const handleStatusChange = async (tableId, newStatus) => {
    try {
      await updateTableStatus(tableId, newStatus);
      toast.success('Cập nhật trạng thái bàn thành công');
      fetchTables();
    } catch (error) {
      console.error('Error updating table status:', error);
      toast.error('Lỗi cập nhật trạng thái bàn');
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'AVAILABLE':
        return 'bg-green-100 text-green-800';
      case 'OCCUPIED':
        return 'bg-red-100 text-red-800';
      case 'RESERVED':
        return 'bg-yellow-100 text-yellow-800';
      case 'COMPLETED':
        return 'bg-blue-100 text-blue-800';
      default:
        return 'bg-gray-100 text-gray-800';
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
      case 'COMPLETED':
        return 'Hoàn thành';
      default:
        return status;
    }
  };

  const getAvailableActions = (currentStatus) => {
    switch (currentStatus) {
      case 'AVAILABLE':
        return [
          { status: 'RESERVED', label: 'Đặt trước', variant: 'outline' },
          { status: 'OCCUPIED', label: 'Nhận khách', variant: 'default' }
        ];
      case 'RESERVED':
        return [
          { status: 'OCCUPIED', label: 'Khách đến', variant: 'default' },
          { status: 'AVAILABLE', label: 'Hủy đặt', variant: 'outline' }
        ];
      case 'OCCUPIED':
        return [
          { status: 'COMPLETED', label: 'Thanh toán', variant: 'default' }
        ];
      case 'COMPLETED':
        return [
          { status: 'AVAILABLE', label: 'Dọn bàn xong', variant: 'default' }
        ];
      default:
        return [];
    }
  };

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Quản lý Bàn</h1>
        <div className="flex gap-3">
          <div className="flex bg-gray-100 rounded-lg p-1">
            <button
              className={`px-3 py-1 rounded ${viewMode === 'grid' ? 'bg-white shadow' : ''}`}
              onClick={() => setViewMode('grid')}
            >
              <Grid3x3 className="w-4 h-4" />
            </button>
            <button
              className={`px-3 py-1 rounded ${viewMode === 'list' ? 'bg-white shadow' : ''}`}
              onClick={() => setViewMode('list')}
            >
              <List className="w-4 h-4" />
            </button>
          </div>
          <Button onClick={() => setIsAddModalOpen(true)}>
            <Plus className="w-4 h-4 mr-2" />
            Thêm bàn mới
          </Button>
        </div>
      </div>

      {/* Grid View */}
      {viewMode === 'grid' && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {tables.map((table) => (
            <div key={table.id} className="bg-white rounded-lg shadow p-6 hover:shadow-lg transition-shadow">
              <div className="flex justify-between items-start mb-4">
                <h3 className="text-xl font-bold">{table.name}</h3>
                <span className={`px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(table.status)}`}>
                  {getStatusText(table.status)}
                </span>
              </div>
              
              <div className="space-y-2 mb-4">
                {table.location && (
                  <div className="flex items-center text-gray-600">
                    <MapPin className="w-4 h-4 mr-2" />
                    <span>{table.location}</span>
                  </div>
                )}
              </div>

              <div className="flex flex-wrap gap-2">
                {getAvailableActions(table.status).map((action) => (
                  <Button
                    key={action.status}
                    variant={action.variant}
                    size="sm"
                    onClick={() => handleStatusChange(table.id, action.status)}
                  >
                    {action.label}
                  </Button>
                ))}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* List View */}
      {viewMode === 'list' && (
        <div className="bg-white rounded-lg shadow overflow-hidden">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Tên bàn
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Vị trí
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Trạng thái
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Thao tác
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {tables.map((table) => (
                <tr key={table.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <Users className="w-5 h-5 mr-2 text-gray-400" />
                      <span className="text-sm font-medium text-gray-900">{table.name}</span>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center text-sm text-gray-500">
                      <MapPin className="w-4 h-4 mr-2" />
                      {table.location || '-'}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`px-3 py-1 rounded-full text-xs font-medium ${getStatusColor(table.status)}`}>
                      {getStatusText(table.status)}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm">
                    <div className="flex gap-2">
                      {getAvailableActions(table.status).map((action) => (
                        <Button
                          key={action.status}
                          variant={action.variant}
                          size="sm"
                          onClick={() => handleStatusChange(table.id, action.status)}
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
      )}

      {/* Add Table Modal */}
      {isAddModalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
          <div className="bg-white p-6 rounded-lg w-full max-w-md">
            <h2 className="text-xl font-bold mb-4">Thêm bàn mới</h2>
            <form onSubmit={handleSubmit}>
              <div className="space-y-4">
                <div>
                  <label className="block mb-1">Tên bàn</label>
                  <input
                    type="text"
                    className="w-full p-2 border rounded"
                    value={formData.name}
                    onChange={(e) => setFormData({...formData, name: e.target.value})}
                    required
                    placeholder="VD: Bàn 1, Bàn VIP..."
                  />
                </div>
                <div>
                  <label className="block mb-1">Vị trí</label>
                  <input
                    type="text"
                    className="w-full p-2 border rounded"
                    value={formData.location}
                    onChange={(e) => setFormData({...formData, location: e.target.value})}
                    placeholder="VD: Tầng 1, Gần cửa sổ..."
                  />
                </div>
              </div>
              <div className="flex justify-end space-x-4 mt-6">
                <Button 
                  type="button" 
                  variant="outline"
                  onClick={() => setIsAddModalOpen(false)}
                >
                  Hủy
                </Button>
                <Button type="submit">
                  Thêm bàn
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
