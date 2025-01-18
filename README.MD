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

## 기술 스택

- **백엔드**: Java, Spring Boot, JPA, MySQL
- **프론트엔드**: React.js (관리자 페이지)
- **배포**: Docker, Kubernetes
- **CI/CD**: GitHub Actions
- **메시지 큐 (확장)**: RabbitMQ / Kafka

## 설치 방법

1. **레포지토리 클론**

   ```bash
   git clone https://github.com/yourusername/shop-api.git
   cd shop-api
   

----- endpoint------

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