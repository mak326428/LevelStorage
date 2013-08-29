package makmods.levelstorage;

import makmods.levelstorage.armor.ItemArmorEnhancedLappack;
import makmods.levelstorage.armor.ItemArmorForcefieldChestplate;
import makmods.levelstorage.armor.ItemArmorLevitationBoots;
import makmods.levelstorage.armor.ItemArmorSupersonicLeggings;
import makmods.levelstorage.armor.ItemArmorTeslaHelmet;
import makmods.levelstorage.block.BlockAdvancedMiner;
import makmods.levelstorage.block.BlockCableSuperconductor;
import makmods.levelstorage.block.BlockMassInfuser;
import makmods.levelstorage.block.BlockMolecularHeater;
import makmods.levelstorage.block.BlockWirelessConductor;
import makmods.levelstorage.block.BlockWirelessPowerSynchronizer;
import makmods.levelstorage.block.BlockXpCharger;
import makmods.levelstorage.block.BlockXpGenerator;
import makmods.levelstorage.item.ItemAdvancedScanner;
import makmods.levelstorage.item.ItemDestructor;
import makmods.levelstorage.item.ItemElectricMagnet;
import makmods.levelstorage.item.ItemElectricSickle;
import makmods.levelstorage.item.ItemEnergeticEnrichedMatterOrb;
import makmods.levelstorage.item.ItemEnhancedDiamondDrill;
import makmods.levelstorage.item.ItemFrequencyCard;
import makmods.levelstorage.item.ItemLevelStorageBook;
import makmods.levelstorage.item.ItemPocketRefrigerant;
import makmods.levelstorage.item.ItemPortableTeleporter;
import makmods.levelstorage.item.ItemQuantumRing;
import makmods.levelstorage.item.ItemQuantumSaber;
import makmods.levelstorage.item.ItemRemoteAccessor;
import makmods.levelstorage.item.ItemSuperconductor;
import makmods.levelstorage.item.ItemTimeAccelerator;
import makmods.levelstorage.item.ItemWirelessCharger;

/**
 * Contains all the items and blocks mod has. These are initialized dynamically
 * via {@link ModUniversalInitializer} during LevelStorage's init-phase. Block's
 * type name MUST start with Block
 * 
 * @author mak326428
 * 
 */
public class LSBlockItemList {
	// Blocks
	public static BlockXpGenerator blockXpGen;
	public static BlockXpCharger blockXpCharger;
	public static BlockWirelessConductor blockWlessConductor;
	public static BlockWirelessPowerSynchronizer blockWlessPowerSync;
	public static BlockCableSuperconductor blockSuperconductor;
	// public static BlockMassInfuser blockMassInfuser;
	public static BlockAdvancedMiner blockAdvMiner;
	public static BlockMolecularHeater blockMolHeater;

	// Items
	public static ItemLevelStorageBook itemLevelStorageBook;
	public static ItemAdvancedScanner itemAdvScanner;
	public static ItemFrequencyCard itemFreqCard;
	public static ItemPortableTeleporter itemCompactTeleporter;
	public static ItemEnhancedDiamondDrill itemEnhDiamondDrill;
	public static ItemPocketRefrigerant itemPocketRefrigerant;
	public static ItemArmorLevitationBoots itemLevitationBoots;
	public static ItemArmorSupersonicLeggings itemSupersonicLeggings;
	public static ItemArmorForcefieldChestplate itemArmorForcefieldChestplate;
	public static ItemArmorTeslaHelmet itemArmorTeslaHelmet;
	public static ItemSuperconductor itemSuperconductor;
	public static ItemEnergeticEnrichedMatterOrb itemStorageFourMillion;
	public static ItemWirelessCharger itemWirelessCharger;
	public static ItemElectricSickle itemElectricSickle;
	public static ItemQuantumSaber itemQuantumSaber;
	public static ItemTimeAccelerator itemTimeAccelerator;
	public static ItemQuantumRing itemQuantumRing;
	public static ItemElectricMagnet itemElectricMagnet;
	public static ItemDestructor itemDestructor;
	public static ItemArmorEnhancedLappack itemEnhLappack;
	public static ItemRemoteAccessor itemRemoteAccessor;
}
