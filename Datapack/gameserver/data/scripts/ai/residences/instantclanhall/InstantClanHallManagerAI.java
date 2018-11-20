package ai.residences.instantclanhall;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.ai.NpcAI;
import org.l2j.gameserver.instancemanager.ReflectionManager;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.components.NpcString;
import org.l2j.gameserver.templates.item.ItemTemplate;
import org.l2j.gameserver.utils.ChatUtils;
import org.l2j.gameserver.utils.ItemFunctions;

/**
 * @author Bonux
**/
public class InstantClanHallManagerAI extends NpcAI
{
	private int _attackCount = 0;

	private static final int Social_Reply_Timer_1 = 1000;
	private static final int Social_Reply_Timer_2 = 1001;
	private static final int Social_Reply_Timer_3 = 1002;
	private static final int Social_Reply_Timer_4 = 1003;
	private static final int instantAgit_StrotyTime = 1004;
	private static final int giran_tel_time = 1005;
	private static final int aden_tel_time = 1006;
	private static final int gludin_tel_time = 1007;

	public InstantClanHallManagerAI(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		NpcInstance actor = getActor();
		if(actor == null)
			return;

		//addTimer(1077, 1000);
		addTimer(instantAgit_StrotyTime, (60 * 60) * 1000);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		NpcInstance actor = getActor();
		if(actor == null)
			return;

		_attackCount++;

		if(_attackCount > 50 && Rnd.get(100) < 1)
		{
			_attackCount = 0;
			ChatUtils.say(actor, NpcString.HEYHEYHEY_DONT_HIT_ME_BUT_LOVE_ME);
		}
	}

	@Override
	protected void onEvtTimer(int timerId, Object arg1, Object arg2)
	{
		super.onEvtTimer(timerId, arg1, arg2);

		if(timerId == 1077)
		{
			// Не актуально, данное умение пассивное.
			//altUseSkill(SkillHolder.getInstance().getSkillEntry(4375, 1), player);
			//addTimer(1077, 300000);
		}
		else if(timerId == Social_Reply_Timer_1)
		{
			switch(Rnd.get(3))
			{
				case 0:
				{
					ChatUtils.say(getActor(), NpcString.HELLO_MASTER);
					break;
				}
				case 1:
				{
					ChatUtils.say(getActor(), NpcString.YOURE_ALWAYS_SO_NICE_);
					break;
				}
				case 2:
				{
					ChatUtils.say(getActor(), NpcString.NICE_TO_SEE_YOU_TOO);
					break;
				}
			}
		}
		else if(timerId == Social_Reply_Timer_2)
		{
			switch(Rnd.get(3))
			{
				case 0:
				{
					ChatUtils.say(getActor(), NpcString.HELLO_YOU_DONT_HAVE_TO_BE_POLITE_WITH_ME);
					break;
				}
				case 1:
				{
					ChatUtils.say(getActor(), NpcString.MAY_THE_ONE_STRAND_OF_COOL_WIND_BE_WITH_YOU_MASTER);
					break;
				}
				case 2:
				{
					ChatUtils.say(getActor(), NpcString.THE_WORLD_REVOLVES_AROUND_YOU_MASTER);
					break;
				}
			}
		}
		else if(timerId == Social_Reply_Timer_3)
		{
			switch(Rnd.get(3))
			{
				case 0:
				{
					ChatUtils.say(getActor(), NpcString.MASTER_DID_SOMETHING_SAD_HAPPEN);
					break;
				}
				case 1:
				{
					ChatUtils.say(getActor(), NpcString.THE_SKY_BECOMES_CLEAR_AFTER_A_RAIN_LET_US_BOTH_CHEER_UP);
					break;
				}
				case 2:
				{
					ChatUtils.say(getActor(), NpcString.IM_BESIDE_YOU);
					break;
				}
			}
		}
		else if(timerId == Social_Reply_Timer_4)
		{
			if(Rnd.get(50) < 1)
			{
				ChatUtils.say(getActor(), NpcString.IF_YOU_BECOME_THE_KING_OF_ADEN_I_WILL_THINK_ABOUT_IT);
			}
			else
			{
				switch(Rnd.get(3))
				{
					case 0:
					{
						ChatUtils.say(getActor(), NpcString.NO_YOU_CANNOT_MASTER);
						break;
					}
					case 1:
					{
						ChatUtils.say(getActor(), NpcString.MASTER__);
						break;
					}
					case 2:
					{
						ChatUtils.say(getActor(), NpcString.DOT_DOT_DOT_DOT_DOT_DOT);
						break;
					}
				}
			}
		}
		else if(timerId == instantAgit_StrotyTime)
		{
			if(Rnd.get(5) < 1)
				ChatUtils.say(getActor(), NpcString.THIS_IS_A_HIDEOUT_MADE_WITH_SHINE_STONE_USED_IN_GIANTS_DIMENSIONAL_TECHNIQUE__SUPPOSEDLY_THE_CAPITAL_OF_THE_GIANTS_THAT_HAVE_NOT_BEEN_FOUND_IS_STILL_IN_THE_DIMENSIONAL_GAP_SOMEWHERE);
		}
		else if(timerId == giran_tel_time)
		{
			// TODO: Сверить по оффу.
			if(arg1 != null && (arg1 instanceof Player))
			{
				Player player = (Player) arg1;
				ItemFunctions.deleteItem(player, ItemTemplate.ITEM_ID_ADENA, 2000);
				switch(Rnd.get(3))
				{
					case 0:
					{
						player.teleToLocation(83384, 148088, -3408, ReflectionManager.MAIN);
						break;
					}
					case 1:
					{
						player.teleToLocation(81416, 149608, -3472, ReflectionManager.MAIN);
						break;
					}
					case 2:
					{
						player.teleToLocation(81032, 148616, -3472, ReflectionManager.MAIN);
						break;
					}
				}
			}
		}
		else if(timerId == aden_tel_time)
		{
			// TODO: Сверить по оффу.
			if(arg1 != null && (arg1 instanceof Player))
			{
				Player player = (Player) arg1;
				ItemFunctions.deleteItem(player, ItemTemplate.ITEM_ID_ADENA, 2000);
				switch(Rnd.get(3))
				{
					case 0:
					{
						player.teleToLocation(147468, 26791, -2200, ReflectionManager.MAIN);
						break;
					}
					case 1:
					{
						player.teleToLocation(147869, 25781, -2008, ReflectionManager.MAIN);
						break;
					}
					case 2:
					{
						player.teleToLocation(146330, 25626, -2008, ReflectionManager.MAIN);
						break;
					}
				}
			}
		}
		else if(timerId == gludin_tel_time)
		{
			// TODO: Сверить по оффу.
			if(arg1 != null && (arg1 instanceof Player))
			{
				Player player = (Player) arg1;
				ItemFunctions.deleteItem(player, ItemTemplate.ITEM_ID_ADENA, 2000);
				switch(Rnd.get(3))
				{
					case 0:
					{
						player.teleToLocation(-81352, 151528, -3120, ReflectionManager.MAIN);
						break;
					}
					case 1:
					{
						player.teleToLocation(-81320, 149768, -3120, ReflectionManager.MAIN);
						break;
					}
					case 2:
					{
						player.teleToLocation(-80840, 149944, -3040, ReflectionManager.MAIN);
						break;
					}
				}
			}
		}
	}
}