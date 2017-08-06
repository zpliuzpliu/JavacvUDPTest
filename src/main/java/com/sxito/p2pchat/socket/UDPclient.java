package com.sxito.p2pchat.socket;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class UDPclient extends Thread {

    private DatagramSocket s;
    private InetAddress hostAddress;
    private byte[] buf = new byte[1000];
    private DatagramPacket dp = new DatagramPacket(buf, buf.length);
    private int id;
    static OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

    public UDPclient(int identifier) {
        id = identifier;
        try {
            s = new DatagramSocket();
            hostAddress = InetAddress.getByName("localhost");

        } catch (UnknownHostException e) {
            System.err.println("Cannot find host");
            System.exit(1);
        } catch (SocketException e) {
            System.err.println("Can't open socket");
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("ChatterClient starting");
    }

    public void run() {
        try {
            Loader.load(opencv_objdetect.class);
            OpenCVFrameGrabber grabber = OpenCVFrameGrabber.createDefault(0);
            grabber.start();
            Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
            String mess = "";
            byte[] starMess = mess.getBytes();
            byte[] bytes = null;
            double split_length = 1000;
            int array_length = 0;
            int from, to;
            while (true) {
                BufferedImage bufferedImage = java2DFrameConverter.convert(grabber.grab());
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpg", bao);
                bytes = bao.toByteArray();
                array_length = (int) Math.ceil(bytes.length / split_length);
                mess = "start:" + array_length*1000;
                starMess = mess.getBytes();
                s.send(new DatagramPacket(starMess, starMess.length, hostAddress, UDPserver.INPORT));
                for (int i = 0; i < array_length ; i++){
                    from = (int) (i * split_length);
                    to = (int) (from + split_length);
                    if ( to > bytes.length){
                        to = bytes.length;
                    }
                    byte[] newByte = Arrays.copyOfRange(bytes, from, to);
                    s.send(new DatagramPacket(newByte, newByte.length, hostAddress, UDPserver.INPORT));
                }
                mess = "over";
                starMess = mess.getBytes();
                s.send(new DatagramPacket(starMess, starMess.length, hostAddress, UDPserver.INPORT));
                Thread.sleep(50);//50毫秒刷新一次图像
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        new UDPclient(0).start();

    }
}
