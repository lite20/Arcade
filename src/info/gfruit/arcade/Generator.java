package info.gfruit.arcade;

import java.util.Map;

import info.gfruit.arcade.structures.ASMBlock;

public class Generator {

	public static void generate(Preprocessor proc) {
		ASMBlock block;

		System.out.println("section .text");
		System.out.println("    global _init" + System.lineSeparator());

		for (Map.Entry<String, ASMBlock> entry : proc.fork().entrySet()) {
			block = entry.getValue();
			System.out.println(block.getName() + ":");
			for (int i = 0; i < block.getThreadCount(); i++) {
				System.out.println(block.serializeBlock(i));
			}
		}

		System.out.println("section .data");
		for (int i = 0; i < proc.constants.size(); i++) {
			System.out.println(proc.constants.get(i).serialize());
		}
	}
}
