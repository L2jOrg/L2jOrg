package l2s.gameserver.skills.effects;

import l2s.gameserver.listener.actor.player.OnPlayerSummonServitorListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.stats.funcs.Func;
import l2s.gameserver.stats.funcs.FuncTemplate;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author G1ta0
 */
public class EffectServitorShare extends Effect
{
	public static class FuncShare extends Func
	{
		public FuncShare(Stats stat, int order, Object owner, double value)
		{
			super(stat, order, owner, value);
		}

		@Override
		public void calc(Env env)
		{
			double val = 0;
			// временный хардкод, до переписи статов
			switch(stat)
			{
				case MAX_HP:
					val = env.character.getPlayer().getMaxHp();
					break;
				case MAX_MP:
					val = env.character.getPlayer().getMaxMp();
					break;
				case POWER_ATTACK:
					val = env.character.getPlayer().getPAtk(null);
					break;
				case MAGIC_ATTACK:
					val = env.character.getPlayer().getMAtk(null, null);
					break;
				case POWER_DEFENCE:
					val = env.character.getPlayer().getPDef(null);
					break;
				case MAGIC_DEFENCE:
					val = env.character.getPlayer().getMDef(null, null);
					break;
				case POWER_ATTACK_SPEED:
					val = env.character.getPlayer().getPAtkSpd();
					break;
				case MAGIC_ATTACK_SPEED:
					val = env.character.getPlayer().getMAtkSpd();
					break;
				case BASE_P_CRITICAL_RATE:
					val = env.character.getPlayer().getPCriticalHit(null);
					break;
				case BASE_M_CRITICAL_RATE:
					val = env.character.getPlayer().getMCriticalHit(null, null);
					break;
				case PVP_PHYS_DMG_BONUS:
				case PVP_PHYS_SKILL_DMG_BONUS:
				case PVP_MAGIC_SKILL_DMG_BONUS:
				case PVP_PHYS_DEFENCE_BONUS:
				case PVP_PHYS_SKILL_DEFENCE_BONUS:
				case PVP_MAGIC_SKILL_DEFENCE_BONUS:
				case PVE_PHYS_DMG_BONUS:
				case PVE_PHYS_SKILL_DMG_BONUS:
				case PVE_MAGIC_SKILL_DMG_BONUS:
				case PVE_PHYS_DEFENCE_BONUS:
				case PVE_PHYS_SKILL_DEFENCE_BONUS:
				case PVE_MAGIC_SKILL_DEFENCE_BONUS:
					val = env.character.getPlayer().calcStat(stat, 1) - 1;
					break;
				default:
					val = env.character.getPlayer().calcStat(stat, stat.getInit());
					break;
			}

			env.value += val * value;
		}
	}

	private class OnPlayerSummonServitorListenerImpl implements OnPlayerSummonServitorListener
	{
		@Override
		public void onSummonServitor(Player player, Servitor servitor)
		{
			FuncTemplate[] funcTemplates = getTemplate().getAttachedFuncs();
			Func[] funcs = new Func[funcTemplates.length];
			for(int i = 0; i < funcs.length; i++)
				funcs[i] = new FuncShare(funcTemplates[i]._stat, funcTemplates[i]._order, EffectServitorShare.this, funcTemplates[i]._value);

			servitor.addStatFuncs(funcs);
			servitor.updateStats();
		}
	}

	private OnPlayerSummonServitorListener _listener = new OnPlayerSummonServitorListenerImpl();

	public EffectServitorShare(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(!getEffected().isPlayer())
			return false;
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		getEffected().addListener(_listener);

		for(Servitor servitor : getEffected().getServitors())
			_listener.onSummonServitor(null, servitor);
	}

	@Override
	public void onExit()
	{
		getEffected().removeListener(_listener);

		for(Servitor servitor : getEffected().getServitors())
		{
			servitor.removeStatsOwner(this);
			servitor.updateStats();
		}
	}

	@Override
	public Func[] getStatFuncs()
	{
		return Func.EMPTY_FUNC_ARRAY;
	}
}