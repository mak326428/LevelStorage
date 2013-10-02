package makmods.levelstorage.math;

public class Ratio {

	final int numerator;
	final int denominator;
	
	public Ratio(int numerator, int denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
	}

	public int getNumerator() {
		return numerator;
	}

	public int getDenominator() {
		return denominator;
	}
}
