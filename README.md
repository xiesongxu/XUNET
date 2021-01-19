# XUNET

### 这是一个基于Reactor模式的网络框架 【作者：谢松序 ,联系电话：13883840752，作为平时兴趣写的框架，功能尚未完善】

## 1.整体架构图




## 2.服务端的使用

```
@Config(isServer = true,
        serverAddress = { @ServerAddress(address = "127.0.0.1",port = 8989),
                          @ServerAddress(address = "127.0.0.1",port = 8181) }
)
public class Test2 {
    

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
                System.out.println(s);
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
```

## 3.客户端的使用

```
@Config(isServer = false,
        clientAddress = { @ClientAddress(address = "127.0.0.1",port = 8888,toAddress = "127.0.0.1",toPort = 8989)}
)
public class Test6 {

    static int i = 1;
    
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
        chain.add(new RSAHandlerData());
        chain.add(new DecodeHandlerData());
        chain.add(new EncodeHandlerData());

//        chain.add(new DESHandlerData());
        chain.add(new HandlerData() {
            @Override
            public void read(HandlerChain chain, Datagram datagram) {
                String s = datagram.getsData();
                System.out.println((i++)+s);
            }

            @Override
            public void write(HandlerChain chain, Datagram datagram) {
                datagram.setsData("898989");
                chain.write(datagram,this);
    
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
    
                for (int i = 0; i < 5; i++) {
                    datagram.setsData("xiesongxu谢松序");
                    chain.write(datagram,this);
                }
            }
        });
    }
    
    public static void main(String[] args) {
        Bootstrap.start(Test6.class);
    }

}
```

