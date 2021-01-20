package ninja.skyrocketing.bot.fuyao.listener.group;

import kotlin.coroutines.CoroutineContext;
import lombok.NoArgsConstructor;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import ninja.skyrocketing.bot.fuyao.function.coin.Coin;
import ninja.skyrocketing.bot.fuyao.function.exp.Exp;
import ninja.skyrocketing.bot.fuyao.function.fishing.Fishing;
import ninja.skyrocketing.bot.fuyao.sender.group.GroupMessageSender;
import ninja.skyrocketing.bot.fuyao.service.bot.BotBanedGroupService;
import ninja.skyrocketing.bot.fuyao.service.bot.BotConfigService;
import ninja.skyrocketing.bot.fuyao.service.bot.BotReplyMessageService;
import ninja.skyrocketing.bot.fuyao.service.user.BotBanedUserService;
import ninja.skyrocketing.bot.fuyao.util.MessageUtil;
import ninja.skyrocketing.bot.fuyao.util.TimeUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

/**
 * @Author skyrocketing Hong
 * @Date 2020-11-28 14:55:32
 */

@Component
@NoArgsConstructor
public class GroupMessageListener extends SimpleListenerHost {
    private static BotBanedGroupService botBanedGroupService;
    private static BotBanedUserService botBanedUserService;
    private static BotConfigService botConfigService;
    private static BotReplyMessageService botReplyMessageService;
    @Autowired
    public GroupMessageListener(
            BotBanedGroupService botBanedGroupService,
            BotBanedUserService botBanedUserService,
            BotConfigService botConfigService,
            BotReplyMessageService botReplyMessageService
    ) {
        GroupMessageListener.botBanedGroupService = botBanedGroupService;
        GroupMessageListener.botBanedUserService = botBanedUserService;
        GroupMessageListener.botConfigService = botConfigService;
        GroupMessageListener.botReplyMessageService = botReplyMessageService;
    }

    //监听群消息
    @EventHandler
    public ListeningStatus onMessage(GroupMessageEvent event) throws Exception {
        //首先判断是否为@机器人
        if (event.getMessage().toString().matches(".*\\[mirai:at:" + event.getBot().getId() + ",.*\\].*") &&
                !event.getMessage().toString().matches(".*\\[mirai:quote:\\d*,\\d*\\].*")) {
            event.getGroup().sendMessage(botConfigService.GetConfigValueByKey("reply_after_at"));
        } else {
            if (event.getMessage().contentToString().matches("^(~|～).+")) {
                //判断是否为被禁用户或群
                if (botBanedGroupService.IsBaned(event.getGroup().getId()) ||
                        botBanedUserService.IsBaned(event.getSender().getId())) {
                    return ListeningStatus.LISTENING;
                } else {
                    //调用消息对应的实现类，并保存返回值（对应的回复）
                    Message message = GroupMessageSender.Sender(event);
                    if (message != null) {
                        //发送消息，并在开头添加@触发人
                        event.getGroup().sendMessage(new At(event.getSender().getId()).plus("\n" + message));
                        return ListeningStatus.LISTENING;
                    }
                }
            }
            //非~开头的消息
            else {
//                if (FuyaoBotApplication.botReplyMessageList == null) {
//                    FuyaoBotApplication.botReplyMessageList = botReplyMessageService.GetAllReplyMessage();
//                }
            }
        }
        return ListeningStatus.LISTENING;
    }

    //监听成员进群，并发送欢迎消息
    @EventHandler
    public ListeningStatus onJoin(MemberJoinEvent.Active event) throws IOException {
        //上传头像
//        File image = FileUtil.GetAvatarImageFile(event.getMember().getId());
//        Image avatarImage = Contact.uploadImage(event.getGroup(), image);

        //生成消息
        MessageChainBuilder messages = new MessageChainBuilder();
        messages.add("👏 欢迎第" + (event.getGroup().getMembers().size() + 1) + "名群员。" + "\n");
//        messages.add(avatarImage);
        messages.add(new At(event.getMember().getId()));
        messages.add("\n" + "记得阅读群公告（如果有的话）哦！");
        event.getGroup().sendMessage(messages.asMessageChain());
        return ListeningStatus.LISTENING;
    }

    //监听群员主动退群
    @EventHandler
    public ListeningStatus onQuit(MemberLeaveEvent.Quit event) {
        MessageChainBuilder messages = new MessageChainBuilder();
        messages.add("⚠ 群员减少提醒\n" +
                "群员 \"" +
                MessageUtil.NameOfMember(event.getMember()) + "\" (" +
                event.getMember().getId() + ") " +
                "悄悄地溜了...\n" +
                "(提醒消息将在1分钟内自动撤回)"
        );
        //清理数据
        Exp.CleanExpData(event.getGroup().getId(), event.getMember().getId());
        Coin.CleanCoinData(event.getGroup().getId(), event.getMember().getId());
        Fishing.CleanFishingData(event.getGroup().getId(), event.getMember().getId());
        //撤回消息
        event.getGroup().sendMessage(messages.asMessageChain()).recallIn(60000);
        return ListeningStatus.LISTENING;
    }

    //群成员被踢
    @EventHandler
    public ListeningStatus onKick(MemberLeaveEvent.Kick event) {
        MessageChainBuilder messages = new MessageChainBuilder();
        messages.add("⚠ 群员减少提醒\n" +
                "群员 \"" +
                MessageUtil.NameOfMember(event.getMember()) + "\" (" +
                event.getMember().getId() + ") " +
                "已被 \"" +
                MessageUtil.NameOfMember(event.getOperator()) + "\" (" +
                event.getOperator().getId() + ") " +
                "移出群聊。\n" +
                "(提醒消息将在1分钟内自动撤回)"
        );

        //清理数据
        Exp.CleanExpData(event.getGroup().getId(), event.getMember().getId());
        Coin.CleanCoinData(event.getGroup().getId(), event.getMember().getId());
        Fishing.CleanFishingData(event.getGroup().getId(), event.getMember().getId());
        //撤回消息
        event.getGroup().sendMessage(messages.asMessageChain()).recallIn(60000);
        return ListeningStatus.LISTENING;
    }

    //机器人被移除群聊
    @EventHandler
    public ListeningStatus onBotKick(BotLeaveEvent.Active event) {
        //清理数据
        Exp.CleanExpData(event.getGroup().getId(), 0L);
        Coin.CleanCoinData(event.getGroup().getId(), 0L);
        Fishing.CleanFishingData(event.getGroup().getId(), 0L);
        return ListeningStatus.LISTENING;
    }

    //监听群成员荣誉修改
    @EventHandler
    public ListeningStatus onMemberHonorChange(MemberHonorChangeEvent event) {
        MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
        messageChainBuilder.add("恭喜" + " \"" + MessageUtil.NameOfMember(event.getMember()) + "\" " + "\n" + new At(event.getMember().getId()) +
                "于 " + TimeUtil.NowDateTime(new Date()) + " " +
                "喜提" +  " \"" + event.getHonorType() + "\" "
        );
        event.getGroup().sendMessage(messageChainBuilder.asMessageChain());
        return ListeningStatus.LISTENING;
    }

    //监听群头衔修改
    @EventHandler
    public ListeningStatus onMemberSpecialTitleChange(MemberSpecialTitleChangeEvent event) {
        MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
        messageChainBuilder.add("恭喜" + " \"" + MessageUtil.NameOfMember(event.getMember()) + "\" " + "\n" + new At(event.getMember().getId()) +
                "于 " + TimeUtil.NowDateTime(new Date()) + " " +
                "喜提新头衔" +  " \"" + event.getNew() + "\" "
        );
        event.getGroup().sendMessage(messageChainBuilder.asMessageChain());
        return ListeningStatus.LISTENING;
    }
    //处理事件处理时抛出的异常
    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        System.out.println(context + " " + exception.getMessage());
    }
}
