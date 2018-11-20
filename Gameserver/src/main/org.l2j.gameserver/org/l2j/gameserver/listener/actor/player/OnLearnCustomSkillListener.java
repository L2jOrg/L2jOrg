package org.l2j.gameserver.listener.actor.player;

import org.l2j.gameserver.listener.PlayerListener;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.SkillLearn;

public interface OnLearnCustomSkillListener extends PlayerListener
{
    public void onLearnCustomSkill(Player player, SkillLearn skillLearn);
}