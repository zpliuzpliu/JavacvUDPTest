package com.sxito.p2pchat.socket;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.*;

public class UDPAudioClient extends Thread {

    private DatagramSocket s;
    private InetAddress hostAddress;
    private byte[] buf = new byte[1000];
    private DatagramPacket dp = new DatagramPacket(buf, buf.length);
    private int id;

    public UDPAudioClient(int identifier) {
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
            AudioFormat audioFormat =
//                    new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100F,
//                    8, 1, 1, 44100F, false);
                    new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,44100F, 16, 2, 4,
                            44100F, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat); TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            targetDataLine.open(audioFormat);
            int nByte = 0;
            final int bufSize=4*100;
            byte[] buffer = new byte[bufSize];
            targetDataLine.start();
            while (nByte != -1) {
                nByte = targetDataLine.read(buffer, 0, bufSize);
                s.send(new DatagramPacket(buffer, bufSize, hostAddress, UDPAudioServer.INPORT));
            }


        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        new UDPAudioClient(0).start();

    }
}
