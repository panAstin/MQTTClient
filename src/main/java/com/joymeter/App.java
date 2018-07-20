package com.joymeter;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;

import com.joymeter.util.MQTTClientUtil;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	String configpath  = System.getProperty("user.dir")+File.separator+"log4j.properties";
    	PropertyConfigurator.configure(configpath);
        MQTTClientUtil.getInstance();
    }
}
