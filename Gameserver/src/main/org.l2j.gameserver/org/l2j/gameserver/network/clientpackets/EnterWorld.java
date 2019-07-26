package org.l2j.gameserver.network.clientpackets;

import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.data.sql.impl.AnnouncementsTable;
import org.l2j.gameserver.data.sql.impl.OfflineTradersTable;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.data.xml.impl.BeautyShopData;
import org.l2j.gameserver.data.xml.impl.ClanHallData;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.enums.SubclassInfoType;
import org.l2j.gameserver.instancemanager.*;
import org.l2j.gameserver.model.*;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.*;
import org.l2j.gameserver.model.holders.AttendanceInfoHolder;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.model.variables.PlayerVariables;
import org.l2j.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.network.ConnectionState;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.network.serverpackets.attendance.ExVipAttendanceItemList;
import org.l2j.gameserver.network.serverpackets.dailymission.ExConnectedTimeAndGettableReward;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritInfo;
import org.l2j.gameserver.network.serverpackets.friend.FriendListPacket;
import org.l2j.gameserver.util.BuilderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enter World Packet Handler
 * <p>
 * <p>
 * 0000: 03
 * <p>
 * packet format rev87 bddddbdcccccccccccccccccccc
 * <p>
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
        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            LOGGER.warn("EnterWorld failed! activeChar returned 'null'.");
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
            final PlayerVariables vars = activeChar.getVariables();
            final Instance instance = InstanceManager.getInstance().getPlayerInstance(activeChar, false);
            if ((instance != null) && (instance.getId() == vars.getInt("INSTANCE_RESTORE", 0))) {
                activeChar.setInstance(instance);
            }
            vars.remove("INSTANCE_RESTORE");
        }

        activeChar.updatePvpTitleAndColor(false);

        // Apply special GM properties to the GM when entering
        if (activeChar.isGM()) {
            onGameMasterEnter(activeChar);
        }

        // Chat banned icon.
        if (activeChar.isChatBanned()) {
            activeChar.getEffectList().startAbnormalVisualEffect(AbnormalVisualEffect.NO_CHAT);
        }

        // Set dead status if applies
        if (activeChar.getCurrentHp() < 0.5) {
            activeChar.setIsDead(true);
        }

        if (Config.ENABLE_VITALITY) {
            activeChar.sendPacket(new ExVitalityEffectInfo(activeChar));
        }

        // Send Macro List
        activeChar.getMacros().sendAllMacros();

        // Send Teleport Bookmark List
        client.sendPacket(new ExGetBookMarkInfoPacket(activeChar));

        // Send Item List
        client.sendPacket(new ItemList(1, activeChar));
        client.sendPacket(new ItemList(2, activeChar));

        // Send Quest Item List
        client.sendPacket(new ExQuestItemList(1, activeChar));
        client.sendPacket(new ExQuestItemList(2, activeChar));

        // Send Adena and Inventory Count
        client.sendPacket(new ExAdenaInvenCount(activeChar));

        // Send Shortcuts
        client.sendPacket(new ShortCutInit(activeChar));

        // Send Action list
        activeChar.sendPacket(ExBasicActionList.STATIC_PACKET);

        // Send blank skill list
        activeChar.sendPacket(new SkillList());

        // Send castle state.
        for (Castle castle : CastleManager.getInstance().getCastles()) {
            activeChar.sendPacket(new ExCastleState(castle));
        }

        // Send Dye Information
        activeChar.sendPacket(new HennaInfo(activeChar));

        // Send Skill list
        activeChar.sendSkillList();

        // Send EtcStatusUpdate
        activeChar.sendPacket(new EtcStatusUpdate(activeChar));

        boolean showClanNotice = false;

        // Clan related checks are here
        final Clan clan = activeChar.getClan();
        // Clan packets
        if (clan != null) {
            notifyClanMembers(activeChar);
            notifySponsorOrApprentice(activeChar);

            for (Siege siege : SiegeManager.getInstance().getSieges()) {
                if (!siege.isInProgress()) {
                    continue;
                }

                if (siege.checkIsAttacker(clan)) {
                    activeChar.setSiegeState((byte) 1);
                    activeChar.setSiegeSide(siege.getCastle().getResidenceId());
                } else if (siege.checkIsDefender(clan)) {
                    activeChar.setSiegeState((byte) 2);
                    activeChar.setSiegeSide(siege.getCastle().getResidenceId());
                }
            }

            for (FortSiege siege : FortSiegeManager.getInstance().getSieges()) {
                if (!siege.isInProgress()) {
                    continue;
                }

                if (siege.checkIsAttacker(clan)) {
                    activeChar.setSiegeState((byte) 1);
                    activeChar.setSiegeSide(siege.getFort().getResidenceId());
                } else if (siege.checkIsDefender(clan)) {
                    activeChar.setSiegeState((byte) 2);
                    activeChar.setSiegeSide(siege.getFort().getResidenceId());
                }
            }

            // Residential skills support
            if (activeChar.getClan().getCastleId() > 0) {
                CastleManager.getInstance().getCastleByOwner(clan).giveResidentialSkills(activeChar);
            }

            if (activeChar.getClan().getFortId() > 0) {
                FortDataManager.getInstance().getFortByOwner(clan).giveResidentialSkills(activeChar);
            }

            showClanNotice = clan.isNoticeEnabled();

            clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(activeChar));
            PledgeShowMemberListAll.sendAllTo(activeChar);
            clan.broadcastToOnlineMembers(new ExPledgeCount(clan));
            activeChar.sendPacket(new PledgeSkillList(clan));
            final ClanHall ch = ClanHallData.getInstance().getClanHallByClan(clan);
            if ((ch != null) && (ch.getCostFailDay() > 0)) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW);
                sm.addInt(ch.getLease());
                activeChar.sendPacket(sm);
            }
        } else {
            activeChar.sendPacket(ExPledgeWaitingListAlarm.STATIC_PACKET);
        }

        // Send SubClass Info
        activeChar.sendPacket(new ExSubjobInfo(activeChar, SubclassInfoType.NO_CHANGES));

        // Send Inventory Info
        activeChar.sendPacket(new ExUserInfoInvenWeight(activeChar));

        // Send Adena / Inventory Count Info
        activeChar.sendPacket(new ExAdenaInvenCount(activeChar));

        // Send Unread Mail Count
        if (MailManager.getInstance().hasUnreadPost(activeChar)) {
            activeChar.sendPacket(new ExUnReadMailCount(activeChar));
        }

        // Send Quest List
        activeChar.sendPacket(new QuestList(activeChar));

        if (Config.PLAYER_SPAWN_PROTECTION > 0) {
            activeChar.setSpawnProtection(true);
        }

        activeChar.spawnMe();
        activeChar.sendPacket(new ExRotation(activeChar.getObjectId(), activeChar.getHeading()));

        activeChar.getInventory().applyItemSkills();

        if (Event.isParticipant(activeChar)) {
            Event.restorePlayerEventStatus(activeChar);
        }

        if (activeChar.isCursedWeaponEquipped()) {
            CursedWeaponsManager.getInstance().getCursedWeapon(activeChar.getCursedWeaponEquippedId()).cursedOnLogin();
        }

        if (Config.PC_CAFE_ENABLED) {
            if (activeChar.getPcCafePoints() > 0) {
                activeChar.sendPacket(new ExPCCafePointInfo(activeChar.getPcCafePoints(), 0, 1));
            } else {
                activeChar.sendPacket(new ExPCCafePointInfo());
            }
        }

        // Expand Skill
        activeChar.sendPacket(new ExStorageMaxCount(activeChar));

        // Friend list
        client.sendPacket(new FriendListPacket(activeChar));

        SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_FRIEND_S1_JUST_LOGGED_IN);
        sm.addString(activeChar.getName());
        for (int id : activeChar.getFriendList()) {
            final WorldObject obj = World.getInstance().findObject(id);
            if (obj != null) {
                obj.sendPacket(sm);
            }
        }

        activeChar.sendPacket(SystemMessageId.WELCOME_TO_THE_WORLD_OF_LINEAGE_II);

        AnnouncementsTable.getInstance().showAnnouncements(activeChar);

        if ((Config.SERVER_RESTART_SCHEDULE_ENABLED) && (Config.SERVER_RESTART_SCHEDULE_MESSAGE)) {
            activeChar.sendPacket(new CreatureSay(2, ChatType.BATTLEFIELD, "[SERVER]", "Next restart is scheduled at " + ServerRestartManager.getInstance().getNextRestartTime() + "."));
        }

        if (showClanNotice) {
            final NpcHtmlMessage notice = new NpcHtmlMessage();
            notice.setFile(activeChar, "data/html/clanNotice.htm");
            notice.replace("%clan_name%", activeChar.getClan().getName());
            notice.replace("%notice_text%", activeChar.getClan().getNotice().replaceAll("\r\n", "<br>"));
            notice.disableValidation();
            client.sendPacket(notice);
        } else if (Config.SERVER_NEWS) {
            final String serverNews = HtmCache.getInstance().getHtm(activeChar, "data/html/servnews.htm");
            if (serverNews != null) {
                client.sendPacket(new NpcHtmlMessage(serverNews));
            }
        }

        if (Config.PETITIONING_ALLOWED) {
            PetitionManager.getInstance().checkPetitionMessages(activeChar);
        }

        if (activeChar.isAlikeDead()) // dead or fake dead
        {
            // no broadcast needed since the player will already spawn dead to others
            client.sendPacket(new Die(activeChar));
        }

        client.sendPacket(new SkillCoolTime(activeChar));
        client.sendPacket(new ExVoteSystemInfo(activeChar));

        for (Item item : activeChar.getInventory().getItems()) {
            if (item.isTimeLimitedItem()) {
                item.scheduleLifeTimeTask();
            }
            if (item.isShadowItem() && item.isEquipped()) {
                item.decreaseMana(false);
            }
        }

        for (Item whItem : activeChar.getWarehouse().getItems()) {
            if (whItem.isTimeLimitedItem()) {
                whItem.scheduleLifeTimeTask();
            }
        }

        if (activeChar.getClanJoinExpiryTime() > System.currentTimeMillis()) {
            activeChar.sendPacket(SystemMessageId.YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN_YOU_ARE_NOT_ALLOWED_TO_JOIN_ANOTHER_CLAN_FOR_24_HOURS);
        }

        // remove combat flag before teleporting
        if (activeChar.getInventory().getItemByItemId(9819) != null) {
            final Fort fort = FortDataManager.getInstance().getFort(activeChar);
            if (fort != null) {
                FortSiegeManager.getInstance().dropCombatFlag(activeChar, fort.getResidenceId());
            } else {
                final long slot = activeChar.getInventory().getSlotFromItem(activeChar.getInventory().getItemByItemId(9819));
                activeChar.getInventory().unEquipItemInBodySlot(slot);
                activeChar.destroyItem("CombatFlag", activeChar.getInventory().getItemByItemId(9819), null, true);
            }
        }

        // Attacker or spectator logging in to a siege zone.
        // Actually should be checked for inside castle only?
        if (!activeChar.canOverrideCond(PcCondOverride.ZONE_CONDITIONS) && activeChar.isInsideZone(ZoneId.SIEGE) && (!activeChar.isInSiege() || (activeChar.getSiegeState() < 2))) {
            activeChar.teleToLocation(TeleportWhereType.TOWN);
        }

        // Remove demonic weapon if character is not cursed weapon equipped.
        if ((activeChar.getInventory().getItemByItemId(8190) != null) && !activeChar.isCursedWeaponEquipped()) {
            activeChar.destroyItem("Zariche", activeChar.getInventory().getItemByItemId(8190), null, true);
        }
        if ((activeChar.getInventory().getItemByItemId(8689) != null) && !activeChar.isCursedWeaponEquipped()) {
            activeChar.destroyItem("Akamanah", activeChar.getInventory().getItemByItemId(8689), null, true);
        }

        if (Config.ALLOW_MAIL) {
            if (MailManager.getInstance().hasUnreadPost(activeChar)) {
                client.sendPacket(ExNoticePostArrived.valueOf(false));
            }
        }

        if (Config.WELCOME_MESSAGE_ENABLED) {
            activeChar.sendPacket(new ExShowScreenMessage(Config.WELCOME_MESSAGE_TEXT, Config.WELCOME_MESSAGE_TIME));
        }

        final int birthday = activeChar.checkBirthDay();
        if (birthday == 0) {
            activeChar.sendPacket(SystemMessageId.HAPPY_BIRTHDAY_ALEGRIA_HAS_SENT_YOU_A_BIRTHDAY_GIFT);
            // activeChar.sendPacket(new ExBirthdayPopup()); Removed in H5?
        } else if (birthday != -1) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_S1_DAYS_REMAINING_UNTIL_YOUR_BIRTHDAY_ON_YOUR_BIRTHDAY_YOU_WILL_RECEIVE_A_GIFT_THAT_ALEGRIA_HAS_CAREFULLY_PREPARED);
            sm.addString(Integer.toString(birthday));
            activeChar.sendPacket(sm);
        }

        if (!activeChar.getPremiumItemList().isEmpty()) {
            activeChar.sendPacket(ExNotifyPremiumItem.STATIC_PACKET);
        }

        if ((Config.OFFLINE_TRADE_ENABLE || Config.OFFLINE_CRAFT_ENABLE) && Config.STORE_OFFLINE_TRADE_IN_REALTIME) {
            OfflineTradersTable.onTransaction(activeChar, true, false);
        }

        if (BeautyShopData.getInstance().hasBeautyData(activeChar.getRace(), activeChar.getAppearance().getSexType())) {
            activeChar.sendPacket(new ExBeautyItemList(activeChar));
        }

        if(activeChar.getActiveElementalSpiritType() >= 0) {
            client.sendPacket(new ElementalSpiritInfo(activeChar.getActiveElementalSpiritType(), (byte) 2));
        }

        activeChar.broadcastUserInfo();
        // Send Equipped Items
        activeChar.sendPacket(new ExUserInfoEquipSlot(activeChar));

        if (Config.ENABLE_WORLD_CHAT) {
            activeChar.sendPacket(new ExWorldChatCnt(activeChar));
        }
        activeChar.sendPacket(new ExConnectedTimeAndGettableReward(activeChar));

        // Handle soulshots, disable all on EnterWorld
        activeChar.sendPacket(new ExAutoSoulShot(0, true, 0));
        activeChar.sendPacket(new ExAutoSoulShot(0, true, 1));
        activeChar.sendPacket(new ExAutoSoulShot(0, true, 2));
        activeChar.sendPacket(new ExAutoSoulShot(0, true, 3));


        // Fix for equipped item skills
        if (!activeChar.getEffectList().getCurrentAbnormalVisualEffects().isEmpty()) {
            activeChar.updateAbnormalVisualEffects();
        }

        if (Config.ENABLE_ATTENDANCE_REWARDS) {
            sendAttendanceInfo(activeChar);
        }

        if (Config.HARDWARE_INFO_ENABLED) {
            ThreadPoolManager.schedule(() ->
            {
                if (client.getHardwareInfo() == null) {
                    Disconnection.of(client).defaultSequence(false);
                }
            }, 5000);
        }

        activeChar.onPlayerEnter();
        Quest.playerEnter(activeChar);
    }

    private void sendAttendanceInfo(Player activeChar) {
        ThreadPoolManager.schedule(() -> {
            // Check if player can receive reward today.
            final AttendanceInfoHolder attendanceInfo = activeChar.getAttendanceInfo();
            if (attendanceInfo.isRewardAvailable()) {
                final int lastRewardIndex = attendanceInfo.getRewardIndex() + 1;
                activeChar.sendPacket(new ExShowScreenMessage("Your attendance day " + lastRewardIndex + " reward is ready.", ExShowScreenMessage.TOP_CENTER, 7000, 0, true, true));
                activeChar.sendMessage("Your attendance day " + lastRewardIndex + " reward is ready.");
                activeChar.sendMessage("Click on General Menu -> Attendance Check.");
                if (Config.ATTENDANCE_POPUP_WINDOW) {
                    activeChar.sendPacket(new ExVipAttendanceItemList(activeChar));
                }
            }
        }, Config.ATTENDANCE_REWARD_DELAY * 60  * 1000);
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

            final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_HAS_LOGGED_INTO_GAME);
            msg.addString(activeChar.getName());
            clan.broadcastToOtherOnlineMembers(msg, activeChar);
            clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListUpdate(activeChar), activeChar);
        }
    }

    private void notifySponsorOrApprentice(Player activeChar) {
        if (activeChar.getSponsor() != 0) {
            final Player sponsor = World.getInstance().findPlayer(activeChar.getSponsor());
            if (sponsor != null) {
                final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOUR_APPRENTICE_S1_HAS_LOGGED_IN);
                msg.addString(activeChar.getName());
                sponsor.sendPacket(msg);
            }
        } else if (activeChar.getApprentice() != 0) {
            final Player apprentice = World.getInstance().findPlayer(activeChar.getApprentice());
            if (apprentice != null) {
                final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOUR_SPONSOR_C1_HAS_LOGGED_IN);
                msg.addString(activeChar.getName());
                apprentice.sendPacket(msg);
            }
        }
    }
}
