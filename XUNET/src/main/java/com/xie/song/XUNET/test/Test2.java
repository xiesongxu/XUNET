package com.xie.song.XUNET.test;

import com.xie.song.XUNET.Bootstrap;
import com.xie.song.XUNET.annotation.*;
import com.xie.song.XUNET.handler.*;
import com.xie.song.XUNET.service.Datagram;

import java.nio.channels.SocketChannel;
import java.util.List;

@Config(isServer = true,
        serverAddress = { @ServerAddress(address = "127.0.0.1",port = 8989),
                          @ServerAddress(address = "127.0.0.1",port = 8181) }
)
public class Test2 {

    static int i = 1;

    @AddHandlerChannel
    public void addHandlerChannel(List chain) {
        chain.add(new HandlerChannel() {
            @Override
            public void handler(HandlerChain chain, SocketChannel socketChannel) {
                System.out.println("Test2  处理成功=====299===========");
                chain.proceed(socketChannel);
            }

        });
    }

    @AddHandlerData
    public void addHandlerData(List chain){
        chain.add(new FieldLengthSegmentationData());
        chain.add(new RSAHandlerData());
        chain.add(new DecodeHandlerData());
        chain.add(new EncodeHandlerData());
//        chain.add(new DESHandlerData());
        chain.add(new HandlerData() {
            @Override
            public void read(HandlerChain chain, Datagram datagram) {
                String s = datagram.getsData();
                System.out.println((i++)+s);
                datagram.setsData("Test2=====");
                chain.write(datagram,this);
            }

            @Override
            public void write(HandlerChain chain, Datagram datagram) {
            }
        });
    }

    public static void main(String[] args) {
        Bootstrap.start(Test2.class);
    }

}
