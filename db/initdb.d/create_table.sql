create TABLE `member` (
    `member_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `email` VARCHAR(255) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `phone_number` VARCHAR(30) NOT NULL UNIQUE,
    `role` VARCHAR(10) NOT NULL,
    `member_uuid` VARCHAR(36) NOT NULL UNIQUE,
    `balance` DECIMAL(10,2) NOT NULL CHECK (`balance` >= 0),

    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,

    CONSTRAINT `chk_member_role` CHECK (`role` IN ('USER', 'ADMIN'))
);

create Table `address` (
    `address_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `zipcode` VARCHAR(10) NOT NULL,
    `state` VARCHAR(255) NOT NULL,
    `city` VARCHAR(255) NOT NULL,
    `street` VARCHAR(255) NOT NULL,
    `address_uuid` VARCHAR(36) NOT NULL UNIQUE,
    `member_id` BIGINT NOT NULL,


    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,

    CONSTRAINT `fk_address_member` FOREIGN KEY (`member_id`) REFERENCES `member`(`member_id`) ON delete CASCADE
);

create TABLE `product` (
    `product_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL UNIQUE,
    `price` DECIMAL(10,2) NOT NULL,
    `description` VARCHAR(255) NOT NULL,
    `stock` INT NOT NULL,
    `product_uuid` VARCHAR(36) NOT NULL UNIQUE,
    `owner_uuid` VARCHAR(36) NOT NULL,

    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP
);

create TABLE `orders` (
    `orders_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `member_id` BIGINT NOT NULL,
    `total_amount` DECIMAL(19,2) NOT NULL,
    `status` VARCHAR(25) NOT NULL,
    `payment_method` VARCHAR(50) NOT NULL,
    `order_uuid` VARCHAR(36) NOT NULL UNIQUE,

    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,

    CONSTRAINT `chk_order_status` CHECK (`status` IN ('PENDING', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT `fk_order_member` FOREIGN KEY (`member_id`) REFERENCES `member`(`member_id`) ON delete CASCADE
);

create TABLE `order_item` (
    `order_item_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `orders_id` BIGINT NOT NULL,
     `product_id` BIGINT NOT NULL,
     `quantity` INTEGER NOT NULL,
     `price` DECIMAL(19,2) NOT NULL,
     `owner_uuid` VARCHAR(36) NOT NULL,
     `order_item_uuid` VARCHAR(36) NOT NULL UNIQUE,

    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,

    CONSTRAINT `fk_order_item_orders` FOREIGN KEY (`orders_id`) REFERENCES `orders`(`orders_id`) ON delete CASCADE,
    CONSTRAINT `fk_order_item_product` FOREIGN KEY (`product_id`) REFERENCES `product`(`product_id`) ON delete CASCADE
);

create TABLE `cart` (
    `cart_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `member_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `quantity` INT NOT NULL,
    `cart_uuid` VARCHAR(36) NOT NULL UNIQUE,

    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,

    CONSTRAINT `fk_cart_member` FOREIGN KEY (`member_id`) REFERENCES `member`(`member_id`) ON delete CASCADE,
    CONSTRAINT `fk_cart_product` FOREIGN KEY (`product_id`) REFERENCES `product`(`product_id`) ON delete CASCADE,
    CONSTRAINT `unique_member_product` UNIQUE (`member_id`, `product_id`)
);

create TABLE `payment` (
    `payment_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `orders_id` BIGINT NOT NULL,
    `status` VARCHAR(50) NOT NULL,
    `payment_method` VARCHAR(100) NOT NULL,
    `owner_uuid` VARCHAR(36) NOT NULL,
    `payment_uuid` VARCHAR(36) NOT NULL UNIQUE,

    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,

    CONSTRAINT `fk_payment_order` FOREIGN KEY (`orders_id`) REFERENCES `orders`(`orders_id`) ON delete CASCADE,
    CONSTRAINT `chk_payment_status` CHECK (`status` IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED'))
);

create TABLE `product_image` (
    `image_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `product_id` BIGINT NOT NULL,
    `image_url` VARCHAR(255) NOT NULL,
    `owner_uuid` VARCHAR(36) NOT NULL,
    `product_image_uuid` VARCHAR(36) NOT NULL UNIQUE,

    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,

    CONSTRAINT `fk_product_image` FOREIGN KEY (`product_id`) REFERENCES `product`(`product_id`) ON delete CASCADE
);
