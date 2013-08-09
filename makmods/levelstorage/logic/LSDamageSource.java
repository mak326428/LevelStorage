package makmods.levelstorage.logic;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.DamageSource;

public class LSDamageSource extends DamageSource {

	public static LSDamageSource energyField = (new LSDamageSource(
			"energyFieldKill", "was in radius of high energy field."));
	public static LSDamageSource forcefieldArmor = (new LSDamageSource(
			"energyFieldKill", "was zapped by a huge impulse of energy."));
	public static DamageSource forcefieldArmorInstaKill = (DamageSource)((new LSDamageSource(
			"energyFieldKill", "was zapped by a huge impulse of energy."))).setDamageBypassesArmor();
	public static DamageSource teslaRay = (DamageSource)((new LSDamageSource(
			"energyFieldKill", "was zapped by a huge impulse of energy caused by a tesla ray."))).setDamageBypassesArmor();
	
	private String killMessage;

	@Override
	public ChatMessageComponent getDeathMessage(
			EntityLivingBase par1EntityLivingBase) {
		if (par1EntityLivingBase instanceof EntityPlayer)
			return ChatMessageComponent
					.func_111066_d(((EntityPlayer) par1EntityLivingBase).username
							+ " " + this.killMessage);
		return ChatMessageComponent.func_111066_d(this.killMessage);
	}

	protected LSDamageSource(String par1Str, String killMessage) {
		super(par1Str);
		this.setKillMessage(killMessage);
	}

	public LSDamageSource setKillMessage(String message) {
		this.killMessage = message;
		return this;
	}
}
