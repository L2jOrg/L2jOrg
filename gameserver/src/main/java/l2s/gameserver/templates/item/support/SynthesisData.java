package l2s.gameserver.templates.item.support;

public class SynthesisData
{
	private final int _item1Id;
	private final int _item2Id;
	private final double _chance;
	private final int _synthesizedItemId;
	private final int _failItemId;

	public SynthesisData(int item1Id, int item2Id, double chance, int synthesizedItemId, int failItemId)
	{
		_item1Id = item1Id;
		_item2Id = item2Id;
		_chance = chance;
		_synthesizedItemId = synthesizedItemId;
		_failItemId = failItemId;
	}

	public int getItem1Id()
	{
		return _item1Id;
	}

	public int getItem2Id()
	{
		return _item2Id;
	}

	public double getChance()
	{
		return _chance;
	}

	public int getSynthesizedItemId()
	{
		return _synthesizedItemId;
	}

	public int getFailItemId()
	{
		return _failItemId;
	}
}