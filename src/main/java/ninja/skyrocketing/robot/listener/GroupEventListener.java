package ninja.skyrocketing.robot.listener;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import ninja.skyrocketing.RobotApplication;
import ninja.skyrocketing.robot.messages.LogMessage;
import org.jetbrains.annotations.NotNull;

import static ninja.skyrocketing.robot.sender.AdminListenerMessageSender.AdminMessageSender;
import static ninja.skyrocketing.robot.sender.AdminListenerMessageSender.ErrorMessageSender;


/**
 * @Author skyrocketing Hong
 * @Date 2020-08-20 020 20:11:15
 * @Version 1.0
 */

public class GroupEventListener extends SimpleListenerHost {
	//进群后的第一条消息
	@EventHandler
	public ListeningStatus onInviteGroup(BotJoinGroupEvent event) {
		event.getGroup().sendMessage("大家好，@我就能获取功能列表了哦。");
		return ListeningStatus.LISTENING;
	}
	
	//机器人被禁言
	@EventHandler
	public ListeningStatus onBotMute(BotMuteEvent event) {
		MessageChainBuilder messages = LogMessage.logMessage("FATAL");
		messages.add("机器人被禁言" + "\n" +
				"1. 解封时间：" + DateUtil.offsetSecond(new DateTime(), event.getDurationSeconds()) + "\n" +
				"2. 群名：" + event.getGroup().getName() + "\n" +
				"3. 群号：" + event.getGroup().getId() + "\n" +
				"4. 操作人：" + event.getOperator().getId() + " " + event.getOperator().getNameCard() + "\n");
		AdminMessageSender(messages, event.getBot());
		return ListeningStatus.LISTENING;
	}
	
	//机器人被解禁
	@EventHandler
	public ListeningStatus onBotUnmute(BotUnmuteEvent event) {
		MessageChainBuilder messages = LogMessage.logMessage("FATAL");
		messages.add("机器人被解除禁言" + "\n" +
				"1. 群名：" + event.getGroup().getName() + "\n" +
				"2. 群号：" + event.getGroup().getId() + "\n" +
				"3. 操作人：" + event.getOperator().getId() + " " + event.getOperator().getNameCard() + "\n");
		AdminMessageSender(messages, event.getBot());
		return ListeningStatus.LISTENING;
	}
	
	//机器人被移除群聊
	@EventHandler
	public ListeningStatus onBotKick(BotLeaveEvent.Active event) {
		MessageChainBuilder messages = LogMessage.logMessage("FATAL");
		messages.add("机器人被移除群聊" + "\n" +
				"1. 群名：" + event.getGroup().getName() + "\n" +
				"2. 群号：" + event.getGroup().getId() + "\n"
		);
		AdminMessageSender(messages, event.getBot());
		return ListeningStatus.LISTENING;
	}
	
	//机器人加入群聊
	@EventHandler
	public ListeningStatus onBotJoin(BotJoinGroupEvent.Invite event) {
		MessageChainBuilder messages = LogMessage.logMessage("FATAL");
		messages.add("机器人加入群聊" + "\n" +
				"1. 群名：" + event.getGroup().getName() + "\n" +
				"2. 群号：" + event.getGroup().getId() + "\n" +
				"3. 邀请人：" + event.getInvitor().getNameCard() + " " + event.getInvitor().getId() + "\n");
		AdminMessageSender(messages, event.getBot());
		return ListeningStatus.LISTENING;
	}
	
	//机器人收到入群邀请
	@EventHandler
	public ListeningStatus onBotInvite(BotInvitedJoinGroupRequestEvent event) {
		event.accept();
		return ListeningStatus.LISTENING;
	}
	
	//处理事件处理时抛出的异常
	@Override
	public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
		ErrorMessageSender(context, exception, RobotApplication.bot);
	}
}