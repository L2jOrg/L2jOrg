package org.l2j.gameserver.network.authcomm;

/**
 * <p>This class is used to represent session keys used by the client to authenticate in the gameserver</p>
 * <p>A SessionKey is made up of two 8 bytes keys. One is send in the LoginOk packet and the other is sent in PlayOk</p>
 * 
 * @author -Wooden-
 */
public class SessionKey
{
	public final int gameserverSession;
	public final int gameserverAccountId;
	public final int authAccountId;
	public final int authKey;

	private final int hashCode;

	public SessionKey(int authAccountId, int authKey, int gameserverSession, int gameserverAccountId)
	{
		this.gameserverSession = gameserverSession;
		this.gameserverAccountId = gameserverAccountId;
		this.authAccountId = authAccountId;
		this.authKey = authKey;

		int hashCode = gameserverSession;
		hashCode *= 17;
		hashCode += gameserverAccountId;
		hashCode *= 37;
		hashCode += authAccountId;
		hashCode *= 51;
		hashCode += authKey;

		this.hashCode = hashCode;
	}

	public boolean equals(Object o)
	{
		if(this == o)
			return true;
		if(o == null)
			return false;
		if(o.getClass() == this.getClass())
		{
			SessionKey skey = (SessionKey) o;
			return gameserverSession == skey.gameserverSession && gameserverAccountId == skey.gameserverAccountId && authAccountId == skey.authAccountId && authKey == skey.authKey;
		}
		return false;
	}

	public int hashCode()
	{
		return hashCode;
	}

	public String toString()
	{
		return new StringBuilder().append("[gameserverSession: ").append(gameserverSession).append(" gameserverAccountId: ").append(gameserverAccountId).append(" authAccountId: ").append(authAccountId).append(" authKey: ").append(authKey).append("]").toString();
	}
}
