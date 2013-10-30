package makmods.levelstorage.init;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.minecraft.item.Item;

/**
 * <p>Tells the ModUniversalInitializer to only register this item/block
 * when a specific mod is installed</p>
 * 
 * <p>Example: <pre>@RegisterWith("Thaumcraft")<br />public class ItemSuperEpicThingy extends Item {</pre>
 * In this example, ModUniversalInitializer will only register and load the item/block when Thaumcraft is installed.</p>
 * 
 * @author mak326428
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RegisterWith {
	String value();
}
