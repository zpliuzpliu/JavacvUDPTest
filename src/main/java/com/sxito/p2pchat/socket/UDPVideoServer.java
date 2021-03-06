package com.sxito.p2pchat.socket;

import org.bytedeco.javacv.CanvasFrame;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPVideoServer {
    static final int INPORT = 1712;
    private byte[] buf = new byte[1000];
    private DatagramPacket dp = new DatagramPacket(buf, buf.length);
    private DatagramSocket socket;
    private CanvasFrame canvas;
    byte[] bytes = new byte[50000];

    public UDPVideoServer() {
        try {
            canvas = new CanvasFrame("摄像头");//新建一个窗口
            canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            canvas.setAlwaysOnTop(true);
            socket = new DatagramSocket(INPORT);
            System.out.println("Server started");
            String mess = "";
            int index = 0;
            while (true) {
                socket.receive(dp);
                mess = new String(dp.getData(), 0, dp.getLength());
                if (mess.contains("start")) {
                    bytes = new byte[Integer.parseInt(mess.split(":")[1])];
                    index = 0;
                } else if (mess.equals("over")) {
                    ByteArrayInputStream in = new ByteArrayInputStream(bytes);

                    BufferedImage image = null;
                    image = ImageIO.read(in);
                    canvas.showImage(image);
                } else {
                    byte[] b = dp.getData();
                    for (int a = 0; a < 1000; index++, a++) {
                        bytes[index] = b[a];
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            System.err.println("Can't open socket");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Communication error");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new UDPVideoServer();
    }
}
