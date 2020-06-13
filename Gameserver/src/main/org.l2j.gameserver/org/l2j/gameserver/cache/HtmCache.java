/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.cache;

import org.l2j.commons.cache.CacheFactory;
import org.l2j.commons.util.FilterUtil;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.BuilderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.Cache;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author Layane
 * @author JoeAlisson
 */
public class HtmCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(HtmCache.class);

    private static final Pattern EXTEND_PATTERN = Pattern.compile("<extend template=\"([a-zA-Z0-9-_./ ]*)\">(.*?)</extend>", Pattern.DOTALL);
    private static final Pattern ABSTRACT_BLOCK_PATTERN = Pattern.compile("<abstract block=\"([a-zA-Z0-9-_. ]*)\" ?/>", Pattern.DOTALL);
    private static final Pattern BLOCK_PATTERN = Pattern.compile("<block name=\"([a-zA-Z0-9-_. ]*)\">(.*?)</block>", Pattern.DOTALL);

    private static final Cache<String, String> CACHE = CacheFactory.getInstance().getCache("html", String.class, String.class);

    private HtmCache() {
        reload();
    }

    public void reload() {
        CACHE.clear();
        LOGGER.info("Cache[HTML]: Running lazy cache");
    }

    public boolean purge(String path) {
        return CACHE.remove(path);
    }

    public String loadFile(String filePath) {
        var path =  getSettings(ServerSettings.class).dataPackDirectory().resolve(filePath);
        if(FilterUtil.htmlFile(path)) {
            try {
                var content = processHtml(Files.readString(path));
                content = content.replaceAll("(?s)<!--.*?-->", "").replaceAll("[\r\n\t]", ""); // Remove html comments and spaces
                CACHE.put(filePath, content);
                return content;
            } catch (Exception e) {
                LOGGER.warn("Problem with htm file:", e);
            }
        }
        return null;
    }

    public String getHtmForce(Player player, String path) {
        String content = getHtm(player, path);
        if (content == null) {
            content = "<html><body>My text is missing:<br>" + path + "</body></html>";
            LOGGER.warn("Cache[HTML]: Missing HTML page: " + path);
        }
        return content;
    }

    public String getHtm(Player player, String path) {
        var content = getHtm(path);
        if (content != null && !contains(path)) {
            CACHE.put(path, content);
        }

        if ((player != null) && player.isGM() && (path != null) && Config.GM_DEBUG_HTML_PATHS) {
            BuilderUtil.sendHtmlMessage(player, path.substring(5));
        }
        return content;
    }

    private String getHtm(String path) {
        return Util.isNullOrEmpty(path) ? "" : CACHE.containsKey(path) ? CACHE.get(path) : loadFile(path);
    }

    public boolean contains(String path) {
        return CACHE.containsKey(path);
    }

    /**
     * @param path The path to the HTM
     * @return {@code true} if the path targets a HTM or HTML file, {@code false} otherwise.
     */
    public boolean isLoadable(String path) {
        return FilterUtil.htmlFile(getSettings(ServerSettings.class).dataPackDirectory().resolve(path));
    }

    private String parseTemplateName(String name) {
        if (!name.startsWith("data/")) {
            if (name.startsWith("html/")) {
                return "data/" + name;
            } else if (name.startsWith("CommunityBoard/")) {
                return "data/html/" + name;
            } else if (name.startsWith("scripts/")) {
                return "data/scripts/" + name;
            }
        }
        return name;
    }

    private String processHtml(String result) {
        final Matcher extendMatcher = EXTEND_PATTERN.matcher(result);
        if (extendMatcher.find()) {
            // If extend matcher finds something, process template
            final String templateName = parseTemplateName(extendMatcher.group(1));

            // Generate block name -> content map
            final Map<String, String> blockMap = generateBlockMap(result);

            // Attempt to find the template
            String template = getHtm(templateName + "-template.htm");
            if (template != null) {
                // Attempt to find the abstract blocks
                final Matcher blockMatcher = ABSTRACT_BLOCK_PATTERN.matcher(template);
                while (blockMatcher.find()) {
                    final String name = blockMatcher.group(1);
                    if (!blockMap.containsKey(name)) {
                        LOGGER.warn(": Abstract block definition [" + name + "] is not implemented!");
                        continue;
                    }

                    // Replace the matched content with the block.
                    template = template.replace(blockMatcher.group(0), blockMap.get(name));
                }

                // Replace the entire extend block
                result = result.replace(extendMatcher.group(0), template);
            } else {
                LOGGER.warn(": Missing template: " + templateName + "-template.htm !");
            }
        }

        return result;
    }

    private Map<String, String> generateBlockMap(String data) {
        final Map<String, String> blockMap = new LinkedHashMap<>();
        final Matcher blockMatcher = BLOCK_PATTERN.matcher(data);
        while (blockMatcher.find()) {
            final String name = blockMatcher.group(1);
            final String content = blockMatcher.group(2);
            blockMap.put(name, content);
        }
        return blockMap;
    }

    public static HtmCache getInstance() {
        return Singleton.INSTANCE;
    }


    private static class Singleton {
        private static final HtmCache INSTANCE = new HtmCache();
    }
}
