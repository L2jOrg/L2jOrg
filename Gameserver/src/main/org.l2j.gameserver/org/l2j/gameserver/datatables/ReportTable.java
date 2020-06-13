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
package org.l2j.gameserver.datatables;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.dao.BotReportDAO;
import org.l2j.gameserver.data.database.data.BotReportData;
import org.l2j.gameserver.engine.captcha.CaptchaEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.instancemanager.PunishmentManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.impl.CaptchaRequest;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.captcha.ReceiveBotCaptchaImage;
import org.l2j.gameserver.network.serverpackets.captcha.ReceiveBotCaptchaResult;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.zone.ZoneType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author BiggBoss
 */
public final class ReportTable {
    public static final int ATTACK_ACTION_BLOCK_ID = -1;
    public static final int TRADE_ACTION_BLOCK_ID = -2;
    public static final int PARTY_ACTION_BLOCK_ID = -3;
    public static final int ACTION_BLOCK_ID = -4;
    public static final int CHAT_BLOCK_ID = -5;
    // Zoey76: TODO: Split XML parsing from SQL operations, use GameXmlReader instead of SAXParser.
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportTable.class);

    private static final String REPORT_TYPE_ADENA_ADS = "ADENA_ADS";

    private IntMap<BotReportedCharData> reports;
    private IntMap<ReporterCharData> reporters;

    private Map<Integer, Long> ipRegistry;
    private Map<Integer, PunishHolder> _punishments;

    private ReportTable() {
        if (Config.BOTREPORT_ENABLE) {
            reports = new CHashIntMap<>();
            ipRegistry = new HashMap<>();
            reporters = new CHashIntMap<>();
            _punishments = new ConcurrentHashMap<>();

            try {
                final File punishments = new File("./config/BotReportPunishments.xml");
                if (!punishments.exists()) {
                    throw new FileNotFoundException(punishments.getName());
                }

                final SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                parser.parse(punishments, new PunishmentsLoader());
            } catch (Exception e) {
                LOGGER.warn("Could not load punishments from /config/BotReportPunishments.xml", e);
            }

            loadReportedCharData();
            scheduleResetPointTask();
        }
    }

    /**
     * Checks and return if the abstract barrier specified by an integer (map key) has accomplished the waiting time
     *
     * @param map      (a Map to study (Int = barrier, Long = fully qualified unix time)
     * @param objectId (an existent map key)
     * @return true if the time has passed.
     */
    private static boolean timeHasPassed(Map<Integer, Long> map, int objectId) {
        if (map.containsKey(objectId)) {
            return (System.currentTimeMillis() - map.get(objectId)) > Config.BOTREPORT_REPORT_DELAY;
        }
        return true;
    }

    /**
     * Loads all reports of each reported bot into this cache class.<br>
     * Warning: Heavy method, used only on server start up
     */
    private void loadReportedCharData() {
        var botReportDao = getDAO(BotReportDAO.class);
        var yesterday = LocalDateTime.now().minusDays(1);

        botReportDao.removeExpiredReports(yesterday);
        var reportsData = botReportDao.findAll();

        for (BotReportData report : reportsData) {
            reports.computeIfAbsent(report.getBotId(), bot -> new BotReportedCharData()).addReporter(report);
            reporters.computeIfAbsent(report.getReporterId(), reporter -> new ReporterCharData()).decreaseReportPoints();

        }
        LOGGER.info("Loaded {} bot reports", reports.size());
    }

    /**
     * Save all reports for each reported bot down to database.<br>
     * Warning: Heavy method, used only at server shutdown
     */
    public void saveReportedCharData() {
        var botReportDao = getDAO(BotReportDAO.class);
        reports.values().forEach(r -> {
            r.adsReporters.values().forEach(botReportDao::save);
            r.reporters.values().forEach(botReportDao::save);
        });
    }

    /**
     * Attempts to perform a bot report. R/W to ip and char id registry is synchronized. Triggers bot punish management<br>
     *
     * @param reporter (Player who issued the report)
     * @return True, if the report was registered, False otherwise
     */
    public boolean reportBot(Player reporter) {
        final WorldObject target = reporter.getTarget();
        if (!isPlayer(target) || target.getObjectId() == reporter.getObjectId()) {
            return false;
        }

        final Player bot = ((Player) target);

        if (bot.isInsideZone(ZoneType.PEACE) || bot.isInsideZone(ZoneType.PVP)) {
            reporter.sendPacket(SystemMessageId.YOU_CANNOT_REPORT_A_CHARACTER_WHO_IS_IN_A_PEACE_ZONE_OR_A_BATTLEGROUND);
            return false;
        }

        if (bot.isInOlympiadMode()) {
            reporter.sendPacket(SystemMessageId.THIS_CHARACTER_CANNOT_MAKE_A_REPORT_YOU_CANNOT_MAKE_A_REPORT_WHILE_LOCATED_INSIDE_A_PEACE_ZONE_OR_A_BATTLEGROUND_WHILE_YOU_ARE_AN_OPPOSING_CLAN_MEMBER_DURING_A_CLAN_WAR_OR_WHILE_PARTICIPATING_IN_THE_OLYMPIAD);
            return false;
        }

        if ((nonNull(bot.getClan())) && bot.getClan().isAtWarWith(reporter.getClan())) {
            reporter.sendPacket(SystemMessageId.YOU_CANNOT_REPORT_WHEN_A_CLAN_WAR_HAS_BEEN_DECLARED);
            return false;
        }

        if (bot.getExp() == bot.getStats().getStartingExp()) {
            reporter.sendPacket(SystemMessageId.YOU_CANNOT_REPORT_A_CHARACTER_WHO_HAS_NOT_ACQUIRED_ANY_XP_AFTER_CONNECTING);
            return false;
        }

        BotReportedCharData rcd = reports.get(bot.getObjectId());
        ReporterCharData rcdRep = reporters.get(reporter.getObjectId());
        final int reporterId = reporter.getObjectId();


        final int ip = GameUtils.hashIp(reporter);
        if (!timeHasPassed(ipRegistry, ip)) {
            reporter.sendPacket(SystemMessageId.THIS_CHARACTER_CANNOT_MAKE_A_REPORT_THE_TARGET_HAS_ALREADY_BEEN_REPORTED_BY_EITHER_YOUR_CLAN_OR_HAS_ALREADY_BEEN_REPORTED_FROM_YOUR_CURRENT_IP);
            return false;
        }

        if (nonNull(rcd)) {
            if (rcd.alredyReportedBy(reporterId)) {
                reporter.sendPacket(SystemMessageId.YOU_CANNOT_REPORT_THIS_PERSON_AGAIN_AT_THIS_TIME);
                return false;
            }

            if (!Config.BOTREPORT_ALLOW_REPORTS_FROM_SAME_CLAN_MEMBERS && rcd.reportedBySameClan(reporter.getClan())) {
                reporter.sendPacket(SystemMessageId.THIS_CHARACTER_CANNOT_MAKE_A_REPORT_THE_TARGET_HAS_ALREADY_BEEN_REPORTED_BY_EITHER_YOUR_CLAN_OR_HAS_ALREADY_BEEN_REPORTED_FROM_YOUR_CURRENT_IP);
                return false;
            }
        }

        if (nonNull(rcdRep)) {
            if (rcdRep.getPointsLeft() == 0) {
                reporter.sendPacket(SystemMessageId.YOU_VE_SPENT_ALL_POINTS_THE_POINTS_WILL_BE_RESET_AT_06_30_SO_THAT_YOU_CAN_USE_THEM_AGAIN);
                return false;
            }

            final long reuse = (System.currentTimeMillis() - rcdRep.getLastReporTime());
            if (reuse < Config.BOTREPORT_REPORT_DELAY) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_CAN_MAKE_ANOTHER_REPORT_IN_S1_MINUTE_S_YOU_HAVE_S2_POINT_S_REMAINING_ON_THIS_ACCOUNT);
                sm.addInt((int) (reuse / 60000));
                sm.addInt(rcdRep.getPointsLeft());
                reporter.sendPacket(sm);
                return false;
            }
        }

        final long curTime = System.currentTimeMillis();
        synchronized (this) {
            if (isNull(rcd)) {
                rcd = new BotReportedCharData();
                reports.put(bot.getObjectId(), rcd);
            }
            rcd.addReporter(bot.getObjectId(), reporterId, "BOT");

            if (isNull(rcdRep)) {
                rcdRep = new ReporterCharData();
            }
            rcdRep.registerReport(curTime);

            ipRegistry.put(ip, curTime);
            reporters.put(reporterId, rcdRep);
        }

        SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_WAS_REPORTED_AS_A_BOT);
        sm.addString(bot.getName());
        reporter.sendPacket(sm);

        sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_USED_A_REPORT_POINT_ON_C1_YOU_HAVE_S2_POINTS_REMAINING_ON_THIS_ACCOUNT);
        sm.addString(bot.getName());
        sm.addInt(rcdRep.getPointsLeft());
        reporter.sendPacket(sm);

        handleReport(bot, rcd);
        return true;
    }

    /**
     * Find the punishs to apply to the given bot and triggers the punish method.
     *
     * @param bot (Player to be punished)
     * @param rcd (RepotedCharData linked to this bot)
     */
    private void handleReport(Player bot, BotReportedCharData rcd) {
        // Report count punishment
        punishBot(bot, _punishments.get(rcd.getReportCount()));

        // Range punishments
        for (int key : _punishments.keySet()) {
            if ((key < 0) && (Math.abs(key) <= rcd.getReportCount())) {
                punishBot(bot, _punishments.get(key));
            }
        }

        // each 3 report request captcha
        if((rcd.getReportCount() % 3) == 0) {
            var captcha = CaptchaEngine.getInstance().next();
            if(!bot.hasRequest(CaptchaRequest.class)) {
                var request = new CaptchaRequest(bot, captcha);
                bot.addRequest(request);
                bot.sendPacket(new ReceiveBotCaptchaImage(captcha, request.getRemainingTime()));
                bot.sendPacket(SystemMessageId.PLEASE_ENTER_THE_AUTHENTICATION_CODE_IN_TIME_TO_CONTINUE_PLAYING);
            }
        }
    }

    /**
     * Applies the given punish to the bot if the action is secure
     *
     * @param bot (Player to punish)
     * @param ph  (PunishHolder containing the debuff and a possible system message to send)
     */
    private void punishBot(Player bot, PunishHolder ph) {
        if (nonNull(ph)) {
            ph._punish.applyEffects(bot, bot);
            if (ph._systemMessageId > -1) {
                final SystemMessageId id = SystemMessageId.getSystemMessageId(ph._systemMessageId);
                if (nonNull(id)) {
                    bot.sendPacket(id);
                }
            }
        }
    }

    /**
     * Adds a debuff punishment into the punishments record. If skill does not exist, will log it and return
     *
     * @param neededReports (report count to trigger this debuff)
     * @param skillId
     * @param skillLevel
     * @param sysMsg        (id of a system message to send when applying the punish)
     */
    private void addPunishment(int neededReports, int skillId, int skillLevel, int sysMsg) {
        final Skill sk = SkillEngine.getInstance().getSkill(skillId, skillLevel);
        if (nonNull(sk)) {
            _punishments.put(neededReports, new PunishHolder(sk, sysMsg));
        } else {
            LOGGER.warn("Could not add punishment for {} report(s): Skill {} - {} does not exist!", neededReports, skillId , skillLevel );
        }
    }

    private void resetPointsAndSchedule() {
        reporters.values().forEach(r -> r.setPoints(7));
        scheduleResetPointTask();
    }

    private void scheduleResetPointTask() {
        try {
            final String[] hour = Config.BOTREPORT_RESETPOINT_HOUR;
            final Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour[0]));
            c.set(Calendar.MINUTE, Integer.parseInt(hour[1]));

            if (System.currentTimeMillis() > c.getTimeInMillis()) {
                c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + 1);
            }

            ThreadPool.schedule(new ResetPointTask(), c.getTimeInMillis() - System.currentTimeMillis());
        } catch (Exception e) {
            ThreadPool.schedule(new ResetPointTask(), 24 * 3600 * 1000);
            LOGGER.warn(getClass().getSimpleName() + ": Could not properly schedule bot report points reset task. Scheduled in 24 hours.", e);
        }
    }

    public void punishBotDueUnsolvedCaptcha(Player bot) {
        CommonSkill.BOT_REPORT_STATUS.getSkill().applyEffects(bot, bot);
        bot.removeRequest(CaptchaRequest.class);
        var msg = SystemMessage.getSystemMessage(SystemMessageId.IF_A_USER_ENTERS_A_WRONG_AUTHENTICATION_CODE_3_TIMES_IN_A_ROW_OR_DOES_NOT_ENTER_THE_CODE_IN_TIME_THE_SYSTEM_WILL_QUALIFY_HIM_AS_A_RULE_BREAKER_AND_CHARGE_HIS_ACCOUNT_WITH_A_PENALTY_S1);
        msg.addSkillName(CommonSkill.BOT_REPORT_STATUS.getId());
        bot.sendPacket(msg);
        bot.sendPacket(ReceiveBotCaptchaResult.FAILED);
    }

    public void reportAdenaADS(int reporterId, int reportedId) {
        var reportedData = reports.get(reportedId);
        if(isNull(reportedData)) {
            reportedData = new BotReportedCharData();
        } else {
            if(reportedData.alreadyReportedAdenaADS(reporterId)) {
                return;
            }
        }

        reportedData.addReporter(reporterId, reportedId, REPORT_TYPE_ADENA_ADS);
        var reportedCount = reportedData.getADSReportedCount();

        if(reportedCount >= getSettings(GeneralSettings.class).banChatAdenaAdsReportCount()) {
            var manager = PunishmentManager.getInstance();
            if(manager.hasPunishment(reportedId, PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN)) {
                manager.stopPunishment(reportedId, PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN);
            }
            manager.startPunishment(new PunishmentTask(0, reportedId, PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN, Instant.now().plus(14, ChronoUnit.HOURS).toEpochMilli(), "Chat banned bot report", "system", false));
        }

    }

    public static ReportTable getInstance() {
        return Singleton.INSTANCE;
    }

    private static final class Singleton {
        private static final ReportTable INSTANCE = new ReportTable();
    }

    /**
     * Represents the info about a reporter
     */
    private final class ReporterCharData {

        private long _lastReport;
        private byte _reportPoints;

        ReporterCharData() {
            _reportPoints = 7;
            _lastReport = 0;
        }

        void registerReport(long time) {
            _reportPoints -= 1;
            _lastReport = time;
        }

        long getLastReporTime() {
            return _lastReport;
        }

        byte getPointsLeft() {
            return _reportPoints;
        }

        void setPoints(int points) {
            _reportPoints = (byte) points;
        }

        void decreaseReportPoints() {
            _reportPoints--;
        }
    }


    /**
     * Represents the info about a reported character
     */
    private final class BotReportedCharData {

        IntMap<BotReportData> reporters;
        IntMap<BotReportData> adsReporters;

        BotReportedCharData() {
            reporters = new HashIntMap<>();
            adsReporters = new HashIntMap<>();
        }

        int getReportCount() {
            return reporters.size();
        }

        boolean alredyReportedBy(int objectId) {
            return reporters.containsKey(objectId);
        }

        boolean reportedBySameClan(Clan clan) {
            if (isNull(clan)) {
                return false;
            }

            var it = reporters.keySet().iterator();
            while (it.hasNext()) {
                if(clan.isMember(it.nextInt())) {
                    return true;
                }
            }
            return false;
        }

        void addReporter(BotReportData report) {
            if (REPORT_TYPE_ADENA_ADS.equalsIgnoreCase(report.getType())) {
                adsReporters.put(report.getReporterId(), report);
            } else {
                reporters.put(report.getReporterId(), report);
            }
        }

        void addReporter(int botId, int reporterId, String type) {
            addReporter(new BotReportData(botId, reporterId, type));
        }

        boolean alreadyReportedAdenaADS(int reporterId) {
            return adsReporters.containsKey(reporterId);
        }

        int getADSReportedCount() {
            return adsReporters.size();
        }
    }

    private final class PunishmentsLoader extends DefaultHandler {

        PunishmentsLoader() {
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attr) {
            if (qName.equals("punishment")) {
                int reportCount = -1;
                int skillId = -1;
                int skillLevel = 1;
                int sysMessage = -1;
                try {
                    reportCount = Integer.parseInt(attr.getValue("neededReportCount"));
                    skillId = Integer.parseInt(attr.getValue("skillId"));
                    final String level = attr.getValue("skillLevel");
                    final String systemMessageId = attr.getValue("sysMessageId");
                    if (level != null) {
                        skillLevel = Integer.parseInt(level);
                    }

                    if (systemMessageId != null) {
                        sysMessage = Integer.parseInt(systemMessageId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                addPunishment(reportCount, skillId, skillLevel, sysMessage);
            }
        }
    }

    private class PunishHolder {

        final Skill _punish;
        final int _systemMessageId;

        PunishHolder(Skill sk, int sysMsg) {
            _punish = sk;
            _systemMessageId = sysMsg;
        }
    }

    private class ResetPointTask implements Runnable {
        @Override
        public void run() {
            resetPointsAndSchedule();
        }
    }


}
