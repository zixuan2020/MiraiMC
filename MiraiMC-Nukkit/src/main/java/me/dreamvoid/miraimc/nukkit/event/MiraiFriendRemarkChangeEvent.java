package me.dreamvoid.miraimc.nukkit.event;

import cn.nukkit.event.HandlerList;
import me.dreamvoid.miraimc.api.bot.MiraiFriend;
import net.mamoe.mirai.event.events.FriendRemarkChangeEvent;
import cn.nukkit.event.Event;

/**
 * 好友昵称改变
 */
public class MiraiFriendRemarkChangeEvent extends Event {
    public MiraiFriendRemarkChangeEvent(FriendRemarkChangeEvent event) {
        this.event = event;
    }

    private final FriendRemarkChangeEvent event;

    private static final HandlerList handlers = new HandlerList();
    public static HandlerList getHandlers() { return handlers; }
    //public static HandlerList getHandlerList() { return handlers; }

    /**
     * 获取机器人账号
     * @return 机器人账号
     */
    public long getBotID() { return event.getBot().getId(); }

    /**
     * 获取好友更名之前的昵称
     * @return 昵称
     */
    public String getOldRemark() {
        return event.getOldRemark();
    }

    /**
     * 获取好友更名之后的昵称
     * @return 昵称
     */
    public String getNewRemark() {
        return event.getNewRemark();
    }

    /**
     * 获取哈希值
     * @return 哈希值
     */
    public int getHashCode() {
        return event.hashCode();
    }

    /**
     * 获取原始事件内容<br>
     * [!] 不推荐使用
     * @return 原始事件内容
     */
    public String eventToString() {
        return event.toString();
    }

    /**
     * 获取好友实例
     * @return MiraiFriend 实例
     */
    public MiraiFriend getFriend(){
        return new MiraiFriend(event.getBot(), event.getFriend().getId());
    }
}
