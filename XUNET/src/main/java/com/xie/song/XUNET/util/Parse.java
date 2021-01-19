package com.xie.song.XUNET.util;

import com.xie.song.XUNET.annotation.*;
import com.xie.song.XUNET.config.ClientIpAddress;
import com.xie.song.XUNET.config.Configuration;
import com.xie.song.XUNET.config.ServerIpAddress;
import com.xie.song.XUNET.exception.ExistException;
import com.xie.song.XUNET.exception.IpAddressException;
import com.xie.song.XUNET.exception.ParameterLengthException;
import com.xie.song.XUNET.exception.PortException;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

/**
 * 用于解析配置类，
 * 如果配置类没有配置那么将使用默认的配置
 */
public class Parse {

    private Configuration configuration;

    /**
     * 默认资源的指定位置
     */
    private static final String DEFAULT_CONFIG_FILE = "configuration.properties";

    private static final String HANDLER_CHANNEL = "com.xie.song.XUNET.handler.HandlerChannel";

    private static final String HANDLER_DATA = "com.xie.song.XUNET.handler.HandlerData";

    private static final Properties properties = new Properties();


    static {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Parse() {
        configuration = new Configuration();
    }

    /**
     * 开始解析配置类的注解 和 配置类里的方法
     * @param target 配置类
     * @return
     * @throws ExistException
     */
    public Configuration parse(Object target) throws ExistException{
        Object targetConfig = target;
        Class<?> aClass = target.getClass();
        Config config = aClass.getAnnotation(Config.class);
        if(config == null) {
            throw new ExistException("Config 注解不存在");
        }
        try {
            //初始化配置
            initConfiguration(config);
        } catch(Exception e) {
            e.printStackTrace();
        }
        //解析方法
        Method[] methods = aClass.getDeclaredMethods();
        for(Method method : methods){
            AddHandlerData handlerData = method.getAnnotation(AddHandlerData.class);
            if(handlerData != null){
                try {
                    parseDataHandlerMethod(targetConfig,method);
                } catch (ParameterLengthException e) {
                    e.printStackTrace();
                }
            } else if(method.getAnnotation(AddHandlerChannel.class) != null){
                try {
                    parseChannelHandlerMethod(targetConfig,method);
                } catch (ParameterLengthException e) {
                    e.printStackTrace();
                }
            } else {
                continue;
            }
        }

        boolean inspectConfiguration = inspectConfiguration();
        if(inspectConfiguration) {
            registerDefaultConfiguration();
        }
        configuration.initDataHandlerChain();
        return configuration;
    }


    /**
     * 解析ChannelHandler方法，在处理中会给方法注入一个ChannelHandlerChain
     * @param targetConfig 配置类
     * @param method 加@AddHandlerChannel注解的方法
     * @throws ParameterLengthException
     */
    public void parseChannelHandlerMethod(Object targetConfig,Method method) throws ParameterLengthException {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if(parameterTypes.length > 1){
            throw new ParameterLengthException("只能有一个参数");
        }
        Class<?> parameterType = parameterTypes[0];
        if(List.class.isAssignableFrom(parameterType) || parameterType == List.class) {
            try {
                method.setAccessible(true);
                method.invoke(targetConfig,configuration.getChannelHandlerChain().getHandlerChain());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解析DataHandler方法，在处理中会给方法注入一个DataHandlerChain
     * @param targetConfig 配置类
     * @param method 加@AddHandlerData注解的方法
     * @throws ParameterLengthException
     */
    public void parseDataHandlerMethod(Object targetConfig,Method method) throws ParameterLengthException {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if(parameterTypes.length > 1){
            throw new ParameterLengthException("只能有一个参数");
        }
        Class<?> parameterType = parameterTypes[0];
        if (List.class.isAssignableFrom(parameterType) || parameterType == List.class) {
            try {
                method.setAccessible(true);
                method.invoke(targetConfig,configuration.getDataHandlerChain().getHandlerChain());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 对框架配置进行初始化
     * @param config 传入配置类加的@Config注解对象
     * @throws PortException
     */
    public void initConfiguration(Config config) throws PortException {
        boolean isServer = config.isServer();
        try {
            configIpAddress(config,isServer);
        } catch (IpAddressException e) {
            e.printStackTrace();
        }
        configuration.setServer(isServer);
        configuration.init();
    }

    /**
     * 配置服务端IP地址或客户端的IP地址
     * @param config config注解
     * @param isServer 判断是否为服务端
     */
    public void configIpAddress(Config config,boolean isServer) throws IpAddressException {
        if (isServer) {
            ServerAddress[] serverAddresses = config.serverAddress();
            for (ServerAddress server : serverAddresses) {
                ServerIpAddress serverIpAddress = new ServerIpAddress(server.address(), server.port());
                configuration.addServerAddresses(serverIpAddress);
            }
        } else {
            ClientAddress[] clientAddresses = config.clientAddress();
            for (ClientAddress client : clientAddresses) {
                if (client.toAddress().equals("") || client.toAddress() == null || client.port() <= 0) {
                    throw new IpAddressException("客户端要连接的地址没有给出，或端口小于等于0");
                }
                ClientIpAddress clientIpAddress = new ClientIpAddress(client.address(), client.port(), client.toAddress(), client.toPort());
                configuration.addClientAddresses(clientIpAddress);
            }
        }
    }

    /**
     * 判断框架配置是否完整
     * @return false:完整配置
     *         true:不完整配置
     */
    public boolean inspectConfiguration() {
        boolean inspect = configuration.inspect();
        return inspect;
    }

    /**
     * 如果框架配置不完整，将会使用默认配置
     *
     */
    public void registerDefaultConfiguration() {
        if(configuration.getChannelHandlerChain().isEmpty()) {
            String s = (String)properties.get(HANDLER_CHANNEL);
            String[] classes = s.split(",");
            Object[] objects = ClassUtil.createObjectsByStrings(classes);
            configuration.getChannelHandlerChain().getHandlerChain().add(objects);
        }

        if(configuration.getDataHandlerChain().isEmpty()){
            String s = (String)properties.get(HANDLER_DATA);
            String[] classes = s.split(",");
            Object[] objects = ClassUtil.createObjectsByStrings(classes);
            configuration.getDataHandlerChain().getHandlerChain().add(objects);
        }
    }
}
