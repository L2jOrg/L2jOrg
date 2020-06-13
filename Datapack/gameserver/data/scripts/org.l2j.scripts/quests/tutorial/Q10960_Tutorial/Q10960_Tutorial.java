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
package quests.tutorial.Q10960_Tutorial;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.interfaces.ILocational;
import quests.tutorial.Tutorial;

/**
 * @author joeAlisson
 */
public class Q10960_Tutorial extends Tutorial {

    private static final QuestSoundHtmlHolder STARTING_HTML_VOICE = new QuestSoundHtmlHolder("tutorial_voice_001j", "main.html");
    private static final int NEWBIE_HELPER = 34108;
    private static final int SUPERVISOR = 34109;
    private static final Location HELPER_LOC = new Location(-124731, 38070, 1208);
    private static final Location VILLAGE_LOCATION = new Location(-118073, 45131, 368, 43039);

    public Q10960_Tutorial() {
        super(10960, ClassId.JIN_KAMAEL_SOLDIER);
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
        return STARTING_HTML_VOICE;
    }

    @Override
    protected Location helperLocation() {
        return HELPER_LOC;
    }
}
