# 👤 Dokumentasi API User

Base URL: `/users`

Semua response dibungkus dalam objek `WebResponse<T>`.  
Gunakan header `Authorization: Bearer <token>` untuk endpoint yang memerlukan autentikasi.

---

## ✏️ PATCH `/users/me`

Update data user yang sedang login.  
**Hanya untuk role:** `USER`

### Request

**Headers:**
- `Authorization: Bearer <token>`
- `Content-Type: application/json`

**Body:**
```json
{
  "name": "Nama Baru",
  "email": "emailbaru@example.com",
  "bio": "Bio baru"
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
  "errors": "Email sudah terdaftar"
}
```

**Status Code:** `401 Unauthorized`
```json
{
  "errors": "Invalid token"
}
```

---

## 👁️ GET `/users/{id}`

Mendapatkan profil publik dari user berdasarkan ID.

### Request

**Path Variable:**
- `id` — ID user yang ingin dilihat

### Response

**Status Code:** `200 OK`
```json
{
  "data": {
    "name": "Nama Lengkap",
    "email": "user@example.com",
    "bio": "Bio",
    "profilePicture": "http://example.com",
    "followers": 2,
    "followings": 2,
    "points": 0
  }
}
```

### Error Response

**Status Code:** `404 Not Found`
```json
{
  "errors": "User tidak ditemukan"
}
```

---

## 📦 Struktur `WebResponse<T>`

### Sukses:
```json
{
  "data": { ... }
}
```

### Gagal:
```json
{
  "errors": "Deskripsi error"
}
```

---

## ✅ Validasi & Aturan

- Endpoint `/users/me` memerlukan token JWT.
- Jika user mengubah email, sistem akan validasi agar tidak duplikat.
- `/users/{id}` terbuka tanpa autentikasi, hanya untuk melihat data publik.

---
