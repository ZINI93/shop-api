# ショッピングモール API

このプロジェクトは、商品検索、カート機能、注文および支払い処理ができるシンプルなショッピングモールAPIです。また、管理者ページを通じて商品登録や在庫管理も可能です。このプロジェクトは、DockerとKubernetesを使用してデプロイでき、CI/CDパイプラインをGitHub Actionsで自動化しています。

## 機能

### 商品管理
- 商品の検索および一覧表示
- 管理者機能：商品登録、編集、削除、および在庫管理

### カート機能
- 商品をカートに追加、編集、削除
- ユーザーごとのカート管理

### 注文および支払い
- 注文作成（モックデータを使った支払い処理）
- 注文履歴の表示

### 管理者ページ
- 商品登録および在庫管理ページ

## 技術スタック
- **バックエンド**: Java, Spring Boot, JPA, MySQL
- **デプロイ**: Docker, Kubernetes
- **CI/CD**: GitHub Actions

## インストール方法
1. リポジトリのクローン
   ```bash
   git clone https://github.com/ZINI93/shop-api.git
   cd shop-api

# API 設計

###  Auth
- **POST /authenticate** → ログイン（JWTトークン発行）
- **TEST TOKEN 発行**
    - ID: `user1@example.com`
    - Password: `12341234`

###  会員
- **GET /api/member/{memberId}** → 会員IDで情報取得
- **POST /api/member/join** → 会員登録
- **PUT /api/member** → 会員情報編集

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

### 追加事項
- **商品登録および編集時の在庫管理**: 管理者ページで商品を登録または編集する際に、在庫数を追加または編集するAPIが必要です。
- **注文状態の管理**: 注文が進行する各状態（支払い待ち、支払い完了、配送準備など）を管理するフィールドが必要です。


---- 한국어 --- 

# 쇼핑몰 API

이 프로젝트는 상품 조회, 장바구니 기능, 주문 및 결제를 처리할 수 있는 간단한 쇼핑몰 API입니다. 또한 관리자 페이지를 통해 상품 등록 및 재고 관리가 가능합니다. 이 프로젝트는 Docker와 Kubernetes를 사용하여 배포할 수 있으며, CI/CD 파이프라인을 GitHub Actions로 자동화합니다.

## 기능

- **상품 관리**
    - 상품 조회 및 검색
    - 관리자 기능: 상품 등록, 수정, 삭제 및 재고 관리
- **장바구니 기능**
    - 상품을 장바구니에 추가, 수정, 삭제
    - 사용자별 장바구니 관리
- **주문 및 결제**
    - 주문 생성 (Mock 데이터 기반 결제 처리)
    - 주문 내역 조회
- **관리자 페이지**
    - 상품 등록 및 재고 관리 페이지

## 技術スタック

- **백엔드**: Java, Spring Boot, JPA, MySQL
- **배포**: Docker, Kubernetes
- **CI/CD**: GitHub Actions

## インストール方法

1. **레포지토리 클론**

   bash
   git clone https://github.com/yourusername/shop-api.git
   cd shop-api
1. 상품 조회 및 검색
   GET /api/products: 모든 상품 목록을 조회
   GET /api/products/{id}: 특정 상품의 상세 정보 조회
   GET /api/products/search: 상품을 검색 (쿼리 파라미터로 필터링 가능)
2. 장바구니 기능
   POST /api/cart: 장바구니에 상품 추가
   GET /api/cart: 사용자의 장바구니 조회
   PUT /api/cart/{id}: 장바구니에 담긴 상품 수량 수정
   DELETE /api/cart/{id}: 장바구니에서 상품 제거
3. 주문 및 결제
      POST /api/orders: 주문 생성
      GET /api/orders/{orderId}: 주문 상세 조회
      GET /api/orders: 사용자의 모든 주문 조회
4. 관리자 페이지 (상품 관리 및 재고 관리)
      POST /api/admin/products: 상품 등록
      PUT /api/admin/products/{id}: 상품 정보 수정
      DELETE /api/admin/products/{id}: 상품 삭제
      PUT /api/admin/products/{id}/stock: 상품 재고 수정

테이블 관계 요약
Member와 Order: 1:N 관계 (1명의 회원은 여러 주문을 할 수 있음)
Order와 OrderItem: 1:N 관계 (1개의 주문에는 여러 상품이 포함될 수 있음)
Product와 Cart: 1:N 관계 (하나의 상품은 여러 장바구니에 담길 수 있음)
Product와 ProductCategory: N:1 관계 (여러 상품이 하나의 카테고리에 속할 수 있음)
Product와 ProductImage: 1:N 관계 (하나의 상품은 여러 이미지를 가질 수 있음)


API 설계
간소화된 설계에 맞춰 필요한 기본적인 API를 설계합니다.

1. 상품 조회
   GET /api/products: 모든 상품 조회
   GET /api/products/{id}: 특정 상품 조회
2. 장바구니
   POST /api/cart: 장바구니에 상품 추가
   GET /api/cart: 장바구니 조회
   DELETE /api/cart/{id}: 장바구니에서 상품 삭제
3. 주문
   POST /api/orders: 주문 생성 (장바구니에서 주문으로 전환)
   GET /api/orders/{id}: 주문 상세 조회
4. 결제
   POST /api/payments: 결제 생성 (주문에 대한 결제 진행)
   GET /api/payments/{id}: 결제 상태 조회


 상품 동적쿼리
페이징: ?page=0&size=10
가격 범위: ?minPrice=100&maxPrice=500
정렬: ?sortField=price
검색: ?keyword=sample

✅ 동적 검색 조건
✔️ 주문 상태 (status)
✔️ 주문 기간 (startDate, endDate)
✔️ 주문 금액 범위 (minAmount, maxAmount)
✔️ 정렬 필드 (sortField)

### **🛠 Auth**
- `POST /authenticate` → 로그인 (JWT 발급)
- **TEST TOKEN** 발급 ID: user1@example.com  Password: 12341234

### **👥 Member**
- `GET /api/member/{memberId}` → 회원 ID로 조회
- `POST /api/member/join` → 회원가입
- `PUT /api/member` → 회원 수정

### **🛒 Cart**
- `POST /api/cart` → 장바구니에 상품 추가
- `GET /api/cart/{cartId}` → 장바구니 개별 상품 조회
- `GET /api/cart` → 장바구니 전체 조회
- `PUT /api/cart/{cartId}` → 장바구니 상품 수량 변경
- `DELETE /api/cart/{cartId}` → 장바구니 상품 삭제

### **📦 Order**
- `POST /api/orders` → 주문 생성
- `GET /api/orders/{orderId}` → 주문 ID로 조회
- `GET /api/orders?status=&startDate=&endDate=&sort=` → **(필터 & 정렬)** 전체 주문 조회
- `PUT /api/orders/{orderId}` → 주문 수정
- `DELETE /api/orders/{orderId}` → 주문 삭제

### **💰 Payment**
- `POST /api/payment` → 결제 생성
- `GET /api/payment/{paymentId}` → 결제 ID로 조회
- `PUT /api/payment/{paymentId}/method` → 결제 방법 수정
- `PUT /api/payment/{paymentId}/confirm` → 결제 완료 처리
- `PUT /api/payment/{paymentId}/refund` → 환불 요청
- `DELETE /api/payment/{paymentId}` → 결제 삭제

### **🛍 Product**
- `POST /api/admin/product` → **(관리자)** 상품 생성
- `GET /api/product/{productId}` → 상품 ID로 조회
- `GET /api/products?keyword=&priceMin=&priceMax=&sort=` → 상품 검색
- `PUT /api/admin/product/{productId}` → **(관리자)** 상품 수정
- `DELETE /api/admin/product/{productId}` → **(관리자)** 상품 삭제

### **🖼 Product Image**
- `POST /api/admin/product-images` → **(관리자)** 상품 이미지 추가
- `GET /api/product-images?productId={productId}` → 특정 상품의 모든 이미지 조회 (미구현)
- `PUT /api/admin/product-images/{imageId}` → **(관리자)** 상품 이미지 수정
- `DELETE /api/admin/product-images/{imageId}` → **(관리자)** 상품 이미지 삭제

### **관리자 관련 API**
- `GET /api/admin/payment-manege` → 지불 전체 조회
- `GET /api/admin/memberSearch` → 전체 회원 조회 (전화번호, 이름으로 조회 가능)
- `DELETE /api/admin/member/{memberId}` → 회원 삭제
- `POST /api/admin/product` → 상품 등록
- `PUT /api/admin/product/{productId}` → 상품 수정
- `DELETE /api/admin/product/{productId}` → 상품 삭제

