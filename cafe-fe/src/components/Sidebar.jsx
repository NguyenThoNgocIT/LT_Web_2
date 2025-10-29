import React from 'react';
import { 
  Coffee,
  LayoutDashboard, 
  Users, 
  Settings, 
  ChefHat,
  ClipboardList,
  LogOut
} from 'lucide-react';
import { cn } from '../lib/utils';
import { Button } from './ui/button';
import { useNavigate } from 'react-router-dom';

const Sidebar = ({ className }) => {
  const navigate = useNavigate();
  const menuItems = [
    { icon: LayoutDashboard, label: 'Dashboard', path: '/admin' },
    { icon: Users, label: 'Quản lý người dùng', path: '/admin/users' },
    { icon: Coffee, label: 'Quản lý menu', path: '/admin/menu' },
    { icon: ChefHat, label: 'Quản lý bàn', path: '/admin/tables' },
    { icon: ClipboardList, label: 'Đơn đặt bàn', path: '/admin/orders' },
    { icon: Settings, label: 'Cài đặt', path: '/admin/settings' },
  ];

  return (
    <div className={cn("pb-12 min-h-screen w-64", className)}>
      <div className="space-y-4 py-4">
        <div className="px-3 py-2">
          <h2 className="mb-2 px-4 text-lg font-semibold">
            Cafe Admin
          </h2>
          <div className="space-y-1">
            {menuItems.map((item) => (
              <Button
                key={item.path}
                variant="ghost"
                className="w-full justify-start"
                onClick={() => navigate(item.path)}
              >
                <item.icon className="mr-2 h-4 w-4" />
                {item.label}
              </Button>
            ))}
          </div>
        </div>
      </div>
      <div className="px-3 py-2 absolute bottom-4 w-64">
        <Button
          variant="ghost"
          className="w-full justify-start text-red-500 hover:text-red-600 hover:bg-red-100"
          onClick={() => {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            navigate('/login');
          }}
        >
          <LogOut className="mr-2 h-4 w-4" />
          Đăng xuất
        </Button>
      </div>
    </div>
  );
};

export default Sidebar;