package me.dreamvoid.miraimc.velocity;

import com.velocitypowered.api.event.Subscribe;
import me.dreamvoid.miraimc.internal.Utils;
import me.dreamvoid.miraimc.velocity.event.MiraiFriendMessageEvent;
import me.dreamvoid.miraimc.velocity.event.MiraiGroupMessageEvent;

public class Events {
    @Subscribe
    public void onMiraiGroupMessageReceived(MiraiGroupMessageEvent e){
        Utils.logger.info("[GroupMessage/"+e.getBotID()+"] ["+e.getGroupName()+"("+e.getGroupID()+")] "+e.getSenderNameCard()+"("+e.getSenderID()+") -> "+e.getMessage());
    }

    @Subscribe
    public void onMiraiFriendMessageReceived(MiraiFriendMessageEvent e){
        Utils.logger.info("[FriendMessage/"+e.getBotID()+"] "+e.getSenderNick()+"("+e.getSenderID()+") -> "+e.getMessage());
    }
}
