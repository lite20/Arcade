package info.gfruit.arcade.misc;

public class TermCounter {
	private static char alphabet[] = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
			'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

	public int position = -1;

	public TermCounter() {

	}

	public String nextTerm() {
		position++;
		return strFromNumber(position);
	}

	/*
	 * Takes in a number and returns its representation in base-26 (based off
	 * alphabet)
	 */
	private String strFromNumber(int number) {
		StringBuilder result = new StringBuilder();
		int id = 0;
		while (number > 0) {
			id++;
			number--;
			if (id == alphabet.length) {
				id = 0;
				result.append('z');
			}
		}

		result.append(alphabet[id]);
		return result.toString();
	}
}
