package l2s.gameserver.listener.actor.player;

import l2s.gameserver.listener.PlayerListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.SkillLearn;

public interface OnLearnCustomSkillListener extends PlayerListener
{
    public void onLearnCustomSkill(Player player, SkillLearn skillLearn);
}