# フリーマーケット API

## 1. **API 概要**
このプロジェクトは、中古取引プラットフォームのAPIです。すべてのユーザーが商品を登録して販売でき、他のユーザーが商品を検索・購入できる機能を提供します。

現在、このAPIは Railway を通じてデプロイされており、以下のURLからアクセスできます。                 
URL: shop-api-production-3d05.up.railway.app

API のテストを行う際は、本ドキュメントを参考にしてください。
### **主要機能**
- **販売者**: `商品を登録し、在庫を管理できます。販売金額から5%の手数料を差し引いた額が残高に反映されます。`
- **購入者**: `商品を検索し、カートに追加して注文および決済を行えます。`
- **管理者**: `システム全体をモニタリングし、ユーザーや商品を管理できます。`

### **API バージョン**
#### **バージョン:** `v1.0`
#### **バージョン:** `v2.0`
- *ID管理をUUIDに変更*: `UUIDを使用することで、システム内で重複のない一意なIDを生成し、セキュリティとデータ整合性を向上させています。`
- *セキュリティ設定を環境変数で管理*: `機密性の高い設定情報（APIキー、データベース接続情報など）を環境変数で管理するように変更しました。
- *会員登録完了,お支払い完了のメール機能を追加* -

## 🛠️ 技術スタック

- **バックエンド**: Java, Spring Boot, Spring Security, JPA, QueryDsl
- **データベース**: MySQL
- **DevOps**: Docker, Docker Compose, GitHub CI/CD , railway
---

## 2. **認証方式**
- **認証方法:** `JWTトークン方式`
- **トークン発行エンドポイント:** `/authenticate`
- **Googleトークン発行エンドポイント:** `/api/auth/google`
- **有効時間:** `10時間`
- ----
## 3. **エンドポイント一覧**

### URL : shop-api-production-3d05.up.railway.app 

### 3.1. **認証**

#### **1) トークン発行**
- **HTTPメソッド:** `POST`
- **URL:** `/authenticate`
```json
{
  "email": "zini@gmail.com",
  "password": "Abc123!@"
}
```
---

### 3.2. **管理者機能**

#### **1) 会員リスト**
- **HTTPメソッド:** `GET`
- **検索機能:** `お名前`, `電話番号`
- **URL:** `/api/admin/members/search?name=park&phoneNumber=080`

#### **2) 会員削除**
- **HTTPメソッド:** `DELETE`
- **URL:** `/api/admin/members/{memberUuId}`
---

### 3.3. **会員機能**

#### **1) 会員登録**
- **HTTPメソッド:** `POST`
- **URL:** `/api/join`
- **リクエストパラメータ説明:**:  
*Email*: `有効なメールアドレス形式 (example@email.com)`  
*Password* : `8～20文字, 数字・大文字・小文字・特殊文字を含む必要あり`  
*phoneNumber* : `070-xxxx-xxxx, 080-xxxx-xxxx, 090-xxxx-xxxx の形式のみ有効`

**リクエスト本文:**
```json
{
  "email": "zini@gmail.com",  
  "password": "Abc1234!@", 
  "name": "zini",
  "phoneNumber": "080-1234-5678"
}
```
#### **2) 会員情報の取得**
- **HTTPメソッド:** `GET`
- **検索機能:**　`ユーザー情報`
- **URL:** `/api/members/me`

#### **3) 会員情報の更新**
- **HTTPメソッド:** `PUT`
- **URL:** `/api/members/update`

**リクエスト本文:**
```json
{
  "password": "Abc1234!@",
  "name": "KOGUMA",
  "phoneNumber": "080-1514-5678"
}
```
---
---

### 3.4. **住所機能**

#### **1) 住所登録**
- **HTTPメソッド:** `POST`
- **URL:** `/api/address`
- **リクエストパラメータ説明:**:

**リクエスト本文:**
```json
{
  "zipcode": "123-123",
  "state": "神奈川県",
  "city": "川崎市",
  "street": "宮前区有馬１－２－３"
}
```
#### **2) 住所情報の取得**
- **HTTPメソッド:** `GET`
- **検索機能:**　`住所情報`
- **URL:** `/api/address/{addressUuid}`

#### **3) 住所情報の更新**
- **HTTPメソッド:** `PUT`
- **URL:** `/api/address/{addressUuid}`

**リクエスト本文:**
```json
{
  "zipcode": "123-123",
  "state": "茨城県",
  "city": "多摩市",
  "street": "宮前区有馬１－２－３"
}
```
#### **5) 住所を削除**
- **HTTPメソッド:** `DELETE`
- **URL:** `/api/address/{addressUuid}`

---

### 3.5. **注文機能**

#### **1) 注文作成**
- **HTTPメソッド:** `POST`
- **URL:** `/api/orders`

**リクエスト本文:**
```json
{
  "memberId": 1,
  "paymentMethod": "CREDIT_CARD",
  "orderItems": [
    {
      "productId": 1,
      "quantity": 100
    },
    {
      "productId": 2,
      "quantity": 50
    }
  ]
}
```
#### **２) 注文リスト**
- **HTTPメソッド:** `GET`
- **検索機能:** `注文のステータス`, `日付、時間`, `値段範囲`
- **URL:** `/api/orders?status=PENDING&starDate=2023-10-01T00:00:00&endDate=2026-10-31T23:59:59&minAmount=100&maxAmount=2000&sortField=createdAt`


#### **3) 注文情報の取得**
- **HTTPメソッド:** `GET`
- **URL:** `/api/orders/{orderUuid}`


#### **4) 注文情報の更新**
- **HTTPメソッド:** `PUT`
- **URL:** `/api/orders/{orderUuid}`

**リクエスト本文:**
```json
{
  "totalAmount": "100.00",
  "status": "CANCELLED",
  "paymentMethod": "PayPay",
  "orderItems": "1"
}
```
#### **5) 注文削除**
- **HTTPメソッド:** `DELETE`
- **URL:** `/api/orders/{orderUuid} `

---

### 3.6. **注文商品**

#### **1) 注文商品の作成**
- `注文作成時に自動的に追加されま`


#### **２) 注文商品リスト**
- **HTTPメソッド:** `GET`
- **URL:** `/api/order-items`


#### **3) 注文商品情報の取得**
- **HTTPメソッド:** `GET`
- **URL:** `/api/order-items/{orderItemUuid}`

---
### 3.7. **お支払い機能**

#### **1) 決済作成**
- **HTTPメソッド:** `POST`
- **URL:** `/api/payments`
- - **追加機能:** `お支払い完了したら、商品の在庫減少`


**リクエスト本文:**
```json
{
  "orderId": "6"
}
```
#### **２) 決済リスト**
- **HTTPメソッド:** `GET`
- **URL:** `/api/payments`


#### **3) 決済情報の取得**　
- **HTTPメソッド:** `GET`
- **URL:** `/api/payments/{paymentUuId}`


#### **5) 決済情報の更新**
- **HTTPメソッド:** `PUT`
- **URL:** `/api/payments/{paymentUuId}`
- **追加機能:** `返金したら、在庫を戻す`

**リクエスト本文:**
```json
{
  "status": "CANCELED",
  "paymentMethod": "PayPay"
}
```
#### **5) 決済削除**
- **HTTPメソッド:** `DELETE`
- **URL:** `/api/payments/{paymentUuId} `

---
### 3.8. **商品機能**

#### **1) 商品登録**
- **HTTPメソッド:** `POST`
- **URL:** `/api/products`

**リクエスト本文:**
```json
{
  "name": "おしゃれカーテン",
  "price": 2000.00,
  "description": "キラキラカーテン",
  "stock" : 100
}
```
#### **2) 商品リスト**
- **HTTPメソッド:** `GET`
- **URL:** `/api/products`


#### **3) 商品情報の取得**　
- **HTTPメソッド:** `GET`
- **URL:** `/api/products/{productUuid}`


#### **4) 商品情報の更新**
- **HTTPメソッド:** `PUT`
- **URL:** `/api/products/{productUuid}`

**リクエスト本文:**
```json
{
  "name": "可愛いカーテン",
  "price": 1000.00,
  "description": "長いカーテン",
  "stock" : 400
}
```
#### **5) 商品削除**
- **HTTPメソッド:** `DELETE`
- **URL:** `/api/products/{productUuid}`
---
### 3.9. **商品のイメージ機能**

#### **1) 商品登録**
- **HTTPメソッド:** `POST`
- **URL:** `/api/product-images`

**リクエスト本文:**
```json
{
  "productId": 1,
  "imageUrl": "https://www.image.com"
}
```
#### **2) 商品イメージリスト**
- **HTTPメソッド:** `GET`
- **URL:** `/api/product-images`

#### **3)  商品イメージ情報の更新**
- **HTTPメソッド:** `PUT`
- **URL:** `/api/product-images/{productImageUuid}`

**リクエスト本文:**
```json
{
  "imageUrl": "https://www.image.com"
}
```
#### **5) 商品イメージ削除**
- **HTTPメソッド:** `DELETE`
- **URL:** `/api/product-images/{productImageUuid}`
---
### 4.0. **カート機能**

#### **1) カート作成**
- **HTTPメソッド:** `POST`
- **URL:** `/api/carts`

**リクエスト本文:**
```json
{
  "memberId": 5,
  "productId": "1",
  "quantity": 1
}
```
#### **2) カートリスト**
- **HTTPメソッド:** `GET`
- **URL:** `/api/carts `


#### **3) カート情報の更新**
- **HTTPメソッド:** `PUT`
- **URL:** `/api/carts/{cartUuid}`

**リクエスト本文:**
```json
{
  "productId": "1",
  "quantity": 1
}
```
#### **4) カート削除**
- **HTTPメソッド:** `DELETE`
- **URL:** `/api/carts/{cartUuid}`
---

---
テスト
- **Postmanテスト:** `すべてのエンドポイントはPostmanを使用してテストをしました。`
- **サービス層テスト:** `ビジネスロジックはサービス層で単体テストを行います。各テストはJUnitを使用して作成され、外部の依存関係はMockingを使用して最小限に抑えます。`
