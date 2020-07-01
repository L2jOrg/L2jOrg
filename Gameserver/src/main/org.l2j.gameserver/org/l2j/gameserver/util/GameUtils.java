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
package org.l2j.gameserver.util;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.HtmlActionScope;
import org.l2j.gameserver.enums.IllegalActionPunishmentType;
import org.l2j.gameserver.instancemanager.WalkingManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.*;
import org.l2j.gameserver.model.actor.instance.*;
import org.l2j.gameserver.model.actor.tasks.player.IllegalPlayerActionTask;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.item.Armor;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.serverpackets.ShowBoard;
import org.l2j.gameserver.network.serverpackets.html.AbstractHtmlPacket;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.ZoneType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.util.Util.isAnyNull;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius2D;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;

/**
 * General Utility functions related to game server.
 *
 * TODO move generic functions to Util of Commons
 * @author JoeAlisson
 */
public final class GameUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameUtils.class);
    private static final NumberFormat ADENA_FORMATTER = NumberFormat.getIntegerInstance(Locale.ENGLISH);

    public static void handleIllegalPlayerAction(Player actor, String message) {
        handleIllegalPlayerAction(actor, message, getSettings(GeneralSettings.class).defaultPunishment());
    }

    public static void handleIllegalPlayerAction(Player actor, String message, IllegalActionPunishmentType punishment) {
        ThreadPool.schedule(new IllegalPlayerActionTask(actor, message, punishment), 5000);
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

    /**
     * @param range
     * @param obj1
     * @param obj2
     * @param includeZAxis
     * @return {@code true} if the two objects are within specified range between each other, {@code false} otherwise
     */
    public static boolean checkIfInRange(int range, WorldObject obj1, WorldObject obj2, boolean includeZAxis) {
        if (isAnyNull(obj1, obj2) || (obj1.getInstanceWorld() != obj2.getInstanceWorld())) {
            return false;
        }
        if (range == -1) {
            return true; // not limited
        }

        int radius = 0;
        if (isCreature(obj1)) {
            radius += ((Creature) obj1).getTemplate().getCollisionRadius();
        }
        if (isCreature(obj2)) {
            radius += ((Creature) obj2).getTemplate().getCollisionRadius();
        }

        return includeZAxis ? isInsideRadius3D(obj1, obj2, range + radius) : isInsideRadius2D(obj1, obj2, range + radius);
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
    public static boolean checkIfInShortRange(int range, WorldObject obj1, WorldObject obj2, boolean includeZAxis) {
        if (isAnyNull(obj1, obj2)) {
            return false;
        }
        if (range == -1) {
            return true; // not limited
        }

        return includeZAxis ? isInsideRadius3D(obj1, obj2, range) : isInsideRadius2D(obj1, obj2, range);
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

    private static void buildHtmlBypassCache(Player player, HtmlActionScope scope, String html) {
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

    private static void buildHtmlLinkCache(Player player, HtmlActionScope scope, String html) {
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
    public static void buildHtmlActionCache(Player player, HtmlActionScope scope, int npcObjId, String html) {
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
    public static void sendCBHtml(Player activeChar, String html) {
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
    public static void sendCBHtml(Player activeChar, String html, int npcObjId) {
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
    public static void sendCBHtml(Player activeChar, String html, String fillMultiEdit) {
        sendCBHtml(activeChar, html, fillMultiEdit, 0);
    }

    /**
     * Helper method to send a community board html to the specified player.<br>
     * It fills a multiedit field in the send html if {@code fillMultiEdit}<br>
     * is not null. When {@code npcObjId} is greater -1 the HtmlActionCache will be build<br>
     * with the npcObjId as origin. An origin of 0 means the cached bypasses<br>
     * are not bound to a specific npc.
     *
     * @param player    the player
     * @param html          the html content
     * @param fillMultiEdit text to fill the multiedit field with(may be null)
     * @param npcObjId      bypass origin to use
     */
    public static void sendCBHtml(Player player, String html, String fillMultiEdit, int npcObjId) {
        if (isNull(player) || isNull(html)) {
            return;
        }

        player.clearHtmlActions(HtmlActionScope.COMM_BOARD_HTML);

        if (npcObjId > -1) {
            buildHtmlActionCache(player, HtmlActionScope.COMM_BOARD_HTML, npcObjId, html);
        }

        if (fillMultiEdit != null) {
            player.sendPacket(new ShowBoard(html, "1001"));
            fillMultiEditContent(player, fillMultiEdit);
        } else if (html.length() < 16250) {
            player.sendPacket(new ShowBoard(html, "101"));
            player.sendPacket(new ShowBoard(null, "102"));
            player.sendPacket(new ShowBoard(null, "103"));
        } else if (html.length() < (16250 * 2)) {
            player.sendPacket(new ShowBoard(html.substring(0, 16250), "101"));
            player.sendPacket(new ShowBoard(html.substring(16250), "102"));
            player.sendPacket(new ShowBoard(null, "103"));
        } else if (html.length() < (16250 * 3)) {
            player.sendPacket(new ShowBoard(html.substring(0, 16250), "101"));
            player.sendPacket(new ShowBoard(html.substring(16250, 16250 * 2), "102"));
            player.sendPacket(new ShowBoard(html.substring(16250 * 2), "103"));
        } else {
            player.sendPacket(new ShowBoard("<html><body><br><center>Error: HTML was too long!</center></body></html>", "101"));
            player.sendPacket(new ShowBoard(null, "102"));
            player.sendPacket(new ShowBoard(null, "103"));
        }
    }

    /**
     * Fills the community board's multiedit window with text. Must send after sendCBHtml
     *
     * @param player
     * @param text
     */
    public static void fillMultiEditContent(Player player, String text) {
        player.sendPacket(new ShowBoard(Arrays.asList("0", "0", "0", "0", "0", "0", player.getName(), Integer.toString(player.getObjectId()), player.getAccountName(), "9", " ", " ", text.replaceAll("<br>", System.lineSeparator()), "0", "0", "0", "0")));
    }

    public static boolean isInsideRangeOfObjectId(WorldObject obj, int targetObjId, int radius) {
        final WorldObject target = World.getInstance().findObject(targetObjId);
        return (target != null) && isInsideRadius3D(obj, target, radius);
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

    public static int hashIp(Player player) {
        return org.l2j.commons.util.Util.hashIp(player.getIPAddress());
    }

    public static boolean isPlayer(WorldObject object) {
        return object instanceof Player;
    }

    public static double calcIfIsPlayer(WorldObject object, ToDoubleFunction<Player> function) {
        return object instanceof Player player ? function.applyAsDouble(player) : 0;
    }

    public static boolean isCreature(WorldObject object) {
        return object instanceof Creature;
    }

    public static void doIfIsCreature(WorldObject object, Consumer<Creature> action) {
        if(object instanceof Creature creature) {
            action.accept(creature);
        }
    }

    public static boolean isMonster(WorldObject object) {
        return object instanceof Monster;
    }

    public static boolean isNpc(WorldObject object) {
        return object instanceof Npc;
    }

    public static void doIfIsNpc(WorldObject object, Consumer<Npc> action) {
        if(object instanceof Npc npc) {
            action.accept(npc);
        }
    }

    public static boolean isPlayable(WorldObject object) {
        return object instanceof Playable;
    }

    public static boolean isArtifact(WorldObject object) {
        return object instanceof Artefact;
    }

    public static boolean isGM(WorldObject object) {
        return object instanceof Player player && player.isGM();
    }

    public static boolean isWalker(WorldObject object) {
        if(isMonster(object)) {
            Monster monster = (Monster) object;
            Monster leader;
            return nonNull(leader = monster.getLeader()) ? isWalker(leader) : WalkingManager.getInstance().isRegistered(monster);
        }
        return isNpc(object) &&  WalkingManager.getInstance().isRegistered((Npc) object);
    }

    public static boolean isSummon(WorldObject object) {
        return object instanceof Summon;
    }

    public static boolean isServitor(WorldObject object) {
        return object instanceof Servitor;
    }

    public static boolean isAttackable(WorldObject object) {
        return object instanceof Attackable && !(object instanceof FriendlyNpc);
    }

    public static boolean isPet(WorldObject object) {
        return object instanceof Pet;
    }

    public static boolean isDoor(WorldObject object) {
        return object instanceof Door;
    }

    public static boolean isTrap(WorldObject object) {
        return object instanceof Trap;
    }

    public static boolean isItem(WorldObject object) {
        return object instanceof Item;
    }

    public static boolean isWarehouseManager(WorldObject object) {
        return object instanceof Warehouse;
    }

    public static boolean isWeapon(Item item) {
        return nonNull(item) && isWeapon(item.getTemplate());
    }

    public static boolean isWeapon(ItemTemplate item) {
        return item instanceof Weapon;
    }

    public static boolean isArmor(ItemTemplate item) {
        return item instanceof Armor;
    }

    public static boolean canTeleport(Player player) {
        return !( isNull(player) || player.isInDuel() || player.isControlBlocked() || player.isConfused() || player.isFlying() || player.isFlyingMounted() ||
                player.isInOlympiadMode() || player.isAlikeDead() || player.isOnCustomEvent() || player.getPvpFlag() > 0 || player.isInsideZone(ZoneType.JAIL) || player.isInTimedHuntingZone());
    }
}
