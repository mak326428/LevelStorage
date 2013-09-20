package makmods.levelstorage.init;

import makmods.levelstorage.LSBlockItemList;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ModAchievements {

	public static final ModAchievements instance = new ModAchievements();

	public AchievementPage LEVEL_STORAGE_ACHIEVEMENT_PAGE = null;

	// ACHIEVEMENTS THEMSELVES
	public Achievement craftHelmet = null;

	// END

	private ModAchievements() {
		;
	}

	public void init() {
		/*
		 * Achievement[] achs = new Achievement[ACHIEVEMENTS_LIMIT]; Field[] f =
		 * this.getClass().getDeclaredFields(); ArrayList<Field> okayAchs =
		 * Lists.newArrayList(); for (Field f2 : f) { if (f2 != null) if
		 * (f2.getType().getCanonicalName() == Achievement.class
		 * .getCanonicalName()) { okayAchs.add(f2); } } for (int index = 0;
		 * index < okayAchs.size(); index++) { try { achs[index] = (Achievement)
		 * okayAchs.get(index).get(instance); } catch (Exception e) {
		 * FMLLog.severe(Reference.MOD_NAME +
		 * ": failed to register achievement"); e.printStackTrace(); } }
		 */
		craftHelmet = new Achievement(736781, "tesla", 3, -4, new ItemStack(
		        LSBlockItemList.itemArmorTeslaHelmet), null)
		        .registerAchievement();
		LanguageRegistry.instance().addStringLocalization("achievement.tesla",
		        "Rays, rays everywhere!");
		LanguageRegistry.instance().addStringLocalization(
		        "achievement.tesla.desc", "Craft a tesla helmet.");
		LEVEL_STORAGE_ACHIEVEMENT_PAGE = new AchievementPage("LevelStorage");
		AchievementPage.registerAchievementPage(LEVEL_STORAGE_ACHIEVEMENT_PAGE);
	}
}
