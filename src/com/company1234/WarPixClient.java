package com.company1234;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.*;

public class WarPixClient extends JFrame{
	private static final long serialVersionUID = 1L; // ХЗ что это, но избавился от предупреждения
	
	private static DatagramSocket sendSocket;
    private static DatagramPacket sendPacket;
    private static DatagramSocket listenSocket;
    private static DatagramPacket listenPacket;
    private static JPanel jPanel = new JPanel();
    private static Rect[] rects = new Rect[256];
    private static byte[] listenBytes = new byte[256];


    public static void main(String[] args) throws IOException {
        new WarPixClient();
        while(true){
            listenSocket = new DatagramSocket(7000, InetAddress.getByName("37.193.176.157"));
            listenPacket = new DatagramPacket(listenBytes, 256);
            listenSocket.receive(listenPacket);
            if(listenPacket.getLength() == 256){
                for(int i = 0; i < 256; i++){
                    rects[i] = new Rect(i % 16 * 30, i / 16 * 30, listenPacket.getData()[i]);
                }
            }
        }
    }

    WarPixClient() throws IOException {
        super("WarPix");
        jPanelInit(jPanel);
        jFrameInit();
        add(jPanel);
        setVisible(true);
    }

    public static void jPanelInit(JPanel jPanel){
        jPanel.setSize(480,480);
        jPanel.setBackground(Color.white);
    }

    public void jFrameInit() throws IOException {
        setBounds(550, 150, 480, 480);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        sendSocket = new DatagramSocket();
        byte[] bytes = new byte[1];
        bytes[0] = 1;
        sendPacket = new DatagramPacket(bytes, 1, InetAddress.getByName("37.192.213.120"), 60000);
        sendSocket.send(sendPacket);
    }
    
    @Override
    public void dispose() {
    	listenSocket.close();
    	try {
			sendPacket = new DatagramPacket(new byte[1], 1, InetAddress.getByName("37.192.213.120"), 60000);
			sendSocket.send(sendPacket);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	sendSocket.close();
    	super.dispose();
    	System.exit(0);
    }
}

