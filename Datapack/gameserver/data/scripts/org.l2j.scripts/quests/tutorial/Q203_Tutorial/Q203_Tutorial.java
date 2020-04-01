package quests.tutorial.Q203_Tutorial;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.interfaces.ILocational;
import quests.tutorial.Tutorial;

/**
 * @author JoeAlisson
 */
public class Q203_Tutorial extends Tutorial {

    private static final int SUPERVISOR = 30370;
    private static final int NEWBIE_HELPER = 30400;
    private static final Location HELPER_LOCATION  = new Location(46112, 41200, -3504);
    private static final Location VILLAGE_LOCATION = new Location(45479, 48318, -3056, 55707);

    private static final QuestSoundHtmlHolder STARTING_VOICE_HTML =  new QuestSoundHtmlHolder("tutorial_voice_001c", "main.html");

    public Q203_Tutorial() {
        super(203, ClassId.ELVEN_FIGHTER, ClassId.ELVEN_MAGE);
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
