# ショッピングモール API

## 1. **API 概要**
このプロジェクトは、商品検索、カート機能、注文および支払い処理ができるシンプルなショッピングモールAPIです。また、管理者ページを通じて商品登録や在庫管理も可能です。


### **主要機能**

- 商品管理 
- カート機能 
- 注文および支払い
- 管理者ページ

### **API バージョン**
- **バージョン:** `v1.0`


## 🛠️ 技術スタック

- **Backend**: Java, Spring Boot, Spring Security, JPA, QueryDsl
- **Database**: MySQL
- **DevOps**: Docker, Docker Compose
---
## 2. **認証方式**
- **認証方法:** `JWTトークン方式`
- **トークン発行エンドポイント:** `/authenticate`
- **Googleトークン発行エンドポイント:** `/api/auth/google`
- **有効時間:** `10時間`
- ----
## 3. **エンドポイント一覧**


###  会員
- **GET /api/member/{memberId}** → 会員IDで情報取得
- **POST /api/member/join** → 会員登録
- **PUT /api/member/{memberId}** → 会員情報編集

###  カート
- **POST /api/cart** → カートに商品を追加
- **GET /api/cart/{cartId}** → カート内の個別商品取得
- **GET /api/cart** → カート全体の表示
- **PUT /api/cart/{cartId}** → カート内の商品数量を変更
- **DELETE /api/cart/{cartId}** → カートから商品を削除

###  注文
- **POST /api/orders** → 注文作成
- **GET /api/orders/{orderId}** → 注文IDで情報取得
- **GET /api/orders?status=&startDate=&endDate=&sort=** → (フィルター & 並べ替え) 全注文の取得
- **PUT /api/orders/{orderId}** → 注文情報を編集
- **DELETE /api/orders/{orderId}** → 注文削除

###  支払い
- **POST /api/payment** → 支払い作成
- **GET /api/payment/{paymentId}** → 支払いIDで情報取得
- **PUT /api/payment/{paymentId}/method** → 支払い方法の変更
- **PUT /api/payment/{paymentId}/confirm** → 支払い完了処理
- **PUT /api/payment/{paymentId}/refund** → 返金リクエスト
- **DELETE /api/payment/{paymentId}** → 支払い削除

###  商品
- **POST /api/admin/product** → (管理者) 商品作成
- **GET /api/product/{productId}** → 商品IDで情報取得
- **GET /api/products?keyword=&priceMin=&priceMax=&sort=** → 商品検索
- **PUT /api/admin/product/{productId}** → (管理者) 商品編集
- **DELETE /api/admin/product/{productId}** → (管理者) 商品削除

###  商品画像
- **POST /api/admin/product-images** → (管理者) 商品画像追加
- **GET /api/product-images?productId={productId}** → 特定の商品に対する画像取得（未実装）
- **PUT /api/admin/product-images/{imageId}** → (管理者) 商品画像編集
- **DELETE /api/admin/product-images/{imageId}** → (管理者) 商品画像削除

### 管理者関連 API
- **GET /api/admin/payment-manege** → 支払い一覧の取得
- **GET /api/admin/memberSearch** → 会員一覧の取得（電話番号、名前で検索可能）
- **DELETE /api/admin/member/{memberId}** → 会員削除
- **POST /api/admin/product** → 商品登録
- **PUT /api/admin/product/{productId}** → 商品編集
- **DELETE /api/admin/product/{productId}** → 商品削除

### 動的クエリ
#### 商品検索

GET /api/products?keyword=sample&minPrice=100&maxPrice=500&sort=price
GET /api/orders?status=completed&startDate=2024-01-01&endDate=2024-12-31&sort=totalAmount
GET /api/products?page=0&size=10

### テーブル関係の概要
- **Member と Order**: 1:N 関係（1人の会員が複数の注文を行える）
- **Order と OrderItem**: 1:N 関係（1つの注文に複数の商品が含まれる）
- **Product と Cart**: 1:N 関係（1つの商品が複数のカートに入ることができる）
- **Product と ProductCategory**: N:1 関係（複数の商品が1つのカテゴリーに属する）
- **Product と ProductImage**: 1:N 関係（1つの商品が複数の画像を持つ）

---
テスト
- **Postmanテスト:** `すべてのエンドポイントはPostmanを使用してテストをしました。`
- **サービス層テスト:** `ビジネスロジックはサービス層で単体テストを行います。各テストはJUnitを使用して作成され、外部の依存関係はMockingを使用して最小限に抑えます。`
