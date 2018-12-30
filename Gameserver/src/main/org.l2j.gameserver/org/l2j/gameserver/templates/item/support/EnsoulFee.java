package org.l2j.gameserver.templates.item.support;

import java.util.Collections;
import java.util.List;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

/**
 * @author Bonux
 **/
public class EnsoulFee
{
	public static class EnsoulFeeInfo
	{
		private List<EnsoulFeeItem> _insertFee = Collections.emptyList();
		private List<EnsoulFeeItem> _changeFee = Collections.emptyList();
		private List<EnsoulFeeItem> _removeFee = Collections.emptyList();

		public void setInsertFee(List<EnsoulFeeItem> value)
		{
			_insertFee = value;
		}

		public List<EnsoulFeeItem> getInsertFee()
		{
			return _insertFee;
		}

		public void setChangeFee(List<EnsoulFeeItem> value)
		{
			_changeFee = value;
		}

		public List<EnsoulFeeItem> getChangeFee()
		{
			return _changeFee;
		}

		public void setRemoveFee(List<EnsoulFeeItem> value)
		{
			_removeFee = value;
		}

		public List<EnsoulFeeItem> getRemoveFee()
		{
			return _removeFee;
		}
	}

	public static class EnsoulFeeItem
	{
		private final int _id;
		private final long _count;

		public EnsoulFeeItem(int id, long count)
		{
			_id = id;
			_count = count;
		}

		public int getId()
		{
			return _id;
		}

		public long getCount()
		{
			return _count;
		}
	}

	private IntObjectMap<IntObjectMap<EnsoulFeeInfo>> _ensoulsFee = null;

	public void addFeeInfo(int type, int id, EnsoulFeeInfo feeInfo)
	{
		if(_ensoulsFee == null)
			_ensoulsFee = new HashIntObjectMap<>();

		IntObjectMap<EnsoulFeeInfo> ensoulFeeInfos = _ensoulsFee.get(type);
		if(ensoulFeeInfos == null)
		{
			ensoulFeeInfos = new HashIntObjectMap<EnsoulFeeInfo>();
			_ensoulsFee.put(type, ensoulFeeInfos);
		}
		ensoulFeeInfos.put(id, feeInfo);
	}

	public EnsoulFeeInfo getFeeInfo(int type, int id)
	{
		if(_ensoulsFee == null)
			return null;

		IntObjectMap<EnsoulFeeInfo> ensoulFeeInfos = _ensoulsFee.get(type);
		if(ensoulFeeInfos == null)
			return null;

		return ensoulFeeInfos.get(id);
	}
}