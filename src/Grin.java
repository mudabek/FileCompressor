import java.io.IOException;

public class Grin {
	public static void main(String[] args) throws IOException {

		if (args.length != 3) {
			throw new IllegalArgumentException("Must specify command, infile, and outfile.");
		}

		String command = args[0];
		String infile = args[1];
		String outfile = args[2];

		switch (command) {
		case "encode":
			GrinEncoder.encode (infile, outfile);
			break;
		case "decode":
			GrinDecoder.decode (infile, outfile);
			break;
		default:
			throw new IllegalArgumentException("Enter encode or decode");
		}
	}
}
