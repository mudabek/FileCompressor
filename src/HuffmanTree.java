import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class HuffmanTree {
	public Node root; 
	public PriorityQueue<Node> p; 
	public Map<Short, String> path;
	public boolean eofCheck;

	public class Node implements Comparable<Node>{
		public short bit; 
		public int freq;
		public Node left;
		public Node right;

		public Node(short bit, int freq) { 
			this.bit = bit; 
			this.freq = freq; 
			this.left = null;
			this.right = null;
		}

		public Node (short bit, int freq, Node left, Node right) {
			this.bit = bit;
			this.freq = freq;
			this.left = left;
			this.right = right;
		}

		public int compareTo (Node n) {
			if (this.freq == n.freq) {
				return 0;
			} else {
				return this.freq > n.freq? 1:-1;
			}	
		}
	}


	public HuffmanTree(Map<Short, Integer> m) {
		p = new PriorityQueue<Node>();
		Set<Short> s = m.keySet();
		Iterator<Short> iter = s.iterator();

		while (iter.hasNext()) {
			short key = (short) iter.next(); 
			int freq = (int) m.get(key);
			p.add(new Node(key, freq));
		}
		p.add (new Node ((short) 256, 1)); 

		p = addNodes(p);
		root = p.peek();
		eofCheck = true;
	}

	/**
	 * Recursive function which helps to build a huffman tree 
	 * @param p, a PriorityQueue that contains Nodes that contains values and its frequencies. 
	 * @return a PriorityQueue that has been modified for building huffman tree
	 */
	public PriorityQueue<Node> addNodes(PriorityQueue<Node> p) {
		if (p.size() == 1) {
			return p;
		} else {
			Node n1 = p.poll();
			Node n2 = p.poll();
			Node newNode = new Node ((short) 0, n1.freq + n2.freq, n1, n2);
			p.add(newNode);
			return addNodes(p);
		}
	}

	/**
	 * Build a map including the bits and their path in the Huffman Tree. 
	 * @param cur, a root Node 
	 * @param s, a String that carries the binary path.
	 */
	public void makePathMap (Node cur, String s) {
		if (cur == null) {
			return;
		} else if (cur.right != null && cur.left != null) {
			makePathMap (cur.left, s+"0");
			makePathMap (cur.right, s+"1");
		} else {
			path.put(cur.bit, s);
		}
	}

	/**
	 * Compresses the input by converting it into smaller file using Huffman method.
	 * @param in, a BitInputStream from the file the user wishes to compress. 
	 * @param out, a BitOutputStream to the file the user wishes to save the encoded data.
	 */
	void encode(BitInputStream in, BitOutputStream out){
		path = new HashMap<>();
		encodeHelper (root);
		short bit;
		while ((bit = (short) in.readBits(8)) != -1) {
			if (path.containsKey(bit)) {
				String huffcode = path.get(bit);
				for (char c: huffcode.toCharArray()) {
					out.writeBit(c-48);
				}
			} 
		}
		String eof = path.get((short)256);
		for (char c: eof.toCharArray()) {
			out.writeBit(c-48);
		}
	}

	public void encodeHelper (Node cur) {
		String s = "";
		makePathMap (cur, s);
	}

	/**
	 * Recursively find the bit value in the huffman tree using the huffcode given from the input
	 * 	stream. 
	 * @param in, a BitInputStream
	 * @param out, a BitOutputStream
	 * @param cur, a Node
	 */
	void decodeHelper (BitInputStream in, BitOutputStream out, Node cur) {
		if (cur.right == null && cur.left == null) {
			if (cur.bit != 256) {
				out.writeBits(cur.bit, 8);
				return;
			} else {
				eofCheck = false;
				return;
			}
		}
		int bit = in.readBit(); 
		if (bit == 0) {
			decodeHelper (in, out, cur.left);
		} else if (bit == 1) {
			decodeHelper (in, out, cur.right);
		}
	}

	/**
	 * Decodes the given input using the decodeHelper. 
	 * @param in, a BitInputStream the encoded file to decode
	 * @param out, a BitOutputStream the file to which we are saving the decoded data
	 */
	void decode(BitInputStream in, BitOutputStream out){
		while (in.hasBits() && eofCheck) {
			decodeHelper (in, out, root);
		}
	}
}
