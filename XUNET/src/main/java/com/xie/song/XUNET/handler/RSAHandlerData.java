package com.xie.song.XUNET.handler;


import com.xie.song.XUNET.service.Datagram;

import java.nio.channels.SocketChannel;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA加密处理器
 */
public class RSAHandlerData implements HandlerData {


    //判断当前通道是否需要进行RSA加密解密，在这个容器里就需要RSA加密
    private boolean channelUseRSA = false;

    //记录传输过来通道的公钥
    private ConcurrentHashMap<SocketChannel,byte[]> channelPublicKey = new ConcurrentHashMap<>();

    //本通道的公钥
    private byte[] basisPublicKey;

    //本通道的密钥
    private byte[] basisPrivateKey;

    //表示当前是否是公钥的字段长度
    private final int preByteNum = 4;

    //如果等于这个数字就是公钥
    private final int isPublicKey = 898989;

    public RSAHandlerData() {
        try {
            //获取本通道的密钥对
            genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     *  对读取的数据进行解密
     *  或进行存放传输过来客户端通道对应的公钥
     * @param chain 处理器链
     * @param datagram 数据报
     */
    @Override
    public void read(HandlerChain chain, Datagram datagram) {
        byte[] bytes = datagram.getbData();
        byte[] preBytes = getPreBytes(bytes, preByteNum);
        int targetInt = getIntFromBytes(preBytes);
        if (targetInt == isPublicKey) {
            if (channelPublicKey.get(datagram.getSocketChannel()) == null) {
                byte[] targetBytes = getTargetBytes(bytes, preByteNum);
                //存放传送过来通道的公钥
                channelPublicKey.put(datagram.getSocketChannel(), targetBytes);
                if (!channelUseRSA) {
                    byte[] writeBytes = addPreBytes(basisPublicKey, preBytes);
                    datagram.setbData(writeBytes);
                    chain.write(datagram, this);
                }
            } else {
                //todo
            }
        } else if (channelPublicKey.get(datagram.getSocketChannel()) != null) {
            System.out.println("加密后传输的数据为；；；；"+ new String(bytes));
            try {
                //私钥解密
                byte[] decrypt = decrypt(bytes, basisPrivateKey);
                datagram.setbData(decrypt);
                chain.read(datagram,this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            chain.read(datagram, this);
        }
    }


    /**
     * 把字节数组转换为对应的int值
     * @param bytes
     * @return
     */
    public int getIntFromBytes(byte[] bytes) {
        int target = 0;
        target = bytes[0] << 24
                | (bytes[1] << 24) >>> 8
                | (bytes[2] << 8) & 0xff00
                | bytes[3] & 0xff;
        return target;
    }

    /**
     * 把int转换为对应的字节数组
     * @param num
     * @return
     */
    public byte[] getBytesFromInt(int num) {
        byte[] bytes = new byte[preByteNum];
        bytes[3] = (byte) (num & 0xff);
        bytes[2] = (byte) (num >> 8 & 0xff);
        bytes[1] = (byte) (num >> 16 & 0xff);
        bytes[0] = (byte) (num >> 24 & 0xff);
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
     * 从总的字节数组中，获取目标字节数组
     * @param allBytes 总的字节数组
     * @param fieldLength 前面要减去的字节长度，从头开始减
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
     * 添加一个字节到目标字节里
     * @param target 目标字节
     * @param preBytes 添加的字节，加在目标字节的前面
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
     * 写出数据
     * @param chain 处理链
     * @param datagram 数据报
     */
    @Override
    public void write(HandlerChain chain, Datagram datagram) {
        byte[] bytes = datagram.getbData();
        String string = new String(bytes);
        if (string.equals("898989")) {
            channelUseRSA = true;
            byte[] bytesFromInt = getBytesFromInt(898989);
            byte[] bytes1 = addPreBytes(basisPublicKey, bytesFromInt);
            datagram.setbData(bytes1);
            chain.write(datagram,this);
        } else {
            byte[] encrypt = null;
            try {
                SocketChannel socketChannel = datagram.getSocketChannel();
                byte[] publicKey = channelPublicKey.get(socketChannel);
                encrypt = encrypt(datagram.getbData(), publicKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
            datagram.setbData(encrypt);
            chain.write(datagram,this);
        }
    }

    /**
     * 随机生成密钥对
     * @throws NoSuchAlgorithmException
     */
    public void genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(1024,new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        // 得到公钥
        basisPublicKey = Base64.encodeBase64(publicKey.getEncoded());
        // 得到私钥
        basisPrivateKey = Base64.encodeBase64((privateKey.getEncoded()));
    }

    /**
     * RSA公钥加密
     *
     * @param targetBytes
     *            加密字符串
     * @param publicKey
     *            公钥
     * @return 密文
     * @throws Exception
     *             加密过程中的异常信息
     */
    public byte[] encrypt(byte[] targetBytes, byte[] publicKey) throws Exception{
        byte[] Key = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Key));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] outStr = Base64.encodeBase64(cipher.doFinal(targetBytes));
        return outStr;
    }

    /**
     * RSA私钥解密
     *
     * @param targetBytes
     *            加密字符串
     * @param privateKey
     *            私钥
     * @return 铭文
     * @throws Exception
     *             解密过程中的异常信息
     */
    public byte[] decrypt(byte[] targetBytes, byte[] privateKey) throws Exception{
        byte[] key = Base64.decodeBase64(privateKey);
        //64位解码加密后的字符串
        byte[] inputByte = Base64.decodeBase64(targetBytes);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(key));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        byte[] outStr = cipher.doFinal(inputByte);
        return outStr;
    }


}
