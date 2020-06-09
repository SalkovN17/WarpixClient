package com.company1234;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.*;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class WarPixClient extends JFrame{
	private static final long serialVersionUID = 1L; // ХЗ что это, но избавился от предупреждения
	
	private static DatagramSocket sendSocket;
    private static DatagramPacket sendPacket;
    private static DatagramSocket listenSocket;
    private static DatagramPacket listenPacket;
    private static JPanel jPanel = new JPanel();
    private static Rect[] rects = new Rect[256];


    public static void main(String[] args) throws IOException {
        listenSocket = new DatagramSocket(7000);
        new WarPixClient();
        while(true){
            System.out.println("InsideWhile");
            listenPacket = new DatagramPacket(new byte[256], 256);
            listenSocket.receive(listenPacket);
            System.out.println(listenPacket.getLength());
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
        timer(rects,jPanel);
    }

    public static void paint(Graphics g, Rect rect){
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawRect(rect.getRectangle().x , rect.getRectangle().y , rect.getRectangle().width, rect.getRectangle().height);
        switch(rect.getColorByte()){
            case 1:
                g2.setColor(Color.RED);
                break;
            case 2:
                g2.setColor(Color.GREEN);
                break;
            case 3:
                g2.setColor(Color.BLUE);
                break;
            case 0:
                g2.setColor(Color.WHITE);
            default:
                break;
        }
        g2.fillRect(rect.getRectangle().x , rect.getRectangle().y , rect.getRectangle().width, rect.getRectangle().height);
    }


    public static void draw(Rect[] rects, JPanel jPanel) {
        int w = jPanel.getWidth();
        int h = jPanel.getHeight();
        BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        bufferedImage.getGraphics().setColor(Color.BLACK);


        for (int i = 0; i < 256; i++) {
            paint(bufferedImage.getGraphics(), rects[i]);
        }

        jPanel.getGraphics().drawImage(bufferedImage, 0, 0, null);
    }


    public static void timer(Rect[] rects, JPanel jPanel){
        java.util.Timer timer = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                draw(rects, jPanel);
            }
        };
        timer.schedule(tt, 0, 10);
    }


    public static void jPanelInit(JPanel jPanel){
        jPanel.setSize(480,480);
        jPanel.setBackground(Color.white);
    }

    public void jFrameInit() throws IOException {
        setBounds(550, 150, 480 + 14, 480 + 37);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        sendSocket = new DatagramSocket();
        byte[] bytes = new byte[1];
        bytes[0] = 1;
        sendPacket = new DatagramPacket(bytes, 1, InetAddress.getByName("37.192.213.120"), 60000);
        sendSocket.send(sendPacket);
        listenPacket = new DatagramPacket(new byte[256], 256);
        listenSocket.receive(listenPacket);
        System.out.println(listenPacket.getLength());
        if(listenPacket.getLength() == 256){
            for(int i = 0; i < 256; i++){
                rects[i] = new Rect(i % 16 * 30, i / 16 * 30, listenPacket.getData()[i]);
            }
            //draw(rects, jPanel);
        }
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(e.getX());
                System.out.println(e.getY());
                for(int i = 0; i < 256; i++){
                    if(rects[i].getRectangle().contains(e.getX() - 7,e.getY() - 30 )){
                        byte[] sendBytes = new byte[2];
                        sendBytes[0] =  (byte)i;
                        Random r = new Random();
                        sendBytes[1] = (byte)(r.nextInt() %3 + 1);
                        try {
                            sendPacket = new DatagramPacket(sendBytes, 2, InetAddress.getByName("37.192.213.120"), 60000);
                            sendSocket.send(sendPacket);

                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
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

