package quests.tutorial.Q205_Tutorial;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.interfaces.ILocational;
import quests.tutorial.Tutorial;

/**
 * @author JoeAlisson
 */
public class Q205_Tutorial extends Tutorial {

    private static final int SUPERVISOR = 30573;
    private static final int NEWBIE_HELPER = 30575;
    private static final Location HELPER_LOCATION  = new Location(-56736, -113680, -672);
    private static final Location VILLAGE_LOCATION = new Location(-45113, -113598, -192, 45809);

    private static final QuestSoundHtmlHolder STARTING_VOICE_HTML =  new QuestSoundHtmlHolder("tutorial_voice_001g", "main.html");

    public Q205_Tutorial() {
        super(205, ClassId.ORC_FIGHTER, ClassId.ORC_MAGE);
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
