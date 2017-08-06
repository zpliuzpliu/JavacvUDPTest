package com.sxito.p2pchat.socket;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPAudioServer {
    static final int INPORT = 1713;
    private byte[] buf = new byte[1000];
    private DatagramPacket dp = new DatagramPacket(buf, buf.length);
    private DatagramSocket socket;

    public UDPAudioServer() {
        try {
            AudioFormat audioFormat =
                    new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,44100F, 16, 2, 4,
                            44100F, true);
            socket = new DatagramSocket(INPORT);
            System.out.println("Server started");
            final SourceDataLine sourceDataLine;
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceDataLine.open(audioFormat);

            sourceDataLine.start();
            FloatControl fc=(FloatControl)sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
            double value=2;
            float dB = (float) (Math.log(value==0.0?0.0001:value)/Math.log(10.0)*20.0);
            fc.setValue(dB);
            final int bufSize=4*100;
            byte[] buffer = new byte[bufSize];


            while (true) {
                socket.receive(dp);
                sourceDataLine.write(dp.getData(), 0, bufSize);
            }
        } catch (SocketException e) {
            System.err.println("Can't open socket");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Communication error");
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        new UDPAudioServer();
    }
}
