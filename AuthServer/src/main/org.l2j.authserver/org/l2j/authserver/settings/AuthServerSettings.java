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
        return getInstance().settings.getString("GameServerListenHostname", "*");
    }

    public static int gameServerListenPort() {
        return  getInstance().settings.getInteger("GameServerListenPort", 9013);
    }

    public static String listenHost() {
        return getInstance().settings.getString("Hostname", "*");
    }

    public static int listenPort() {
        return getInstance().settings.getInteger("Port", 2106);
    }

    public static boolean isAutoCreateAccount(){
        return getInstance().settings.getBoolean("AutoCreateAccounts", false);
    }

    public static int authTriesBeforeBan(){
        return getInstance().settings.getInteger("AuthTriesBeforeBan", 10);
    }

    public static int loginBlockAfterBan() {
        return getInstance().settings.getInteger("AuthBlockAfterBan", 600);
    }

    public static boolean acceptNewGameServerEnabled() {
        return  getInstance().settings.getBoolean("AcceptNewGameServer", false);
    }

    public static String usernameTemplate() {
        return getInstance().settings.getString("UsernameTemplate", "[A-Za-z0-9_]{5,32}");
    }

    public int gmMinimumLevel() {
        return getInstance().settings.getInteger("GMMinLevel", 100);
    }

    private static AuthServerSettings getInstance() {
        return getSettings(AuthServerSettings.class);
    }


}
