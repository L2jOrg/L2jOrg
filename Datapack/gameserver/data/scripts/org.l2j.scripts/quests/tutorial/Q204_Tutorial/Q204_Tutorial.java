package quests.tutorial.Q204_Tutorial;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.interfaces.ILocational;
import quests.tutorial.Tutorial;

/**
 * @author JoeAlisson
 */
public class Q204_Tutorial extends Tutorial {

    private static final int SUPERVISOR = 30129;
    private static final int NEWBIE_HELPER = 30131;
    private static final Location HELPER_LOCATION  = new Location(28384, 11056, -4233);
    private static final Location VILLAGE_LOCATION = new Location(12161, 16674, -4584, 60030);

    private static final QuestSoundHtmlHolder STARTING_VOICE_HTML =  new QuestSoundHtmlHolder("tutorial_voice_001e", "main.html");

    public Q204_Tutorial() {
        super(204, ClassId.DARK_FIGHTER, ClassId.DARK_MAGE);
        addFirstTalkId(SUPERVISOR);
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
        return STARTING_VOICE_HTML;
    }

    @Override
    protected Location helperLocation() {
        return HELPER_LOCATION;
    }
}
