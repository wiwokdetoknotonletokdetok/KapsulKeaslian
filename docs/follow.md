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

**Request Param:**
- `page`: Menentukan halaman saat ini yang ingin diakses. (integer, default = 1)
    - Nilai minimum: `1`
    - Contoh: `1` berarti halaman pertama, `2` berarti halaman kedua, dan seterusnya.
- `size`: Menentukan jumlah data yang akan ditampilkan per halaman. (integer, default = 10)
    - Nilai minimum: `1`
    - Contoh: `10` berarti setiap halaman akan berisi maksimal 10 data.

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
  ],
  "pageInfo": {
    "size": 10,
    "currentPage": 1,
    "totalPages": 1,
    "totalElements": 1
  }
}
```

---

## 👣 GET `/users/{id}/followings`

Mendapatkan daftar pengguna yang di-follow oleh user tersebut.

### Request

**Path Variable:**
- `id`: ID user yang ingin dicek followings-nya

**Request Param:**
- `page`: Menentukan halaman saat ini yang ingin diakses. (integer, default = 1)
    - Nilai minimum: `1`
    - Contoh: `1` berarti halaman pertama, `2` berarti halaman kedua, dan seterusnya.
- `size`: Menentukan jumlah data yang akan ditampilkan per halaman. (integer, default = 10)
    - Nilai minimum: `1`
    - Contoh: `10` berarti setiap halaman akan berisi maksimal 10 data.

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
  ],
  "pageInfo": {
    "size": 10,
    "currentPage": 1,
    "totalPages": 1,
    "totalElements": 1
  }
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
