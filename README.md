# Java-Based API Gateway with Secure Cloud Infrastructure Deployment

## Project Rationale and Executive Summary
This project constitutes the design, implementation, and deployment of a fully operational API Gateway, developed from foundational principles using Java. The system is deployed within a secure, custom-configured network architecture on Amazon Web Services (AWS). It is engineered to function as a robust, centralized entry point for a microservices-based backend, proficiently managing critical operations such as dynamic request routing, rate limiting, and response caching.

The principal objective of this initiative was to transcend conventional application development by demonstrating a proficient, holistic understanding of the architectural, networking, and security paradigms essential for constructing resilient, enterprise-grade backend systems.

---

## Core Functional Components
The API Gateway incorporates the following key functionalities, each implemented from scratch:

- **Dynamic HTTP Routing**  
  The gateway functions as a sophisticated reverse proxy, intelligently forwarding incoming client requests to the appropriate backend service based on URI path analysis. This methodology effectively decouples the client-facing interface from the internal microservice infrastructure.

- **Rate Limiting via Token Bucket Algorithm**  
  To safeguard backend services from potential abuse and to maintain high availability, the gateway integrates a thread-safe, in-memory rate limiting mechanism. This component, implemented for each unique client IP address, mitigates the risk of system degradation from excessive request volumes.

- **In-Memory Response Caching**  
  For idempotent GET requests, the gateway employs a caching strategy, storing responses for a predefined duration of 30 seconds. This capability substantially reduces latency for recurrent requests and alleviates computational load on backend services.

- **Secure Cloud Architecture**  
  The entire system is deployed on AWS within a bespoke Virtual Private Cloud (VPC). The gateway component is strategically positioned in a public subnet for internet accessibility, whereas the backend services are securely isolated within a private subnet devoid of direct internet access, thereby ensuring comprehensive protection of internal assets.

---

## System Architecture Diagram
The system architecture is meticulously designed to prioritize security and scalability. A core principle of this design is the complete isolation of backend services from the public internet. All inbound traffic is mandated to traverse the API Gateway, which serves as the single point of enforcement for all security and routing policies.

```text
+-----------------------------------------------------------------+
|                        The Internet                             |
+-----------------------------------------------------------------+
      |                                              ^
      | HTTP Request (Port 80)                       | Response
      v                                              |
+-----------------------------------------------------------------+
|                     AWS Cloud (eu-north-1)                      |
|                                                                 |
|  +------------------------- VPC -----------------------------+  |
|  |     (api-gateway-vpc / 10.0.0.0/16)                       |  |
|  |                                                           |  |
|  |  +--------- Public Subnet ---------------+  +-- Private Subnet --+  |
|  |  |                                      |  |                   |  |
|  |  |  +-----------------+                 |  |  +-------------+  |  |
|  |  |  |   EC2 Instance  |       Forwarded |  |  | EC2 Instance  |  |  |
|  |  |  |  API Gateway    |      Request    |  |  | Backend Svc |  |  |
|  |  |  | (gateway-app.jar) |--------------->|..|..| (backend-app.jar) |  |
|  |  |  |   Nginx Proxy   |       (Port 9001)|  |  | (No Public IP)|  |  |
|  |  |  +-----------------+  <---------------|  |  +-------------+  |  |
|  |  |      ^       |          Response      |  |      ^       |    |  |
|  |  |      |       |                        |  |      |       |    |  |
|  |  +------|-------+------------------------+  +------|-------+----+  |
|  |         |                                          |            |  |
|  |  Security Group (gateway-sg)                  Security Group (backend-sg) |
|  |  - Allows Port 80, 22 from Internet           - Allows Port 9001, 22 from gateway-sg |
|  |                                                                 |  |
|  +-----------------------------------------------------------------+  |
|                                                                         |
+-------------------------------------------------------------------------+

```
## 🛠️ Technology Stack

### 💻 Programming Language
- ![Java](https://img.shields.io/badge/Java-17-orange?logo=java&logoColor=white) **Amazon Corretto JDK 17**

### 📚 Core Libraries
- ⚡ `java.net.HttpServer` → Core web server component  
- 🔗 `java.net.http.HttpClient` → Programmatic request forwarding  
- 🧵 `java.util.concurrent.ConcurrentHashMap` → Thread-safe rate limiting & caching  

### 🌐 Web Server
- ![Nginx](https://img.shields.io/badge/Nginx-Reverse%20Proxy-green?logo=nginx&logoColor=white)  

### 🔧 Build Tool
- ![Maven](https://img.shields.io/badge/Maven-Build%20Automation-C71A36?logo=apachemaven&logoColor=white)  

### ☁️ Cloud Provider: AWS
- ![AWS](https://img.shields.io/badge/AWS-Cloud-orange?logo=amazonaws&logoColor=white)  
  - 🖥️ **EC2 (Elastic Compute Cloud):** Virtual server instances  
  - 🔒 **VPC (Virtual Private Cloud):** Isolated network environment  
  - 🌐 **Subnets:** Public & private separation  
  - 🛡️ **Security Groups:** Stateful firewalls  
  - 🗺️ **Route Tables & Internet Gateway:** Network traffic control  


# Build and Deployment Instructions

## Prerequisites
- Java Development Kit (JDK 17+)
- Apache Maven
- AWS account with credentials

---

## 1. Application Compilation

### Compile Gateway JAR
Ensure your pom.xml has:
- <mainClass>org.example.ApiGatewayServer</mainClass>

Run:
- mvn clean package

Rename:
- mv target/...-jar-with-dependencies.jar gateway-app.jar

### Compile Backend JAR
Modify pom.xml:
- <mainClass>org.example.MockBackendServer</mainClass>

Run:
- mvn clean package

Rename:
- mv target/...-jar-with-dependencies.jar backend-app.jar

---

## 2. AWS Infrastructure Deployment

### EC2 Provisioning
- Launch 2 × t2.micro (Amazon Linux 2023 AMI).
- Place:
  - API Gateway → Public Subnet
  - Backend → Private Subnet

### Security Groups
- gateway-sg: allow ports 80, 22 (internet).
- backend-sg: allow 9001, 22 (from gateway only).

### Artifact Deployment
- scp gateway-app.jar ec2-user@<gateway-public-ip>:/home/ec2-user/
- scp backend-app.jar ec2-user@<gateway-public-ip>:/home/ec2-user/
- From gateway → copy backend JAR to backend EC2.

### Install Java 17
- sudo amazon-linux-extras enable corretto17  
- sudo yum install java-17-amazon-corretto -y

### Configure Nginx (on gateway)
- sudo yum install nginx -y  
- Forward port 80 → 8080

### Run Applications
On backend-server:
- nohup java -jar backend-app.jar &

On gateway-server:
- nohup java -jar gateway-app.jar http://<backend-private-ip>:9001 &

---

## 3. Access Point
- Access via API Gateway public IPv4: http://<gateway-public-ip>

---
