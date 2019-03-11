/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.cache;

import org.l2j.commons.util.filter.HTMLFilter;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.util.Util;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Layane
 */
public class HtmCache {
    private static final Logger LOGGER = Logger.getLogger(HtmCache.class.getName());

    private static final HTMLFilter HTML_FILTER = new HTMLFilter();
    private static final Pattern EXTEND_PATTERN = Pattern.compile("<extend template=\"([a-zA-Z0-9-_./\\ ]*)\">(.*?)</extend>", Pattern.DOTALL);
    private static final Pattern ABSTRACT_BLOCK_PATTERN = Pattern.compile("<abstract block=\"([a-zA-Z0-9-_. ]*)\" ?/>", Pattern.DOTALL);
    private static final Pattern BLOCK_PATTERN = Pattern.compile("<block name=\"([a-zA-Z0-9-_. ]*)\">(.*?)</block>", Pattern.DOTALL);

    private static final Map<String, String> _cache = Config.LAZY_CACHE ? new ConcurrentHashMap<>() : new HashMap<>();

    private int _loadedFiles;
    private long _bytesBuffLen;

    protected HtmCache() {
        reload();
    }

    public static HtmCache getInstance() {
        return SingletonHolder._instance;
    }

    public void reload() {
        reload(Config.DATAPACK_ROOT);
    }

    public void reload(File f) {
        if (!Config.LAZY_CACHE) {
            LOGGER.info("Html cache start...");
            parseDir(f);
            LOGGER.info("Cache[HTML]: " + String.format("%.3f", getMemoryUsage()) + " megabytes on " + _loadedFiles + " files loaded");
        } else {
            _cache.clear();
            _loadedFiles = 0;
            _bytesBuffLen = 0;
            LOGGER.info("Cache[HTML]: Running lazy cache");
        }
    }

    public void reloadPath(File f) {
        parseDir(f);
        LOGGER.info("Cache[HTML]: Reloaded specified path.");
    }

    public double getMemoryUsage() {
        return (float) _bytesBuffLen / 1048576;
    }

    public int getLoadedFiles() {
        return _loadedFiles;
    }

    private void parseDir(File dir) {
        final File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    loadFile(file);
                } else {
                    parseDir(file);
                }
            }
        }
    }

    public String loadFile(File file) {
        if (HTML_FILTER.accept(file)) {
            try {
                String content = processHtml(Util.readAllLines(file, StandardCharsets.UTF_8, null));
                content = content.replaceAll("(?s)<!--.*?-->", ""); // Remove html comments
                // content = content.replaceAll("\r", "").replaceAll("\n", ""); // Remove new lines

                final String oldContent = _cache.put(file.toURI().getPath().substring(Config.DATAPACK_ROOT.toURI().getPath().length()), content);
                if (oldContent == null) {
                    _bytesBuffLen += content.length() * 2;
                    _loadedFiles++;
                } else {
                    _bytesBuffLen = (_bytesBuffLen - oldContent.length()) + (content.length() * 2);
                }
                return content;
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Problem with htm file:", e);
            }
        }
        return null;
    }

    public String getHtmForce(L2PcInstance player, String path) {
        String content = getHtm(player, path);
        if (content == null) {
            content = "<html><body>My text is missing:<br>" + path + "</body></html>";
            LOGGER.warning("Cache[HTML]: Missing HTML page: " + path);
        }
        return content;
    }

    public String getHtm(L2PcInstance player, String path) {
        final String prefix = player != null ? player.getHtmlPrefix() : "en";
        String newPath = null;
        String content;
        if ((prefix != null) && !prefix.isEmpty()) {
            newPath = prefix + path;
            content = getHtm(newPath);
            if (content != null) {
                if ((player != null) && player.isGM() && Config.GM_DEBUG_HTML_PATHS) {
                    BuilderUtil.sendHtmlMessage(player, newPath.substring(5));
                }
                return content;
            }
        }

        content = getHtm(path);
        if ((content != null) && (newPath != null)) {
            _cache.put(newPath, content);
        }

        if ((player != null) && player.isGM() && (path != null) && Config.GM_DEBUG_HTML_PATHS) {
            BuilderUtil.sendHtmlMessage(player, path.substring(5));
        }
        return content;
    }

    private String getHtm(String path) {
        // TODO: Check why some files do not get in cache on server startup.
        return (path == null) || path.isEmpty() ? "" : _cache.get(path) == null ? loadFile(new File(Config.DATAPACK_ROOT, path)) : _cache.get(path);
    }

    public boolean contains(String path) {
        return _cache.containsKey(path);
    }

    /**
     * @param path The path to the HTM
     * @return {@code true} if the path targets a HTM or HTML file, {@code false} otherwise.
     */
    public boolean isLoadable(String path) {
        return HTML_FILTER.accept(new File(Config.DATAPACK_ROOT, path));
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
                        LOGGER.warning(getClass().getSimpleName() + ": Abstract block definition [" + name + "] is not implemented!");
                        continue;
                    }

                    // Replace the matched content with the block.
                    template = template.replace(blockMatcher.group(0), blockMap.get(name));
                }

                // Replace the entire extend block
                result = result.replace(extendMatcher.group(0), template);
            } else {
                LOGGER.warning(getClass().getSimpleName() + ": Missing template: " + templateName + "-template.htm !");
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

    private static class SingletonHolder {
        protected static final HtmCache _instance = new HtmCache();
    }
}
