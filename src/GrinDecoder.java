import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GrinDecoder {

	public static void decode(String infile, String outfile) throws IOException { 
		BitInputStream in = new BitInputStream (infile);
		BitOutputStream out = new BitOutputStream (outfile);

		int magic = in.readBits(32); 
		if (magic != 1846) {
			throw new IllegalArgumentException(); 
		}

		int leaves = in.readBits(32);

		Map<Short, Integer> huffMap = new HashMap<>(); 

		for (int i = 0; i < leaves-1; i++) {
			short value = (short) in.readBits(16);
			int freq = in.readBits(32);
			huffMap.put(value, freq); 
		}

		HuffmanTree h = new HuffmanTree(huffMap);

		h.decode(in, out);

		in.close();
		out.close();
	}
}

