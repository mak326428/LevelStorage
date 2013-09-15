package makmods.levelstorage.proxy;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorageCreativeTab;
import makmods.levelstorage.lib.Reference;
import makmods.levelstorage.render.ItemWirelessConductorRender;
import makmods.levelstorage.render.RenderSuperconductorCable;
import makmods.levelstorage.render.WirelessConductorRender;
import makmods.levelstorage.tileentity.TileEntityWirelessConductor;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ClientProxy extends CommonProxy {

	// Item textures
	public static final String BOOK_TEXTURE = getTexturePathFor("itemLevelBook");
	public static final String ADV_SCANNER_TEXTURE = "ic2:itemScannerAdv";
	public static final String COMPACT_TELEPORTER_TEXTURE = getTexturePathFor("itemCompactTeleporter");
	public static final String ITEM_ENHANCED_DIAMOND_DRILL_PASS_ONE = getTexturePathFor("enhancedDrill_1");
	public static final String ITEM_ENHANCED_DIAMOND_DRILL_PASS_TWO = getTexturePathFor("enhancedDrill_2");
	public static final String FREQUENCY_CARD_TEXTURE = getTexturePathFor("itemFreqCard");
	public static final String SUPERSONIC_LEGGINGS_TEXTURE = getTexturePathFor("itemArmorSupersonicLeggings");
	public static final String POCKET_REFRIGERANT_TEXTURE = getTexturePathFor("itemPocketRefrigerant");
	public static final String LEVITATION_BOOTS_TEXTURE = getTexturePathFor("itemArmorLevitationBoots");
	public static final String FORCEFIELD_CHESTPLATE_TEXTURE = getTexturePathFor("itemArmorForcefieldChestplate");
	public static final String TESLA_HELMET_TEXTURE = getTexturePathFor("itemArmorTeslaHelmet");
	public static final String ITEM_SUPERCONDUCTOR_TEXTURE = getTexturePathFor("itemSuperconductor");
	public static final String WIRELESS_CHARGER_TEXTURE = getTexturePathFor("itemWirelessCharger");
	public static final String ENERGETIC_ENRICHED_MATTER_ORB_TEXTURE = getTexturePathFor("itemEnergeticEnrichedMatterOrb");
	public static final String ELECTRIC_SICKLE_TEXTURE = getTexturePathFor("itemElectricSickle");
	public static final String QUANTUM_SABER_TEXTURE = getTexturePathFor("itemQuantumSaber");
	public static final String TIME_ACCELERATOR_TEXTURE = getTexturePathFor("itemTimeAccelerator");
	public static final String QUANTUM_RING_TEXTURE = getTexturePathFor("itemQuantumRing");
	public static final String ELECTRIC_MAGNET_TEXTURE = getTexturePathFor("itemElectricMagnet");
	public static final String DESTRUCTOR_TEXTURE = getTexturePathFor("itemDestructor");
	public static final String ENHANCED_LAPPACK_TEXTURE = getTexturePathFor("itemEnhLappack");
	public static final String REMOTE_ACESSOR_TEXTURE = getTexturePathFor("itemRemoteAccessor");
	public static final String CAPACIOUS_FLUID_CELL_TEXTURE = getTexturePathFor("itemCapaciousFluidCell");
	public static final String ELECTRIC_LIGHTER_TEXTURE = getTexturePathFor("itemElectricLighter");

	// Block textures
	public static final String XP_GEN_TEXTURE = getTexturePathFor("blockXpGen");
	public static final String XP_CHARGER_TEXTURE = getTexturePathFor("blockXpCharger");
	public static final String WIRELESS_POWER_SYNC_TEXTURE = getTexturePathFor("blockWirelessPSync");
	public static final String BLOCK_SUPERCONDUCTOR_TEXTURE = getTexturePathFor("blockSuperconductorCable");
	public static final String MULTINUKE_CORE_TEXTURE = getTexturePathFor("blockMultinukeCore");
	public static final String MULTINUKE_CHAMBER_TEXTURE = getTexturePathFor("blockMultinukeChamber");
	public static final String ADV_MINER_TEXTURE = getTexturePathFor("blockAdvMiner");
	public static final String MOLECULAR_HEATER_TEXTURE = "blockMolHeater";
	public static final String MOLECULAR_HEATER_FACING = getTexturePathFor("blockMolHeaterFacing");
	public static final String ATOMIC_REPLICATOR_TEXTURE = "blockAtomicReplicator";
	public static final String ATOMIC_REPLICATOR_FACING = getTexturePathFor("blockAtomicReplicatorFacing");
	public static final String MULTICORE_SOLAR_PANEL_TEXTURE = "blockMulticoreSolarPanel";
	public static final String ANTIMATTER_STONE_TEXTURE = getTexturePathFor("blockAntimatterStone");

	// Fluids
	public static final String FLUID_ELECTROLYTE_TEXTURE = getTexturePathFor("electrolyte_still");

	// GUIs textures
	public static final ResourceLocation GUI_SINGLE_SLOT = getResourceLocation("gui/singleSlot.png");
	public static final ResourceLocation GUI_CHARGER = getResourceLocation("gui/charger.png");
	public static final ResourceLocation GUI_CHARGER_NO_UUM = getResourceLocation("gui/chargeroutd.png");
	public static final ResourceLocation GUI_NO_SLOTS = getResourceLocation("gui/noSlots.png");
	public static final ResourceLocation GUI_MASS_INFUSER = getResourceLocation("gui/massInfuser.png");
	public static final ResourceLocation GUI_MOLECULAR_HEATER = getResourceLocation("gui/molecularHeater.png");
	public static final ResourceLocation GUI_ATOMIC_REPLICATOR = getResourceLocation("gui/atomicReplicator.png");
	public static final ResourceLocation GUI_MINER = new ResourceLocation(
			"ic2", "textures/gui/GUIMiner.png");

	// Models
	public static final ResourceLocation CONDUCTOR_MODEL = getResourceLocation("model/WirelessConductorModel.png");
	public static final ResourceLocation TESLA_RAY_MODEL = getResourceLocation("model/teslaRay.png");
	public static final ResourceLocation MASS_INFUSER_MODEL = getResourceLocation("model/MassInfuserModel.png");
	public static final ResourceLocation INV_PROVIDER_MODEL = getResourceLocation("model/ModelProviderTexture.png");

	public static final String ARMOR_SUPERSONIC_LEGGINGS_TEXTURE = "/textures/models/armor/supersonic_layer_2.png";
	public static final String ARMOR_LEVITATION_BOOTS_TEXTURE = "/textures/models/armor/supersonic_layer_1.png";

	// Custom renders
	public static final int CABLE_RENDER_ID = RenderingRegistry
			.getNextAvailableRenderId();
	public static final ResourceLocation TESLA_RAY_1 = getResourceLocation("misc/tesla.png");

	@SideOnly(Side.CLIENT)
	public static CreativeTabs getCreativeTab(String name) {
		for (CreativeTabs t : CreativeTabs.creativeTabArray) {
			if (t.getTabLabel() == name)
				return t;
		}
		return null;
	}

	public int getArmorIndexFor(String forWhat) {
		if (forWhat == SUPERSONIC_DUMMY)
			return ARMOR_SUPERSONIC_RENDER_INDEX;
		else if (forWhat == ENH_LAPPACK_DUMMY)
			return ARMOR_ENHANCED_LAPPACK_RENDER_INDEX;
		return 0;
	}

	@SideOnly(Side.CLIENT)
	public static String getTexturePathFor(String name) {
		return Reference.MOD_ID.toLowerCase() + ":" + name;
	}

	@SideOnly(Side.CLIENT)
	public static ResourceLocation getResourceLocation(String path) {

		return new ResourceLocation(Reference.MOD_ID.toLowerCase(), path);
	}

	@Override
	public void init() {
		RenderingRegistry.registerBlockHandler(new RenderSuperconductorCable());
		ARMOR_SUPERSONIC_RENDER_INDEX = RenderingRegistry
				.addNewArmourRendererPrefix("supersonic");
		ARMOR_ENHANCED_LAPPACK_RENDER_INDEX = RenderingRegistry
				.addNewArmourRendererPrefix("enhlappack");
		LevelStorageCreativeTab.instance = new LevelStorageCreativeTab();
		super.init();
		ClientRegistry.bindTileEntitySpecialRenderer(
				TileEntityWirelessConductor.class,
				new WirelessConductorRender());
		MinecraftForgeClient.registerItemRenderer(
				LSBlockItemList.blockWlessConductor.blockID,
				new ItemWirelessConductorRender());
		// MinecraftForge.EVENT_BUS.register((new RenderOreRadar()));
	}

	@Override
	public void messagePlayer(EntityPlayer player, String message, Object[] args) {
		Minecraft.getMinecraft().ingameGUI.getChatGUI().addTranslatedMessage(
				message, args);
	}
}