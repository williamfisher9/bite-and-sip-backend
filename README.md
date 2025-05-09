# 🍽️ Bite & Sip - A Restaurant Management App – Frontend

# 🍽️ Restaurant Management App – Backend

This is the backend API for a full-featured restaurant management and e-commerce platform. Built with Java and Spring Boot, it powers user authentication, menu management, order processing, and secure payment handling via Stripe.

## 🛠️ Tech Stack

- **Java 17+**
- **Spring Boot**
- **Spring Security & JWT**
- **Stripe API** (for payments)
- **Maven**
- **MySQL / PostgreSQL** (or H2 for testing)
- **JPA / Hibernate**

## 🎯 Project Purpose

The backend supports both customer-facing and admin-facing operations, including:
- Secure user authentication and role-based access
- Menu item and category management
- Cart and coupon handling
- Payment processing via Stripe
- Order tracking and admin control features

## 🔐 Authentication & Security

- JWT-based login and registration
- Password hashing (BCrypt)
- Role-based access for Admin and Users
- Email verification and password reset logic (if implemented)

## 📦 Features

### User Endpoints
- Register, login, verify account
- View menu categories and items
- Add items to cart
- Apply coupons and checkout via Stripe
- View order history

### Admin Endpoints
- CRUD operations for users, categories, and products
- View and manage orders
- Dashboard summaries

## 🧩 Sample Endpoint List

| Method | Endpoint                        | Description                     |
|--------|----------------------------------|---------------------------------|
| POST   | `/api/auth/register`            | Register new user               |
| POST   | `/api/auth/login`               | Authenticate user               |
| GET    | `/api/menu/categories`          | Get all menu categories         |
| POST   | `/api/cart/add`                 | Add item to user’s cart         |
| POST   | `/api/payment/checkout`         | Stripe payment processing       |
| GET    | `/api/admin/dashboard`          | Admin dashboard summary         |



## 📚 Setup Instructions

1. **Clone the Repository**
```bash
git clone https://github.com/williamfisher9/bite-and-sip-backend
cd bite-and-sip-backend
```

2. **Build and Run**
```bash
mvn clean install
mvn spring-boot:run
```

## 🔗 Frontend Integration
This backend powers a React-based frontend client which handles:
- User interface and form input
- Session/token management
- Displaying menu items and order data
- Cart/checkout logic synced with backend APIs

## 🌐 Live Demo
[Link to Live Demo](https://willtechbooth.dev/biteandsip/)
