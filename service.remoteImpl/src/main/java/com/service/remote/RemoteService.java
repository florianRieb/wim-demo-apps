package com.service.remote;

import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class RemoteService {
    static final String  PORT = "5555";
    static final Logger LOGGER =  Logger.getLogger(RemoteService.class.getName());


    public static void main(String... args){
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket server = context.socket(ZMQ.REP);
        server.bind("tcp://*:5555");
        List<Double> numbers;


        LOGGER.info("Server listen on PORT:" + PORT);
        while(!Thread.currentThread().isInterrupted()){
            double avg = 0;
            numbers = new ArrayList<>();
            ZMsg recvMsg = ZMsg.recvMsg(server);
            if(recvMsg.size()>1){
                LOGGER.info("server received a message consists of "+  recvMsg.size() +"frames ");

                //lese Nachricht aus
                Iterator<ZFrame> frames = recvMsg.iterator();
                while(frames.hasNext()){
                    ZFrame frame = frames.next();
                    //byte[] -> double
                    numbers.add(ByteBuffer.wrap(frame.getData()).getDouble());
                }

                System.out.println("Msg Inhalt:" + numbers);

                //do Cald()
                double sum =0;
                for(double n:numbers){
                    sum+=n;
                }

                avg = sum / numbers.size();
                LOGGER.info("calc() result: " + avg);
            }
            //double -> byte[]
            byte[] bytes = new byte[8];
            ByteBuffer.wrap(bytes).putDouble(avg);
            server.send(bytes);


        }

    }


}
