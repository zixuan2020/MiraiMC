package me.dreamvoid.miraimc.nukkit;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.AsyncTask;
import me.dreamvoid.miraimc.api.MiraiBot;
import me.dreamvoid.miraimc.internal.*;
import me.dreamvoid.miraimc.nukkit.commands.MiraiCommand;
import me.dreamvoid.miraimc.nukkit.commands.MiraiMcCommand;
import me.dreamvoid.miraimc.nukkit.commands.MiraiVerifyCommand;
import me.dreamvoid.miraimc.nukkit.utils.MetricsLite;

import java.io.IOException;
import java.sql.SQLException;

public class NukkitPlugin extends PluginBase {

    private static NukkitPlugin nukkitPlugin;

    private MiraiEvent MiraiEvent;
    private MiraiAutoLogin MiraiAutoLogin;

    public static NukkitPlugin getInstance() {
        return nukkitPlugin;
    }

    @Override
    public void onLoad() {
        nukkitPlugin = this;
        try {
            Utils.setLogger(new NukkitLogger("MiraiMC-Nukkit",null, this));
            Utils.setClassLoader(this.getClass().getClassLoader());
            new NukkitConfig(this).loadConfig();

            MiraiLoader.loadMiraiCore();
            this.MiraiEvent = new MiraiEvent(this);
            this.MiraiAutoLogin = new MiraiAutoLogin(this);
        } catch (Exception e) {
            getLogger().warning("An error occurred while loading plugin." );
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        getLogger().info("Mirai working dir: " + Config.Gen_MiraiWorkingDir);

        if(Config.Gen_AddProperties_MiraiNoDesktop){
            System.setProperty("mirai.no-desktop","MiraiMC");
        }
        if(Config.Gen_AddProperties_MiraiSliderCaptchaSupported){
            System.setProperty("mirai.slider.captcha.supported","MiraiMC");
        }

        getLogger().info("Starting Mirai-Events listener.");
        MiraiEvent.startListenEvent();

        getLogger().info("Loading auto-login file.");
        MiraiAutoLogin.loadFile();
        MiraiAutoLogin.doStartUpAutoLogin(); // 服务器启动完成后执行自动登录机器人

        // 注册命令
        getLogger().info("Registering commands.");
        getServer().getCommandMap().register("", new MiraiCommand());
        getServer().getCommandMap().register("", new MiraiMcCommand());
        getServer().getCommandMap().register("", new MiraiVerifyCommand());

        if(Config.Bot_LogEvents){
            getLogger().info("Registering events.");
            this.getServer().getPluginManager().registerEvents(new Events(this), this);
        }

        if(Config.DB_AllowDatabases)
        switch (Config.DB_Type.toLowerCase()){
            case "sqlite":
            default: {
                getLogger().info("Initializing SQLite database.");
                try {
                    Utils.initializeSQLite();
                } catch (SQLException | ClassNotFoundException e) {
                    getLogger().warning("Failed to initialize SQLite database, reason: " + e);
                }
                break;
            }
            case "mysql": {
                getLogger().info("Initializing MySQL database.");
                Utils.initializeMySQL();
                break;
            }
        }

        // bStats统计
        if(Config.Gen_AllowBStats && !getDescription().getVersion().contains("dev")) {
            getLogger().info("Initializing bStats metrics.");
            int pluginId = 12744;
            new MetricsLite(this, pluginId);
        }

        // 安全警告
        if(!(Config.Gen_DisableSafeWarningMessage)){
            getLogger().warning("确保您正在使用开源的MiraiMC插件，未知来源的插件可能会盗取您的账号！");
            getLogger().warning("请始终从Github或作者指定的其他途径下载插件: https://github.com/DreamVoid/MiraiMC");
        }

        getServer().getScheduler().scheduleAsyncTask(this, new AsyncTask() {
            @Override
            public void onRun() {
                getLogger().info("Checking update...");
                try {
                    PluginUpdate fetch = new PluginUpdate();
                    String version = !getDescription().getVersion().contains("-") ? fetch.getLatestRelease() : fetch.getLatestPreRelease();
                    if(!getDescription().getVersion().equals(version)){
                        getLogger().info("已找到新的插件更新，最新版本: " + version);
                        getLogger().info("从Github下载更新: https://github.com/DreamVoid/MiraiMC/releases/latest");
                    } else getLogger().info("你使用的是最新版本");
                } catch (IOException e) {
                    getLogger().warning("An error occurred while fetching the latest version, reason: " + e);
                }
            }
        });

        getLogger().info("All tasks done. Welcome to use MiraiMC!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Stopping bot event listener.");
        MiraiEvent.stopListenEvent();

        getLogger().info("Closing all bots");
        MiraiLoginSolver.closeAllVerifyThreads();
        for (long bots : MiraiBot.getOnlineBots()){
            MiraiBot.getBot(bots).doLogout();
        }

        if(Config.DB_AllowDatabases)
        switch (Config.DB_Type.toLowerCase()){
            case "sqlite":
            default: {
                getLogger().info("Closing SQLite database.");
                try {
                    Utils.closeSQLite();
                } catch (SQLException e) {
                    getLogger().error("Failed to close SQLite database!");
                    getLogger().error("Reason: " + e);
                }
                break;
            }
            case "mysql": {
                getLogger().info("Closing MySQL database.");
                Utils.closeMySQL();
                break;
            }
        }

        getLogger().info("All tasks done. Thanks for use MiraiMC!");
    }

}
