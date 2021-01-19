package com.xie.song.XUNET.test;

import com.xie.song.XUNET.Bootstrap;
import com.xie.song.XUNET.annotation.AddHandlerChannel;
import com.xie.song.XUNET.annotation.AddHandlerData;
import com.xie.song.XUNET.annotation.ClientAddress;
import com.xie.song.XUNET.annotation.Config;
import com.xie.song.XUNET.handler.*;
import com.xie.song.XUNET.service.Datagram;

import java.nio.channels.SocketChannel;
import java.util.List;


@Config(isServer = false,
        clientAddress = { @ClientAddress(address = "127.0.0.1",port = 8889,toAddress = "127.0.0.1",toPort = 8989)}
)
public class Test7 {

    @AddHandlerChannel
    public void addHandlerChannel(List chain) {
        chain.add(new HandlerChannel() {
            @Override
            public void handler(HandlerChain chain, SocketChannel socketChannel) {
                System.out.println("Test2  处理成功=====299===========");
            }

        });
    }

    @AddHandlerData
    public void addHandlerData(List chain){
        chain.add(new FieldLengthSegmentationData());
        chain.add(new DecodeHandlerData());
        chain.add(new EncodeHandlerData());
        chain.add(new HandlerData() {
            @Override
            public void read(HandlerChain chain, Datagram datagram) {
                String s = datagram.getsData();
                System.out.println(s);
            }

            @Override
            public void write(HandlerChain chain, Datagram datagram) {
                datagram.setsData("Test7=====");
            }
        });
    }

    public static void main(String[] args) {
        Bootstrap.start(Test7.class);
    }

}
