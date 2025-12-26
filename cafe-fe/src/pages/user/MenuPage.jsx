import { useState, useEffect } from 'react';
import { Button } from '../../components/ui/button';
import { Coffee, Wine, Soup, Plus, Minus, X } from 'lucide-react';
import { toast } from 'sonner';
import { getMenu } from '../../api/product.api';
import { createOrder } from '../../api/order.api';

const MenuPage = () => {
    const [products, setProducts] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState('ALL');
    // Load cart from localStorage on mount
    const [cart, setCart] = useState(() => {
        const savedCart = localStorage.getItem('cart');
        return savedCart ? JSON.parse(savedCart) : [];
    });
    const [deliveryTime, setDeliveryTime] = useState('ASAP');
    const [isSubmitting, setIsSubmitting] = useState(false);

    const categories = [
        { id: 'COFFEE', name: 'CÀ PHÊ', icon: Coffee },
        { id: 'TEA', name: 'TRÀ', icon: Wine },
        { id: 'CAKE', name: 'BÁNH NGỌT', icon: Soup },
    ];

    // Save cart to localStorage whenever it changes
    useEffect(() => {
        localStorage.setItem('cart', JSON.stringify(cart));
    }, [cart]);

    useEffect(() => {
        fetchProducts();
    }, []);

    const fetchProducts = async () => {
        try {
            const data = await getMenu();
            console.log('Menu products:', data);
            console.log('First product imageUrl:', data[0]?.imageUrl);
            setProducts(data);
        } catch (e) {
            console.error(e);
            toast.error('Lỗi tải danh sách sản phẩm');
        }
    };

    const filteredProducts = selectedCategory === 'ALL' ? products : products.filter(p => p.category === selectedCategory);

    const addToCart = (product) => {
        setCart(prev => {
            const found = prev.find(i => i.id === product.id && i.size === (product.defaultSize || 'M'));
            if (found) {
                return prev.map(i => i.id === product.id && i.size === (product.defaultSize || 'M') ? { ...i, quantity: i.quantity + 1 } : i);
            }
            return [...prev, { id: product.id, name: product.name, price: product.price, quantity: 1, size: product.defaultSize || 'M', notes: '' }];
        });
        toast.success('Đã thêm vào giỏ hàng');
    };

    const changeQuantity = (index, delta) => {
        setCart(prev => {
            const copy = [...prev];
            copy[index].quantity += delta;
            if (copy[index].quantity <= 0) copy.splice(index, 1);
            return copy;
        });
    };

    const removeItem = (index) => {
        setCart(prev => prev.filter((_, i) => i !== index));
    };

    const updateItem = (index, patch) => {
        setCart(prev => prev.map((it, i) => i === index ? { ...it, ...patch } : it));
    };

    const total = cart.reduce((s, i) => s + (i.price || 0) * i.quantity, 0);

    return (
        <div className="min-h-screen bg-[#fcf7f2] p-6">
            <div className="max-w-7xl mx-auto">
                <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
                    {/* Left column: categories & notes (col-span 3 on lg) */}
                    <div className="lg:col-span-3 bg-white rounded-lg shadow-sm p-4">
                        <h2 className="text-lg font-semibold mb-4">Danh mục</h2>
                        <div className="space-y-2">
                            {categories.map(cat => {
                                return (
                                    <button
                                        key={cat.id}
                                        onClick={() => setSelectedCategory(cat.id)}
                                        className={`w-full flex items-center space-x-3 px-3 py-2 transition relative 
                                            ${selectedCategory === cat.id ? 'bg-white border-l-4 border-red-500 text-brown-700 font-semibold' : 'hover:bg-gray-50'}`}
                                    >
                                        <div className="w-2 h-2 rounded-full bg-brown-700" />
                                        <span>{cat.name}</span>
                                    </button>
                                );
                            })}
                        </div>

                        <div className="mt-6 border-t pt-4">
                            <h3 className="font-semibold mb-2">Lưu ý khi đặt hàng</h3>
                            <ul className="text-sm text-gray-600 space-y-2 list-disc list-inside">
                                <li>Sau khi đặt hàng sẽ có nhân viên liên hệ để xác nhận đơn hàng</li>
                                <li>Tuỳ vào số lượng đơn hàng mà thời gian chuẩn bị sẽ khác nhau</li>
                                <li>Quý khách vui lòng kiểm tra sản phẩm trước khi nhận hàng</li>
                            </ul>
                        </div>
                    </div>

                    {/* Center column: product list (col-span 6 on lg) */}
                    <div className="lg:col-span-6">
                        <div className="bg-white rounded-lg shadow-sm p-4">
                            <h2 className="text-lg font-semibold mb-4">Thực đơn</h2>

                            <div className="space-y-4">
                                {filteredProducts.length === 0 && (
                                    <div className="text-center text-gray-500 py-8">Không có sản phẩm trong danh mục này.</div>
                                )}

                                {filteredProducts.map(product => (
                                    <div key={product.id} className="flex items-start space-x-4 p-4 rounded-lg hover:bg-gray-50/50 transition border">
                                        {/* Image left */}
                                        <div className="w-24 h-24 bg-gray-100 rounded-lg flex items-center justify-center overflow-hidden">
                                            {product.imageUrl ? (
                                                <img 
                                                    src={product.imageUrl} 
                                                    alt={product.name} 
                                                    className="object-cover w-full h-full"
                                                    onError={(e) => {
                                                        console.error('Image load error:', product.imageUrl);
                                                        e.target.style.display = 'none';
                                                        e.target.parentElement.innerHTML = '<svg class="w-12 h-12 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"></path></svg>';
                                                    }}
                                                />
                                            ) : (
                                                <Coffee className="w-12 h-12 text-gray-300" />
                                            )}
                                        </div>

                                        {/* Info right */}
                                        <div className="flex-1">
                                            <div className="flex justify-between items-start">
                                                <div>
                                                    <h3 className="font-bold text-lg text-gray-900">{product.name}</h3>
                                                    <p className="text-sm text-gray-500 mt-1 line-clamp-2">{product.description}</p>
                                                </div>
                                                <div className="text-right">
                                                    <div className="text-lg font-semibold text-gray-900">{product.price ? `${product.price.toLocaleString()}₫` : 'Liên hệ'}</div>
                                                    <div className="mt-2">
                                                        <button onClick={() => addToCart(product)} className="w-8 h-8 rounded-full bg-brown-100 hover:bg-brown-200 flex items-center justify-center transition-colors">
                                                            <Plus className="w-5 h-5 text-brown-700" />
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>

                                            {/* Optional: size selector (if product.sizes exists) */}
                                            {product.sizes && product.sizes.length > 0 && (
                                                <div className="mt-2 flex space-x-2 text-sm">
                                                    {product.sizes.map(sz => (
                                                        <button key={sz} className="px-2 py-1 rounded border text-gray-600 text-xs">{sz}</button>
                                                    ))}
                                                </div>
                                            )}

                                        </div>

                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>

                    {/* Right column: order details (col-span 3 on lg) */}
                    <div className="lg:col-span-3">
                        <div className="bg-white rounded-lg shadow-sm p-4 sticky top-6">
                            <div className="flex items-center space-x-2 mb-4">
                                <div className="w-2 h-2 rounded-full bg-brown-700" />
                                <h2 className="text-lg font-bold tracking-wide">CHI TIẾT ĐƠN HÀNG</h2>
                            </div>

                            <div className="space-y-4 max-h-[50vh] overflow-auto pr-2">
                                {cart.length === 0 && <div className="text-gray-500">Chưa có món nào.</div>}

                                {cart.map((it, idx) => (
                                    <div key={`${it.id}-${idx}`} className="flex flex-col gap-2 p-3 rounded-lg bg-gray-50/50 border">
                                        <div className="flex-1">
                                            <div className="flex items-start justify-between">
                                                <div>
                                                    <div className="font-semibold text-gray-900">{it.name} {it.size ? `- ${it.size}` : ''}</div>
                                                    <div className="text-sm text-gray-500">{it.notes || 'Không có ghi chú'}</div>
                                                </div>
                                                <div className="text-right">
                                                    <div className="font-semibold text-gray-900">{(it.price * it.quantity).toLocaleString()}₫</div>
                                                </div>
                                            </div>

                                            <div className="mt-2 flex items-center space-x-3">
                                                <button onClick={() => changeQuantity(idx, -1)} className="w-7 h-7 rounded-full bg-gray-200 hover:bg-gray-300 flex items-center justify-center">
                                                    <Minus className="w-4 h-4" />
                                                </button>
                                                <div className="font-medium w-8 text-center">{it.quantity}</div>
                                                <button onClick={() => changeQuantity(idx, 1)} className="w-7 h-7 rounded-full bg-gray-200 hover:bg-gray-300 flex items-center justify-center">
                                                    <Plus className="w-4 h-4" />
                                                </button>
                                            </div>

                                            <div className="mt-3">
                                                <input
                                                    className="w-full border rounded-lg px-3 py-1.5 text-sm bg-white"
                                                    placeholder="Ghi chú cho món..."
                                                    value={it.notes}
                                                    onChange={(e) => updateItem(idx, { notes: e.target.value })}
                                                />
                                            </div>

                                            <button 
                                                onClick={() => removeItem(idx)}
                                                className="mt-2 text-sm text-red-500 hover:text-red-600 flex items-center space-x-1"
                                            >
                                                <X className="w-4 h-4" />
                                                <span>Xoá</span>
                                            </button>
                                        </div>
                                    </div>
                                ))}
                            </div>

                            <div className="mt-6 border-t pt-4">
                                <div className="flex items-center justify-between mb-4">
                                    <div className="font-bold text-gray-900">Tổng cộng</div>
                                    <div className="text-xl font-bold text-red-600">{total.toLocaleString()}₫</div>
                                </div>

                                <div className="mb-4">
                                    <select 
                                        className="w-full border rounded-lg px-3 py-2.5 bg-white" 
                                        value={deliveryTime} 
                                        onChange={(e) => setDeliveryTime(e.target.value)}
                                    >
                                        <option value="">Thời gian giao</option>
                                        <option value="ASAP">Giao nhanh (ASAP)</option>
                                        <option value="15">Sau 15 phút</option>
                                        <option value="30">Sau 30 phút</option>
                                        <option value="60">Sau 1 giờ</option>
                                    </select>
                                </div>

                                <Button
                                    className="w-full bg-red-600 hover:bg-red-700 text-white py-3 text-lg font-bold tracking-wide"
                                    onClick={async () => {
                                        if (cart.length === 0) {
                                            toast('Giỏ hàng trống');
                                            return;
                                        }
                                        // Backend expects: { tableId, items:[{productId, quantity}], note }
                                        const allNotes = cart.map(i => i.notes).filter(n => n).join('; ');
                                        const payload = {
                                            tableId: null,
                                            items: cart.map(i => ({
                                                productId: i.id,
                                                quantity: i.quantity
                                            })),
                                            note: allNotes || null
                                        };

                                        console.debug('Creating order with payload:', payload);
                                        setIsSubmitting(true);
                                        try {
                                            const res = await createOrder(payload);
                                            console.debug('✅ Order created successfully:', res);
                                            toast.success('Đặt hàng thành công');
                                            setCart([]);
                                            localStorage.removeItem('cart'); // Clear cart from localStorage
                                        } catch (e) {
                                            console.error('❌ Order error:', e);
                                            console.error('Error response:', e?.response);
                                            const serverMsg = e?.response?.data?.message || e?.response?.data || e?.message || 'Lỗi kết nối tới server';
                                            toast.error('Lỗi đặt hàng: ' + serverMsg);
                                        } finally {
                                            setIsSubmitting(false);
                                        }
                                    }}
                                    disabled={isSubmitting}
                                >
                                    {isSubmitting ? 'Đang xử lý...' : 'ĐẶT HÀNG'}
                                </Button>
                            </div>

                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default MenuPage;
