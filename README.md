# Java Client-Server Application

This repository contains a simple **Java client–server application** built using **TCP sockets**.  
It demonstrates how a client can connect to a server, exchange messages/data, and close the connection cleanly.

> Files in this repo:
> - `Server.java` – starts the server and waits for client connections  
> - `Client.java` – connects to the server and communicates with it  

---

## 🧩 Features

- Simple example of **Java socket programming**
- Separate **Client** and **Server** classes
- Command-line based interaction
- Easy to understand and extend for:
  - Chat applications  
  - File transfer  
  - Basic request–response systems  

*(Update or remove any feature that doesn’t match your actual code.)*

---
## 🚀 How to Run

1️⃣ Compile the files

Open terminal in the project folder and run:
   javac Server.java Client.java

2️⃣ Start the Server

Run:

   java Server

Example server output:

Server started on port 5000
Waiting for client connection...

3️⃣ Start the Client

Open another terminal window and run:

java Client


If your client asks for IP/Port:

Enter server IP: 127.0.0.1
Enter server port: 5000


Once connected, clients can chat securely with each other.

---

## 🛠 Tech Stack

- **Language:** Java (JDK 8+ or later)
- **Concepts used:**  
  - TCP Sockets (`ServerSocket`, `Socket`)  
  - Input/Output streams  
  - Basic networking & I/O

---

## 📁 Project Structure

```text
.
├── Client.java   # Java client implementation
├── Server.java   # Java server implementation
└── README.md     # Project documentation

