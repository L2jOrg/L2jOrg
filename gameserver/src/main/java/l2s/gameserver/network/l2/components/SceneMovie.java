package l2s.gameserver.network.l2.components;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExStartScenePlayer;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;

/**
 * @author VISTALL
 * @date  12:51/29.12.2010
 */
public enum SceneMovie implements IBroadcastPacket
{
	LINDVIOR_SPAWN(1, 45500, false),

	// Echmus
	ECHMUS_OPENING(2, 62000, false),
	ECHMUS_SUCCESS(3, 18000, false),
	ECHMUS_FAIL(4, 17000, false),

	// Tiat
	TIAT_OPENING(5, 54200, false),
	TIAT_SUCCESS(6, 26100, false),
	TIAT_FAIL(7, 24800, false),

	// Seven Signs Quests
	SSQ_SERIES_OF_DOUBT(8, 26000, false),
	SSQ_DYING_MESSAGE(9, 27000, false),
	SSQ_MAMMONS_CONTRACT(10, 98000, false),
	SSQ_SECRET_RITUAL_PRIEST(11, 30000, false),
	SSQ_SEAL_EMPEROR_1(12, 18000, false),
	SSQ_SEAL_EMPEROR_2(13, 26000, false),
	SSQ_EMBRYO(14, 28000, false),

	// Freya
	FREYA_OPENING(15, 53500, false),
	FREYA_PHASE_CHANGE_A(16, 21100, false),
	FREYA_PHASE_CHANGE_B(17, 21500, false),
	KEGOR_INTRUSION(18, 27000, false),
	FREYA_ENDING_A(19, 16000, false),
	FREYA_ENDING_B(20, 56000, false),
	FREYA_FORCED_DEFEAT(21, 21000, false),
	FREYA_DEFEAT(22, 20500, false),
	ICE_HEAVY_KNIGHT_SPAWN(23, 7000, false),

	// High Five Seven Signs Quests
	SSQ2_HOLY_BURIAL_GROUND_OPENING(24, 23000, false),
	SSQ2_HOLY_BURIAL_GROUND_CLOSING(25, 22000, false),
	SSQ2_SOLINA_TOMB_OPENING(26, 25000, false),
	SSQ2_SOLINA_TOMB_CLOSING(27, 15000, false),
	SSQ2_ELYSS_NARRATION(28, 59000, false),
	SSQ2_BOSS_OPENING(29, 60000, false),
	SSQ2_BOSS_CLOSING(30, 60000, false),

	SCENE_ISTINA_OPENING(31, 36700, false),
	SCENE_ISTINA_ENDING_A(32, 23300, false),
	SCENE_ISTINA_ENDING_B(33, 22200, false),
	SCENE_ISTINA_BRIDGE(34, 7200, false),

	SCENE_OCTABIS_OPENING(35, 26600, false),
	SCENE_OCTABIS_phasech_A(36, 10000, false),
	SCENE_OCTABIS_phasech_B(37, 14000, false),
	SCENE_OCTABIS_ENDING(38, 38000, false),

	SCENE_GD1_PROLOGUE(42, 64000, false),

	SCENE_TALKING_ISLAND_BOSS_OPENING(43, 47430, false),
	SCENE_TALKING_ISLAND_BOSS_ENDING(44, 32040, false),

	SCENE_AWAKENING_OPENING(45, 27000, false),
	SCENE_AWAKENING_BOSS_OPENING(46, 29950, false),
	SCENE_AWAKENING_BOSS_ENDING_A(47, 25050, false),
	SCENE_AWAKENING_BOSS_ENDING_B(48, 13100, false),

	SCENE_EARTHWORM_ENDING(49, 32600, false),

	SCENE_SPACIA_OPENING(50, 38600, false),
	SCENE_SPACIA_A(51, 29500, false),
	SCENE_SPACIA_B(52, 45000, false),
	SCENE_SPACIA_C(53, 36000, false),
	SCENE_SPACIA_ENDING(54, 23000, false),

	SCENE_AWEKENING_VIEW(55, 34000, true),

	SCENE_AWAKENING_OPENING_C(56, 28500, false),
	SCENE_AWAKENING_OPENING_D(57, 20000, false),
	SCENE_AWAKENING_OPENING_E(58, 24000, false),
	SCENE_AWAKENING_OPENING_F(59, 38100, false),

	SCENE_TAUTI_OPENING_B(69, 15000, false),
	SCENE_TAUTI_OPENING(70, 15000, false),
	SCENE_TAUTI_PHASE(71, 15000, false),
	SCENE_TAUTI_ENDING(72, 15000, false),
	SC_SUB_QUEST(75, 25000, false),
	LINDVIOR_ARRIVE(76, 10000, false),
	SCENE_KATACOMB(77, 34000, false),
	SCENE_NECRO(78, 34000, false),
	SCENE_HELLBOUND(79, 40000, false),
	SCENE_NOBLE_OPENING(99, 10000, false),
	SCENE_NOBLE_ENDING(100, 10000, false),

	SINEMA_ILLUSION_01_QUE(101, 29200, true),
	SINEMA_ILLUSION_02_QUE(102, 27150, true),
	SINEMA_ILLUSION_03_QUE(103, 16100, true),

	SINEMA_ARKAN_ENTER(104, 30300, true),
	SINEMA_BARLOG_OPENING(105, 19300, false),
	SINEMA_BARLOG_STORY(106, 67500, false),
	SINEMA_ILLUSION_04_QUE(107, 10100, false),
	SINEMA_ILLUSION_05_QUE(108, 10100, false),
	SCENE_BLOODVEIN_OPENING(109, 13000, false),

	ERTHEIA_QUEST_A(110, 9500, false),
	ERTHEIA_QUEST_B(111, 23500, false),

	EPIC_FREYA_SLIDE(112, 19000, false),
	EPIC_KELBIM_SLIDE(113, 19000, false),
	EPIC_TAUTI_SLIDE(114, 19000, false),
	EPIC_FREYA_SCENE(115, 26000, false),
	EPIC_KELBIM_SCENE(116, 26000, false),
	EPIC_TAUTI_SCENE(117, 48500, false),

	// Airship
	LANDING_KSERTH_LEFT(1000, 10000, false),
	LANDING_KSERTH_RIGHT(1001, 10000, false),
	LANDING_INFINITY(1002, 10000, false),
	LANDING_DESTRUCTION(1003, 10000, false),
	LANDING_ANNIHILATION(1004, 15000, false),
	BR_G_CARTIA_1_SIN(2001, 17800, false),
	BR_G_CARTIA_2_SIN(2002, 15800, false);

	public static final SceneMovie[] VALUES = values();

	private final int _id;
	private final int _duration;
	private final boolean _cancellable;
	private final L2GameServerPacket _static;

	SceneMovie(int id, int duration, boolean cancellable)
	{
		_id = id;
		_duration = duration;
		_cancellable = cancellable;
		_static = new ExStartScenePlayer(id);
	}

	public int getId()
	{
		return _id;
	}

	public int getDuration()
	{
		return _duration;
	}

	public boolean isCancellable()
	{
		return _cancellable;
	}

	@Override
	public L2GameServerPacket packet(Player player)
	{
		return _static;
	}

	public static SceneMovie getMovie(int id)
	{
		for(SceneMovie movie : VALUES)
		{
			if(movie.getId() == id)
				return movie;
		}
		return null;
	}
}
