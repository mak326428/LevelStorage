package makmods.levelstorage.substance;

import java.util.EnumSet;

public enum Substance {
	// PRIMALS
	EXPENSIVE, STRONG, FRAGILE,
	// COMPOUNDS
	FLEXIBLE(STRONG, FRAGILE),
	TITANIUM(FLEXIBLE);

	private final Substance[] components;
	private final String name;

	public Substance[] getComponents() {
		return components;
	}

	private Substance() {
		this.components = null;
		this.name = upperFirstLetter(name().toLowerCase());
		System.out.println(name);
	}

	private Substance(Substance... properties) {
		this.components = properties;
		this.name = upperFirstLetter(name().toLowerCase());
	}

	private String upperFirstLetter(String s) {
		if (s.length() > 1) {
			String firstLetter = s.substring(0, 1);
			String otherLetters = s.substring(1, s.length());
			return firstLetter.toUpperCase() + otherLetters.toLowerCase();
		}
		return "";
	}

}
