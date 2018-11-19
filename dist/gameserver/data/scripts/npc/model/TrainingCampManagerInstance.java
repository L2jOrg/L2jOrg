package npc.model;

import java.util.concurrent.TimeUnit;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.Config;
import l2s.gameserver.instancemanager.TrainingCampManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.TrainingCamp;
import l2s.gameserver.model.base.Experience;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExTrainingZone_Admission;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
**/
public class TrainingCampManagerInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public TrainingCampManagerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onMenuSelect(Player player, int ask, long reply)
	{
		if(ask == 4378)
		{
			if(!Config.TRAINING_CAMP_ENABLE)
				return;

			if(reply == 1)
			{
				if(player.getLevel() <= Config.TRAINING_CAMP_MIN_LEVEL)
				{
					if(Config.TRAINING_CAMP_MIN_LEVEL == 40)
						showChatWindow(player, "default/g_training_officer_lowlevel.htm", false);
					else
						player.sendPacket(new SystemMessagePacket(SystemMsg.LV_S1_OR_ABOVE).addInteger(Config.TRAINING_CAMP_MIN_LEVEL));
					return;
				}

				if(player.getLevel() >= Config.TRAINING_CAMP_MAX_LEVEL)
				{
					player.sendPacket(new SystemMessagePacket(SystemMsg.LV_S1_OR_BELOW).addInteger(Config.TRAINING_CAMP_MAX_LEVEL));
					return;
				}

				if(player.hasServitor())
				{
					showChatWindow(player, "default/g_training_officer_nopet.htm", false);
					return;
				}
	
				if(player.isTransformed())
				{
					player.sendPacket(SystemMsg.YOU_CANNOT_ENTER_THE_TRAINING_CAMP_WITH_A_MOUNT_OR_IN_A_TRANSFORMED_STATE);
					return;
				}

				if(player.isInParty())
				{
					player.sendPacket(SystemMsg.YOU_CANNOT_ENTER_THE_TRAINING_CAMP_WHILE_IN_A_PARTY_OR_USING_THE_AUTOMATIC_REPLACEMENT_SYSTEM);
					return;
				}

				if(player.isInDuel())
					return;

				if(player.isInOlympiadMode() || Olympiad.isRegistered(player))
					return;

				if(!player.getReflection().isMain())
					return;

				if(player.isInSiegeZone())
					return;

				if(player.isFishing())
					return;

				final int trainingCampDuration = TrainingCampManager.getInstance().getTrainingCampDuration(player.getAccountName());
				if(trainingCampDuration >= Config.TRAINING_CAMP_MAX_DURATION)
				{
					//player.sendPacket(SystemMsg.YOU_HAVE_COMPLETED_THE_DAYS_TRAINING);
					showChatWindow(player, "default/g_training_officer_overtime.htm", false);
				}
				else if(player.hasPremiumAccount() || !Config.TRAINING_CAMP_PREMIUM_ONLY)
				{
					TrainingCamp trainingCamp = TrainingCampManager.getInstance().getTrainingCamp(player);
					if(trainingCamp != null)
					{
						if(trainingCamp.getObjectId() != player.getObjectId())
						{
							//player.sendPacket(SystemMsg.ONLY_ONE_CHARACTER_PER_ACCOUNT_MAY_ENTER_AT_ANY_TIME);
							showChatWindow(player, "default/g_training_officer_nomulti.htm", false);
							return;
						}

						if(trainingCamp.getTrainingTime(TimeUnit.MINUTES) > 1)
						{
							showChatWindow(player, "default/g_training_complete001.htm", false);
							return;
						}
					}

					trainingCamp = new TrainingCamp(player.getAccountName(), player.getObjectId(), player.getActiveSubClass().getIndex(), player.getLevel(), System.currentTimeMillis());

					if(TrainingCampManager.getInstance().addTrainingCamp(player, trainingCamp))
					{
						TrainingCampManager.getInstance().onEnterTrainingCamp(player);
						player.startTrainingCampTask(trainingCamp.getMaxDuration() * 1000L);
						player.sendPacket(new ExTrainingZone_Admission(trainingCamp));
					}
				}
				else
					showChatWindow(player, "default/g_training_officer_nopremium.htm", false);
			}
			else if(reply == 2)
			{
				final TrainingCamp trainingCamp = TrainingCampManager.getInstance().getTrainingCamp(player);
				if(trainingCamp != null && trainingCamp.getObjectId() == player.getObjectId())
				{
					if(trainingCamp.getClassIndex() == player.getActiveSubClass().getIndex())
					{
						final long trainingTime = trainingCamp.getTrainingTime(TimeUnit.MINUTES);
						if(trainingTime >= 1)
						{
							final double experience = (trainingTime * (Experience.getExpForLevel(trainingCamp.getLevel()) * Experience.getTrainingRate(trainingCamp.getLevel()))) / TrainingCamp.TRAINING_DIVIDER;
							final long expGained = (long) (experience * Config.RATE_XP_BY_LVL[trainingCamp.getLevel()]);
							final long spGained = (long) ((experience * Config.RATE_SP_BY_LVL[trainingCamp.getLevel()]) / 250d);
							showChatWindow(player, "default/g_training_officer005.htm", false, "<?training_level?>", trainingCamp.getLevel(), "<?training_time?>", trainingTime, "<?training_exp?>", expGained, "<?training_sp?>", spGained);
						}
						else
							showChatWindow(player, "default/g_training_officer_notraining.htm", false);
					}
					else
						showChatWindow(player, "default/g_training_officer_different.htm", false);
				}
				else
					showChatWindow(player, "default/g_training_officer_notraining.htm", false);
			}
			else if(reply == 3)
			{
				final TrainingCamp trainingCamp = TrainingCampManager.getInstance().getTrainingCamp(player);
				if(trainingCamp != null && trainingCamp.getObjectId() == player.getObjectId())
				{
					if(trainingCamp.getClassIndex() == player.getActiveSubClass().getIndex())
					{
						final long trainingTime = trainingCamp.getTrainingTime(TimeUnit.MINUTES);
						if(trainingTime >= 1)
						{
							TrainingCampManager.getInstance().removeTrainingCamp(player);
							TrainingCampManager.getInstance().addTrainingCampDuration(player.getAccountName(), (int) trainingCamp.getTrainingTime(TimeUnit.SECONDS));

							player.sendPacket(SystemMsg.CALCULATING_XP_AND_SP_OBTAINED_FROM_TRAINING);

							final double experience = (trainingTime * (Experience.getExpForLevel(trainingCamp.getLevel()) * Experience.getTrainingRate(trainingCamp.getLevel()))) / TrainingCamp.TRAINING_DIVIDER;
							final long expGained = (long) (experience * Config.RATE_XP_BY_LVL[trainingCamp.getLevel()]);
							final long spGained = (long) ((experience * Config.RATE_SP_BY_LVL[trainingCamp.getLevel()]) / 250d);
							player.addExpAndSp(expGained, spGained, -1, -1, false, false, false, false, false);

							final SystemMessagePacket sysMsg = new SystemMessagePacket(SystemMsg.YOU_HAVE_COMPLETED_TRAINING_IN_THE_ROYAL_TRAINING_CAMP_AND_OBTAINED_S1_XP_AND_S2_SP);
							sysMsg.addLong(expGained);
							sysMsg.addLong(spGained);
							player.sendPacket(sysMsg);
	
							showChatWindow(player, "default/g_training_officer_needclear.htm", false);
						}
						else
						{
							player.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_REWARDS_FOR_TRAINING_IF_YOU_HAVE_TRAINED_FOR_LESS_THAN_1_MINUTE);
						}
					}
					else
					{
						player.sendPacket(SystemMsg.YOU_CAN_ONLY_BE_REWARDED_AS_THE_CLASS_IN_WHICH_YOU_ENTERED_THE_TRAINING_CAMP);
					}
				}
			}
		}
		else
			super.onMenuSelect(player, ask, reply);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		if(val == 0)
			showChatWindow(player, "default/g_training_officer001.htm", firstTalk);
		else
			super.showChatWindow(player, val, firstTalk, arg);
	}
}
