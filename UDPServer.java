/**
*	UDP Server Program
*	Listens on a UDP port
*	Receives a line of input from a UDP client
*	Returns an upper case version of the line to the client
*
*	@author: Michael Fahy
@	version: 2.0
*/

import java.io.*;
import java.net.*;

class UDPServer {
	
  public static void main(String args[]) throws Exception
    {
    DatagramSocket serverSocket = null;
	int port = 0;
	  
	try
		{
			serverSocket = new DatagramSocket(9876);
		}
	
	catch(Exception e)
		{
			System.out.println("Failed to open UDP socket");
			System.exit(0);
		}

      byte[] receiveData = new byte[1024];
      byte[] sendData  = new byte[1024];

      while(true)
        {
          DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		  
          serverSocket.receive(receivePacket);
		  
          String sentence = new String(receivePacket.getData());

          InetAddress IPAddress = receivePacket.getAddress();

          port = receivePacket.getPort();

          String capitalizedSentence = sentence.toUpperCase();

          sendData = capitalizedSentence.getBytes();

          DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);

          serverSocket.send(sendPacket);
        }
    }
}
