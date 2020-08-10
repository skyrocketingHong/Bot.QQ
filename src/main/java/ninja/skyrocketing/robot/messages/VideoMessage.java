package ninja.skyrocketing.robot.messages;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.LightApp;
import net.mamoe.mirai.message.data.Message;
import ninja.skyrocketing.robot.entity.MessageEncapsulation;
import ninja.skyrocketing.utils.VideoSearchUtil;

import java.io.IOException;

/**
 * @Author skyrocketing Hong
 * @Date 2020-07-09 009 12:38:53
 * @Version 1.0
 */
public class VideoMessage {
	public static Message bilibiliVideo(MessageEncapsulation messageEntity) throws IOException {
		MessageReceipt<Contact> messageReceipt = messageEntity.getGroupMessageEvent().getGroup().sendMessage("等待API返回数据...");
		String json = VideoSearchUtil.bilibiliVideo(messageEntity.getMsg().replaceAll("^来.*个视频\\s*", ""));
		messageReceipt.recall();
		return new LightApp(json);
	}
}