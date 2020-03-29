package quests.tutorial.Q10960_Tutorial;

import io.github.joealisson.primitive.IntMap;
import io.github.joealisson.primitive.HashIntMap;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.HtmlActionScope;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.network.serverpackets.html.TutorialShowHtml;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerBypass;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPressTutorialMark;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.network.serverpackets.html.TutorialWindowType;
import quests.tutorial.Tutorial;

import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Tutorial Quest
 * @author Mobius
 * @author joeAlisson
 *
 */
public class Q10960_Tutorial extends Tutorial {

    private static final QuestSoundHtmlHolder STARTING_HTML_VOICE = new QuestSoundHtmlHolder("tutorial_voice_001j", "main.html");
    private static final int NEWBIE_HELPER = 34108;
    private static final Location HELPER_LOC = new Location(-124731, 38070, 1208);
    private static final Location VILLAGE_LOCATION = new Location(-118073, 45131, 368, 43039);

    // NPCs
    private static final int[] NEWBIE_HELPERS = {
            30131, // dark elf
            30400, // elf
            30530, // dwarf
            30575, // orc
    };

    private static final int[] SUPERVISORS = {
            30129, // dark elf
            30370, // elf
            30528, // dwarf
            30573, // orc
            34109, // jin kamael
    };

    // Others
    private static final IntMap<QuestSoundHtmlHolder> STARTING_VOICE_HTML = new HashIntMap<>();

    {

        STARTING_VOICE_HTML.put(18, new QuestSoundHtmlHolder("tutorial_voice_001c", "tutorial_elven_fighter001.html"));
        STARTING_VOICE_HTML.put(25, new QuestSoundHtmlHolder("tutorial_voice_001d", "tutorial_elven_mage001.html"));
        STARTING_VOICE_HTML.put(31, new QuestSoundHtmlHolder("tutorial_voice_001e", "tutorial_delf_fighter001.html"));
        STARTING_VOICE_HTML.put(38, new QuestSoundHtmlHolder("tutorial_voice_001f", "tutorial_delf_mage001.html"));
        STARTING_VOICE_HTML.put(44, new QuestSoundHtmlHolder("tutorial_voice_001g", "tutorial_orc_fighter001.html"));
        STARTING_VOICE_HTML.put(49, new QuestSoundHtmlHolder("tutorial_voice_001h", "tutorial_orc_mage001.html"));
        STARTING_VOICE_HTML.put(53, new QuestSoundHtmlHolder("tutorial_voice_001i", "tutorial_dwarven_fighter001.html"));
    }

    private static final IntMap<Location> HELPER_LOCATION = new HashIntMap<>();
    {

        HELPER_LOCATION.put(18, new Location(46112, 41200, -3504));
        HELPER_LOCATION.put(25, new Location(46112, 41200, -3504));
        HELPER_LOCATION.put(31, new Location(28384, 11056, -4233));
        HELPER_LOCATION.put(38, new Location(28384, 11056, -4233));
        HELPER_LOCATION.put(44, new Location(-56736, -113680, -672));
        HELPER_LOCATION.put(49, new Location(-56736, -113680, -672));
        HELPER_LOCATION.put(53, new Location(108567, -173994, -406));
    }

    private static final IntMap<Location> COMPLETE_LOCATION = new HashIntMap<>();
    {
        COMPLETE_LOCATION.put(18, new Location(45479, 48318, -3056, 55707));
        COMPLETE_LOCATION.put(25, new Location(45479, 48318, -3056, 55707));
        COMPLETE_LOCATION.put(31, new Location(12161, 16674, -4584, 60030));
        COMPLETE_LOCATION.put(38, new Location(12161, 16674, -4584, 60030));
        COMPLETE_LOCATION.put(44, new Location(-45113, -113598, -192, 45809));
        COMPLETE_LOCATION.put(49, new Location(-45113, -113598, -192, 45809));
        COMPLETE_LOCATION.put(53, new Location(115575, -178014, -904, 9808));
    }

    public Q10960_Tutorial() {
        super(10960, ClassId.JIN_KAMAEL_SOLDIER);
        addTalkId(NEWBIE_HELPERS);
        addTalkId(SUPERVISORS);
        addFirstTalkId(NEWBIE_HELPERS);
        addFirstTalkId(SUPERVISORS);
    }

    @Override
    protected int newbieHelperId() {
        return NEWBIE_HELPER;
    }

    @Override
    protected ILocational villageLocation() {
        return VILLAGE_LOCATION;
    }

    @Override
    protected QuestSoundHtmlHolder startingVoiceHtml() {
        return STARTING_HTML_VOICE;
    }

    @Override
    protected Location helperLocation() {
        return HELPER_LOC;
    }
}
