package com.joymeter.util;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.alibaba.fastjson.JSONObject;
/**
 * MQTT客户端
 * @author Pan Shuoting
 * @version 1.0.0
 */
public class MQTTClientUtil {
	private static final Logger logger = Logger.getLogger(MQTTClientUtil.class.getName());
	
	private static MqttClient mqttClient = null;
	
	private static String MQTTSERVER_URL = Config.getMqttserverurl();
	private static String CLIENT_ID = Config.getClientId();
	private static String[] SUBSCRIBE_TOPICs = Config.getSubscribetopics();
	private static int[] SUBSCRIBE_QoSs = Config.getSubscribeqoss();
	/**
	 * 单例
	 * @return
	 */
	public static MqttClient getInstance() {
		if(mqttClient == null) {
			try {
				mqttClient = new MqttClient(MQTTSERVER_URL, CLIENT_ID,new MemoryPersistence());
				
				mqttClient.setCallback(new MqttCallbackExtended() {
					
					@Override
					public void messageArrived(String topic, MqttMessage message) throws Exception { //接收消息
						String content = new String(message.getPayload());
						System.out.println("received topic:"+topic+" content:"+content);
						String[] topicStrings = topic.split("/");
						switch (topicStrings[0]) {
						case "$event":
							try {
								JSONObject event = JSONObject.parseObject(content) ;
								receiveEvent(topicStrings[1], event);
							} catch (Exception e) {
								logger.error("格式错误:"+e);
							}
							break;
						}
						
					}
					
					@Override
					public void deliveryComplete(IMqttDeliveryToken token) {  //消息送达
						logger.info("Delivery complete:"+token.isComplete());
						
					}
					
					@Override
					public void connectionLost(Throwable throwable) {   //断开连接
						logger.error("Lost connecttion:"+throwable);
						
					}
					
					@Override
					public void connectComplete(boolean reconnet, String serverUrl) { //连接成功
						logger.info("Connect to "+serverUrl+" success");
						
					}

				});
				
				logger.info("start connect");
				//建立连接
				mqttClient.connect(getOptions());
				//订阅主题
				IMqttToken resulToken= mqttClient.subscribeWithResponse(SUBSCRIBE_TOPICs,SUBSCRIBE_QoSs);
				if(resulToken.isComplete()) {
					logger.info("subscribe successed");
				}else {
					logger.info("subscribe failed");
				}
			} catch (MqttException e) {
				logger.error(e.toString());
			}
			
		}
		return mqttClient;
	}
	
	/**
	 * 发送消息
	 * @param topic
	 * @param content
	 * @param qos
	 * @param retain
	 */
	public static void publish(String topic,String content,int qos,boolean retain) {
		try {
			getInstance().publish(topic, content.getBytes(),qos,retain);
			logger.info(mqttClient.getClientId()+" publish topic:"+topic+" content:"+content+" QoS:"+qos+" retain:"+retain);
		} catch (MqttException e) {
			logger.error(e.toString());
		}
	}
	
	/**
	 * 接收指令
	 * @param deviceId
	 * @param content
	 */
	private static void receiveEvent(String deviceId,JSONObject content) {
		String operation = content.getString("operation");
		logger.info("event operation:"+operation);
		String code = "200";   
		String msg = "";
		switch (operation) {
		case "GetState":  //获取状态
			content.put("onlineState", 1);  //模拟数据
			content.put("enableState", 1);
			
			break;

		case "ReadRecord":  //抄表
			content.put("value", 1);  //模拟数据
			
			break;

		case "ElecSwitchOn":  //电表合闸
			
			break;

		case "ElecSwitchOff":  //电表拉闸
			
			break;

		}
		Dataup(deviceId, content, code, msg);
	}
	
	/**
	 * 数据上报
	 * @param produceId
	 * @param data
	 * @param code   执行结果（200成功 400失败）
	 * @param msg  结果描述信息
	 */
	private static void Dataup(String produceId,JSONObject data,String code,String msg) {
		JSONObject content = new JSONObject();
		content.put("code", code);
		content.put("data", data);
		content.put("msg", msg);
		publish("$dataup/"+produceId, content.toString(),2, true);
	}
	
	public static void close() {
		if (mqttClient == null) {
			logger.info("客户端未初始化");
		}else {
			try {
				mqttClient.close();
				logger.info("mqtt client closing");
			} catch (MqttException e) {
				logger.error(e.toString());
			}
		}
	}
	
	/**
	 * MQTT连接配置
	 * @return
	 */
	private static MqttConnectOptions getOptions() {
		MqttConnectOptions options = new MqttConnectOptions();
		//是否清除session
		options.setCleanSession(false);
		//设置超时时间
		options.setConnectionTimeout(10);
		//设置心跳时间
		options.setKeepAliveInterval(30);
		//自动重连
		options.setAutomaticReconnect(true);
		//设置用户名密码
		options.setUserName(Config.getUsername());
		options.setPassword(Config.getPassword().toCharArray());
		return options;
	}
}
