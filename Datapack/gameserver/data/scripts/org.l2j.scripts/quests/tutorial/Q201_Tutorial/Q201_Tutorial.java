package quests.tutorial.Q201_Tutorial;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.interfaces.ILocational;
import quests.tutorial.Tutorial;

/**
 * @author JoeAlisson
 */
public class Q201_Tutorial extends Tutorial {

    private static final int SUPERVISOR = 30008;
    private static final int NEWBIE_HELPER = 30009;
    private static final Location HELPER_LOCATION  = new Location(-71424, 258336, -3109);
    private static final Location VILLAGE_LOCATION = new Location(-84046, 243283, -3728, 18316);

    private static final QuestSoundHtmlHolder STARTING_VOICE_HTML =  new QuestSoundHtmlHolder("tutorial_voice_001a", "main.html");

    public Q201_Tutorial() {
        super(201, ClassId.FIGHTER);
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
