package org.l2j.authserver.settings;

import org.l2j.commons.configuration.Settings;
import org.l2j.commons.configuration.SettingsFile;

import static org.l2j.commons.configuration.Configurator.getSettings;
import static java.util.Objects.requireNonNullElseGet;

public class AuthServerSettings implements Settings {

    private SettingsFile settings;

    @Override
    public void load(SettingsFile settingsFile) {
        settings = requireNonNullElseGet(settingsFile, SettingsFile::new);
    }

    public static String gameServerListenHost() {
        return getInstance().settings.getString("LoginHostname", "*");
    }

    public static int gameServerListenPort() {
        return  getInstance().settings.getInteger("LoginPort", 9013);
    }

    public static String loginListenHost() {
        return getInstance().settings.getString("LoginserverHostname", "*");
    }

    public static int loginListenPort() {
        return getInstance().settings.getInteger("LoginserverPort", 2106);
    }

    public static boolean isAutoCreateAccount(){
        return getInstance().settings.getBoolean("AutoCreateAccounts", false);
    }

    public static int loginTryBeforeBan(){
        return getInstance().settings.getInteger("LoginTryBeforeBan", 10);
    }

    public static int loginBlockAfterBan() {
        return getInstance().settings.getInteger("LoginBlockAfterBan", 600);
    }

    public static boolean isFloodProtectionEnabled() {
        return getInstance().settings.getBoolean("EnableFloodProtection", true);
    }

    public static int floodFastConnectionLimit() {
        return getInstance().settings.getInteger("FastConnectionLimit", 15);
    }

    public static int floodNormalConnectionTime() {
        return getInstance().settings.getInteger("NormalConnectionTime", 700);
    }

    public static int floodFastConnectionTime() {
        return getInstance().settings.getInteger("FastConnectionTime", 350);
    }

    public static int maxConnectionPerIP() {
        return getInstance().settings.getInteger("MaxConnectionPerIP", 50);
    }

    public static boolean acceptNewGameServerEnabled() {
        return  getInstance().settings.getBoolean("AcceptNewGameServer", false);
    }

    public static String usernameTemplate() {
        return getInstance().settings.getString("UsernameTemplate", "[A-Za-z0-9_]{5,32}");
    }

    private static AuthServerSettings getInstance() {
        return getSettings(AuthServerSettings.class);
    }
}
