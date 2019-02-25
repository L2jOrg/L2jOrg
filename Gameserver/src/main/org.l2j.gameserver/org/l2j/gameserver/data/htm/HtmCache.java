package org.l2j.gameserver.data.htm;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import org.l2j.commons.cache.CacheFactory;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.utils.HtmlUtils;
import org.l2j.gameserver.utils.Language;
import org.l2j.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.Cache;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

public class HtmCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmCache.class);
    private static final HtmCache INSTANCE = new HtmCache();

    private final IntObjectMap<Cache<String, String>> caches = new HashIntObjectMap<>(Language.values().length);

    public static final int DISABLED = 0;
    public static final int LAZY = 1;

    private HtmCache() {
        if (Config.HTM_CACHE_MODE == DISABLED) {
            LOGGER.info("HtmCache: Disabled.");
        } else {
            LOGGER.info("HtmCache: Lazy Cache Enabled.");
        }
    }

    public static HtmCache getInstance() {
        return INSTANCE;
    }

    public void reload() {
        clear();
    }

    public String getHtml(String fileName, Player player) {
        var lang = isNull(player) ? Language.ENGLISH : player.getLanguage();
        var cache = getCache(fileName, lang);

        if(isNull(cache)) {
            LOGGER.warn("Dialog: data/html/{}/{} not found", lang.getShortName(), fileName);
        }

        return cache;
    }

    public String getIfExists(String fileName, Player player) {
        var lang = isNull(player) ? Language.ENGLISH: player.getLanguage();
        return getCache(fileName, lang);
    }

    public HtmTemplates getTemplates(String fileName, Player player) {
        Language lang = isNull(player) ? Language.ENGLISH: player.getLanguage();
        HtmTemplates templates = Util.parseTemplates(fileName, lang, getHtml(fileName, player));
        if(isNull(templates))
            return HtmTemplates.EMPTY_TEMPLATES;
        return templates;
    }

    public String getCache(String file, Language lang) {
        if(isNull(file)) {
            return null;
        }

        String content;

        if(Config.HTM_CACHE_MODE == DISABLED) {
            content = loadDisabled(lang, file);
            if (isNull(content) && lang != Language.ENGLISH) {
                content = loadDisabled(Language.ENGLISH, file);
            }
        } else {
            final var fileLower = file.toLowerCase();
            content = get(lang, fileLower);
            if(isNull(content)) {
                content = loadLazy(lang, file);
                if (isNull(content) && lang != Language.ENGLISH) {
                    content = loadLazy(Language.ENGLISH, file);
                }
            }

        }
        return content;
    }

    private String loadDisabled(Language lang, String filePath) {
        var file = getSettings(ServerSettings.class).dataPackRootPath().resolve(String.format("data/html/%s/%s", lang.getShortName(), filePath));
        String content = parseFile(lang, file);

        if(isNull(content)) {
            file= getSettings(ServerSettings.class).dataPackRootPath().resolve(String.format("custom/html/%s/%s", lang.getShortName(), filePath));
            content = parseFile(lang, file);
        }
        return content;
    }

    private String parseFile(Language lang, Path path) {
        try {
            if(Files.exists(path)) {
                return HtmlUtils.bbParse(Files.readString(path));
            }
        } catch (IOException e) {
            LOGGER.warn("HtmCache: File error: {} lang: {}", path, lang);
        }
        return null;
    }

    private String loadLazy(Language lang, String filePath) {
        var root = getSettings(ServerSettings.class).dataPackRootPath().resolve("data/html/" + lang.getShortName());
        var file = root.resolve(filePath);
        var cache = putContent(lang, file, root);

        if(nonNull(cache)) {
            return cache;
        }

        root = getSettings(ServerSettings.class).dataPackRootPath().resolve("custom/html/" + lang.getShortName());
        file = root.resolve(filePath);
        return putContent(lang, file, root);
    }

    private String putContent(Language lang, Path file, final Path rootPath) {
        var content = parseFile(lang, file);
        if(isNull(content)) {
            return null;
        }

        var path = file.toString().substring(rootPath.toString().length() + 1).replace("\\", "/");
        var cache = cacheOfLang(lang);
        cache.put(path.toLowerCase(), content);
        return content;
    }

    private Cache<String, String> cacheOfLang(Language lang) {
        if(!caches.containsKey(lang.ordinal())) {
            var cacheAlias = String.format("%s.%s", getClass().getName(), lang);
            caches.put(lang.ordinal(), CacheFactory.getInstance().getCache(cacheAlias, String.class, String.class));
        }
        return caches.get(lang.ordinal());
    }


    private String get(Language lang, String filePath) {
        return cacheOfLang(lang).get(filePath);
    }

    public void clear() {
        caches.values().forEach(Cache::removeAll);
    }
}