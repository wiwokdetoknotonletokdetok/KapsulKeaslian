# 📘 Dokumentasi API Authentication

**Base URL:** `/auth`

Semua response dibungkus dalam objek `WebResponse<T>`.

---

## 🔐 POST `/auth/login`

Login user dan mendapatkan token otentikasi.

### Request

**Headers:**
- `Content-Type: application/json`

**Body:**
```json
{
  "email": "user@example.com",
  "password": "string"
}
```

### Response

**Status Code:** `200 OK`
```json
{
  "data": {
    "token": "jwt-token-string"
  }
}
```

### Error Response

**Status Code:** `401 Unauthorized`
```json
{
  "errors": "Username atau password tidak valid"
}
```

---

## 📝 POST `/auth/register`

Mendaftarkan user baru.

### Request

**Headers:**
- `Content-Type: application/json`

**Body:**
```json
{
  "email": "user@example.com",
  "name": "Nama Lengkap",
  "password": "password123",
  "confirmPassword": "password123"
}
```

### Response

**Status Code:** `201 Created`
```json
{
  "data": "OK"
}
```

### Error Response

**Status Code:** `400 Bad Request`
```json
{
  "errors": "Email sudah terdaftar"
}
```

**Status Code:** `400 Bad Request`
```json
{
  "errors": "Password tidak cocok"
}
```

---

## 🚪 POST `/auth/logout`

Logout dari sistem.  
**Hanya untuk role:** `USER`

### Request

**Headers:**
- `Authorization: Bearer <token>`

### Response

**Status Code:** `200 OK`
```json
{
  "data": "OK"
}
```

---

## 🔒 PATCH `/auth/password`

Mengubah password user.  
**Hanya untuk role:** `USER`

### Request

**Headers:**
- `Authorization: Bearer <token>`
- `Content-Type: application/json`

**Body:**
```json
{
  "currentPassword": "old-password",
  "newPassword": "new-password",
  "confirmNewPassword": "new-password"
}
```

### Response

**Status Code:** `200 OK`
```json
{
  "data": "OK"
}
```

### Error Response

**Status Code:** `400 Bad Request`
```json
{
  "errors": "Password tidak cocok"
}
```

**Status Code:** `401 Unauthorized`
```json
{
  "errors": "Username atau password tidak valid"
}
```

---

## 📦 Struktur `WebResponse<T>`

Semua response dikembalikan dalam format berikut:

### Sukses
```json
{
  "data": { ... }
}
```

### Gagal
```json
{
  "errors": "Deskripsi error"
}
```

---

## 🔑 Tentang Authorization Header

Untuk endpoint yang membutuhkan autentikasi, gunakan header:

```
Authorization: Bearer <JWT_TOKEN>
```

Token JWT diperoleh dari endpoint `/auth/login`. Pastikan token masih valid agar request tidak ditolak (`401 Unauthorized`).

---

## ✅ Validasi & Aturan

- Email harus valid dan unik.
- Password dan konfirmasi harus cocok.
- Akses ke `/logout` dan `/password` hanya diperbolehkan untuk user dengan role `USER`.

---

