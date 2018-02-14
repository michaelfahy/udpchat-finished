/**	
 * UDP Client Program
 * Connects to a UDP Server
 * Receives a line of input from the keyboard and sends it to the server
 * Receives a response from the server and displays it.
 *
 * @author: Ryan Starback and Matthew Greenberg
 * ID: Ryan Starback: 2283933 (primary)
 * Email: Ryan Starback, starb102@mail.chapman.edu (primary)
 * Date: 9/24/2017
 * @ version: 2.1
 */


import java.io.*;
import java.net.*;

class Blue {
    public static void main(String args[]) throws Exception
    {

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("localhost"); 
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        int state = 0;

        String message = "Hello Blue";
        String response = "";

        DatagramPacket sendPacket = null;
        DatagramPacket receivePacket = null;

        while (state < 3) {
            sendData = new byte[1024];
            receiveData = new byte[1024];

            switch (state) {
                case 0:
                    // Send initial message and wait for response
                    sendData = message.getBytes();
                    sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
                    clientSocket.send(sendPacket);

                    // Receive response
                    receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    clientSocket.receive(receivePacket);
                    response = new String(receivePacket.getData());

                    if (response.substring(0, 3).equals("100")) {
                        System.out.print("You're the 1st client to connect – you must wait for the 2nd client before you can begin messaging.\n");
                        state = 1;
                    } else if (response.substring(0, 3).equals("200")) {
                        System.out.print("You're the 2nd client to connect – you must wait for the 1st client to send a message.\n");
                        state = 2;
                    }

                    break;
                case 1:
                    // 1st client to connect - wait to receive 200 to continue to state 2
                    receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    clientSocket.receive(receivePacket);
                    response = new String(receivePacket.getData());

                    if (response.substring(0, 3).equals("200")) {
                        System.out.print("The 2nd client has connected!\n");
                        System.out.print("Message (press enter to send): ");
                        message = inFromUser.readLine();

                        sendData = message.getBytes();
	                    sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
                        clientSocket.send(sendPacket);

                        state = 2;
                    }
                    
                    break;
                case 2:
                    // 2nd client to connect or > 1st message by 1st client
                    receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    clientSocket.receive(receivePacket);
                    response = new String(receivePacket.getData());
                    System.out.print("Message received: " + response + "\n");

                    // Exit if Goodbye received
                    if (response.length() >= 7 && response.substring(0, 7).equals("Goodbye")) {
                        state = 3;
                        break;
                    }

                    System.out.print("Message (press enter to send): ");
                    message = inFromUser.readLine();
                    sendData = message.getBytes();
                    sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
                    clientSocket.send(sendPacket);

                    break;
            }
        }

        clientSocket.close();
        System.exit(0);
    }
}
