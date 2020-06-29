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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.data.database.announce.manager.AnnouncementsManager;
import org.l2j.gameserver.data.sql.impl.OfflineTradersTable;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.data.xml.impl.BeautyShopData;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.enums.StatusUpdateType;
import org.l2j.gameserver.enums.SubclassInfoType;
import org.l2j.gameserver.instancemanager.*;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.model.entity.Event;
import org.l2j.gameserver.model.entity.Siege;
import org.l2j.gameserver.model.holders.AttendanceInfoHolder;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.model.variables.PlayerVariables;
import org.l2j.gameserver.network.ConnectionState;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.network.serverpackets.ExUserBoostStat.BoostStatType;
import org.l2j.gameserver.network.serverpackets.attendance.ExVipAttendanceItemList;
import org.l2j.gameserver.network.serverpackets.autoplay.ExActivateAutoShortcut;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritInfo;
import org.l2j.gameserver.network.serverpackets.friend.FriendListPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.network.serverpackets.item.ItemList;
import org.l2j.gameserver.network.serverpackets.mission.ExConnectedTimeAndGettableReward;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeShowMemberListAll;
import org.l2j.gameserver.settings.*;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.world.MapRegionManager;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.ZoneType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * Enter World Packet Handler
 * <p>
 * <p>
 * 0000: 03
 * <p>
 * packet format rev87 bddddbdcccccccccccccccccccc
 * <p>
 * @author JoeAlisson
 */
public class EnterWorld extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnterWorld.class);
    private final int[][] tracert = new int[5][4];

    @Override
    public void readImpl() {
        for (int i = 0; i < 5; i++) {
            for (int o = 0; o < 4; o++) {
                tracert[i][o] = readByte();
            }
        }
        readInt(); // Unknown Value
        readInt(); // Unknown Value
        readInt(); // Unknown Value
        readInt(); // Unknown Value
        readBytes(new byte[64]); // Unknown Byte Array
        readInt(); // Unknown Value
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            LOGGER.warn("EnterWorld failed! player returned 'null'.");
            Disconnection.of(client).defaultSequence(false);
            return;
        }

        client.setConnectionState(ConnectionState.IN_GAME);

/*
        TODO send address to authserver
        final String[] address = new String[5];
        for (int i = 0; i < 5; i++) {
            address[i] = tracert[i][0] + "." + tracert[i][1] + "." + tracert[i][2] + "." + tracert[i][3];
        }

        AuthServerCommunication.getInstance().sendClientTracert(activeChar.getAccountName(), adress);*/

        client.setClientTracert(tracert);

        // Restore to instanced area if enabled
        if (Config.RESTORE_PLAYER_INSTANCE) {
            final PlayerVariables vars = player.getVariables();
            final Instance instance = InstanceManager.getInstance().getPlayerInstance(player, false);
            if ((instance != null) && (instance.getId() == vars.getInt("INSTANCE_RESTORE", 0))) {
                player.setInstance(instance);
            }
            vars.remove("INSTANCE_RESTORE");
        }

        player.updatePvpTitleAndColor(false);

        if (player.isGM()) {
            onGameMasterEnter(player);
        }

        if (player.isChatBanned()) {
            player.getEffectList().startAbnormalVisualEffect(AbnormalVisualEffect.NO_CHAT);
        }

        if (player.getCurrentHp() < 0.5) {
            player.setIsDead(true);
        }

        if (Config.ENABLE_VITALITY) {
            player.sendPacket(new ExVitalityEffectInfo(player));
        }

        client.sendPacket(new ExEnterWorld());
        player.getMacros().sendAllMacros();
        client.sendPacket(new ExGetBookMarkInfoPacket(player));

        ItemList.sendList(player);
        client.sendPacket(new ExQuestItemList(1, player));
        client.sendPacket(new ExQuestItemList(2, player));
        player.sendPacket(ExBasicActionList.STATIC_PACKET);

        for (Castle castle : CastleManager.getInstance().getCastles()) {
            player.sendPacket(new ExCastleState(castle));
        }

        player.sendPacket(new HennaInfo(player));
        player.sendSkillList();
        player.sendPacket(new EtcStatusUpdate(player));

        boolean showClanNotice = false;

        // Clan related checks are here
        final Clan clan = player.getClan();
        // Clan packets
        if (clan != null) {
            notifyClanMembers(player);
            notifySponsorOrApprentice(player);

            for (Siege siege : SiegeManager.getInstance().getSieges()) {
                if (!siege.isInProgress()) {
                    continue;
                }

                if (siege.checkIsAttacker(clan)) {
                    player.setSiegeState((byte) 1);
                    player.setSiegeSide(siege.getCastle().getId());
                } else if (siege.checkIsDefender(clan)) {
                    player.setSiegeState((byte) 2);
                    player.setSiegeSide(siege.getCastle().getId());
                }
            }

            // Residential skills support
            if (player.getClan().getCastleId() > 0) {
                CastleManager.getInstance().getCastleByOwner(clan).giveResidentialSkills(player);
            }

            showClanNotice = clan.isNoticeEnabled();

            clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(player));
            PledgeShowMemberListAll.sendAllTo(player);
            clan.broadcastToOnlineMembers(new ExPledgeCount(clan));
            player.sendPacket(new PledgeSkillList(clan));
            final ClanHall ch = ClanHallManager.getInstance().getClanHallByClan(clan);
            if ((ch != null) && (ch.getCostFailDay() > 0)) {
                final SystemMessage sm = getSystemMessage(SystemMessageId.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW);
                sm.addInt(ch.getLease());
                player.sendPacket(sm);
            }
        } else {
            player.sendPacket(ExPledgeWaitingListAlarm.STATIC_PACKET);
        }

        client.sendPacket(new ExSubjobInfo(player, SubclassInfoType.NO_CHANGES));
        client.sendPacket(new ExUserInfoInvenWeight(player));
        client.sendPacket(new ExAdenaInvenCount(player));
        client.sendPacket(new ExBloodyCoinCount());
        client.sendPacket(new ShortCutInit());
        player.forEachShortcut(s -> {
            if(s.isActive()) {
                client.sendPacket(new ExActivateAutoShortcut(s.getClientId(), true));
            }
        });
        client.sendPacket(new ExDressRoomUiOpen());

        if (Config.PLAYER_SPAWN_PROTECTION > 0) {
            player.setSpawnProtection(true);
        }

        player.spawnMe();
        player.sendPacket(new ExRotation(player.getObjectId(), player.getHeading()));

        if (Event.isParticipant(player)) {
            Event.restorePlayerEventStatus(player);
        }

        if (Config.PC_CAFE_ENABLED) {
            if (player.getPcCafePoints() > 0) {
                player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), 0, 1));
            } else {
                player.sendPacket(new ExPCCafePointInfo());
            }
        }

        player.sendPacket(new ExStorageMaxCount(player));
        client.sendPacket(new FriendListPacket(player));

        SystemMessage sm = getSystemMessage(SystemMessageId.YOUR_FRIEND_S1_JUST_LOGGED_IN).addString(player.getName());
        var world = World.getInstance();
        player.getFriendList().stream().mapToObj(world::findPlayer).filter(Objects::nonNull).forEach(sm::sendTo);

        player.sendPacket(SystemMessageId.WELCOME_TO_THE_WORLD);

        AnnouncementsManager.getInstance().showAnnouncements(player);

        if (getSettings(ServerSettings.class).scheduleRestart() && (Config.SERVER_RESTART_SCHEDULE_MESSAGE)) {
            player.sendPacket(new CreatureSay(2, ChatType.BATTLEFIELD, "[SERVER]", "Next restart is scheduled at " + ServerRestartManager.getInstance().getNextRestartTime() + "."));
        }

        if (showClanNotice) {
            final NpcHtmlMessage notice = new NpcHtmlMessage();
            notice.setFile(player, "data/html/clanNotice.htm");
            notice.replace("%clan_name%", player.getClan().getName());
            notice.replace("%notice_text%", player.getClan().getNotice().replaceAll("\r\n", "<br>"));
            notice.disableValidation();
            client.sendPacket(notice);
        } else if (Config.SERVER_NEWS) {
            final String serverNews = HtmCache.getInstance().getHtm(player, "data/html/servnews.htm");
            if (serverNews != null) {
                client.sendPacket(new NpcHtmlMessage(serverNews));
            }
        }

        if (Config.PETITIONING_ALLOWED) {
            PetitionManager.getInstance().checkPetitionMessages(player);
        }

        client.sendPacket(new SkillCoolTime(player));
        client.sendPacket(new ExVoteSystemInfo(player));

        for (Item item : player.getInventory().getItems()) {
            if (item.isTimeLimitedItem()) {
                item.scheduleLifeTimeTask();
            }
        }

        for (Item whItem : player.getWarehouse().getItems()) {
            if (whItem.isTimeLimitedItem()) {
                whItem.scheduleLifeTimeTask();
            }
        }

        if (player.getClanJoinExpiryTime() > System.currentTimeMillis()) {
            player.sendPacket(SystemMessageId.YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN_YOU_ARE_NOT_ALLOWED_TO_JOIN_ANOTHER_CLAN_FOR_24_HOURS);
        }

        // Attacker or spectator logging in to a siege zone.
        // Actually should be checked for inside castle only?
        if (!player.canOverrideCond(PcCondOverride.ZONE_CONDITIONS) && player.isInsideZone(ZoneType.SIEGE) && (!player.isInSiege() || (player.getSiegeState() < 2))) {
            player.teleToLocation(TeleportWhereType.TOWN);
        }

        if (getSettings(GeneralSettings.class).allowMail()) {
            MailEngine.getInstance().sendUnreadCount(player);
        }

        if (Config.WELCOME_MESSAGE_ENABLED) {
            player.sendPacket(new ExShowScreenMessage(Config.WELCOME_MESSAGE_TEXT, Config.WELCOME_MESSAGE_TIME));
        }

        if (!player.getPremiumItemList().isEmpty()) {
            player.sendPacket(ExNotifyPremiumItem.STATIC_PACKET);
        }

        if ((Config.OFFLINE_TRADE_ENABLE || Config.OFFLINE_CRAFT_ENABLE) && Config.STORE_OFFLINE_TRADE_IN_REALTIME) {
            OfflineTradersTable.onTransaction(player, true, false);
        }

        if (BeautyShopData.getInstance().hasBeautyData(player.getRace(), player.getAppearance().getSexType())) {
            player.sendPacket(new ExBeautyItemList(player));
        }

        if(player.getActiveElementalSpiritType() >= 0) {
            client.sendPacket(new ElementalSpiritInfo(player.getActiveElementalSpiritType(), (byte) 2));
        }

        player.broadcastUserInfo();
        player.sendPacket(StatusUpdate.of(player, StatusUpdateType.CUR_HP, (int) player.getCurrentHp()).addUpdate(StatusUpdateType.MAX_HP, player.getMaxHp()));
        player.sendPacket(new ExUserInfoEquipSlot(player));

        if (getSettings(ChatSettings.class).worldChatEnabled()) {
            player.sendPacket(new ExWorldChatCnt(player));
        }

        // Fix for equipped item skills
        if (!player.getEffectList().getCurrentAbnormalVisualEffects().isEmpty()) {
            player.updateAbnormalVisualEffects();
        }

        if (getSettings(AttendanceSettings.class).enabled()) {
            sendAttendanceInfo(player);
        }

        var rateXp = getSettings(RateSettings.class).xp();
        if(rateXp > 1) {
           // player.sendPacket(new ExUserBoostStat(BoostStatType.SERVER, (short) (rateXp * 100 - 100)));
        }

        player.sendPacket(new ExConnectedTimeAndGettableReward(player));
        player.sendPacket(new ExAutoSoulShot(0, true, 0));
        player.sendPacket(new ExAutoSoulShot(0, true, 1));
        player.sendPacket(new ExAutoSoulShot(0, true, 2));
        player.sendPacket(new ExAutoSoulShot(0, true, 3));

        if (Config.HARDWARE_INFO_ENABLED) {
            ThreadPool.schedule(() -> {
                if (client.getHardwareInfo() == null) {
                    Disconnection.of(client).defaultSequence(false);
                }
            }, 5000);
        }

        // Check if in time limited hunting zone.
        if (player.isInTimedHuntingZone())
        {
            final long currentTime = System.currentTimeMillis();
            final long pirateTombExitTime = player.getVariables().getLong(PlayerVariables.HUNTING_ZONE_RESET_TIME + 2, 0);
            if ((pirateTombExitTime > currentTime) && player.isInTimedHuntingZone(2))
            {
                player.startTimedHuntingZone(1, pirateTombExitTime - currentTime);
            }
            else
            {
                player.teleToLocation(MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.TOWN));
            }
        }
        player.onEnter();
        Quest.playerEnter(player);
    }

    private void sendAttendanceInfo(Player activeChar) {
        var attendanceSettings = getSettings(AttendanceSettings.class);
        ThreadPool.schedule(() -> {
            // Check if player can receive reward today.
            final AttendanceInfoHolder attendanceInfo = activeChar.getAttendanceInfo();
            if (attendanceInfo.isRewardAvailable()) {
                final int lastRewardIndex = attendanceInfo.getRewardIndex() + 1;
                activeChar.sendPacket(new ExShowScreenMessage("Your attendance day " + lastRewardIndex + " reward is ready.", ExShowScreenMessage.TOP_CENTER, 7000, 0, true, true));
                activeChar.sendMessage("Your attendance day " + lastRewardIndex + " reward is ready.");
                activeChar.sendMessage("Click on General Menu -> Attendance Check.");
                if (attendanceSettings.popUpWindow()) {
                    activeChar.sendPacket(new ExVipAttendanceItemList(activeChar));
                }
            }
        }, attendanceSettings.delay() * 60  * 1000);
    }

    private void onGameMasterEnter(Player activeChar) {

        if (Config.GM_GIVE_SPECIAL_SKILLS) {
            SkillTreesData.getInstance().addSkills(activeChar, false);
        }

        if (Config.GM_GIVE_SPECIAL_AURA_SKILLS) {
            SkillTreesData.getInstance().addSkills(activeChar, true);
        }

        if (Config.GM_STARTUP_AUTO_LIST && AdminData.getInstance().hasAccess("admin_gmliston", activeChar.getAccessLevel())) {
            AdminData.getInstance().addGm(activeChar, false);
        } else {
            AdminData.getInstance().addGm(activeChar, true);
        }

        if (Config.GM_STARTUP_BUILDER_HIDE && AdminData.getInstance().hasAccess("admin_hide", activeChar.getAccessLevel())) {
            BuilderUtil.setHiding(activeChar, true);

            BuilderUtil.sendSysMessage(activeChar, "hide is default for builder.");
            BuilderUtil.sendSysMessage(activeChar, "FriendAddOff is default for builder.");
            BuilderUtil.sendSysMessage(activeChar, "whisperoff is default for builder.");

            // It isn't recommend to use the below custom L2J GMStartup functions together with retail-like GMStartupBuilderHide, so breaking the process at that stage.
            return;
        }

        if (Config.GM_STARTUP_INVULNERABLE && AdminData.getInstance().hasAccess("admin_invul", activeChar.getAccessLevel())) {
            activeChar.setIsInvul(true);
        }

        if (Config.GM_STARTUP_INVISIBLE && AdminData.getInstance().hasAccess("admin_invisible", activeChar.getAccessLevel())) {
            activeChar.setInvisible(true);
            activeChar.getEffectList().startAbnormalVisualEffect(AbnormalVisualEffect.STEALTH);
        }

        if (Config.GM_STARTUP_SILENCE && AdminData.getInstance().hasAccess("admin_silence", activeChar.getAccessLevel())) {
            activeChar.setSilenceMode(true);
        }

        if (Config.GM_STARTUP_DIET_MODE && AdminData.getInstance().hasAccess("admin_diet", activeChar.getAccessLevel())) {
            activeChar.setDietMode(true);
            activeChar.refreshOverloaded(true);
        }
    }

    private void notifyClanMembers(Player activeChar) {
        final Clan clan = activeChar.getClan();
        if (clan != null) {
            clan.getClanMember(activeChar.getObjectId()).setPlayerInstance(activeChar);

            final SystemMessage msg = getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_HAS_LOGGED_INTO_GAME);
            msg.addString(activeChar.getName());
            clan.broadcastToOtherOnlineMembers(msg, activeChar);
            clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListUpdate(activeChar), activeChar);
        }
    }

    private void notifySponsorOrApprentice(Player activeChar) {
        if (activeChar.getSponsor() != 0) {
            final Player sponsor = World.getInstance().findPlayer(activeChar.getSponsor());
            if (sponsor != null) {
                final SystemMessage msg = getSystemMessage(SystemMessageId.YOUR_APPRENTICE_S1_HAS_LOGGED_IN);
                msg.addString(activeChar.getName());
                sponsor.sendPacket(msg);
            }
        } else if (activeChar.getApprentice() != 0) {
            final Player apprentice = World.getInstance().findPlayer(activeChar.getApprentice());
            if (apprentice != null) {
                final SystemMessage msg = getSystemMessage(SystemMessageId.YOUR_SPONSOR_C1_HAS_LOGGED_IN);
                msg.addString(activeChar.getName());
                apprentice.sendPacket(msg);
            }
        }
    }
}
