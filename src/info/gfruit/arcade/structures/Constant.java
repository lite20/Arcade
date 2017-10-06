package info.gfruit.arcade.structures;

public class Constant {

	String id;
	String value;

	public Constant(String constant_id, String value) {
		this.id = constant_id;
		this.value = value;
	}

	public String serialize() {
		return "CONSTANT";
	}

}
