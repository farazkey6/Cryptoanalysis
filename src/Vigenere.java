import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Vigenere {

	public static void main(String[] args) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Please enter your cipher text : ");
		String cipher = "";
		try {
			cipher = reader.readLine();
		} catch (IOException e) {
			System.out.println("Invalid input!");
			e.printStackTrace();
			System.exit(1);
		}
		String test_cipher = "PATGSJKGSPFPCTSSKHOIGSDHNBCUHVIHKSHVBKPBQLEGVFSHPLTQFLYRWSRLYBSSRPPPPGIUOTUSHVPTZSVLNBCHCIWMIZSZKPWWZLZKXJWUCMWFCBCAACBKKGDBHOAPPMHVBKPBQLDXKWGPPXSZCUZHCNCVWGSOGRAWIVSTPHROFLBHGHVLYNQIBAEEWWGYAMJFBDDBRVVLKIIWAPOMXQOSHRPBHPYBEOHLZPDIZKXXCCZVJZTFHOWGIKCDAXZGCMYHJFGLPATKOYSTHBCAPHTBRZKJJWQRHR";
		System.out.println("Hint: Possible key sizes along with their frequency according to Kasiski test: "
				+ Kasiski(cipher, 5, 10));
		System.out.println("Please enter minimum key size: ");
		int minKey = 2;
		try {
			minKey = Integer.parseInt(reader.readLine());
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Please enter maximum key size: ");
		int maxKey = 20;
		try {
			maxKey = Integer.parseInt(reader.readLine());
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int[] keys = EstimateKeySize(cipher, minKey, maxKey);
		System.out.println(
				"By calculating the index of coincidence, top 3 candidates for key lengths are: (ordered by likelihood)");
		for (int x : keys) {
			System.out.println("Candidate key length: " + x);
		}
		System.out.println("Please enter your candid key size (The top result is usually the best choice): ");
		int chosenKey = 2;
		try {
			chosenKey = Integer.parseInt(reader.readLine());
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("The Possible decrypted cipher is as followed: ");
		System.out.println(Friedman(cipher, chosenKey));

	}

	// Friedman function takes the cipher and keysizes and attempts to break it ->
	// cracked
	public static String Friedman(String cipher, int keysize) {

		String cracked = "";
		String keyword = "";

		int key = keysize;
		String[] container = new String[key];
		String[] stabbed = new String[key]; // result of a row in container getting caesered
		for (int j = 0; j < container.length; j++) { // empty the string array for a new key size

			container[j] = "";
		}
		for (int j = 0; j < cipher.length(); j++) {

			container[Math.floorMod(j, key)] += cipher.charAt(j);
		}

		for (int row = 0; row < container.length; row++) {

			String[] temp = Caeser(container[row]);
			stabbed[row] = temp[0];
			keyword += temp[1];
			// System.out.println(container[row] + " --> " + stabbed[row]);
		}

		String decrypto = "";
		for (String X : stabbed) {

			decrypto += X;
		}

		cracked = Crack(decrypto, key);
		System.out.println("The Keyword is: " + keyword);

		return cracked;
	}

	// The Crack function rearranges the letters in a string to their original form
	// before being put into different bins (number of bins = key size)
	private static String Crack(String cipher, int key) {

		cipher = cipher.toUpperCase();
		String unscrambled = "";
		int N = cipher.length();
		double ix = Math.ceil((double) N / (double) key);
		int index = (int) ix;

		int holes = (index * key) - N;

		for (int i = (holes - 1); i >= 0; i--) {

			cipher = new StringBuilder(cipher).insert((key * index - 1) - (i * index), "$").toString();
		}

		for (int i = 0; i < index; i++) {
			for (int j = 0; j < key; j++) {
				int x = (j * index) + i;
				int y = index * key;
				int z = Math.floorMod(x, y);
				char c = cipher.charAt(z);
				if (c != '$') {
					unscrambled += c;
				}
			}
		}
		return unscrambled;
	}

	// matchLetterIC finds a letter with the closest IOC to another letter (removed
	// from the main algorithm)
	public static char matchLetterIC(String cipher, char c, char[] letter, double[] coincidence) {

		char key = 'A';
		double frequency = 0;
		double N = cipher.length();
		double highScore = 1;

		for (int i = 0; i < cipher.length(); i++) {
			if (cipher.charAt(i) == c) {
				frequency -= -1;
			}
		}

		double ioc = (frequency * (frequency - 1) / N * (N - 1));

		if (ioc == 0) {

			return '?';
		}

		for (int i = 0; i < letter.length; i++) {

			double score = Math.pow(ioc - coincidence[i], 2);
			if (score < highScore) {

				highScore = score;
				key = (char) letter[i];
			}

		}

		return key;
	}

	// calculates IOC of a single character in a string
	private static double LetterIC(String str, char c) {

		double count = 0;
		double N = (double) str.length();
		for (int i = 0; i < str.length(); i++) {

			if (str.charAt(i) == c) {

				count -= -1;
			}
		}
		double freq = count / N;
		return freq;
	}

	// calculates the IOC of the whole string
	private static double calcIC(String cipher) {

		cipher = cipher.toUpperCase();
		double[] frequencies = new double[26];
		for (int i = 0; i < cipher.length(); i++) {

			frequencies[cipher.charAt(i) - 'A'] -= -1;
		}

		double ioc = 0.0;
		int N = cipher.length();
		for (int i = 0; i < frequencies.length; i++) {

			ioc += (frequencies[i] * (frequencies[i] - 1)) / (N * (N - 1));
		}
		return ioc;
	}

	// Estimate most probable key sizes
	private static int[] EstimateKeySize(String cipher, int minKeySize, int MaxKeySize) {

		System.out.println("Looking for best keys between lengths of " + minKeySize + "-" + MaxKeySize);
		int[] keySizes = { 0, 0, 0 };
		double[] highScores = { 1, 1, 1, 0 };
		for (int key = minKeySize; key <= MaxKeySize; key++) { // for each key size

			String[] container = new String[key];
			double score = 0;

			for (int j = 0; j < container.length; j++) {
				container[j] = "";
			}
			for (int j = 0; j < cipher.length(); j++) {

				container[Math.floorMod(j, key)] += cipher.charAt(j);
			}

			for (int j = 0; j < container.length; j++) {

				// System.out.print("Key size of: " + key + " The " + j + " sequence is: " +
				// container[j]);
				// System.out.println(" Partial cipher size is: " + container[j].length() + "
				// IOC = " + calcIC(container[j]));
				score += Math.pow(calcIC(container[j]) - 0.065, 2);
			}

			score /= key;
			if (score < highScores[2]) {

				// System.out.println(key + " has the score of: " +score);
				highScores = Sorted_Insert(score, key, highScores, keySizes);
			}

		}

		return keySizes;
	}

	// insert an element into our top results while keeping the order
	private static double[] Sorted_Insert(double score, int candidKey, double[] highScores, int[] keys) {
		boolean done = false;
		int index = 0;
		while (!done && index < highScores.length - 1) { // last cell is reserved for the index return
			if (score < highScores[index]) {

				done = true;
			} else {

				index -= -1;
			}
		}
		if (done) {

			if (index + 1 != highScores.length - 1) {
				for (int i = highScores.length - 2; i > index; i--) {

					highScores[i] = highScores[i - 1];
					keys[i] = keys[i - 1];
				}
			}
			highScores[index] = score;
			keys[index] = candidKey;
			highScores[highScores.length - 1] = index;
		}

		return highScores;
	}

	// Kasiski test that calculates GCD
	public static HashMap<Integer, Integer> Kasiski(String cipher, int minKeyLength, int maxKeyLength) {

		List<Integer> occurances = new ArrayList<Integer>();
		List<Integer> divisors = new ArrayList<Integer>();
		HashMap<Integer, Integer> frequency = new HashMap<Integer, Integer>();

		for (int key = maxKeyLength; key > minKeyLength; key--) {

			for (int i = 0; i + key < cipher.length(); i++) {

				String temp = cipher.substring(i, i + key);
				for (int j = i + key; j + key < cipher.length(); j++) {

					if (temp.equals(cipher.substring(j, j + key))) {
						occurances.add(j - i);
					}
				}
			}
		}

		for (Integer number : occurances) {

			for (int i = 2; i < number; i++) {

				if (Math.floorMod(number, i) == 0) {
					divisors.add(i);
				}
			}
		}

		for (Integer number : divisors) {
			if (frequency.containsKey(number)) {

				frequency.replace(number, frequency.get(number) + 1);
			} else {
				frequency.put(number, 1);
			}
		}

		return sortByValue(frequency);
	}

	// simple sort for most frequent GCDs for Kasiski test
	public static LinkedHashMap<Integer, Integer> sortByValue(HashMap<Integer, Integer> hm) {
		LinkedHashMap<Integer, Integer> mapSortedBasedOnValues = hm.entrySet().stream()
				.sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		return mapSortedBasedOnValues;
	}

	// creates 26 different shifts on a single string and returns the most probable
	// one in English language
	public static String[] Caeser(String cipher) {

		final char english_letters[] = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
				'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
		final double english_freqs[] = { 0.082, 0.015, 0.028, 0.043, 0.127, 0.022, 0.020, 0.061, 0.070, 0.002, 0.008,
				0.040, 0.024, 0.067, 0.075, 0.019, 0.001, 0.060, 0.063, 0.091, 0.028, 0.010, 0.023, 0.001, 0.020,
				0.001 };

		cipher = cipher.toUpperCase();
		String stabbed[] = { "", "" };
		double highScore = 1;
		// Double.MAX_VALUE;
		char ord = 'A';

		for (int shift = 0; shift < 26; shift++) {
			String rotation = "";
			double score = 0.0;
			// ArrayList<Integer> vars = new ArrayList<>();

			for (int i = 0; i < cipher.length(); i++) {
				int ca = (int) cipher.charAt(i);
				if (ca >= 65 && ca <= 90) {

					if (ca + shift <= 90) {
						ca += shift;
					} else {

						ca += shift - 26;
					}
				}
				// vars.add((Math.floorMod(ca - matchLetterIC(cipher, (char) ca,
				// english_letters, english_freqs), english_letters.length)));
				rotation += (char) ca;

			}
			for (int i = 0; i < rotation.length(); i++) {
				char c = rotation.charAt(i);
				score += Math.pow(LetterIC(rotation, c) - english_freqs[findIndex(english_letters, c)], 2);
			}
			score /= rotation.length();
			if (score < highScore) {

				highScore = score;
				ord = (char) (shift + 65);
				stabbed[0] = rotation;
			}
		}
		stabbed[1] = String.valueOf(ord);
		return stabbed;
	}

	// calculate variance of an arraylist. no longer used in the algorithm
	public static double calcVariance(ArrayList<Integer> arr) {

		double mean = 0.0;
		for (int X : arr) {

			mean += X;
		}
		mean /= arr.size();

		double variance = 0;
		for (int X : arr) {
			variance += Math.pow(X - mean, 2);
		}
		variance /= arr.size();

		return variance;
	}

	// gets a character and returns its position in the given alphabet
	public static int findIndex(char arr[], char t) {

		ArrayList<Character> clist = new ArrayList<>();

		for (char c : arr)
			clist.add(c);

		return clist.indexOf(t);
	}
}