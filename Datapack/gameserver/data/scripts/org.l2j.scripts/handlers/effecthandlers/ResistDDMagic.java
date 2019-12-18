package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class ResistDDMagic extends AbstractStatEffect {
	public ResistDDMagic(StatsSet params)
	{
		super(params, Stat.MAGIC_SUCCESS_RES);
	}
}
