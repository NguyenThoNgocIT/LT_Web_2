import { useState, useEffect } from 'react';
import { Plus, Pencil, Trash2, Upload, X } from 'lucide-react';
import { Button } from '../../components/ui/button';
import { getAllProducts, createProduct, updateProduct, deleteProduct } from '../../api/product.api';
import { uploadImage } from '../../api/upload.api';
import { toast } from 'sonner';

export default function MenuManagement() {
  const [products, setProducts] = useState([]);
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    price: '',
    category: '',
    description: '',
    imageUrl: '',
  });
  const [imageFile, setImageFile] = useState(null);
  const [imagePreview, setImagePreview] = useState(null);
  const [isUploading, setIsUploading] = useState(false);

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      if (file.size > 5 * 1024 * 1024) { // 5MB
        toast.error('Kích thước ảnh không được vượt quá 5MB');
        return;
      }
      setImageFile(file);
      // Tạo preview
      const reader = new FileReader();
      reader.onloadend = () => {
        setImagePreview(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleImageUpload = async () => {
    if (!imageFile) return null;
    
    setIsUploading(true);
    try {
      console.log('Uploading image:', imageFile.name);
      const imageUrl = await uploadImage(imageFile, 'products');
      console.log('Upload successful, URL:', imageUrl);
      toast.success('Upload ảnh thành công');
      return imageUrl;
    } catch (error) {
      console.error('Error uploading image:', error);
      toast.error('Lỗi upload ảnh: ' + error.message);
      return null;
    } finally {
      setIsUploading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // Upload ảnh nếu có
      let imageUrl = formData.imageUrl;
      if (imageFile) {
        imageUrl = await handleImageUpload();
        if (!imageUrl && imageFile) {
          // Upload failed
          return;
        }
      }

      const productData = {
        ...formData,
        imageUrl: imageUrl || formData.imageUrl || null,
      };

      console.log('Submitting product data:', productData);

      if (editingProduct) {
        await updateProduct(editingProduct.id, productData);
        toast.success('Cập nhật món thành công');
        setIsEditModalOpen(false);
        setEditingProduct(null);
      } else {
        await createProduct(productData);
        toast.success('Thêm món thành công');
        setIsAddModalOpen(false);
      }
      fetchProducts();
      resetForm();
    } catch (error) {
      console.error('Error saving product:', error);
      toast.error('Lỗi: ' + (error.response?.data || 'Đã có lỗi xảy ra'));
    }
  };

  const resetForm = () => {
    setFormData({ name: '', price: '', category: '', description: '', imageUrl: '' });
    setImageFile(null);
    setImagePreview(null);
  };

  const fetchProducts = async () => {
    try {
      const data = await getAllProducts();
      console.log('Products data:', data);
      console.log('First product imageUrl:', data[0]?.imageUrl);
      setProducts(data);
    } catch (error) {
      console.error('Error fetching products:', error);
      toast.error('Lỗi tải danh sách sản phẩm');
    }
  };

  useEffect(() => {
    fetchProducts();
  }, []);

  const handleDelete = async (id) => {
    if (window.confirm('Bạn có chắc muốn xóa sản phẩm này?')) {
      try {
        await deleteProduct(id);
        toast.success('Xóa sản phẩm thành công');
        fetchProducts();
      } catch (error) {
        console.error('Error deleting product:', error);
        toast.error('Lỗi xóa sản phẩm');
      }
    }
  };

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Quản lý Menu</h1>
        <Button onClick={() => setIsAddModalOpen(true)}>
          <Plus className="w-4 h-4 mr-2" />
          Thêm món mới
        </Button>
      </div>

      {/* Products Table */}
      <div className="bg-white rounded-lg shadow">
        <table className="min-w-full">
          <thead>
            <tr className="border-b">
              <th className="px-6 py-3 text-left">Hình ảnh</th>
              <th className="px-6 py-3 text-left">Tên món</th>
              <th className="px-6 py-3 text-left">Danh mục</th>
              <th className="px-6 py-3 text-left">Giá</th>
              <th className="px-6 py-3 text-left">Mô tả</th>
              <th className="px-6 py-3 text-right">Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {products.map((product) => (
              <tr key={product.id} className="border-b">
                <td className="px-6 py-4">
                  {product.imageUrl ? (
                    <div>
                      <img 
                        src={product.imageUrl} 
                        alt={product.name}
                        className="w-16 h-16 object-cover rounded"
                        onError={(e) => {
                          console.error('Image load error for:', product.imageUrl);
                          e.target.src = '';
                          e.target.style.display = 'none';
                        }}
                      />
                      <small className="text-xs text-gray-400 block mt-1">
                        {product.imageUrl.substring(0, 30)}...
                      </small>
                    </div>
                  ) : (
                    <div className="w-16 h-16 bg-gray-200 rounded flex items-center justify-center text-gray-400">
                      No image
                    </div>
                  )}
                </td>
                <td className="px-6 py-4">{product.name}</td>
                <td className="px-6 py-4">{product.category}</td>
                <td className="px-6 py-4">{product.price.toLocaleString()}đ</td>
                <td className="px-6 py-4">{product.description}</td>
                <td className="px-6 py-4 text-right">
                  <Button 
                    variant="ghost" 
                    size="sm" 
                    className="mr-2"
                    onClick={() => {
                      setEditingProduct(product);
                      setFormData({
                        name: product.name,
                        price: product.price,
                        category: product.category,
                        description: product.description || '',
                        imageUrl: product.imageUrl || '',
                      });
                      setImagePreview(null);
                      setImageFile(null);
                      setIsEditModalOpen(true);
                    }}
                  >
                    <Pencil className="w-4 h-4" />
                  </Button>
                  <Button 
                    variant="ghost" 
                    size="sm"
                    onClick={() => handleDelete(product.id)}
                  >
                    <Trash2 className="w-4 h-4 text-red-500" />
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Add Product Modal */}
      {isAddModalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg w-full max-w-md">
            <h2 className="text-xl font-bold mb-4">Thêm món mới</h2>
            <form onSubmit={handleSubmit}>
              <div className="space-y-4">
                <div>
                  <label className="block mb-1">Tên món</label>
                  <input
                    type="text"
                    className="w-full p-2 border rounded"
                    value={formData.name}
                    onChange={(e) => setFormData({...formData, name: e.target.value})}
                    required
                  />
                </div>
                <div>
                  <label className="block mb-1">Danh mục</label>
                  <select
                    className="w-full p-2 border rounded"
                    value={formData.category}
                    onChange={(e) => setFormData({...formData, category: e.target.value})}
                    required
                  >
                    <option value="">Chọn danh mục</option>
                    <option value="Cà phê">Cà phê</option>
                    <option value="Trà">Trà</option>
                    <option value="Đồ uống đặc biệt">Đồ uống đặc biệt</option>
                  </select>
                </div>
                <div>
                  <label className="block mb-1">Giá</label>
                  <input
                    type="number"
                    className="w-full p-2 border rounded"
                    value={formData.price}
                    onChange={(e) => setFormData({...formData, price: e.target.value})}
                    required
                  />
                </div>
                <div>
                  <label className="block mb-1">Mô tả</label>
                  <textarea
                    className="w-full p-2 border rounded"
                    value={formData.description}
                    onChange={(e) => setFormData({...formData, description: e.target.value})}
                    rows="3"
                  />
                </div>
                
                {/* Image Upload Section */}
                <div>
                  <label className="block mb-1">Hình ảnh sản phẩm</label>
                  <div className="border-2 border-dashed border-gray-300 rounded-lg p-4">
                    {imagePreview ? (
                      <div className="relative">
                        <img 
                          src={imagePreview} 
                          alt="Preview" 
                          className="w-full h-48 object-cover rounded"
                        />
                        <button
                          type="button"
                          onClick={() => {
                            setImageFile(null);
                            setImagePreview(null);
                          }}
                          className="absolute top-2 right-2 bg-red-500 text-white rounded-full p-1 hover:bg-red-600"
                        >
                          <X className="w-4 h-4" />
                        </button>
                      </div>
                    ) : (
                      <div className="text-center">
                        <Upload className="w-12 h-12 mx-auto text-gray-400 mb-2" />
                        <label className="cursor-pointer">
                          <span className="text-blue-500 hover:text-blue-600">Chọn ảnh</span>
                          <input
                            type="file"
                            accept="image/*"
                            className="hidden"
                            onChange={handleImageChange}
                          />
                        </label>
                        <p className="text-sm text-gray-500 mt-1">Kích thước tối đa: 5MB</p>
                      </div>
                    )}
                  </div>
                </div>
              </div>
              <div className="flex justify-end space-x-4 mt-6">
                <Button 
                  type="button" 
                  variant="outline"
                  onClick={() => {
                    setIsAddModalOpen(false);
                    resetForm();
                  }}
                >
                  Hủy
                </Button>
                <Button type="submit" disabled={isUploading}>
                  {isUploading ? 'Đang upload...' : 'Thêm món'}
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Edit Product Modal */}
      {isEditModalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg w-full max-w-md">
            <h2 className="text-xl font-bold mb-4">Chỉnh sửa món</h2>
            <form onSubmit={handleSubmit}>
              <div className="space-y-4">
                <div>
                  <label className="block mb-1">Tên món</label>
                  <input
                    type="text"
                    className="w-full p-2 border rounded"
                    value={formData.name}
                    onChange={(e) => setFormData({...formData, name: e.target.value})}
                    required
                  />
                </div>
                <div>
                  <label className="block mb-1">Danh mục</label>
                  <select
                    className="w-full p-2 border rounded"
                    value={formData.category}
                    onChange={(e) => setFormData({...formData, category: e.target.value})}
                    required
                  >
                    <option value="">Chọn danh mục</option>
                    <option value="Cà phê">Cà phê</option>
                    <option value="Trà">Trà</option>
                    <option value="Đồ uống đặc biệt">Đồ uống đặc biệt</option>
                  </select>
                </div>
                <div>
                  <label className="block mb-1">Giá</label>
                  <input
                    type="number"
                    className="w-full p-2 border rounded"
                    value={formData.price}
                    onChange={(e) => setFormData({...formData, price: e.target.value})}
                    required
                  />
                </div>
                <div>
                  <label className="block mb-1">Mô tả</label>
                  <textarea
                    className="w-full p-2 border rounded"
                    value={formData.description}
                    onChange={(e) => setFormData({...formData, description: e.target.value})}
                    rows="3"
                  />
                </div>
                
                {/* Image Upload Section */}
                <div>
                  <label className="block mb-1">Hình ảnh sản phẩm</label>
                  <div className="border-2 border-dashed border-gray-300 rounded-lg p-4">
                    {imagePreview || formData.imageUrl ? (
                      <div className="relative">
                        <img 
                          src={imagePreview || formData.imageUrl} 
                          alt="Preview" 
                          className="w-full h-48 object-cover rounded"
                        />
                        <button
                          type="button"
                          onClick={() => {
                            setImageFile(null);
                            setImagePreview(null);
                            setFormData({...formData, imageUrl: ''});
                          }}
                          className="absolute top-2 right-2 bg-red-500 text-white rounded-full p-1 hover:bg-red-600"
                        >
                          <X className="w-4 h-4" />
                        </button>
                      </div>
                    ) : (
                      <div className="text-center">
                        <Upload className="w-12 h-12 mx-auto text-gray-400 mb-2" />
                        <label className="cursor-pointer">
                          <span className="text-blue-500 hover:text-blue-600">Chọn ảnh</span>
                          <input
                            type="file"
                            accept="image/*"
                            className="hidden"
                            onChange={handleImageChange}
                          />
                        </label>
                        <p className="text-sm text-gray-500 mt-1">Kích thước tối đa: 5MB</p>
                      </div>
                    )}
                  </div>
                </div>
              </div>
              <div className="flex justify-end space-x-4 mt-6">
                <Button 
                  type="button" 
                  variant="outline"
                  onClick={() => {
                    setIsEditModalOpen(false);
                    setEditingProduct(null);
                    resetForm();
                  }}
                >
                  Hủy
                </Button>
                <Button type="submit" disabled={isUploading}>
                  {isUploading ? 'Đang cập nhật...' : 'Cập nhật'}
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
