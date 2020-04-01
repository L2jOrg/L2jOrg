package quests.tutorial.Q202_Tutorial;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.interfaces.ILocational;
import quests.tutorial.Tutorial;

/**
 * @author JoeAlisson
 */
public class Q202_Tutorial extends Tutorial {

    private static final int SUPERVISOR = 30017;
    private static final int NEWBIE_HELPER = 30019;
    private static final Location HELPER_LOCATION  = new Location(-91036, 248044, -3568);
    private static final Location VILLAGE_LOCATION = new Location(-84046, 243283, -3728, 18316);

    private static final QuestSoundHtmlHolder STARTING_VOICE_HTML =  new QuestSoundHtmlHolder("tutorial_voice_001b", "main.html");

    public Q202_Tutorial() {
        super(202, ClassId.MAGE);
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
