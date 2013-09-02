package makmods.levelstorage.logic;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;

public class LSDamageSource extends DamageSource {

	public static LSDamageSource energyField = (new LSDamageSource(
	        "energyFieldKill"));
	public static LSDamageSource forcefieldArmor = (new LSDamageSource(
	        "forcefieldArmor"));
	public static DamageSource forcefieldArmorInstaKill = (DamageSource) ((new LSDamageSource(
	        "forcefieldArmorInstantKill"))).setDamageBypassesArmor();
	public static DamageSource teslaRay = (DamageSource) ((new LSDamageSource(
	        "teslaHelmetKill"))).setDamageBypassesArmor();
	public static DamageSource disassembled = (DamageSource) ((new LSDamageSource(
	        "disassemble"))).setDamageBypassesArmor();


	private String killMessage;

	@Override
	public ChatMessageComponent getDeathMessage(
	        EntityLivingBase par1EntityLivingBase) {
		if (par1EntityLivingBase instanceof EntityPlayer)
			return ChatMessageComponent.func_111066_d(String.format(
			        StatCollector.translateToLocal(this.killMessage),
			        ((EntityPlayer) par1EntityLivingBase).username));
		return ChatMessageComponent.func_111066_d(this.killMessage);
	}

	protected LSDamageSource(String par1Str) {
		super(par1Str);
		this.setKillMessage("death." + par1Str);
	}

	private LSDamageSource setKillMessage(String message) {
		this.killMessage = message;
		return this;
	}
}
