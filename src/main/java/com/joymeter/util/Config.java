package com.joymeter.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pan Shuoting
 * @version 1.0.0
 */
public class Config {
	
	/**
	 * 获取MQTT服务器链接
	 * @return
	 */
	public static String getMqttserverurl() {
		return getValue("mqttserver_url", "");
	}
	
	/**
	 * 获取客户端ID
	 * @return
	 */
	public static String getClientId() {
		return getValue("client_id", "myclient");
	}

	/**
	 * 获取订阅主题
	 * @return
	 */
	public static String[] getSubscribetopics() {
		return getValue("subscribe_topics", "").split("\\.");
	}
	
	/**
	 * 获取订阅主题对应QoS
	 * @return
	 */
	public static int[] getSubscribeqoss() {
		String[] propqoss=getValue("subscribe_qoss", "").split("\\.");
		int[] qoss = new int[propqoss.length];
		for(int i=0;i<propqoss.length;i++) {
			qoss[i]=Integer.parseInt(propqoss[i]);
		}
		return qoss;
	}
	
	/**
	 * 获取MQTT服务器链接
	 * @return
	 */
	public static String getUsername() {
		return getValue("username", "admin");
	}
	
	/**
	 * 获取客户端ID
	 * @return
	 */
	public static String getPassword() {
		return getValue("password", "123456");
	}
	
    /**
     * 获取值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getValue(final String key, final String defaultValue) {
        InputStream is = null;
        try {
            StringBuilder fileName = new StringBuilder();
            fileName.append(Config.getCurrentPath()).append("/Config.properties");
            is = new FileInputStream(fileName.toString());
            Properties prop = new Properties();
            prop.load(is);
            return prop.getProperty(key, defaultValue);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return defaultValue;
    }

    /**
     *
     * @return
     */
    public static String getCurrentPath() {
        return System.getProperty("user.dir");
    }
}
