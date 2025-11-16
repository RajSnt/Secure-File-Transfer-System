# Secure-File-Transfer-System
Overview
This project implements a secure file transfer solution in Java, providing a protected communication channel to transfer files between two endpoints (client and server).
It uses hybrid cryptographic techniques: AES for high-speed file encryption, and RSA for secure session key exchange, ensuring both confidentiality and secure key distribution.

Features:
1. AES-256 Encryption: Efficiently encrypts and decrypts files for secure transport.
2. RSA-2048 Key Exchange: Ensures only the authorized server can decrypt the AES session key.
3. Hybrid Protocol: Combines the strengths of symmetric and asymmetric cryptography.
4. Original Filename Preservation: Server saves the exact original filename and extension after decryption.
5. Swing GUI: Easy-to-use graphical interfaces for both client and server operations.
6. Multi-format Support: Works with any file type (DOCX, PDF, image, etc.).
7. Progress updates & logging: Both client and server GUIs feature status and logs to track operations.


Workflow:
1. Client User selects a file via a GUI.
2. Client generates an AES key and IV, and requests the server’s RSA public key.
3. Server responds with its public key.
4. Client encrypts the file with AES, encrypts the AES key with RSA, and sends the filename, encrypted AES key, IV, and encrypted file to the server.
5. Server receives all data, decrypts the AES key using its private RSA key, then decrypts the file, and saves it to the server_received/ folder using the original filename.
6. Logs and status are updated in both GUIs.


How to run:
1. Clone the server:
   git clone https://github.com/RajSnt/Secure-File-Transfer-System.git
2. Build the project:
   Open in IntelliJ IDEA / Eclipse, or build via command line:
     text
     javac -cp src src/**/*.java
3. Start the Server
   ->Run ServerGUI.java (or via Main if provided)
   ->Choose port and start; wait for client connection.
4. Start the Client
   ->Run ClientGUI.java
   ->Select a file, enter server IP and port, and click "Send File".
5.Verify Transfer
   On successful completion, check server_received/ for the received decrypted file (with original name and extension).


Dependencies:
1. Java 8+ (Tested with JDK 11–17)
2. Swing (for GUI components, comes with Java SE)
3. No external jar requirements for basic crypto (uses built-in javax.crypto and java.security)




