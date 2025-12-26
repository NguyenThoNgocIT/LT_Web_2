const baseURL = 'http://localhost:8088';

export const uploadImage = async (file, type = 'products') => {
    const formData = new FormData();
    formData.append('file', file);
    
    const response = await fetch(`${baseURL}/api/upload/${type}`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
        body: formData
    });

    if (!response.ok) {
        throw new Error('Upload failed');
    }

    const data = await response.json();
    return data.url;  // URL của ảnh đã upload
};