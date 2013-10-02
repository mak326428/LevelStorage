package makmods.levelstorage.substance;

import makmods.levelstorage.math.Ratio;

public enum SubstancePrefix {
	INGOT(1, 1), NUGGET(1, 9), GEM(1, 9), ORE(2, 1), DUST(1, 1);
	
	final Ratio ratio;
	final String prefix;
	
	private SubstancePrefix(int numerator, int denominator) {
		this.ratio = new Ratio(numerator, denominator);
		this.prefix = name().toLowerCase();
	}
	
	public Ratio getRatio() {
		return ratio;
	}
	
	public String getPrefix() {
		return prefix;
	}
}
