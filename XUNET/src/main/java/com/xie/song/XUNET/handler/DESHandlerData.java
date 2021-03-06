package com.xie.song.XUNET.handler;

import com.xie.song.XUNET.service.Datagram;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;

/**
 * DES加密处理器
 */
public class DESHandlerData implements HandlerData {

    //算法密匙
    private static final byte[] DES_KEY = {112, 21, 1, -110, 82, -32, -85, -128, -65 ,44,-2};

    /**
     * 读数据
     * @param chain 处理器链
     * @param datagram 数据报
     */
    @Override
    public void read(HandlerChain chain, Datagram datagram) {
        String s = datagram.getsData();
        System.out.println("加密后：：："+s);
        String s1 = decryptBasedDes(s);
        datagram.setsData(s1);
        chain.read(datagram,this);
    }

    /**s
     * 写数据
     * @param chain 处理链
     * @param datagram 数据报
     */
    @Override
    public void write(HandlerChain chain, Datagram datagram) {
        String s = datagram.getsData();
        String s1 = encryptBasedDes(s);
        datagram.setsData(s1);
        chain.write(datagram,this);
    }


    /**
     * 数据加密，算法（DES）
     *
     * @param data
     *            要进行加密的数据
     * @return 加密后的数据
     */
    public static String encryptBasedDes(String data) {
        String encryptedData = null;
        try {
            // DES算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();
            DESKeySpec deskey = new DESKeySpec(DES_KEY);
            // 创建一个密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(deskey);
            // 加密对象
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key, sr);
            // 加密，并把字节数组编码成字符串
            encryptedData = new sun.misc.BASE64Encoder().encode(cipher.doFinal(data.getBytes()));
        } catch (Exception e) {
//            log.error("加密错误，错误信息：", e);  加密后UpSbNP/AeM5AfGnWB21HZQ== UpSbNP/AeM5AfGnWB21HZQ==
            throw new RuntimeException("加密错误，错误信息：", e);
        }
        return encryptedData;
    }

    /**
     * 解密
     * @param cryptData
     * @return
     */
    public static String decryptBasedDes(String cryptData) {
        String decryptedData = null;
        try {
            // DES算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();
            DESKeySpec deskey = new DESKeySpec(DES_KEY);
            // 创建一个密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(deskey);
            // 解密对象
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key, sr);
            // 把字符串解码为字节数组，并解密
            decryptedData = new String(cipher.doFinal(new sun.misc.BASE64Decoder().decodeBuffer(cryptData)));
        } catch (Exception e) {
//            log.error("解密错误，错误信息：", e);
            throw new RuntimeException("解密错误，错误信息：", e);
        }
        return decryptedData;
    }
}
