# Uploads Directory

This directory stores uploaded files from the application.

## Structure

```
uploads/
├── products/     # Product images
├── tables/       # Table images (if needed)
└── ...           # Other file types
```

## API Endpoints

### Upload File

- **POST** `/api/upload/{type}`
- **Auth**: ADMIN or ROOT role required
- **Body**: `multipart/form-data` with field `file`
- **Response**:
  ```json
  {
    "url": "http://localhost:8088/uploads/products/uuid-filename.jpg",
    "filename": "uuid-filename.jpg",
    "type": "products",
    "size": "12345"
  }
  ```

### Delete File

- **DELETE** `/api/upload/{type}/{filename}`
- **Auth**: ADMIN or ROOT role required
- **Response**:
  ```json
  {
    "message": "File deleted successfully",
    "filename": "uuid-filename.jpg"
  }
  ```

## Configuration

See `application.properties`:

- `upload.directory=uploads`
- `spring.servlet.multipart.max-file-size=10MB`
- `spring.servlet.multipart.max-request-size=10MB`

## Access Uploaded Files

Files are accessible via:
`http://localhost:8088/uploads/{type}/{filename}`

Example:
`http://localhost:8088/uploads/products/123e4567-e89b-12d3-a456-426614174000.jpg`
