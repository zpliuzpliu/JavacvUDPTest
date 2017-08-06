package com.sxito.p2pchat.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPserver extends Thread{



    @Override
    public void run() {
        super.run();
        try {
            Socket socket = new ServerSocket(38438).accept();
            socket.getOutputStream();

            new AcceptClientThread(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class AcceptClientThread extends Thread{
        private Socket socket;
        private BufferedReader reader = null;
        public AcceptClientThread(Socket socket){
            this.socket = socket;
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println(">>>>>>>>>>>>>>>>>>>>连接打开！");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            super.run();
            String newMesssage = null;

            while(true){
                try {
                    newMesssage = reader.readLine();

                    System.out.println("-----: "+newMesssage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
