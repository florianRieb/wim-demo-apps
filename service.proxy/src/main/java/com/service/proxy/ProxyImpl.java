package com.service.proxy;

import com.api.CalcService;

import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.PollItem;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.util.List;

public class ProxyImpl implements CalcService {
    private final static int REQUEST_TIMEOUT = 2500;    //  msecs, (> 1000!)
    private final static int REQUEST_RETRIES = 3;       //  Before we abandon
    private final static String SERVER_ENDPOINT = "tcp://localhost:5555";

    private ZContext ctx;
    private Socket client;
    private double avg = 0;

    //Empty constructor that the serviceloader is able to instantiated
    public ProxyImpl(){
        this.ctx = new ZContext();
        this.client = ctx.createSocket(ZMQ.REQ);
        assert (client != null);
        //build connection
        client.connect(SERVER_ENDPOINT);

    }


    @Override
    public double calc(List<Double> numbers) throws IOException {
        int retriesLeft = REQUEST_RETRIES;
        while (retriesLeft>0 && !Thread.currentThread().isInterrupted()){

            ZMsg reqMsg = new ZMsg();
            //numbers -> ZFrame -> ZMsg
            for(double d:numbers){
                ByteBuffer buf = ByteBuffer.allocate(8);
                buf.putDouble(d);
                buf.flip();
                reqMsg.add(new ZFrame(buf.array()));
            }
            reqMsg.send(client);

            int expect_reply = 1;
            while (expect_reply > 0) {
                //  Poll socket for a reply, with timeout
                ZMQ.PollItem items[] = {new ZMQ.PollItem(client, ZMQ.Poller.POLLIN)};
                Selector selector = Selector.open();
                int rc = ZMQ.poll(selector,items, REQUEST_TIMEOUT);

                if (rc == -1)
                    break;          //  Interrupted

                //  Here we process a server reply and exit our loop if the
                //  reply is valid. If we didn't a reply we close the client
                //  socket and resend the request. We try a number of times
                //  before finally abandoning:

                if (items[0].isReadable()) {
                    //  We got a reply from the server, must match sequence
                    byte[]reply = client.recv();

                    if (reply == null)
                        break;      //  Interrupted


                    if (reply.length>0) {
                        this.avg = ByteBuffer.wrap(reply).getDouble();
                        //System.out.printf("I: server replied OK (%s)\n", reply);
                        //retriesLeft = REQUEST_RETRIES;
                        retriesLeft = 0;
                        expect_reply = 0;

                    } else
                        System.out.printf("E: malformed reply from server: %s\n",
                                reply);

                } else if (--retriesLeft == 0) {
                    System.out.println("E: server seems to be offline, abandoning\n");
                    this.avg = -1;
                    break;
                } else {
                    System.out.println("W: no response from server, retrying\n");
                    //  Old socket is confused; close it and open a new one
                    ctx.destroySocket(client);
                    System.out.println("I: reconnecting to server\n");
                    client = ctx.createSocket(ZMQ.REQ);
                    client.connect(SERVER_ENDPOINT);
                    //  Send request again, on new socket
                    reqMsg.send(client);
                }
            }


        }

        return this.avg;
    }
}
