package makmods.levelstorage.substance;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * Registry for Substances
 * @author mak326428
 *
 */
public class SubstanceRegistry {
	
	private static Set<Substance> registry = Sets.newHashSet();

	private SubstanceRegistry() {
		;
	}
	
	public static void registerSubstance(Substance s) {
		registry.add(s);
	}
	
	public static void removeSubstance(Substance s) {
		registry.remove(s);
	}
	
	public static Substance getSubstanceByName(String name) {
		for (Substance s : registry) {
			try {
				//if (name.equals(anObject))
			} catch (Throwable t) {
				// Shut up
			}
		}
		return null;
	}

}
