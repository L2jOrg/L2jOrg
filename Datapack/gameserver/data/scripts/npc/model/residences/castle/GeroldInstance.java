package npc.model.residences.castle;

import org.apache.commons.lang3.ArrayUtils;
import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.entity.residence.Castle;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.templates.npc.NpcTemplate;

import java.util.StringTokenizer;

/**
 * @author Bonux
**/
public class GeroldInstance extends NpcInstance
{
	private static final int GIFT_SKILL_ID = 19036;

	public GeroldInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public String getHtmlDir(String filename, Player player)
	{
		return "castle/gerold/";
	}

	@Override
	public String getHtmlFilename(int val, Player player)
	{
		if(val == 0)
		{
			Castle castle = getCastle(player);
			if(castle == null || castle.getOwnerId() == 0)
				return "no_clan.htm";
		}
		return super.getHtmlFilename(val, player);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command);
		String cmd = st.nextToken();
		if(cmd.equals("receive_gift"))
		{
			Skill skill = SkillHolder.getInstance().getSkill(GIFT_SKILL_ID, 1);
			skill.getEffects(player, player);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		Castle castle = getCastle(player);
		Clan clan = (castle == null ? null : castle.getOwner());
		if(clan != null)
		{
			if(arg == null)
				arg = new Object[0];

			arg = ArrayUtils.add(arg, "<?clan_name?>");
			arg = ArrayUtils.add(arg, clan.getName());
			arg = ArrayUtils.add(arg, "<?leader_name?>");
			arg = ArrayUtils.add(arg, clan.getLeaderName());
		}

		super.showChatWindow(player, val, firstTalk, arg);
	}
}