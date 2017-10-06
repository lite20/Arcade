package info.gfruit.arcade.structures;

import java.util.ArrayList;

public class InstructionSet {

	public ArrayList<String> instructions = new ArrayList<String>();

	public InstructionSet() {

	}

	public void addInstruction(String instruction) {
		instructions.add(instruction);
	}

}
