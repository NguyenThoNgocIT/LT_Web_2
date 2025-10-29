# Upload API - Usage Examples

## Backend Implementation ✅

The upload controller is now available at `/api/upload/{type}`.

## Frontend Integration

### Update product.api.js (Already Done)

The `uploadProductImage` function in `product.api.js` now calls:

```javascript
POST / api / upload / products;
```

### Example: Upload in Admin Product Form

```jsx
import { uploadProductImage } from "../../api/product.api";

const handleImageUpload = async (e) => {
  const file = e.target.files[0];
  if (!file) return;

  try {
    setUploading(true);
    const response = await uploadProductImage(null, file);
    // response.url = "http://localhost:8088/uploads/products/uuid-filename.jpg"

    setFormData({ ...formData, imageUrl: response.url });
    toast.success("Upload ảnh thành công");
  } catch (error) {
    console.error("Upload error:", error);
    toast.error("Lỗi upload ảnh");
  } finally {
    setUploading(false);
  }
};
```

### Example: Add to MenuManagement.jsx

Add image upload field to the product form modal:

```jsx
<div>
  <label className="block mb-1">Hình ảnh</label>
  <input
    type="file"
    accept="image/*"
    className="w-full p-2 border rounded"
    onChange={handleImageUpload}
  />
  {formData.imageUrl && (
    <img
      src={formData.imageUrl}
      alt="Preview"
      className="mt-2 w-32 h-32 object-cover rounded"
    />
  )}
</div>
```

## Test Upload API with Postman/cURL

### Upload File

```bash
curl -X POST http://localhost:8088/api/upload/products \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -F "file=@/path/to/image.jpg"
```

Response:

```json
{
  "url": "http://localhost:8088/uploads/products/123e4567-e89b-12d3-a456-426614174000.jpg",
  "filename": "123e4567-e89b-12d3-a456-426614174000.jpg",
  "type": "products",
  "size": "54321"
}
```

### Access Uploaded File

Open in browser:

```
http://localhost:8088/uploads/products/123e4567-e89b-12d3-a456-426614174000.jpg
```

### Delete File

```bash
curl -X DELETE http://localhost:8088/api/upload/products/123e4567-e89b-12d3-a456-426614174000.jpg \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

## Configuration

### application.properties

```properties
# Upload directory (relative to project root)
upload.directory=uploads

# Max file size
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### Supported File Types

Currently only image files are allowed:

- image/jpeg
- image/png
- image/gif
- image/webp

To allow other types, modify validation in `UploadController.java`:

```java
if (contentType == null || !contentType.startsWith("image/")) {
    // Change validation logic here
}
```

## Security Notes

1. ✅ Only ADMIN/ROOT roles can upload
2. ✅ Files are renamed with UUID to prevent conflicts
3. ✅ File type validation (images only)
4. ✅ Size limits configured (10MB default)
5. ⚠️ Consider adding virus scanning for production
6. ⚠️ Consider using cloud storage (AWS S3, Cloudinary) for production

## Next Steps

1. Add image upload to `MenuManagement.jsx` product form
2. Display product images in menu views
3. Add delete functionality when removing products
4. Consider adding image optimization/resizing
