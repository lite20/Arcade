package info.gfruit.arcade;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import info.gfruit.arcade.misc.ParseState;
import info.gfruit.arcade.misc.TermCounter;
import info.gfruit.arcade.structures.ASMBlock;
import info.gfruit.arcade.structures.Constant;

public class Preprocessor {

	public ArrayList<Constant> constants = new ArrayList<Constant>();

	private File inputFile;
	private File outputFile;

	private BufferedReader in;

	private String funcNamespace = "";

	private HashMap<String, ASMBlock> funcs = new HashMap<String, ASMBlock>();

	private TermCounter termEngine = new TermCounter();

	private int lineNum = 0;
	private int column;

	private ASMBlock currentBlock = null;

	private ParseState state = ParseState.DEFAULT;

	private boolean inComment;
	private boolean skipToken = false;
	private boolean inConstant = false;

	private String getpath = null;
	private String namespace = null;
	private String cmd = "";
	private String constant_id;

	private StringBuilder current_constant = new StringBuilder();

	public Preprocessor(File input) throws FileNotFoundException {
		this.inputFile = input;
		this.in = new BufferedReader(new FileReader(inputFile));
	}

	public Preprocessor(File input, String namespace) throws FileNotFoundException {
		this.inputFile = input;
		this.in = new BufferedReader(new FileReader(inputFile));
		this.funcNamespace = namespace;
	}

	public void process() throws IOException {
		StringBuilder token = new StringBuilder(5);
		// read each line of the file
		for (String line = in.readLine(); line != null; line = in.readLine()) {
			column = 0;
			inComment = false;
			lineNum++;
			// process one character at a time
			for (int charNum = 0; charNum < line.length(); charNum++) {
				// handle token once complete parsing it
				if (line.charAt(charNum) == ' ' || charNum == (line.length() - 1)) {
					if (charNum == (line.length() - 1)) {
						token.append(line.charAt(charNum));
					}

					if (token.length() == 0) {
						continue;
					}

					if (skipToken) {
						skipToken = false;
						continue;
					}

					if (inComment) {
						continue;
					}

					switch (state) {
					case GET_PATH:
						getpath = token.toString();
						skipToken = true;
						state = ParseState.GET_NAMESPACE;
						break;
					case GET_NAMESPACE:
						namespace = token.toString();
						System.out.println("> Importing " + namespace + " ...");
						Preprocessor preproc = new Preprocessor(new File(getpath), namespace);
						preproc.process();
						for (Map.Entry<String, ASMBlock> entry : preproc.fork().entrySet()) {
							funcs.put(entry.getKey(), entry.getValue());
						}

						constants.addAll(preproc.constants);
						state = ParseState.DEFAULT;
						break;
					default:
						if (token.charAt(0) == '#') {
							if (token.toString().contains("#get")) {
								state = ParseState.GET_PATH;
							}
						} else if (token.charAt(0) == '|') {
							inComment = false;
							pack();
							column++;
						} else if (token.charAt(0) == ';') {
							inComment = true;
						} else if (token.charAt(token.length() - 1) == ':') {
							// end one function, start the next
							if (currentBlock != null) {
								funcs.put(currentBlock.getName(), currentBlock);
							}

							String name = funcNamespace + "_" + token.toString().substring(0, token.length() - 1);
							debug("> Creating function " + name);
							currentBlock = new ASMBlock(name);
						} else {
							cmd += token + " ";
						}

						break;
					}

					token.setLength(0);
				} else if (line.charAt(charNum) == '"') {
					if (inConstant) {
						// complete the constant
						constants.add(new Constant(constant_id, token.toString()));
						current_constant.setLength(0);
					} else {
						// initialize constant
						constant_id = "_" + termEngine.nextTerm();
						cmd += constant_id + " ";
					}

					inConstant = !inConstant;
				} else if (inConstant) {
					current_constant.append(line.charAt(charNum));
				} else {
					// construct token by adding the new character
					token.append(line.charAt(charNum));
				}
			}

			pack();
			column = 0;
		}

		// add the final function
		if (currentBlock != null) {
			funcs.put(currentBlock.getName(), currentBlock);
		}
	}

	private void debug(String msg) {
		if (Core.debug) {
			System.out.println(msg);
		}
	}

	private void pack() {
		if (currentBlock != null) {
			currentBlock.addInstruction(column, cmd);
		}

		cmd = "";
	}

	public HashMap<String, ASMBlock> fork() {
		return funcs;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}
}
