# 🔁 Dokumentasi API Follow System

Base URL: `/users/{id}`  
Semua response dibungkus dalam objek `WebResponse<T>`.

---

## ➕ POST `/users/{id}/follow`

Follow pengguna lain.  
**Hanya untuk role:** `USER`

### Request

**Headers:**
- `Authorization: Bearer <token>`

**Path Variable:**
- `id`: ID user yang ingin di-follow

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
  "errors": "Tidak dapat mengikuti diri sendiri"
}
```

**Status Code:** `400 Bad Request`
```json
{
  "errors": "Sudah mengikuti pengguna ini"
}
```

**Status Code:** `401 Unauthorized`
```json
{
  "errors": "Invalid token"
}
```

---

## ➖ DELETE `/users/{id}/follow`

Unfollow pengguna.  
**Hanya untuk role:** `USER`

### Request

**Headers:**
- `Authorization: Bearer <token>`

**Path Variable:**
- `id`: ID user yang ingin di-unfollow

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
  "errors": "Belum mengikuti pengguna ini"
}
```

**Status Code:** `401 Unauthorized`
```json
{
  "errors": "Invalid token"
}
```

---

## 👥 GET `/users/{id}/followers`

Mendapatkan daftar pengguna yang mengikuti user tersebut.

### Request

**Path Variable:**
- `id`: ID user yang ingin dicek followers-nya

### Response

**Status Code:** `200 OK`
```json
{
  "data": [
    {
      "id": "user-id",
      "name": "Nama User",
      "profilePicture": "http://example.com"
    }
  ]
}
```

---

## 👣 GET `/users/{id}/followings`

Mendapatkan daftar pengguna yang di-follow oleh user tersebut.

### Request

**Path Variable:**
- `id`: ID user yang ingin dicek followings-nya

### Response

**Status Code:** `200 OK`
```json
{
  "data": [
    {
      "id": "user-id",
      "name": "Nama User",
      "profilePicture": "http://example.com"
    }
  ]
}
```

---

## 📦 Struktur `WebResponse<T>`

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

## ✅ Validasi & Aturan

- Tidak boleh follow diri sendiri.
- Tidak bisa follow user yang sudah di-follow.
- Tidak bisa unfollow user yang belum di-follow.
- Semua endpoint menggunakan `Bearer Token` dan role `USER`.

---
