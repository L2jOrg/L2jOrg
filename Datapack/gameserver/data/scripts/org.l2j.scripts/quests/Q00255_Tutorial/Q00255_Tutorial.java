package quests.Q00255_Tutorial;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.HtmlActionScope;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.Id;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerBypass;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerItemPickup;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPressTutorialMark;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.*;

import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Tutorial Quest
 * @author Mobius
 */
public class Q00255_Tutorial extends Quest {
    // NPCs
    private static final int[] NEWBIE_HELPERS = {
            30009, // human fighter
            30019, // human mystic
            30131, // dark elf
            30400, // elf
            30530, // dwarf
            30575, // orc
    };

    private static final int[] SUPERVISORS = {
            30008, // human fighter
            30017, // human mystic
            30129, // dark elf
            30370, // elf
            30528, // dwarf
            30573, // orc
    };
    // Monsters
    private static final int[] GREMLINS = {
            18342, // this is used for now
            20001
    };
    // Items
    private static final int BLUE_GEM = 6353;
    private static final ItemHolder SOULSHOT_REWARD = new ItemHolder(5789, 200);
    private static final ItemHolder SPIRITSHOT_REWARD = new ItemHolder(5790, 100);
    // Others
    private static final IntObjectMap<QuestSoundHtmlHolder> STARTING_VOICE_HTML = new HashIntObjectMap<>();
    {
        STARTING_VOICE_HTML.put(0,  new QuestSoundHtmlHolder("tutorial_voice_001a", "tutorial_human_fighter001.html"));
        STARTING_VOICE_HTML.put(10, new QuestSoundHtmlHolder("tutorial_voice_001b", "tutorial_human_mage001.html"));
        STARTING_VOICE_HTML.put(18, new QuestSoundHtmlHolder("tutorial_voice_001c", "tutorial_elven_fighter001.html"));
        STARTING_VOICE_HTML.put(25, new QuestSoundHtmlHolder("tutorial_voice_001d", "tutorial_elven_mage001.html"));
        STARTING_VOICE_HTML.put(31, new QuestSoundHtmlHolder("tutorial_voice_001e", "tutorial_delf_fighter001.html"));
        STARTING_VOICE_HTML.put(38, new QuestSoundHtmlHolder("tutorial_voice_001f", "tutorial_delf_mage001.html"));
        STARTING_VOICE_HTML.put(44, new QuestSoundHtmlHolder("tutorial_voice_001g", "tutorial_orc_fighter001.html"));
        STARTING_VOICE_HTML.put(49, new QuestSoundHtmlHolder("tutorial_voice_001h", "tutorial_orc_mage001.html"));
        STARTING_VOICE_HTML.put(53, new QuestSoundHtmlHolder("tutorial_voice_001i", "tutorial_dwarven_fighter001.html"));
    }

    private static final IntObjectMap<Location> HELPER_LOCATION = new HashIntObjectMap<>();
    {
        HELPER_LOCATION.put(0, new Location(-71424, 258336, -3109));
        HELPER_LOCATION.put(10, new Location(-91036, 248044, -3568));
        HELPER_LOCATION.put(18, new Location(46112, 41200, -3504));
        HELPER_LOCATION.put(25, new Location(46112, 41200, -3504));
        HELPER_LOCATION.put(31, new Location(28384, 11056, -4233));
        HELPER_LOCATION.put(38, new Location(28384, 11056, -4233));
        HELPER_LOCATION.put(44, new Location(-56736, -113680, -672));
        HELPER_LOCATION.put(49, new Location(-56736, -113680, -672));
        HELPER_LOCATION.put(53, new Location(108567, -173994, -406));
    }

    private static final IntObjectMap<Location> COMPLETE_LOCATION = new HashIntObjectMap<>();
    {
        COMPLETE_LOCATION.put(0, new Location(-84081, 243227, -3723));
        COMPLETE_LOCATION.put(10, new Location(-84081, 243227, -3723));
        COMPLETE_LOCATION.put(18, new Location(45475, 48359, -3060));
        COMPLETE_LOCATION.put(25, new Location(45475, 48359, -3060));
        COMPLETE_LOCATION.put(31, new Location(12111, 16686, -4582));
        COMPLETE_LOCATION.put(38, new Location(12111, 16686, -4582));
        COMPLETE_LOCATION.put(44, new Location(-45032, -113598, -192));
        COMPLETE_LOCATION.put(49, new Location(-45032, -113598, -192));
        COMPLETE_LOCATION.put(53, new Location(115632, -177996, -905));
    }

    private static final String TUTORIAL_BYPASS = "Quest Q00255_Tutorial ";
    private static final int QUESTION_MARK_ID_1 = 1;
    private static final int QUESTION_MARK_ID_2 = 5;
    private static final int QUESTION_MARK_ID_3 = 28;

    public Q00255_Tutorial() {
        super(255);
        addTalkId(NEWBIE_HELPERS);
        addTalkId(SUPERVISORS);
        addFirstTalkId(NEWBIE_HELPERS);
        addFirstTalkId(SUPERVISORS);
        addKillId(GREMLINS);
        registerQuestItems(BLUE_GEM);
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        final QuestState qs = getQuestState(player, false);

        if (isNull(qs)) {
            return null;
        }

        String htmltext = null;
        switch (event) {
            case "start_newbie_tutorial": {
                if (qs.getMemoState() < 4)
                {
                    qs.startQuest();
                    qs.setMemoState(1);
                    playTutorialVoice(player, STARTING_VOICE_HTML.get(player.getClassId().getId()).getSound());
                    showTutorialHtml(player, STARTING_VOICE_HTML.get(player.getClassId().getId()).getHtml());
                }
                break;
            }
            case "tutorial_02.html":
                player.sendPacket(new TutorialEnableClientEvent(1));
                playTutorialVoice(player,"tutorial_voice_003");
            case "tutorial_03.html": {
                if (qs.isMemoState(1)) {
                    showTutorialHtml(player, event);
                }
                break;
            }
            case "8": // Client Event Initialize Point of View
            case "question_mark_1": {
                if (qs.isMemoState(1)) {
                    player.sendPacket(new TutorialShowQuestionMark(QUESTION_MARK_ID_1, 0));
                    player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
                    player.clearHtmlActions(HtmlActionScope.TUTORIAL_HTML);
                    qs.setMemoState(4);
                }
                break;
            }
            case "reward_2": {
                if (qs.isMemoState(4)) {
                    qs.setMemoState(5);
                    if (player.isMageClass() && (player.getRace() != Race.ORC)) {
                        giveItems(player, SPIRITSHOT_REWARD);
                        playTutorialVoice(player, "tutorial_voice_027");
                    } else {
                        giveItems(player, SOULSHOT_REWARD);
                        playTutorialVoice(player, "tutorial_voice_026");
                    }
                    htmltext = (npc != null ? npc.getId() : player.getTarget().getId()) + "-3.html";
                    player.sendPacket(new TutorialShowQuestionMark(QUESTION_MARK_ID_3, 0));
                }
                break;
            }
            case "close_tutorial": {
                player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
                player.clearHtmlActions(HtmlActionScope.TUTORIAL_HTML);
                break;
            }
            case "1": { // Client Event On Character move
                if(qs.getMemoState() < 2) {
                    player.sendPacket(new TutorialEnableClientEvent(2));
                    playTutorialVoice(player, "tutorial_voice_004");
                    showTutorialHtml(player, "tutorial_05.html");
                }
                break;
            }
            case "2": { // Client Event Change point of view
                if(qs.getMemoState() < 2) {
                    player.sendPacket(new TutorialEnableClientEvent(8));
                    playTutorialVoice(player, "tutorial_voice_005");
                    showTutorialHtml(player, "tutorial_07.html");
                }
                break;
            }


        }
        return htmltext;
    }

    @Override
    public String onFirstTalk(Npc npc, Player player) {
        final QuestState questState = getQuestState(player, false);
        if (nonNull(questState)) {
            // start newbie helpers
            if (Arrays.binarySearch(NEWBIE_HELPERS, npc.getId()) >= 0) {

                if (hasQuestItems(player, BLUE_GEM)) {
                    questState.setMemoState(3);
                }

                switch (questState.getMemoState()) {
                    case 0:
                    case 1: {
                        player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
                        player.clearHtmlActions(HtmlActionScope.TUTORIAL_HTML);
                        questState.setMemoState(2);

                        if (!player.isMageClass()) {
                            return "tutorial_05_fighter.html";
                        }
                        else if (Race.ORC == player.getRace()) {
                            return "tutorial_05_mystic_orc.html";
                        }
                        return "tutorial_05_mystic.html";
                    }
                    case 2: {
                        if (!player.isMageClass()) {
                            return "tutorial_05_fighter_back.html";
                        }
                        else if (Race.ORC == player.getRace()) {
                            return "tutorial_05_mystic_orc_back.html";
                        }
                        return "tutorial_05_mystic_back.html";
                    }
                    case 3: {
                        player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
                        player.clearHtmlActions(HtmlActionScope.TUTORIAL_HTML);
                        questState.setMemoState(4);
                        takeItems(player, BLUE_GEM, -1);
                        if (player.isMageClass() && (player.getRace() != Race.ORC))
                        {
                            giveItems(player, SPIRITSHOT_REWARD);
                            playTutorialVoice(player, "tutorial_voice_027");
                            return npc.getId() + "-3.html";
                        }
                        giveItems(player, SOULSHOT_REWARD);
                        playTutorialVoice(player, "tutorial_voice_026");
                        return npc.getId() + "-2.html";
                    }
                    case 4: {
                        return npc.getId() + "-4.html";
                    }
                    case 5:
                    case 6:
                    {
                        return npc.getId() + "-5.html";
                    }
                }
            }
            // else supervisors
            switch (questState.getMemoState()) {
                case 0:
                case 1:
                case 2:
                case 3: {
                    return npc.getId() + "-1.html";
                }
                case 4: {
                    return npc.getId() + "-2.html";
                }
                case 5:
                case 6: {
                    return npc.getId() + "-4.html";
                }
            }
        }
        return npc.getId() + "-1.html";
    }

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon) {
        final QuestState qs = getQuestState(killer, false);
        if ((qs != null) && qs.isMemoState(2) && !hasQuestItems(killer, BLUE_GEM) && (getRandom(100) < 30)) {
            // check for too many gems on ground
            var counter = L2World.getInstance().getVisibleObjectsInRange(killer, Item.class, 1500).stream().filter(item -> item.getId() == BLUE_GEM).count();
            if (counter < 10) // do not drop if more than 10
            {
                npc.dropItem(killer, BLUE_GEM, 1);
            }
        }
        return super.onKill(npc, killer, isSummon);
    }

    @RegisterEvent(EventType.ON_PLAYER_ITEM_PICKUP)
    @RegisterType(ListenerRegisterType.ITEM)
    @Id(BLUE_GEM)
    public void OnPlayerItemPickup(OnPlayerItemPickup event) {
        final Player player = event.getActiveChar();
        final QuestState qs = getQuestState(player, false);
        if ((qs != null) && (qs.getMemoState() < 3)) {
            qs.setMemoState(3);
            playSound(player, "ItemSound.quest_tutorial");
            playTutorialVoice(player, "tutorial_voice_013");
            player.sendPacket(new TutorialShowQuestionMark(QUESTION_MARK_ID_2, 0));
        }
    }

    @RegisterEvent(EventType.ON_PLAYER_PRESS_TUTORIAL_MARK)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void onPlayerPressTutorialMark(OnPlayerPressTutorialMark event) {
        final QuestState qs = getQuestState(event.getActiveChar(), false);
        if (qs != null) {
            switch (event.getMarkId()) {
                case QUESTION_MARK_ID_1:
                {
                    if (qs.isMemoState(1))
                    {
                        showOnScreenMsg(event.getActiveChar(), NpcStringId.SPEAK_WITH_THE_NEWBIE_HELPER, ExShowScreenMessage.TOP_CENTER, 5000);
                        final int classId = event.getActiveChar().getClassId().getId();
                        addRadar(event.getActiveChar(), HELPER_LOCATION.get(classId).getX(), HELPER_LOCATION.get(classId).getY(), HELPER_LOCATION.get(classId).getZ());
                        showTutorialHtml(event.getActiveChar(), "tutorial_04.html");
                        playTutorialVoice(event.getActiveChar(), "tutorial_voice_007");
                    }
                    break;
                }
                case QUESTION_MARK_ID_2: {
                    if (qs.isMemoState(3))
                    {
                        final int classId = event.getActiveChar().getClassId().getId();
                        addRadar(event.getActiveChar(), HELPER_LOCATION.get(classId).getX(), HELPER_LOCATION.get(classId).getY(), HELPER_LOCATION.get(classId).getZ());
                        showTutorialHtml(event.getActiveChar(), "tutorial_06.html");
                    }
                    break;
                }
                case QUESTION_MARK_ID_3: {
                    if (qs.isMemoState(5))
                    {
                        final int classId = event.getActiveChar().getClassId().getId();
                        addRadar(event.getActiveChar(), COMPLETE_LOCATION.get(classId).getX(), COMPLETE_LOCATION.get(classId).getY(), COMPLETE_LOCATION.get(classId).getZ());
                        playSound(event.getActiveChar(), "ItemSound.quest_tutorial");
                    }
                    break;
                }
            }
        }
    }

    @RegisterEvent(EventType.ON_PLAYER_BYPASS)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void OnPlayerBypass(OnPlayerBypass event) {
        final Player player = event.getActiveChar();
        if (event.getCommand().startsWith(TUTORIAL_BYPASS))
        {
            notifyEvent(event.getCommand().replace(TUTORIAL_BYPASS, ""), null, player);
        }
    }

    @RegisterEvent(EventType.ON_PLAYER_LOGIN)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void OnPlayerLogin(OnPlayerLogin event)
    {
        if (Config.DISABLE_TUTORIAL)
        {
            return;
        }

        final Player player = event.getActiveChar();
        if (player.getLevel() > 6)
        {
            return;
        }

        QuestState qs = getQuestState(player, true);
        if ((qs != null) && (qs.getMemoState() < 4) && STARTING_VOICE_HTML.containsKey(player.getClassId().getId()))
        {
            startQuestTimer("start_newbie_tutorial", 5000, null, player);
        }
    }

    private void showTutorialHtml(Player player, String html)
    {
        player.sendPacket(new TutorialShowHtml(getHtm(player, html)));
    }

    public void playTutorialVoice(Player player, String voice)
    {
        player.sendPacket(new PlaySound(2, voice, 0, 0, player.getX(), player.getY(), player.getZ()));
    }

    private class QuestSoundHtmlHolder
    {
        private final String _sound;
        private final String _html;

        QuestSoundHtmlHolder(String sound, String html)
        {
            _sound = sound;
            _html = html;
        }

        String getSound()
        {
            return _sound;
        }

        String getHtml()
        {
            return _html;
        }
    }
}
