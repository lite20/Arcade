package info.gfruit.arcade.structures;

import java.util.ArrayList;

public class ASMBlock {
	public ArrayList<InstructionSet> instructions = new ArrayList<InstructionSet>();

	private String name;

	private int maxThreadId = 0;

	public ASMBlock(String name) {
		this.name = name;
	}

	public void addInstruction(int thread, String instruction) {
		if (!(instructions.size() > thread)) {
			instructions.add(new InstructionSet());
		}

		instructions.get(thread).addInstruction(instruction);
		if (thread > maxThreadId) {
			maxThreadId = thread;
		}
	}

	public int getThreadCount() {
		return maxThreadId + 1;
	}

	public String getName() {
		return name;
	}

	public String serializeBlock(int column) {
		String block = "";
		for (int i = 0; i < instructions.get(column).instructions.size(); i++) {
			block += instructions.get(column).instructions.get(i) + System.getProperty("line.separator");
		}

		return block;
	}
}
