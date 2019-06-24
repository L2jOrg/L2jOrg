package org.l2j.gameserver.util;

import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.HtmlActionScope;
import org.l2j.gameserver.enums.IllegalActionPunishmentType;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.tasks.player.IllegalPlayerActionTask;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.serverpackets.AbstractHtmlPacket;
import org.l2j.gameserver.network.serverpackets.ShowBoard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * General Utility functions related to game server.
 *
 * TODO move generic functions to Util of Commons
 */
public final class GameUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameUtils.class);
    private static final NumberFormat ADENA_FORMATTER = NumberFormat.getIntegerInstance(Locale.ENGLISH);

    public static void handleIllegalPlayerAction(L2PcInstance actor, String message, IllegalActionPunishmentType punishment) {
        ThreadPoolManager.schedule(new IllegalPlayerActionTask(actor, message, punishment), 5000);
    }

    /**
     * @param from
     * @param to
     * @return degree value of object 2 to the horizontal line with object 1 being the origin.
     */
    public static double calculateAngleFrom(ILocational from, ILocational to) {
        return calculateAngleFrom(from.getX(), from.getY(), to.getX(), to.getY());
    }

    /**
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     * @return degree value of object 2 to the horizontal line with object 1 being the origin
     */
    public static double calculateAngleFrom(int fromX, int fromY, int toX, int toY) {
        double angleTarget = Math.toDegrees(Math.atan2(toY - fromY, toX - fromX));
        if (angleTarget < 0) {
            angleTarget += 360;
        }
        return angleTarget;
    }

    /**
     * Gets a random position around the specified location.
     *
     * @param loc      the center location
     * @param minRange the minimum range from the center to pick a point.
     * @param maxRange the maximum range from the center to pick a point.
     * @return a random location between minRange and maxRange of the center location.
     */
    public static Location getRandomPosition(ILocational loc, int minRange, int maxRange) {
        final int randomX = Rnd.get(minRange, maxRange);
        final int randomY = Rnd.get(minRange, maxRange);
        final double rndAngle = Math.toRadians(Rnd.get(360));

        final int newX = (int) (loc.getX() + (randomX * Math.cos(rndAngle)));
        final int newY = (int) (loc.getY() + (randomY * Math.sin(rndAngle)));

        return new Location(newX, newY, loc.getZ());
    }

    public static double convertHeadingToDegree(int clientHeading) {
        return clientHeading / 182.044444444;
    }

    public static int calculateHeadingFrom(ILocational from, ILocational to) {
        return calculateHeadingFrom(from.getX(), from.getY(), to.getX(), to.getY());
    }

    public static int calculateHeadingFrom(int fromX, int fromY, int toX, int toY) {
        double angleTarget = Math.toDegrees(Math.atan2(toY - fromY, toX - fromX));
        if (angleTarget < 0) {
            angleTarget += 360;
        }
        return (int) (angleTarget * 182.044444444);
    }

    public static int calculateHeadingFrom(double dx, double dy) {
        double angleTarget = Math.toDegrees(Math.atan2(dy, dx));
        if (angleTarget < 0) {
            angleTarget += 360;
        }
        return (int) (angleTarget * 182.044444444);
    }

    /**
     * Calculates distance between one set of x, y, z and another set of x, y, z.
     *
     * @param x1           - X coordinate of first point.
     * @param y1           - Y coordinate of first point.
     * @param z1           - Z coordinate of first point.
     * @param x2           - X coordinate of second point.
     * @param y2           - Y coordinate of second point.
     * @param z2           - Z coordinate of second point.
     * @param includeZAxis - If set to true, Z coordinates will be included.
     * @param squared      - If set to true, distance returned will be squared.
     * @return {@code double} - Distance between object and given x, y , z.
     */
    public static double calculateDistance(double x1, double y1, double z1, double x2, double y2, double z2, boolean includeZAxis, boolean squared) {
        final double distance = Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + (includeZAxis ? Math.pow(z1 - z2, 2) : 0);
        return (squared) ? distance : Math.sqrt(distance);
    }

    /**
     * Calculates distance between 2 locations.
     *
     * @param loc1         - First location.
     * @param loc2         - Second location.
     * @param includeZAxis - If set to true, Z coordinates will be included.
     * @param squared      - If set to true, distance returned will be squared.
     * @return {@code double} - Distance between object and given location.
     */
    public static double calculateDistance(ILocational loc1, ILocational loc2, boolean includeZAxis, boolean squared) {
        return calculateDistance(loc1.getX(), loc1.getY(), loc1.getZ(), loc2.getX(), loc2.getY(), loc2.getZ(), includeZAxis, squared);
    }

    /**
     * @param range
     * @param obj1
     * @param obj2
     * @param includeZAxis
     * @return {@code true} if the two objects are within specified range between each other, {@code false} otherwise
     */
    public static boolean checkIfInRange(int range, L2Object obj1, L2Object obj2, boolean includeZAxis) {
        if ((obj1 == null) || (obj2 == null) || (obj1.getInstanceWorld() != obj2.getInstanceWorld())) {
            return false;
        }
        if (range == -1) {
            return true; // not limited
        }

        int radius = 0;
        if (obj1.isCharacter()) {
            radius += ((L2Character) obj1).getTemplate().getCollisionRadius();
        }
        if (obj2.isCharacter()) {
            radius += ((L2Character) obj2).getTemplate().getCollisionRadius();
        }

        return calculateDistance(obj1, obj2, includeZAxis, false) <= (range + radius);
    }

    /**
     * Checks if object is within short (sqrt(int.max_value)) radius, not using collisionRadius. Faster calculation than checkIfInRange if distance is short and collisionRadius isn't needed. Not for long distance checks (potential teleports, far away castles etc).
     *
     * @param range
     * @param obj1
     * @param obj2
     * @param includeZAxis if true, check also Z axis (3-dimensional check), otherwise only 2D
     * @return {@code true} if objects are within specified range between each other, {@code false} otherwise
     */
    public static boolean checkIfInShortRange(int range, L2Object obj1, L2Object obj2, boolean includeZAxis) {
        if ((obj1 == null) || (obj2 == null)) {
            return false;
        }
        if (range == -1) {
            return true; // not limited
        }

        return calculateDistance(obj1, obj2, includeZAxis, false) <= range;
    }

    /**
     * @param text - the text to check
     * @return {@code true} if {@code text} contains only numbers, {@code false} otherwise
     */
    public static boolean isDigit(String text) {
        if ((text == null) || text.isEmpty()) {
            return false;
        }
        for (char c : text.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param text - the text to check
     * @return {@code true} if {@code text} is integer, {@code false} otherwise
     */
    public static boolean isInteger(String text) {
        if ((text == null) || text.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param text - the text to check
     * @return {@code true} if {@code text} is float, {@code false} otherwise
     */
    public static boolean isFloat(String text) {
        if ((text == null) || text.isEmpty()) {
            return false;
        }
        try {
            Float.parseFloat(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param text - the text to check
     * @return {@code true} if {@code text} is double, {@code false} otherwise
     */
    public static boolean isDouble(String text) {
        if ((text == null) || text.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param <T>
     * @param name     - the text to check
     * @param enumType
     * @return {@code true} if {@code text} is enum, {@code false} otherwise
     */
    public static <T extends Enum<T>> boolean isEnum(String name, Class<T> enumType) {
        if ((name == null) || name.isEmpty()) {
            return false;
        }
        try {
            return Enum.valueOf(enumType, name) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param text - the text to check
     * @return {@code true} if {@code text} contains only letters and/or numbers, {@code false} otherwise
     */
    public static boolean isAlphaNumeric(String text) {
        if ((text == null) || text.isEmpty()) {
            return false;
        }
        for (char c : text.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Format the specified digit using the digit grouping symbol "," (comma).<br>
     * For example, 123456789 becomes 123,456,789.
     *
     * @param amount - the amount of adena
     * @return the formatted adena amount
     */
    public static String formatAdena(long amount) {
        synchronized (ADENA_FORMATTER) {
            return ADENA_FORMATTER.format(amount);
        }
    }

    /**
     * @param val
     * @param format
     * @return formatted double value by specified format.
     */
    public static String formatDouble(double val, String format) {
        final DecimalFormat formatter = new DecimalFormat(format, new DecimalFormatSymbols(Locale.ENGLISH));
        return formatter.format(val);
    }

    /**
     * Format the given date on the given format
     *
     * @param date   : the date to format.
     * @param format : the format to correct by.
     * @return a string representation of the formatted date.
     */
    public static String formatDate(Date date, String format) {
        if (date == null) {
            return null;
        }
        final DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static String getDateString(Date date) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date.getTime());
    }

    private static void buildHtmlBypassCache(L2PcInstance player, HtmlActionScope scope, String html) {
        final String htmlLower = html.toLowerCase(Locale.ENGLISH);
        int bypassEnd = 0;
        int bypassStart = htmlLower.indexOf("=\"bypass ", bypassEnd);
        int bypassStartEnd;
        while (bypassStart != -1) {
            bypassStartEnd = bypassStart + 9;
            bypassEnd = htmlLower.indexOf("\"", bypassStartEnd);
            if (bypassEnd == -1) {
                break;
            }

            final int hParamPos = htmlLower.indexOf("-h ", bypassStartEnd);
            String bypass;
            if ((hParamPos != -1) && (hParamPos < bypassEnd)) {
                bypass = html.substring(hParamPos + 3, bypassEnd).trim();
            } else {
                bypass = html.substring(bypassStartEnd, bypassEnd).trim();
            }

            final int firstParameterStart = bypass.indexOf(AbstractHtmlPacket.VAR_PARAM_START_CHAR);
            if (firstParameterStart != -1) {
                bypass = bypass.substring(0, firstParameterStart + 1);
            }

            if (Config.HTML_ACTION_CACHE_DEBUG) {
                LOGGER.info("Cached html bypass(" + scope + "): '" + bypass + "'");
            }
            player.addHtmlAction(scope, bypass);
            bypassStart = htmlLower.indexOf("=\"bypass ", bypassEnd);
        }
    }

    private static void buildHtmlLinkCache(L2PcInstance player, HtmlActionScope scope, String html) {
        final String htmlLower = html.toLowerCase(Locale.ENGLISH);
        int linkEnd = 0;
        int linkStart = htmlLower.indexOf("=\"link ", linkEnd);
        int linkStartEnd;
        while (linkStart != -1) {
            linkStartEnd = linkStart + 7;
            linkEnd = htmlLower.indexOf("\"", linkStartEnd);
            if (linkEnd == -1) {
                break;
            }

            final String htmlLink = html.substring(linkStartEnd, linkEnd).trim();
            if (htmlLink.isEmpty()) {
                LOGGER.warn("Html link path is empty!");
                continue;
            }

            if (htmlLink.contains("..")) {
                LOGGER.warn("Html link path is invalid: " + htmlLink);
                continue;
            }

            if (Config.HTML_ACTION_CACHE_DEBUG) {
                LOGGER.info("Cached html link(" + scope + "): '" + htmlLink + "'");
            }
            // let's keep an action cache with "link " lowercase literal kept
            player.addHtmlAction(scope, "link " + htmlLink);
            linkStart = htmlLower.indexOf("=\"link ", linkEnd);
        }
    }

    /**
     * Builds the html action cache for the specified scope.<br>
     * An {@code npcObjId} of 0 means, the cached actions can be clicked<br>
     * without beeing near an npc which is spawned in the world.
     *
     * @param player   the player to build the html action cache for
     * @param scope    the scope to build the html action cache for
     * @param npcObjId the npc object id the html actions are cached for
     * @param html     the html code to parse
     */
    public static void buildHtmlActionCache(L2PcInstance player, HtmlActionScope scope, int npcObjId, String html) {
        if ((player == null) || (scope == null) || (npcObjId < 0) || (html == null)) {
            throw new IllegalArgumentException();
        }

        if (Config.HTML_ACTION_CACHE_DEBUG) {
            LOGGER.info("Set html action npc(" + scope + "): " + npcObjId);
        }
        player.setHtmlActionOriginObjectId(scope, npcObjId);
        buildHtmlBypassCache(player, scope, html);
        buildHtmlLinkCache(player, scope, html);
    }

    /**
     * Helper method to send a community board html to the specified player.<br>
     * HtmlActionCache will be build with npc origin 0 which means the<br>
     * links on the html are not bound to a specific npc.
     *
     * @param activeChar the player
     * @param html       the html content
     */
    public static void sendCBHtml(L2PcInstance activeChar, String html) {
        sendCBHtml(activeChar, html, 0);
    }

    /**
     * Helper method to send a community board html to the specified player.<br>
     * When {@code npcObjId} is greater -1 the HtmlActionCache will be build<br>
     * with the npcObjId as origin. An origin of 0 means the cached bypasses<br>
     * are not bound to a specific npc.
     *
     * @param activeChar the player to send the html content to
     * @param html       the html content
     * @param npcObjId   bypass origin to use
     */
    public static void sendCBHtml(L2PcInstance activeChar, String html, int npcObjId) {
        sendCBHtml(activeChar, html, null, npcObjId);
    }

    /**
     * Helper method to send a community board html to the specified player.<br>
     * HtmlActionCache will be build with npc origin 0 which means the<br>
     * links on the html are not bound to a specific npc. It also fills a<br>
     * multiedit field in the send html if fillMultiEdit is not null.
     *
     * @param activeChar    the player
     * @param html          the html content
     * @param fillMultiEdit text to fill the multiedit field with(may be null)
     */
    public static void sendCBHtml(L2PcInstance activeChar, String html, String fillMultiEdit) {
        sendCBHtml(activeChar, html, fillMultiEdit, 0);
    }

    /**
     * Helper method to send a community board html to the specified player.<br>
     * It fills a multiedit field in the send html if {@code fillMultiEdit}<br>
     * is not null. When {@code npcObjId} is greater -1 the HtmlActionCache will be build<br>
     * with the npcObjId as origin. An origin of 0 means the cached bypasses<br>
     * are not bound to a specific npc.
     *
     * @param activeChar    the player
     * @param html          the html content
     * @param fillMultiEdit text to fill the multiedit field with(may be null)
     * @param npcObjId      bypass origin to use
     */
    public static void sendCBHtml(L2PcInstance activeChar, String html, String fillMultiEdit, int npcObjId) {
        if ((activeChar == null) || (html == null)) {
            return;
        }

        activeChar.clearHtmlActions(HtmlActionScope.COMM_BOARD_HTML);

        if (npcObjId > -1) {
            buildHtmlActionCache(activeChar, HtmlActionScope.COMM_BOARD_HTML, npcObjId, html);
        }

        if (fillMultiEdit != null) {
            activeChar.sendPacket(new ShowBoard(html, "1001"));
            fillMultiEditContent(activeChar, fillMultiEdit);
        } else if (html.length() < 16250) {
            activeChar.sendPacket(new ShowBoard(html, "101"));
            activeChar.sendPacket(new ShowBoard(null, "102"));
            activeChar.sendPacket(new ShowBoard(null, "103"));
        } else if (html.length() < (16250 * 2)) {
            activeChar.sendPacket(new ShowBoard(html.substring(0, 16250), "101"));
            activeChar.sendPacket(new ShowBoard(html.substring(16250), "102"));
            activeChar.sendPacket(new ShowBoard(null, "103"));
        } else if (html.length() < (16250 * 3)) {
            activeChar.sendPacket(new ShowBoard(html.substring(0, 16250), "101"));
            activeChar.sendPacket(new ShowBoard(html.substring(16250, 16250 * 2), "102"));
            activeChar.sendPacket(new ShowBoard(html.substring(16250 * 2), "103"));
        } else {
            activeChar.sendPacket(new ShowBoard("<html><body><br><center>Error: HTML was too long!</center></body></html>", "101"));
            activeChar.sendPacket(new ShowBoard(null, "102"));
            activeChar.sendPacket(new ShowBoard(null, "103"));
        }
    }

    /**
     * Fills the community board's multiedit window with text. Must send after sendCBHtml
     *
     * @param activeChar
     * @param text
     */
    public static void fillMultiEditContent(L2PcInstance activeChar, String text) {
        activeChar.sendPacket(new ShowBoard(Arrays.asList("0", "0", "0", "0", "0", "0", activeChar.getName(), Integer.toString(activeChar.getObjectId()), activeChar.getAccountName(), "9", " ", " ", text.replaceAll("<br>", Config.EOL), "0", "0", "0", "0")));
    }

    public static boolean isInsideRangeOfObjectId(L2Object obj, int targetObjId, int radius) {
        final L2Object target = L2World.getInstance().findObject(targetObjId);
        return (target != null) && (obj.calculateDistance3D(target) <= radius);
    }

    /**
     * Re-Maps a value from one range to another.
     *
     * @param input
     * @param inputMin
     * @param inputMax
     * @param outputMin
     * @param outputMax
     * @return The mapped value
     */
    public static int map(int input, int inputMin, int inputMax, int outputMin, int outputMax) {
        input = constrain(input, inputMin, inputMax);
        return (((input - inputMin) * (outputMax - outputMin)) / (inputMax - inputMin)) + outputMin;
    }

    /**
     * Re-Maps a value from one range to another.
     *
     * @param input
     * @param inputMin
     * @param inputMax
     * @param outputMin
     * @param outputMax
     * @return The mapped value
     */
    public static long map(long input, long inputMin, long inputMax, long outputMin, long outputMax) {
        input = constrain(input, inputMin, inputMax);
        return (((input - inputMin) * (outputMax - outputMin)) / (inputMax - inputMin)) + outputMin;
    }

    /**
     * Re-Maps a value from one range to another.
     *
     * @param input
     * @param inputMin
     * @param inputMax
     * @param outputMin
     * @param outputMax
     * @return The mapped value
     */
    public static double map(double input, double inputMin, double inputMax, double outputMin, double outputMax) {
        input = constrain(input, inputMin, inputMax);
        return (((input - inputMin) * (outputMax - outputMin)) / (inputMax - inputMin)) + outputMin;
    }

    /**
     * Constrains a number to be within a range.
     *
     * @param input the number to constrain, all data types
     * @param min   the lower end of the range, all data types
     * @param max   the upper end of the range, all data types
     * @return input: if input is between min and max, min: if input is less than min, max: if input is greater than max
     */
    public static int constrain(int input, int min, int max) {
        return (input < min) ? min : (input > max) ? max : input;
    }

    /**
     * Constrains a number to be within a range.
     *
     * @param input the number to constrain, all data types
     * @param min   the lower end of the range, all data types
     * @param max   the upper end of the range, all data types
     * @return input: if input is between min and max, min: if input is less than min, max: if input is greater than max
     */
    public static long constrain(long input, long min, long max) {
        return (input < min) ? min : (input > max) ? max : input;
    }

    /**
     * Constrains a number to be within a range.
     *
     * @param input the number to constrain, all data types
     * @param min   the lower end of the range, all data types
     * @param max   the upper end of the range, all data types
     * @return input: if input is between min and max, min: if input is less than min, max: if input is greater than max
     */
    public static double constrain(double input, double min, double max) {
        return (input < min) ? min : (input > max) ? max : input;
    }

    /**
     * This will sort a Map according to the values. Default sort direction is ascending.
     *
     * @param <K>        keyType
     * @param <V>        valueType
     * @param map        Map to be sorted.
     * @param descending If you want to sort descending.
     * @return A new Map sorted by the values.
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean descending) {
        if (descending) {
            return map.entrySet().stream().sorted(Map.Entry.comparingByValue(Collections.reverseOrder())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        }
        return map.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public static int hashIp(L2PcInstance player) {
        return org.l2j.commons.util.Util.hashIp(player.getIPAddress());
    }
}
