package org.l2j.authserver.controller;

import org.l2j.commons.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;

class BanManager {

    private static final Logger logger = LoggerFactory.getLogger(BanManager.class);
    private static BanManager instance;
    private final Map<String, Long> bannedAdresses = new ConcurrentHashMap<>();

    private BanManager() {
        loadBanFile();
    }

    static BanManager load() {
        if (isNull(instance)) {
            instance = new BanManager();
        }
        return instance;
    }

    private void loadBanFile() {
        Path path = Paths.get("banned_ip.cfg");
        if (Files.isRegularFile(path)) {
            try {
                Files.readAllLines(path).stream().filter(Util::isNotEmpty).forEach(this::addBannedAddress);
            } catch (IOException e) {
                logger.warn("Error while reading the bans file ({}). Details: {}", path.getFileName(), e.getLocalizedMessage());
            }

            logger.info("Loaded {} IP Bans.", bannedAdresses.size());
        } else {
            logger.info("IP Bans file {} is missing or is a directory, skipped.", path.toAbsolutePath());
        }
    }

    private void addBannedAddress(String bannedInfo) {
        bannedInfo = bannedInfo.trim();
        if (bannedInfo.startsWith("#")) {
            return;
        }

        var infoParts = bannedInfo.split(" ");
        long expiration = -1;
        if (infoParts.length > 1) {
            try {
                expiration = Long.parseLong(infoParts[1]);
            } catch (NumberFormatException e) {
                logger.warn("Skipped: Incorrect ban duration ({}) for address {}", infoParts[1], infoParts[0]);
            }
        }
        addBannedAdress(infoParts[0], expiration);
    }

    void addBannedAdress(String address, long expiration) {
        bannedAdresses.put(address, expiration);
    }

    boolean isBanned(String address) {
        var banned = bannedAdresses.containsKey(address);
        if(banned) {
            Long expiration = bannedAdresses.get(address);
            if((expiration > 0) && (expiration < currentTimeMillis())) {
                bannedAdresses.remove(address);
                return false;
            }
        }
        return banned;
    }
}
