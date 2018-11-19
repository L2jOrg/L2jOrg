package l2s.gameserver.utils;

import java.util.Comparator;

import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Abnormal;

/**
 * Сортирует эффекты по группам для корректного отображения в клиенте: включаемые, танцы/песни, положительные/отрицательные
 *
 * @author G1ta0
 */
public class AbnormalsComparator implements Comparator<Abnormal>
{
	private static final AbnormalsComparator instance = new AbnormalsComparator();

	public static final AbnormalsComparator getInstance()
	{
		return instance;
	}

	@Override
	public int compare(Abnormal a1, Abnormal a2)
	{
		if(a1 == null || a2 == null)
			return 0;

		Skill s1 = a1.getSkill();
		Skill s2 = a2.getSkill();

		boolean toggle1 = s1.isToggle();
		boolean toggle2 = s2.isToggle();

		if(toggle1 && toggle2)
			return compareStartTime(a1, a2);

		if(toggle1 || toggle2)
			if(toggle1)
				return 1;
			else
				return -1;

		boolean music1 = s1.isMusic();
		boolean music2 = s2.isMusic();

		if(music1 && music2)
			return compareStartTime(a1, a2);

		if(music1 || music2)
			if(music1)
				return 1;
			else
				return -1;

		boolean offensive1 = a1.isOffensive();
		boolean offensive2 = a2.isOffensive();

		if(offensive1 && offensive2)
			return compareStartTime(a1, a2);

		if(offensive1 || offensive2)
			if(!offensive1)
				return 1;
			else
				return -1;

		boolean trigger1 = s1.isTrigger();
		boolean trigger2 = s2.isTrigger();

		if(trigger1 && trigger2)
			return compareStartTime(a1, a2);

		if(trigger1 || trigger2)
			if(trigger1)
				return 1;
			else
				return -1;

		return compareStartTime(a1, a2);
	}

	private int compareStartTime(Abnormal o1, Abnormal o2)
	{
		if(o1.isHideTime() && !o2.isHideTime())
			return 1;

		if(!o1.isHideTime() && o2.isHideTime())
			return -1;

		if(o1.isHideTime() && o2.isHideTime())
		{
			if(o1.getDisplayId() > o2.getDisplayId())
				return 1;

			if(o1.getDisplayId() < o2.getDisplayId())
				return -1;
		}

		if(o1.getStartTime() > o2.getStartTime())
			return 1;

		if(o1.getStartTime() < o2.getStartTime())
			return -1;

		return 0;
	}
}