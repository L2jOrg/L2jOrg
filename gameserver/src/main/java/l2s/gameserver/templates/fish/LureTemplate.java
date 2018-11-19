package l2s.gameserver.templates.fish;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bonux
 **/
public final class LureTemplate
{
	private final int _id;
	private final List<FishTemplate> _fishes = new ArrayList<FishTemplate>();

	public LureTemplate(int id, double failChance, int failDuration)
	{
		_id = id;
		if(failChance > 0 && failDuration > 0)
			_fishes.add(new FishTemplate(0, failChance, failDuration, 0));
	}

	public int getId()
	{
		return _id;
	}

	public void addFish(FishTemplate fish)
	{
		_fishes.add(fish);
	}

	public List<FishTemplate> getFishes()
	{
		return _fishes;
	}
}