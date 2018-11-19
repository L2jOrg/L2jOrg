package l2s.gameserver.skills;

import java.time.Duration;
import java.time.Instant;

/**
 * A class representing the basic property resist of mesmerizing debuffs.
 * @author Nik
 */
public class BasicPropertyResist
{
	private static final Duration RESIST_DURATION = Duration.ofSeconds(15); // The resistance stays no longer than 15 seconds after last mesmerizing debuff.

	private volatile Instant _resistanceEndTime = Instant.MIN;
	private volatile int _resistanceLevel;

	/**
	 * Checks if the resist has expired.
	 * @return {@code true} if it has expired, {@code false} otherwise
	 */
	public boolean isExpired()
	{
		return Instant.now().isAfter(_resistanceEndTime);
	}

	/**
	 * Gets the remain time.
	 * @return the remain time
	 */
	public Duration getRemainTime()
	{
		return Duration.between(Instant.now(), _resistanceEndTime);
	}

	/**
	 * Gets the resist level.
	 * @return the resist level
	 */
	public int getResistLevel()
	{
		return !isExpired() ? _resistanceLevel : 0;
	}

	/**
	 * Increases the resist level while checking if the resist has expired so it starts counting it from 1.
	 */
	public synchronized void increaseResistLevel()
	{
		// Check if the level needs to be reset due to timer warn off.
		if(isExpired())
		{
			_resistanceLevel = 1;
			_resistanceEndTime = Instant.now().plus(RESIST_DURATION);
		}
		else
			_resistanceLevel++;
	}
}