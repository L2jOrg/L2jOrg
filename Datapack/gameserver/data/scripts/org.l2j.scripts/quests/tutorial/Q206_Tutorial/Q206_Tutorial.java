package quests.tutorial.Q206_Tutorial;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.interfaces.ILocational;
import quests.tutorial.Tutorial;

/**
 * @author JoeAlisson
 */
public class Q206_Tutorial extends Tutorial {

    private static final int SUPERVISOR = 30528;
    private static final int NEWBIE_HELPER = 30530;
    private static final Location HELPER_LOCATION  = new Location(108567, -173994, -406);
    private static final Location VILLAGE_LOCATION = new Location(115575, -178014, -904, 9808);

    private static final QuestSoundHtmlHolder STARTING_VOICE_HTML =  new QuestSoundHtmlHolder("tutorial_voice_001i", "main.html");


    public Q206_Tutorial() {
        super(206, ClassId.DWARVEN_FIGHTER);
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
