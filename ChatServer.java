/**
* UDP Server Program
* Listens on a UDP port
* Receives a line of input from a UDP client
* Returns an upper case version of the line to the client
*
* @author: Ryan Starback and Matthew Greenberg
* ID: Ryan Starback: 2283933 (primary)
* Email: Ryan Starback, starb102@mail.chapman.edu (primary)
* Date: 9/24/2017
* @ version: 2.1
*/

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class ChatServer {
  public static void main(String[] args) throws Exception {

    DatagramSocket serverSocket = null;
    int port = 0;
    int port1 = 0; // Port nymber of the first client to connect
    int port2 = 0; // Port number of teh second client to connect
    String name1 = ""; // Name of the first client
    String name2 = ""; // Name of the second client
    InetAddress ipAddress = null;
    InetAddress ipAddress1 = null; // IP Address of the first client
    InetAddress ipAddress2 = null; // IP Address of teh second client
    String message = "";
    String response = "";
    DatagramPacket receivePacket;
    DatagramPacket sendPacket;
    int state = 0;
    byte[] receiveData = new byte[1024];
    byte[] sendData  = new byte[1024];
    byte[] messageBytes = new byte[1024];

    try {
      serverSocket = new DatagramSocket(9876);
    } catch (Exception e) {
      System.out.println("Failed to open UDP socket");
      System.exit(0);
    }

    while (state < 3) {
      receiveData = new byte[1024];
      sendData  = new byte[1024];

      switch (state) {
        case 0:
          // Wait for 1st client to connect
          // then  notify 1st client that they're connected via `100` message
          System.out.println("Server running on port 9876");
          System.out.println("waiting for the 1st client to connect.\n");

          receivePacket = new DatagramPacket(receiveData, receiveData.length);
          serverSocket.receive(receivePacket);
          String receivedString = new String(receivePacket.getData());

          if (receivedString.substring(0, 5).equals("Hello")) {
            // Client 1 - store ip address + port
            name1 = receivedString.substring(6);
            ipAddress1 = receivePacket.getAddress();
            port1 = receivePacket.getPort();

            System.out.println("1st client (" + name1 + ") connected!");

            sendData = "100".getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress1, port1);
            serverSocket.send(sendPacket);

            state = 1;
          }

          break;
        case 1:
          // Wait for 2nd client to connect
          // then notify both clients that 2nd client connected via `200` message
          System.out.println("Waiting for the 2nd client to connect.");

          receivePacket = new DatagramPacket(receiveData, receiveData.length);
          serverSocket.receive(receivePacket);
          receivedString = new String(receivePacket.getData());

          if (receivedString.substring(0, 5).equals("Hello")) {
            // Client 2 - store ip address + port
            name2 = receivedString.substring(6);
            ipAddress2 = receivePacket.getAddress();
            port2 = receivePacket.getPort();

            System.out.println("2nd client (" + name2 + ") connected! Clients can now chat!");

            // Send 200 to both clients
            sendData = "200".getBytes();

            sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress1, port1);
            serverSocket.send(sendPacket);

            sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress2, port2);
            serverSocket.send(sendPacket);

            state = 2;
          }
          break;
        case 2:
          // Wait until either client says Goodbye to exit
          receivePacket = new DatagramPacket(receiveData, receiveData.length);
          serverSocket.receive(receivePacket);
          response = new String(receivePacket.getData());

          if (response.length() >= 7 && response.substring(0, 7).equals("Goodbye")) {
            if ((port == port1) && (ipAddress.equals(ipAddress1))) {
              System.out.println("\n-------\n1st client (" + name1 + ") sent Goodbye. Exiting...");
            } else {
              System.out.println("\n-------\n2nd client (" + name2  + ") sent Goodbye. Exiting...");
            }

            state = 3;
            break;
          }

          // Store received ip address, port, and message
          ipAddress = receivePacket.getAddress();
          port = receivePacket.getPort();
          receivedString = new String(receivePacket.getData());

          if ((port == port1) && (ipAddress.equals(ipAddress1))) {
            ipAddress = ipAddress2;
            port = port2;
          } else {
            ipAddress = ipAddress1;
            port = port1;
          }

          sendData = receivedString.getBytes();
          sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
          serverSocket.send(sendPacket);

          break;

        default:
          break;
      }
    }

    // Send Goodbye to both clients and exit
    sendData = "Goodbye".getBytes();

    sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress1, port1);
    serverSocket.send(sendPacket);

    sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress2, port2);
    serverSocket.send(sendPacket);

    serverSocket.close();
    System.exit(0);
  }
}
