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
package org.l2j.scripts.quests.tutorial;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.enums.HtmlActionScope;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerBypass;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPressTutorialMark;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerTutorialEvent;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.network.serverpackets.html.TutorialShowHtml;
import org.l2j.gameserver.network.serverpackets.html.TutorialWindowType;
import org.l2j.gameserver.settings.CharacterSettings;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.network.serverpackets.ShowTutorialMark.Mark;

/**
 * @author JoeAlisson
 */
public abstract class Tutorial extends Quest {

    private static final int BLUE_GEM = 6353;
    private static final int QM_CHAT = 1;
    private static final int QM_QUEST_PROGRESS = 9;
    private static final int QM_MEET_NEWBIE_HELPER = 21;
    private static final String RADAR_HTM = "..\\L2text_Classic\\QT_001_Radar_01.htm";

    private static final ItemHolder SOULSHOT_REWARD = new ItemHolder(91927, 200);
    private static final ItemHolder SPIRITSHOT_REWARD = new ItemHolder(91928, 100);
    private static final ItemHolder ESCAPE_REWARD = new ItemHolder(10650, 5);
    private static final ItemHolder WIND_WALK_POTION = new ItemHolder(49036, 5);

    private static final int[] GREMLINS = {
            18342, // this is used for now
            20001
    };

    private final String TUTORIAL_BYPASS = String.format("Quest %s ",  getClass().getSimpleName());

    public Tutorial(int questId, ClassId... classIds) {
        super(questId);
        addCondClassIds(classIds);
        addFirstTalkId(newbieHelperId());
        addKillId(GREMLINS);
        registerQuestItems(BLUE_GEM);
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        final var state = getQuestState(player, false);

        if (isNull(state)) {
            return null;
        }

        String htmltext = null;
        switch (event) {
            case "start_newbie_tutorial" -> startTutorial(player, state);
            case "tutorial_move.html" -> showMoveTutorial(event, player);
            case "tutorial_exit.html" -> exitTutorial(event, player);
            case "8", "question_mark_1" -> {
                if (state.getMemoState() < 3) {
                    player.sendPackets(ShowTutorialMark.question(Mark.CHAT), TutorialCloseHtml.STATIC_PACKET);
                }
                player.clearHtmlActions(HtmlActionScope.TUTORIAL_HTML);
            }
            case "close_tutorial" -> {
                player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
                player.clearHtmlActions(HtmlActionScope.TUTORIAL_HTML);
            }
            case "reward_2" -> htmltext = receiveTrainerReward(player, state);
            case "1" -> onPlayerMove(player, state);
            case "2" -> onChangePointOfView(player, state);
            case "go_to_newbie_helper" -> {
                player.teleToLocation(villageLocation());
                state.setState(State.COMPLETED);
            }
        }
        return htmltext;
    }

    private String receiveTrainerReward(Player player, QuestState state) {
        if (state.isMemoState(4)) {
            state.setMemoState(5);
            if (player.isMageClass() && (player.getRace() != Race.ORC)) {
                giveItems(player, SPIRITSHOT_REWARD);
                playTutorialVoice(player, "tutorial_voice_027");
            } else {
                giveItems(player, SOULSHOT_REWARD);
                playTutorialVoice(player, "tutorial_voice_026");
            }
            player.sendPacket(ShowTutorialMark.info(Mark.MEET_NEWBIE_HELPER));
            return "go_village.html";
        }
        return null;
    }

    private void onChangePointOfView(Player player, QuestState state) {
        if (state.getMemoState() < 2) {
            player.sendPacket(new TutorialEnableClientEvent(8));
            playTutorialVoice(player, "tutorial_voice_005");
            showTutorialHtml(player, "tutorial_init_point_view.html");
        }
    }

    private void onPlayerMove(Player player, QuestState state) {
        if (state.getMemoState() < 2) {
            player.sendPacket(new TutorialEnableClientEvent(2));
            playTutorialVoice(player, "tutorial_voice_004");
            showTutorialHtml(player, "tutorial_point_view.html");
        }
    }

    private void exitTutorial(String event, Player player) {
        player.sendPacket(new TutorialEnableClientEvent(0));
        showTutorialHtml(player, event);
    }

    private void showMoveTutorial(String event, Player player) {
        player.sendPacket(new TutorialEnableClientEvent(1));
        playTutorialVoice(player, "tutorial_voice_003");
        showTutorialHtml(player, event);
    }

    private void startTutorial(Player player, QuestState state) {
        if (state.getMemoState() == 0) {
            state.startQuest();
            showOnScreenMsg(player, NpcStringId.SPEAK_WITH_THE_NEWBIE_HELPER, ExShowScreenMessage.TOP_CENTER, 5000);
            var startingEvent = startingVoiceHtml();
            playTutorialVoice(player, startingEvent.getSound());
            showTutorialHtml(player, startingEvent.getHtml());
        }
    }

    @Override
    public String onFirstTalk(Npc npc, Player player) {
        final var questState = getQuestState(player, false);
        if(isNull(questState)) {
            return npc.getId() + "1.html";
        }

        if(npc.getId() == newbieHelperId()) {
            String htmlText = onNewbieHelperChat(npc, player, questState);
            if (htmlText != null)
                return htmlText;
        } else {
            String htmlText = onTrainerTalk(npc, questState);
            if (htmlText != null)
                return htmlText;
        }
        return npc.getId() + "-1.html";
    }

    private String onTrainerTalk(Npc npc, QuestState questState) {
        if(questState.isCompleted()) {
            return npc.getId() + "-4.html";
        }
        switch (questState.getMemoState()) {
            case 0, 1, 2, 3 -> {
                return npc.getId() + "-1.html";
            }
            case 4 -> {
                return npc.getId() + "-2.html";
            }
            case 5, 6 -> {
                return npc.getId() + "-4.html";
            }
        }
        return null;
    }

    private String onNewbieHelperChat(Npc npc, Player player, QuestState questState) {
        if(questState.isCompleted()) {
            return npc.getId() + "-5.html";
        }

        if (questState.getMemoState() < 3 && questState.getCond() == 3) {
            questState.setMemoState(3);
        }

        return switch (questState.getMemoState()) {
            case 0 -> showKillGremlins(player, questState);
            case 1 -> showForgetMission(player);
            case 3 -> giveNewbieHelperReward(npc, player, questState);
            case 4 -> npc.getId() + "-4.html";
            case 5, 6 -> npc.getId() + "-5.html";
            default -> null;
        };
    }

    private String giveNewbieHelperReward(Npc npc, Player player, QuestState questState) {
        player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
        player.clearHtmlActions(HtmlActionScope.TUTORIAL_HTML);
        questState.setMemoState(4);
        questState.setCond(3);
        takeItems(player, BLUE_GEM, -1);
        giveItems(player, ESCAPE_REWARD);
        giveItems(player, WIND_WALK_POTION);

        if (player.isMageClass() && (player.getRace() != Race.ORC)) {
            giveItems(player, SPIRITSHOT_REWARD);
            playTutorialVoice(player, "tutorial_voice_027");
            return npc.getId() + "-3.html";
        }
        giveItems(player, SOULSHOT_REWARD);
        playTutorialVoice(player, "tutorial_voice_026");
        return npc.getId() + "-2.html";
    }

    private String showForgetMission(Player player) {
        if (!player.isMageClass()) {
            return "../fighter_back.html";
        } else if (Race.ORC == player.getRace()) {
            return "../mystic_orc_back.html";
        }
        return "../mystic_back.html";
    }

    private String showKillGremlins(Player player, QuestState questState) {
        questState.setMemoState(1);
        if (!player.isMageClass()) {
            return "../kill_gremlins_fighter.html";
        } else if (Race.ORC == player.getRace()) {
            return "../kill_gremlins_orc_mystic.html";
        }
        return "../kill_gremlins_mystic.html";
    }

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon) {
        final var questState = getQuestState(killer, false);
        if (canReceiveBlueGemstone(killer, questState) && Rnd.chance(50)) {
            dropBlueGemstone(killer, questState);
        }
        return super.onKill(npc, killer, isSummon);
    }

    private void dropBlueGemstone(Player killer, QuestState questState) {
        killer.addItem("Quest", BLUE_GEM, 1, killer, true);
        questState.setMemoState(3);
        questState.setCond(2);
        playSound(killer, "ItemSound.quest_tutorial");
        killer.sendPackets(ShowTutorialMark.info(Mark.QUEST_PROGRESS), new TutorialShowHtml(RADAR_HTM, TutorialWindowType.COMPOSITE));
        playTutorialVoice(killer, "tutorial_voice_013");
    }

    private boolean canReceiveBlueGemstone(Player killer, QuestState questState) {
        return nonNull(questState) && questState.getCond() < 2 && !hasQuestItems(killer, BLUE_GEM);
    }

    protected void playTutorialVoice(Player player, String voice) {
        player.sendPacket(PlaySound.voice(voice));
    }

    protected void showTutorialHtml(Player player, String fileName) {
        if(fileName.startsWith("tutorial_")) {
            fileName = fileName.replace("tutorial_", "../");
        }
        var htm = getHtml(player, fileName).replace("%TutorialQuest%", getName());
        player.sendPacket(new TutorialShowHtml(htm));
    }


    @RegisterEvent(EventType.ON_PLAYER_LOGIN)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void OnPlayerLogin(OnPlayerLogin event) {
        if (CharacterSettings.disableTutorial()) {
            return;
        }

        var player = event.getPlayer();
        if (player.getLevel() > 6) {
            return;
        }

        QuestState qs = getQuestState(player, true);
        if (nonNull(qs) && qs.getMemoState() < 4) {
            startQuestTimer("start_newbie_tutorial", 2000, null, player);
        }
    }

    @RegisterEvent(EventType.ON_PLAYER_BYPASS)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void OnPlayerBypass(OnPlayerBypass event) {
        var command = event.getCommand();
        if (command.startsWith(TUTORIAL_BYPASS)) {
            notifyEvent(command.replace(TUTORIAL_BYPASS, ""), null, event.getPlayer());
        }
    }

    @RegisterEvent(EventType.ON_PLAYER_TUTORIAL_EVENT)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void OnRequestTutorialEvent(OnPlayerTutorialEvent event) {
        final var player = event.getPlayer();
        final var qs = getQuestState(player, false);
        if (isNull(qs)) {
            return;
        }
        notifyEvent(String.valueOf(event.getEventId()), null, event.getPlayer());
    }

    @RegisterEvent(EventType.ON_PLAYER_PRESS_TUTORIAL_MARK)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void onPlayerPressTutorialMark(OnPlayerPressTutorialMark event) {
        final var player = event.getPlayer();
        final var qs = getQuestState(player, false);
        if (isNull(qs)) {
            return;
        }

        switch (event.getMarkId()) {
            case QM_CHAT -> showNewbieHelperChat(player, qs);
            case QM_QUEST_PROGRESS -> showGemstoneFound(event, qs);
            case QM_MEET_NEWBIE_HELPER -> onMeetNewbieHelper(event, qs);
        }
    }

    private void onMeetNewbieHelper(OnPlayerPressTutorialMark event, QuestState qs) {
        if (qs.getCond() == 3) {
            addRadar(event.getPlayer(), villageLocation());
            playSound(event.getPlayer(), "ItemSound.quest_tutorial");
            qs.setState(State.COMPLETED);
        }
    }

    private void showGemstoneFound(OnPlayerPressTutorialMark event, QuestState qs) {
        if (qs.getCond() == 2) {
            addRadar(event.getPlayer(), helperLocation());
            showTutorialHtml(event.getPlayer(), "tutorial_gemstone_found.html");
        }
    }

    private void showNewbieHelperChat(Player player, QuestState qs) {
        if (qs.getCond() == 1) {
            showOnScreenMsg(player, NpcStringId.SPEAK_WITH_THE_NEWBIE_HELPER, ExShowScreenMessage.TOP_CENTER, 5000);
            addRadar(player, helperLocation());
            showTutorialHtml(player, "tutorial_newbie_helper.html");
            playTutorialVoice(player, "tutorial_voice_007");
        }
    }

    protected abstract int newbieHelperId();

    protected abstract ILocational villageLocation();

    protected abstract QuestSoundHtmlHolder startingVoiceHtml();

    protected abstract Location helperLocation();

    protected static class QuestSoundHtmlHolder {
        private final String _sound;
        private final String _html;

        public QuestSoundHtmlHolder(String sound, String html) {
            _sound = sound;
            _html = html;
        }

        String getSound() {
            return _sound;
        }

        String getHtml() {
            return _html;
        }
    }
}
