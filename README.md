# 🚠 Gestion-Station-Ski 🚠

Gestion-Station-Ski is a Spring Boot application designed to manage ski station operations with integrated DevOps for continuous deployment and automated data pipelines.

## 📜 Overview

Gestion-Station-Ski is a comprehensive application for managing ski stations, built using Spring Boot. It includes features for tracking ski passes, equipment rental, and weather data. It uses DevOps tools and data pipelines to automate the deployment process and handle large amounts of data in real-time.

## 🔧 Technologies Used

- **Backend**: 
  - 🖥️ **Spring Boot**
  - 🐱‍🏍 **Java 17**
  - 📊 **Spring Data JPA** (for database management)
  - 🛠️ **Spring Security** (for authentication)
- **Database**:
  - 🗄️ **MySQL**
- **DevOps**:
  - 🚀 **Jenkins** (for CI/CD)
  - 🎯 **Docker** (for containerization)
  - 🐋 **Kubernetes** (for orchestration)
  - 🌐 **NGINX** (for reverse proxy)
  - 📈 **Prometheus & Grafana** (for monitoring)
  - 🏗️ **Terraform** (for infrastructure as code)
  - 🔄 **GitHub Actions** (for deployment automation)
- **Data Pipelines**:
  - 🔄 **Apache Kafka** (for data streaming)
  - 🛢️ **Apache Hadoop** (for data storage)

## ⚙️ Features

- 🏞️ **Ski Station Management**: A user-friendly interface for managing various ski resort data like ski lift status, weather updates, and ski pass purchases.
- 📦 **Equipment Rental**: Allows users to rent ski equipment directly through the app.
- 🌤️ **Real-time Weather Data Integration**: Fetches and displays current weather updates for ski stations.
- 🛠️ **Admin Panel**: For administrative tasks such as tracking user activity, managing ski passes, and generating reports.
- 💳 **Payment Integration**: Allows users to purchase ski passes and equipment rental through an integrated payment gateway.

## 🏗️ Architecture

- The **Spring Boot** application serves as the core of the system.
- **NGINX** acts as a reverse proxy for better load balancing.
- The backend is containerized with **Docker** and orchestrated using **Kubernetes**.
- **Jenkins** automates the Continuous Integration/Continuous Deployment (CI/CD) pipeline.
- The **MySQL Database** stores the application's data and is integrated with Spring Data JPA for efficient data management.

![Alt text](https://www.google.com/url?sa=i&url=https%3A%2F%2Fprogrammaticponderings.com%2F2018%2F05%2F12%2Fdeploying-spring-boot-apps-to-aws-with-netflix-nebula-and-spinnaker-part-2-of-2%2F&psig=AOvVaw27sQE8Aa0iURPfiqQQKvcJ&ust=1740665856968000&source=images&cd=vfe&opi=89978449&ved=2ahUKEwjm0PHhw-GLAxUlc0EAHeXvO98QjRx6BAgAEBs)

## 🚀 Deployment Instructions

### Prerequisites

- 📦 **Java 17** or later
- 🐳 **Docker**
- ⚙️ **Kubernetes**
- 📋 **Jenkins**
- 🔧 **Terraform** (optional for infrastructure as code)
