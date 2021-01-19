package com.xie.song.XUNET.handler;

import com.xie.song.XUNET.service.Datagram;

import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 这个类用于解决TCP通信粘包问题
 */
public class FieldLengthSegmentationData implements HandlerData {

    /**
     * 记录描述消息长度的字段，占几位
     */
    private volatile int fieldLength = 4;

    /**
     * 保存每个SocketChannel发来的未完数据
     */
    private ConcurrentHashMap<SocketChannel,byte[]> socketCache = new ConcurrentHashMap<>();

    public FieldLengthSegmentationData() {

    }

    public FieldLengthSegmentationData(int fieldLength){
        this.fieldLength = fieldLength;
    }

    @Override
    public void read(HandlerChain chain, Datagram datagram) {
        SocketChannel socketChannel = datagram.getSocketChannel();
        if (!socketCache.contains(socketChannel)) {
            //得到总的字节数组
            byte[] target = datagram.getbData();
            //得到描述信息长度的字段
            byte[] preBytes = getPreBytes(target, fieldLength);
            //得到信息的长度
            int dataLength = getDataLength(preBytes);

            //目标总的信息刚好满足条件
            if (target.length == dataLength + fieldLength) {
                byte[] targetBytes = getTargetBytes(target, fieldLength);
                datagram.setbData(targetBytes);
                //刚好满足条件的数据，往下传输
                chain.read(datagram,this);
            } else {
                if (target.length < dataLength + fieldLength) {
                    //数据条件不满足，放入缓存里
                    socketCache.put(socketChannel,target);
                } else {
                    byte[] preBytes1 = getPreBytes(target, dataLength + fieldLength);
                    byte[] targetBytes = getTargetBytes(preBytes1, fieldLength);
                    datagram.setbData(targetBytes);
                    //获取其中满足的条件的一个数据，往下传输
                    chain.read(datagram,this);

                    //把剩余数据接着调用本方法
                    byte[] targetBytes1 = getTargetBytes(target, dataLength + fieldLength);
                    datagram.setbData(targetBytes1);
                    this.read(chain,datagram);
                }
            }
        } else {
            synchronized (socketChannel) {
                if (!socketCache.contains(socketChannel)) {
                    this.read(chain,datagram);
                }
                byte[] bytes = socketCache.get(socketChannel);
                socketCache.remove(socketChannel);
                //获取新传过来的字节数组
                byte[] newBytes = datagram.getbData();
                //获取一个拼接的新字节数组
                byte[] bytes1 = addPreBytes(newBytes, bytes);
                datagram.setbData(bytes1);
                //调用本方法，解析拼接字节数组
                this.read(chain, datagram);
            }
        }
    }


    /**
     * 把字节数组转换为对应的int值
     * @param bytes
     * @return
     */
    public int getDataLength(byte[] bytes) {
        int target = 0;
            target = bytes[0] << 24
                    | (bytes[1] << 24) >>> 8
                    | (bytes[2] << 8) & 0xff00
                    | bytes[3] & 0xff;
        return target;
    }

    /**
     * 把int转换为对应的字节数组
     * @param length
     * @return
     */
    public byte[] getBytesByLength(int length) {
        byte[] bytes = new byte[fieldLength];
        bytes[3] = (byte) (length & 0xff);
        bytes[2] = (byte) (length >> 8 & 0xff);
        bytes[1] = (byte) (length >> 16 & 0xff);
        bytes[0] = (byte) (length >> 24 & 0xff);
        return bytes;
    }


    /**
     * 获取字节数组里前面的对应几个字节
     * @param bytes 总的字节数组
     * @param pre 字节数组里描述信息长度的字段
     * @return
     */
    public byte[] getPreBytes(byte[] bytes,int pre) {
        byte[] bData = new byte[pre];
        for (int i = 0; i < pre; i++) {
            bData[i] = bytes[i];
        }
        return bData;
    }


    /**
     * 把描述信息长度的字段添加到信息里
     * @param target 信息
     * @param preBytes 描述信息长度的字段
     * @return
     */
    public byte[] addPreBytes(byte[] target,byte[] preBytes) {
        int allLength = target.length + preBytes.length;
        int preBytesLength = preBytes.length;
        byte[] fullBytes = new byte[allLength];

        for (int i = 0; i < preBytesLength; i++) {
            fullBytes[i] = preBytes[i];
        }

        for (int i = preBytesLength; i < allLength; i++) {
            fullBytes[i] = target[i - preBytesLength];
        }
        return fullBytes;
    }

    /**
     * 从总的字节数组中，获取目标信息字节数组
     * @param allBytes 总的字节数组
     * @param fieldLength 描述信息长度字段的长度
     * @return
     */
    public byte[] getTargetBytes(byte[] allBytes,int fieldLength) {
        int allLength = allBytes.length;
        byte[] bytes = new byte[allLength - fieldLength];
        for (int i = fieldLength; i < allLength; i++) {
            bytes[i - fieldLength] = allBytes[i];
        }
        return bytes;
    }


    /**
     * 给目标字节数组添加字段，以描述信息的长度
     * @param chain 处理链
     * @param datagram 数据报
     */
    @Override
    public void write(HandlerChain chain, Datagram datagram) {
        byte[] target = datagram.getbData();
        int length = target.length;
        byte[] bytesByLength = getBytesByLength(length);
        byte[] allBytes = addPreBytes(target, bytesByLength);
        //设置完整的字节数组（包含描述信息长度的字段）
        datagram.setbData(allBytes);

        //向下传输
        chain.write(datagram,this);
    }
}
