/*
 * Copyright © 2019-2021 L2JOrg
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
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.data.xml.impl.BeautyShopData;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.engine.clan.clanhall.ClanHall;
import org.l2j.gameserver.engine.clan.clanhall.ClanHallEngine;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.enums.StatusUpdateType;
import org.l2j.gameserver.enums.SubclassInfoType;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.instancemanager.PetitionManager;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.entity.Event;
import org.l2j.gameserver.model.entity.Siege;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.network.ConnectionState;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.network.serverpackets.attendance.ExVipAttendanceItemList;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritInfo;
import org.l2j.gameserver.network.serverpackets.friend.FriendListPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.network.serverpackets.magiclamp.ExMagicLampExpInfoUI;
import org.l2j.gameserver.network.serverpackets.mission.ExConnectedTimeAndGettableReward;
import org.l2j.gameserver.network.serverpackets.pledge.ExPledgeCount;
import org.l2j.gameserver.network.serverpackets.pledge.ExPledgeWaitingListAlarm;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeShowMemberListAll;
import org.l2j.gameserver.settings.*;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.world.MapRegionManager;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.ZoneType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
            Disconnection.of(client).logout(false);
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
            final Instance instance = InstanceManager.getInstance().getPlayerInstance(player, false);
            if ((instance != null) && (instance.getId() == player.getInstanceRestore())) {
                player.setInstance(instance);
            }
            player.setInstanceRestore(-1);
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

        if (CharacterSettings.vitalityEnabled()) {
            player.sendPacket(new ExVitalityEffectInfo(player));
        }

        if (Config.ENABLE_MAGIC_LAMP)
        {
            player.sendPacket(new ExMagicLampExpInfoUI(player));
        }

        client.sendPacket(new ExEnterWorld());
        player.getMacros().sendAllMacros();
        client.sendPacket(new ExGetBookMarkInfoPacket(player));
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
            clan.onMemberLogin(player);
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
            final ClanHall ch = ClanHallEngine.getInstance().getClanHallByClan(clan);
            if ((ch != null) && (ch.getCostFailDay() > 0)) {
                var sm = getSystemMessage(SystemMessageId.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW).addLong(ch.getLease());
                player.sendPacket(sm);
            }
        } else {
            player.sendPacket(ExPledgeWaitingListAlarm.STATIC_PACKET);
        }

        client.sendPacket(new ExSubjobInfo(player, SubclassInfoType.NO_CHANGES));
        client.sendPacket(new ExDressRoomUiOpen());

        if (CharacterSettings.spawnProtection() > 0) {
            player.setSpawnProtection(true);
        }

        player.sendPacket(new UserInfo(player));
        player.sendPacket(new ExRotation(player.getObjectId(), player.getHeading()));
        player.spawnMe();

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

        client.sendPacket(new FriendListPacket(player));

        SystemMessage sm = getSystemMessage(SystemMessageId.YOUR_FRIEND_S1_JUST_LOGGED_IN).addString(player.getName());
        var world = World.getInstance();
        player.getFriendList().stream().mapToObj(world::findPlayer).filter(Objects::nonNull).forEach(sm::sendTo);

        player.sendPacket(SystemMessageId.WELCOME_TO_THE_WORLD);

        AnnouncementsManager.getInstance().showAnnouncements(player);

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

        if (CharacterSettings.petitionAllowed()) {
            PetitionManager.getInstance().checkPetitionMessages(player);
        }

        client.sendPacket(new SkillCoolTime(player));
        client.sendPacket(new ExVoteSystemInfo(player));

        if (player.getClanJoinExpiryTime() > System.currentTimeMillis()) {
            player.sendPacket(SystemMessageId.YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN_YOU_ARE_NOT_ALLOWED_TO_JOIN_ANOTHER_CLAN_FOR_24_HOURS);
        }

        // Attacker or spectator logging in to a siege zone.
        // Actually should be checked for inside castle only?
        if (!player.canOverrideCond(PcCondOverride.ZONE_CONDITIONS) && player.isInsideZone(ZoneType.SIEGE) && (!player.isInSiege() || (player.getSiegeState() < 2))) {
            player.teleToLocation(TeleportWhereType.TOWN);
        }

        if (GeneralSettings.allowMail()) {
            MailEngine.getInstance().sendUnreadCount(player);
        }

        if (Config.WELCOME_MESSAGE_ENABLED) {
            player.sendPacket(new ExShowScreenMessage(Config.WELCOME_MESSAGE_TEXT, Config.WELCOME_MESSAGE_TIME));
        }

        if(player.getActiveElementalSpiritType() >= 0) {
            client.sendPacket(new ElementalSpiritInfo(player.getActiveElementalSpiritType(), (byte) 2));
        }

        player.sendPacket(StatusUpdate.of(player, StatusUpdateType.CUR_HP, (int) player.getCurrentHp()).addUpdate(StatusUpdateType.MAX_HP, player.getMaxHp()));
        player.sendPacket(new ExUserInfoEquipSlot(player));

        if (ChatSettings.worldChatEnabled()) {
            player.sendPacket(new ExWorldChatCnt(player));
        }

        // Fix for equipped item skills
        if (!player.getEffectList().getCurrentAbnormalVisualEffects().isEmpty()) {
            player.updateAbnormalVisualEffects();
        }

        if (AttendanceSettings.enabled()) {
            sendAttendanceInfo(player);
        }

        player.sendPacket(new ExConnectedTimeAndGettableReward(player));

        if (ServerSettings.isHardwareInfoEnabled()) {
            ThreadPool.schedule(() -> {
                if (client.getHardwareInfo() == null) {
                    Disconnection.of(client).logout(false);
                }
            }, 5000);
        }

        // Check if in time limited hunting zone.
        if (player.isInTimedHuntingZone()) {
            final long currentTime = System.currentTimeMillis();
            final long pirateTombExitTime = player.getHuntingZoneResetTime(2);
            if ((pirateTombExitTime > currentTime) && player.isInTimedHuntingZone(2)) {
                player.startTimedHuntingZone(1, pirateTombExitTime - currentTime);
            }
            else
            {
                player.teleToLocation(MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.TOWN));
            }
        }
        restoreItems(player);
        player.onEnter();
        Quest.playerEnter(player);
    }

    private void restoreItems(Player player) {
        player.getInventory().restore();
        player.sendItemList();
        player.sendPacket(new ExStorageMaxCount(player));
        player.getWarehouse().forEachItem(Item::isTimeLimitedItem, Item::scheduleLifeTimeTask);

        activeAutoShots(player);

        if (BeautyShopData.getInstance().hasBeautyData(player.getRace(), player.getAppearance().getSexType())) {
            player.sendPacket(new ExBeautyItemList(player));
        }
    }

    private void activeAutoShots(Player player) {
        player.enableAutoSoulShot(ShotType.SOULSHOTS, player.getSavedSoulshot());
        player.enableAutoSoulShot(ShotType.SPIRITSHOTS, player.getSavedSpiritshot());
        player.enableAutoSoulShot(ShotType.BEAST_SOULSHOTS, 0);
        player.enableAutoSoulShot(ShotType.BEAST_SPIRITSHOTS, 0);
    }

    private void sendAttendanceInfo(Player player) {
        ThreadPool.schedule(() -> {
            // Check if player can receive reward today.
            if (player.canReceiveAttendance()) {
                final int lastRewardIndex = player.lastAttendanceReward() + 1;
                player.sendPacket(new ExShowScreenMessage("Your attendance day " + lastRewardIndex + " reward is ready.", ExShowScreenMessage.TOP_CENTER, 7000, 0, true, true));
                player.sendMessage("Your attendance day " + lastRewardIndex + " reward is ready.");
                player.sendMessage("Click on General Menu -> Attendance Check.");
                if (AttendanceSettings.popUpWindow()) {
                    player.sendPacket(new ExVipAttendanceItemList(player));
                }
            }
        }, AttendanceSettings.delay(), TimeUnit.MINUTES);
    }

    private void onGameMasterEnter(Player player) {
        if (AdminSettings.giveGMSkills()) {
            SkillTreesData.getInstance().addGMSkills(player);
        }

        AdminData.getInstance().addGm(player, true);
        player.setDietMode(true);

        if (AdminSettings.startUpHide() && AdminData.getInstance().hasAccess("admin_hide", player.getAccessLevel())) {
            BuilderUtil.setHiding(player, true);

            BuilderUtil.sendSysMessage(player, "hide is default for builder.");
            BuilderUtil.sendSysMessage(player, "FriendAddOff is default for builder.");
            BuilderUtil.sendSysMessage(player, "whisperoff is default for builder.");
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
