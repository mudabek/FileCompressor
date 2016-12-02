import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class GrinEncoder {

	static Map<Short, Integer> createFrequencyMap(String file) throws IOException {
		BitInputStream in = new BitInputStream (file);
		Map<Short, Integer> huffMap = new HashMap<>(); 

		while (in.hasBits()) {
			short val = (short) in.readBits(8);
			if (huffMap.containsKey(val)) {
				int freq = huffMap.get(val);
				huffMap.put(val, freq + 1);
			} else {
				huffMap.put(val, 1);
			}
		}
		return huffMap;
	}

	static void encode(String infile, String outfile) throws IOException {
		BitInputStream in = new BitInputStream (infile);
		BitOutputStream out = new BitOutputStream (outfile);
		Map<Short, Integer> huffMap = createFrequencyMap(infile);

		out.writeBits((short)1846, 32);
		out.writeBits((short) huffMap.size()+1, 32);

		Set<Short> keySet = huffMap.keySet();
		Iterator<Short> iter = keySet.iterator();

		while (iter.hasNext()) {
			short key = (short) iter.next();
			out.writeBits(key, 16);
			int freq = huffMap.get(key);
			out.writeBits(freq, 32);
		}

		HuffmanTree h = new HuffmanTree(huffMap);

		h.encode(in, out);
		in.close();
		out.close();
	}
}