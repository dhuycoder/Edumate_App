# EDUMATE - An Android Application with A.I. to help solve exercises
<br />
<div align="center">
  <a href="https://github.com/othneildrew/Best-README-Template">
    <img src="/app/src/main/res/drawable/edumate.png" alt="Logo" width="300" height="170">
  </a>

<h3 align="center">University Course Project</h3>

  <p align="center">
    <a href="https://github.com/Hg3L/UCP_EDUMATE-Backend-Server"><strong>Explore the project »</strong></a>
    <br />
  </p>
</div>


## 📚 About the Project

**EDUMATE** is an Android application designed to assist students in solving exercises by integrating advanced
**Artificial Intelligence (A.I)** capabilities. Whether you're stuck on a tough math problem or need help understanding
a complex concept, EDUMATE is your intelligent study companion.

This application is built following the **MVVM (Model-View-ViewModel)** architecture combined with clean code principles
in software engineering such as **SOLID**, **DRY**, and **KISS**, ensuring maintainability, scalability, and readability of the codebase.

This application is the part of a larger system developed using a **Client-Server architecture**. In this setup, the Android application
a[]()cts as the **client**, responsible for handling user interactions, managing UI logic, and communicating with a backend server
via RESTful APIs. The server handles data processing, A.I. operations, and user authentication.


### 🧩 System Overview

EDUMATE System is structured based on a **Client-Server Architecture**:

- 🖥️ **Backend**: Built with **Spring Boot**, responsible for handling business logic, authentication,
  A.I. processing, and data management. It exposes RESTful APIs for communication.
- 📱 **Android Client**: Acts as the user-facing application. It interacts with the backend to send
  user input and display processed results.
> ⚠️ This README focuses solely on the Android client-side implementation of the EDUMATE system.
> Check [EDUMATE - Backend Server](https://github.com/Hg3L/UCP_EDUMATE-Backend-Server) for more information about
> our server-side


### 🚀 Features:

- 📸 **Photo Recognition:**
    - **OCR** – Extract text from images to help you understand the content.
    - **Image Content Control** – Identify appropriate images, do not accept violent, obscene, porn images,...
- 🧠 **AI-Powered Solver** – Snap a photo or input a question and let AI guide you through the solution.
- 🔍 **Smart Search & Suggestions** – Read the question in the image, search for similar questions and provide answers based on
  Q&A history
- 💬 **Q&A with other users** – Easily post questions with images and get answers from the community.
- 🌐 **Social Integration** – Authenticate with Google and Facebook account.
- 📱 **User-Friendly Interface** – Designed for ease of use with a clean, intuitive layout.


### 🛠️ Tech Stack
> ❗ Not include Springboot backend.
- **Architecture**: MVVM
- **Language**: Java
- **JDK Version**: 21
- **Build Tool**: Groovy Gradle 8.11.1
- **Authentication Platform**: Firebase
- **Networking**: Retrofit 2.9.0
- **UI/UX**: Material Design Components

