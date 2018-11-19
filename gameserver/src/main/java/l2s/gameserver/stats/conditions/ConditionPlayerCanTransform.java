package l2s.gameserver.stats.conditions;

import l2s.gameserver.data.xml.holder.TransformTemplateHolder;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.TransformType;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.player.transform.TransformTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
**/
public class ConditionPlayerCanTransform extends Condition
{
	private static final Logger _log = LoggerFactory.getLogger(ConditionPlayerCanTransform.class);

	private final int _transformId;

	public ConditionPlayerCanTransform(int transformId)
	{
		_transformId = transformId;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer())
			return false;

		Player player = env.character.getPlayer();
		Skill skill = env.skill;

		if(player.getActiveWeaponFlagAttachment() != null)
		{
			player.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skill));
			return false;
		}

		if(player.isTransformed())
		{
			player.sendPacket(SystemMsg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
			return false;
		}

		TransformTemplate template = TransformTemplateHolder.getInstance().getTemplate(player.getSex(), _transformId);
		if(template == null)
		{
			_log.warn(getClass().getSimpleName() + ": Cannot find transformation template for skill ID[" + skill.getId() + "], LEVEL[" + skill.getLevel() + "]!");
			return false;
		}

		// Нельзя использовать летающую трансформу на территории Aden, или слишком высоко/низко, или при вызванном пете/саммоне, или в инстансе
		if(template.getType() == TransformType.FLYING && (player.getX() > -166168 || player.getZ() <= 0 || player.getZ() >= 6000 || player.hasServitor() || !player.getReflection().isMain()))
		{
			player.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skill));
			return false;
		}

		if(player.isInWater())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_POLYMORPH_INTO_THE_DESIRED_FORM_IN_WATER);
			return false;
		}

		if(player.isMounted())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_PET);
			return false;
		}

		// Для трансформации у игрока не должно быть активировано умение Mystic Immunity.
		if(player.isTransformImmune() || player.getAbnormalList().contains(Skill.SKILL_MYSTIC_IMMUNITY))
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_POLYMORPH_WHILE_UNDER_THE_EFFECT_OF_A_SPECIAL_SKILL);
			return false;
		}

		if(player.isInBoat())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_BOAT);
			return false;
		}

		if(player.getPet() != null && template.getType() == TransformType.MODE_CHANGE)
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_POLYMORPH_WHEN_YOU_HAVE_SUMMONED_A_SERVITORPET);
			return false;
		}

		return true;
	}
}