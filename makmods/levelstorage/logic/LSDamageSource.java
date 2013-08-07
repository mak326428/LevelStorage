package makmods.levelstorage.logic;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.DamageSource;

public class LSDamageSource extends DamageSource {

	public static LSDamageSource energyField = (new LSDamageSource(
			"energyFieldKill")
			.setKillMessage("was in radius of high energy field."));

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

	protected LSDamageSource(String par1Str) {
		super(par1Str);
	}

	public LSDamageSource setKillMessage(String message) {
		this.killMessage = message;
		return this;
	}
}
