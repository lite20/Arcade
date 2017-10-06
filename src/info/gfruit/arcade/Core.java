package info.gfruit.arcade;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import info.gfruit.arcade.misc.TextArt;

public class Core {

	public static final String version = "0.0.1 alpha";

	public static boolean debug = true;

	public static void main(String[] args) {
		if (args.length == 1 && args[0] == "--version") {
			System.out.println(TextArt.title);
			System.out.println(TextArt.version);
		} else if (args.length > 0) {
			File input = new File(args[0]);
			Preprocessor preproc = null;

			try {
				preproc = new Preprocessor(input);
			} catch (FileNotFoundException e1) {
				System.out.println("The file " + args[0] + " does not exist.");
				System.out.println("Usage: arcade <filename> [-param, -param, ...]");
				System.exit(1);
			}

			System.out.println(TextArt.title);
			System.out.println(TextArt.version);
			for (int i = 1; i < args.length; i++) {
				if (args[i].contains("-o")) {
					// increment "i" to now point to the next argument
					i++;
					// only set the path if it's been provided. Fail otherwise
					if (args.length > i) {
						preproc.setOutputFile(new File(args[i]));
						System.out.println("Output path: " + args[i]);
					} else {
						System.out.println("Please specify a filename for the \"-o\" parameter!");
						System.out.println("Usage: arcade <filename> -o <outfile>");
						System.exit(2);
					}
				} else {
					System.out.println("Unknown parameter " + args[i]);
					break;
				}
			}

			try {
				preproc.process();
				System.out.println(TextArt.devider);
				System.out.println("Parsing complete");
				System.out.println("> " + preproc.fork().size() + " functions parsed.");
				System.out.println("> " + preproc.constants.size() + " constants extracted.");
				System.out.println(TextArt.devider);
				Generator.generate(preproc);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Usage: arcade <filename> [-param, -param, ...]");
			System.exit(3);
		}
	}
}
