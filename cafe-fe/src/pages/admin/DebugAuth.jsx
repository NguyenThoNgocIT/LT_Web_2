import { useEffect, useState } from 'react';

export default function DebugAuth() {
  const [authInfo, setAuthInfo] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem('token');
    const userStr = localStorage.getItem('user');
    const user = userStr ? JSON.parse(userStr) : null;

    setAuthInfo({
      hasToken: !!token,
      tokenPreview: token ? token.substring(0, 50) + '...' : 'NULL',
      user: user,
      roles: user?.roles || [],
      isAdmin: user?.roles?.includes('ADMIN') || user?.roles?.includes('ROOT')
    });
  }, []);

  const testAPI = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch('http://localhost:8088/api/admin/tables/1/status', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ status: 'OCCUPIED' })
      });
      
      console.log('Test API Response:', response.status, await response.json());
      alert('API test thành công! Kiểm tra console.');
    } catch (err) {
      console.error('Test API Error:', err);
      alert('API test thất bại! Kiểm tra console.');
    }
  };

  return (
    <div className="p-6 bg-white rounded-lg shadow max-w-2xl mx-auto mt-10">
      <h2 className="text-2xl font-bold mb-4">Debug Authentication</h2>
      
      {authInfo && (
        <div className="space-y-3">
          <div>
            <strong>Has Token:</strong> {authInfo.hasToken ? '✅ Yes' : '❌ No'}
          </div>
          <div>
            <strong>Token Preview:</strong> <code className="bg-gray-100 p-1 text-xs">{authInfo.tokenPreview}</code>
          </div>
          <div>
            <strong>User:</strong> <pre className="bg-gray-100 p-2 text-xs overflow-auto">{JSON.stringify(authInfo.user, null, 2)}</pre>
          </div>
          <div>
            <strong>Roles:</strong> {authInfo.roles.join(', ') || 'NONE'}
          </div>
          <div>
            <strong>Is Admin:</strong> {authInfo.isAdmin ? '✅ Yes' : '❌ No'}
          </div>
          
          <button 
            onClick={testAPI}
            className="mt-4 bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
          >
            Test API Call
          </button>
        </div>
      )}
    </div>
  );
}
