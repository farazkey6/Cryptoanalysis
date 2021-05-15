import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Vigenere{

    public static void main(String[] args) { 
    	
    	//notes
    	//todo: use index of coincidence instead of kasiski
    	/* Map<String, Long> map = a.getSomeStringList()
    	.stream()
    	.collect(Collectors.groupingBy(
    	Function.identity(),
    	Collectors.counting())
    	); */
        
    	//final char english_letters[] = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    	//final double english_freqs[] = {0.082, 0.015, 0.028, 0.043, 0.127, 0.022, 0.020, 0.061, 0.070, 0.002, 0.008, 0.040, 0.024, 0.067, 0.075, 0.019, 0.001, 0.060, 0.063, 0.091, 0.028, 0.010, 0.023, 0.001, 0.020, 0.001};
    	String cipher = "PATGSJKGSPFPCTSSKHOIGSDHNBCUHVIHKSHVBKPBQLEGVFSHPLTQFLYRWSRLYBSSRPPPPGIUOTUSHVPTZSVLNBCHCIWMIZSZKPWWZLZKXJWUCMWFCBCAACBKKGDBHOAPPMHVBKPBQLDXKWGPPXSZCUZHCNCVWGSOGRAWIVSTPHROFLBHGHVLYNQIBAEEWWGYAMJFBDDBRVVLKIIWAPOMXQOSHRPBHPYBEOHLZPDIZKXXCCZVJZTFHOWGIKCDAXZGCMYHJFGLPATKOYSTHBCAPHTBRZKJJWQRHR";
        String decipher = "THESECONDBRIGADEWASPREPARINGTOMOVETOFRANCEINGREATSECRECYHEDECIDEDITWASUNSAFETOTAKEHERINTOBATTLESOWHILEDRIVINGTHROUGHLONDONONTHEWAYTOFRANCEHEVISITEDLONDONZOOANDASKEDTHEMTOCAREFORTHECUBUNTILHISRETURNWHICHHEOPTIMISTICALLYANTICIPATEDWOULDBENOLONGERTHANTWOWEEKSOFCOURSETHEWARWASNOTTOENDSOQUICKLY";
    	String caesar = "YMJXJHTSIGWNLFIJBFXUWJUFWNSLYTRTAJYTKWFSHJNSLWJFYXJHWJHDMJIJHNIJINYBFXZSXFKJYTYFPJMJWNSYTGFYYQJXTBMNQJIWNANSLYMWTZLMQTSITSTSYMJBFDYTKWFSHJMJANXNYJIQTSITSETTFSIFXPJIYMJRYTHFWJKTWYMJHZGZSYNQMNXWJYZWSBMNHMMJTUYNRNXYNHFQQDFSYNHNUFYJIBTZQIGJSTQTSLJWYMFSYBTBJJPXTKHTZWXJYMJBFWBFXSTYYTJSIXTVZNHPQD";
        System.out.println("Possible key sizes along with their frequency: ");
        System.out.println("Kasiski says these are the possible keys" +Kasiski(cipher, 5, 10));
        int[] keys = EstimateKeySize(cipher, 1, 22);
        //System.out.println("Answer is: " + calcIC(decipher) + " with " + calcIC(cipher));
        for(int x : keys) {
        	System.out.println("Candidate key length: " + x);
        }
        int[] keysizes = {6};
        System.out.println("The Possible decrypted cipher is as followed: ");
        System.out.println(Friedman(cipher, keysizes)); //in progress
        
        //System.out.println(Caeser(caesar));
        //System.out.println(Crack("ahovbipwcjqxdkryelszfmtgnu", 7));
        //System.out.println(Crack("aiqybjrzcksdltemufnvgowhpx", 8));
    }

	public static String Friedman(String cipher, int[] keysizes){

		String cracked = "";
		String[] deciphers = {"", "", ""};
    	double[] highScores = {1, 1, 1, 0};
    	
        for (int i = 0; i < keysizes.length; i++){ //for each key size
        	
        	int key = keysizes[i];
        	int cont_size = (int) Math.ceil(cipher.length()/keysizes[i]);
            String[] container = new String[key];
            String[] stabbed = new String[key]; //result of a row in container getting caesered
            for (int j = 0; j < container.length; j++){ //empty the string array for a new key size

                container[j] = "";
            }
            for (int j = 0; j < cipher.length(); j++){

                container[Math.floorMod(j, key)] += cipher.charAt(j);
            }
        		
            for (int row = 0; row < container.length ; row++) {
            	
            	stabbed[row] = Caeser(container[row]);
            	System.out.println(container[row] + " --> " + stabbed[row]);
            }
            //System.out.println(container[0]);
            
            String decrypto = "";
            for ( String X : stabbed) {
            	
            	decrypto += X;
            }
            
            cracked = Crack(decrypto, key);
             
        }
        return cracked;
    }
    
    
    private static String Crack(String cipher, int key) { //fixed: there's a bug cause the holes don't exist. Possible solution: counting the holes, adding a $ from the end by multiple of index and ignoring those letters in the loop

    	cipher = cipher.toUpperCase();
    	String unscrambled = "";
    	int N = cipher.length();
    	double ix = Math.ceil((double) N/ (double) key);
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

	public static char matchLetterIC(String cipher, char c, char[] letter, double[] coincidence) {
    	
    	char key = 'A';
    	double frequency = 0;
    	double N = cipher.length();
    	double highScore = 1;

		for (int i = 0; i < cipher.length(); i++) {
			if (cipher.charAt(i) == c) {
				frequency-=-1;
			}
		}
		
		double ioc = (frequency * (frequency - 1) / N * (N - 1));
		
		if (ioc == 0) {
			
			return '?';
		}
		
		for (int i = 0; i < letter.length; i++) {
			
			double score = Math.pow(ioc - coincidence[i], 2);
			if(score < highScore) {
				
				highScore = score;
				key = (char) letter[i];
			}
			
		}
		
    	return key;
    }
    
    private static double calcIC(String cipher) {

    	cipher = cipher.toUpperCase();
    	double[] frequencies = new double[26];
    		for (int i = 0; i < cipher.length(); i++) {
    			
    			frequencies[cipher.charAt(i) - 'A'] -=-1;
    		}
    		
    		
    		double ioc = 0.0;
        	int N = cipher.length();
    		for (int i = 0; i < frequencies.length; i++) {
    			
    			ioc += (frequencies[i] * (frequencies[i] - 1)) / (N * (N - 1)); 
    		}
		return ioc;
	}

    //Estimate most probable key sizes
    private static int[] EstimateKeySize(String cipher, int minKeySize, int MaxKeySize) {

    	System.out.println("Looking for best keys between lengths of " + minKeySize  + "-" + MaxKeySize);
    	int[] keySizes = {0, 0, 0};
    	double[] highScores = {1, 1, 1, 0};
    	for (int key = minKeySize; key <= MaxKeySize; key++){ //for each key size
        	
            String[] container = new String[key];
            double score = 0;
            
            for (int j = 0; j < container.length; j++) {
                container[j] = "";
            }
            for (int j = 0; j < cipher.length(); j++){

                container[Math.floorMod(j, key)] += cipher.charAt(j);
            }
            
            for (int j = 0; j < container.length; j++) {
            	
            	//System.out.print("Key size of: " + key + " The " + j + " sequence is: " + container[j]);
            	//System.out.println(" Partial cipher size is: " + container[j].length() + " IOC = " + calcIC(container[j]));
            	score += Math.pow(calcIC(container[j]) - 0.065, 2);
            }
            
            score /= key;
            if (score < highScores[2]) {
            	
            	//System.out.println(key + " has the score of: " +score);
            	highScores = Sorted_Insert(score, key, highScores, keySizes);
            }
            
    	}
    	
		return keySizes;
	}
    
    private static double[] Sorted_Insert(double score, int candidKey, double[] highScores, int[] keys) {
		boolean done = false;
		int index = 0;
		while (!done && index < highScores.length - 1) { //last cell is reserved for the index return
			if (score < highScores[index]) {
				
				done = true;
			}else {
				
				index-=-1;
			}
		}
		if (done) {
			
			if (index + 1 != highScores.length-1) {
				for ( int i = highScores.length-2; i > index; i--) {
				
					highScores[i] = highScores[i-1];
					keys[i] = keys[i-1];
				}
			}
			highScores[index] = score;
			keys[index] = candidKey;
			highScores[highScores.length-1] = index;
		}
		
		return highScores;
	}
    
    private static double[] Sorted_Insert(double score, String candidKey, double[] highScores, String[] keys) {
		boolean done = false;
		int index = 0;
		while (!done && index < highScores.length - 1) { //last cell is reserved for the index return
			if (score < highScores[index]) {
				
				done = true;
			}else {
				
				index-=-1;
			}
		}
		if (done) {
			
			if (index + 1 != highScores.length-1) {
				for ( int i = highScores.length-2; i > index; i--) {
				
					highScores[i] = highScores[i-1];
					keys[i] = keys[i-1];
				}
			}
			highScores[index] = score;
			keys[index] = candidKey;
			highScores[highScores.length-1] = index;
		}
		
		return highScores;
	}

	public static HashMap<Integer, Integer> Kasiski(String cipher, int minKeyLength, int maxKeyLength) {
    	
    	List<Integer> occurances = new ArrayList<Integer>();
    	List<Integer> divisors = new ArrayList<Integer>();
    	HashMap<Integer, Integer> frequency = new HashMap<Integer, Integer>();
    	
    	for (int key = maxKeyLength ; key > minKeyLength; key--) {
    		
    		for (int i = 0; i + key < cipher.length(); i++) {
    			
    			String temp = cipher.substring(i, i + key);
    			for (int j = i + key; j + key< cipher.length(); j++) {
    				
    				if(temp.equals(cipher.substring(j, j + key))){
    					occurances.add(j - i);
    				}
    			}
    		}
    		if (occurances.isEmpty() == false) { //found a pattern -> stop searching : only added for testing, remove later 
    			break;
    		}
    	}

    	for (Integer number : occurances) {
    		
    		for (int i = 2; i < number; i++) {
    			
    			if(Math.floorMod(number, i) == 0) {
    				divisors.add(i);
    			}
    		}
    	}
    	
    	for (Integer number : divisors) {
        	if(frequency.containsKey(number)) {
        		
        		frequency.replace(number, frequency.get(number) + 1);
        	}else {
        		frequency.put(number, 1);
        	}
		}
    	
    	return sortByValue(frequency);
    }
    
    public static LinkedHashMap<Integer, Integer> sortByValue(HashMap<Integer, Integer> hm)
    {
    	LinkedHashMap<Integer, Integer> mapSortedBasedOnValues = hm.entrySet().stream().sorted((e1,e2)->
		e2.getValue().compareTo(e1.getValue())).
		collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1,e2)->e1, LinkedHashMap::new));
		
    	return mapSortedBasedOnValues;
    }
    
    public static String Caeser (String cipher){ //create string array with 26 different shifts (including the original shift = 0)  on the input cipher
        
    	final char english_letters[] = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    	final double english_freqs[] = {0.082, 0.015, 0.028, 0.043, 0.127, 0.022, 0.020, 0.061, 0.070, 0.002, 0.008, 0.040, 0.024, 0.067, 0.075, 0.019, 0.001, 0.060, 0.063, 0.091, 0.028, 0.010, 0.023, 0.001, 0.020, 0.001};
    	
    	cipher = cipher.toUpperCase();
		String stabbed = "";
		double highScore = Double.MAX_VALUE;
		char ord = 'A';
		
    	for (int shift = 0; shift < 26; shift++) {
    		String rotation = "";
    		double score = 0.0;
    		ArrayList<Integer> vars = new ArrayList<>();
    		
    		for (int i = 0; i < cipher.length(); i++) {
                int ca = (int) cipher.charAt(i);
                if (ca >= 65 && ca <= 90) {
                	
                	if (ca + shift <= 90) {
                		ca += shift;
                	}else {
                		
                		ca+= shift - 26;
                	}
                }
                vars.add((Math.floorMod(ca - matchLetterIC(cipher, (char) ca, english_letters, english_freqs), english_letters.length)));
                rotation += (char) ca;
            }
    		
    		
    		
    		score = calcVariance(vars);
    		//System.out.println(score);
    		if (score < highScore) {
    			
    			highScore = score;
    			ord = (char) (shift + 65);
    			stabbed = rotation;
    		}
    	}
    	
    	return stabbed;
    }
    
    public static double calcVariance(ArrayList<Integer> arr) {
    	
    	double mean = 0.0;
    	for (int X : arr) {
    		
    		mean+= X;
    	}
    	mean /= arr.size();

    	// The variance
    	double variance = 0;
    	for (int X : arr) {
    	    variance += Math.pow(X - mean, 2);
    	}
    	variance /= arr.size();
    	
    	return variance;
    }
    
    public static int findIndex(char arr[], char t){
    	
        ArrayList<Character> clist = new ArrayList<>();
 
        for (char c : arr)
            clist.add(c);
 
        return clist.indexOf(t);
    }
    
    public static String Stitch(String[][] stabbed, String stitched, int index, int bidex) { //scrapped: too much resource intensive
    	
    	if (index < 26) {
    		stitched += stabbed[bidex][index];
    		index-=-1;
    		for (int i = 0; i < stabbed.length; i++) {
    			
    			Stitch(stabbed, stitched, index, i);
    		}
    		
    	}else {
    		
    		return stabbed[index][stabbed.length];
    	}
    	
    	return null;
    }
}