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
        
    	final char english_letters[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    	final double english_freqs[] = {0.082, 0.015, 0.028, 0.043, 0.127, 0.022, 0.020, 0.061, 0.070, 0.002, 0.008, 0.040, 0.024, 0.067, 0.075, 0.019, 0.001, 0.060, 0.063, 0.091, 0.028, 0.010, 0.023, 0.001, 0.020, 0.001};
    	String cipher = "PATGSJKGSPFPCTSSKHOIGSDHNBCUHVIHKSHVBKPBQLEGVFSHPLTQFLYRWSRLYBSSRPPPPGIUOTUSHVPTZSVLNBCHCIWMIZSZKPWWZLZKXJWUCMWFCBCAACBKKGDBHOAPPMHVBKPBQLDXKWGPPXSZCUZHCNCVWGSOGRAWIVSTPHROFLBHGHVLYNQIBAEEWWGYAMJFBDDBRVVLKIIWAPOMXQOSHRPBHPYBEOHLZPDIZKXXCCZVJZTFHOWGIKCDAXZGCMYHJFGLPATKOYSTHBCAPHTBRZKJJWQRHR";
        System.out.println("Possible key sizes along with their frequency: ");
        System.out.println("Kasiski says these are the possible keys" +Kasiski(cipher, 5, 10));
        int[] keys = EstimateKeySize(cipher, 16, 22);
        for(int x : keys) {
        	System.out.println("Candidate key length: " + x);
        }
        int[] keysizes = {16, 32, 48};
        System.out.println("The Possible decrypted ciphers are as followed: ");
        //System.out.println(Friedman(cipher, keysizes)); //in progress
        //System.out.println(Caeser("abc")[0]);
    }

	public static void Friedman(String cipher, int[] keysizes){

    	//double smol_key = smol(keysizes);
    	
        for (int i = 0; i < keysizes.length; i++){ //for each key size
        	
        	int cont_size = (int) Math.ceil(cipher.length()/keysizes[i]);
            String[] container = new String[keysizes[i]];
            String[][] stabbed = new String[keysizes[i]][26]; //result of a row in container getting caesered
  //          for (int j = 0; j < container.length; j++){ //empty the string array for a new key size
//
 //               container[j] = "";
//            }
            for (int j = 0; j < cipher.length(); j++){

                container[j%keysizes[i]] += cipher.charAt(j);
            }
        		
            for (int row = 0; row < container.length ; row++) {
            	
            	stabbed[row] = Caeser(container[row]);
            }
            System.out.println(container[0]);
        }
    }
    
    
    public static int IndexOfCoincidence(String cipher) {
    	
    	double ioc = calcIC(cipher);
    	int[] frequencies = new int[26];

		for (int i = 0; i < cipher.length(); i++) {
			frequencies[cipher.charAt(i) - 'A']++;
		}
		
		double max = 0.027 * cipher.length();
		double min = (cipher.length() - 1) * ioc - 0.038 * cipher.length() + 0.065;
		
		int keysize = (int) (max/min);
    	return keysize;
    }
    
    private static double calcIC(String cipher) {

    	cipher = cipher.toUpperCase();
    	double[] frequencies = new double[26];
    		for (int i = 0; i < cipher.length(); i++) {
    			
    			frequencies[cipher.charAt(i) - 'A'] -=-1;
    			//System.out.print(cipher.charAt(i) + " ");
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
    	for (int i = minKeySize; i <= MaxKeySize; i++){ //for each key size
        	
        	//int cont_size = (int) Math.ceil(cipher.length()/i); //number of columns for some reason
            String[] container = new String[i];
            double score = 0;
            
            for (int j = 0; j < container.length; j++) {
                container[j] = "";
            }
            for (int j = 0; j < cipher.length(); j++){

                container[j%i] += cipher.charAt(j);
            }
            
            for (int j = 0; j < container.length; j++) {
            	
            	System.out.print("Key size of: " + i + " The " + j + " sequence is: " + container[j]);
            	System.out.println(" Partial cipher size is: " + container[j].length() + " IOC = " + calcIC(container[j]));
            	score += Math.pow(calcIC(container[j]) - 0.065, 2);
            	//score += calcIC(container[j]) - 0.065;
            }
            
            score /= i;
            if (score < highScores[2]) {
            	
            	System.out.println(i + " has the score of: " +score);
            	highScores = Sorted_Insert(score, i, highScores, keySizes);
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
    		if (occurances.isEmpty() == false) { //found a pattern -> stop searching
    			break;
    		}
    	}
		//System.out.print(occurances);

    	for (Integer number : occurances) {
    		
    		for (int i = 2; i < number; i++) {
    			
    			if(number%i == 0) {
    				divisors.add(i);
    			}
    		}
    		//System.out.print(number + "-> " +divisors);
    	}
    	
    	for (Integer number : divisors) {
        	if(frequency.containsKey(number)) {
        		
        		frequency.replace(number, frequency.get(number) + 1);
        	}else {
        		frequency.put(number, 1);
        	}
		}
    	
    	//System.out.print(Collections.max(frequency.entrySet(), HashMap.Entry.comparingByValue()).getKey()); //highest frequency
    	
    	return sortByValue(frequency);
    }
    
    public static LinkedHashMap<Integer, Integer> sortByValue(HashMap<Integer, Integer> hm)
    {
        /*
    	int[] keys = new int[hm.size()];
    	int[] values = new int[hm.size()];

    	for (HashMap.Entry<Integer, Integer> entry : hm.entrySet()) {
    		
    		for(int i = 0; i < hm.size(); i++) {
    			
    			if (values[i] > entry.getValue())
    				continue;
    			for (int j = hm.size() - 1; j > i; j--) {
    				values[j] = values [j-1];
    				keys[j] = keys[j-1];
    			}
    			
    			values[i] = entry.getValue();
    			keys[i] = entry.getKey();
    	        break;
    		}
    	}
    	*/
    	LinkedHashMap<Integer, Integer> mapSortedBasedOnValues = hm.entrySet().stream().sorted((e1,e2)->
		e2.getValue().compareTo(e1.getValue())).
		collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1,e2)->e1, LinkedHashMap::new));
		
		//mapSortedBasedOnValues.forEach((k,v)->System.out.println(k+"\t"+v));
    	return mapSortedBasedOnValues;
    }
    
    public static String[] Caeser (String cipher){ //create string array with 26 different shifts (including the original shift = 0)  on the input cipher
        
    	String uppercaseCipher = cipher.toUpperCase();
		String[] rotation = new String[26];
    	for (int shift = 0; shift < 26; shift++) {
    		for (int i = 0; i < uppercaseCipher.length(); i++) {
                int ca = (int) uppercaseCipher.charAt(i);
                if (ca >= 65 && ca <= 90) {
                	
                	if (ca + shift <= 90) {
                		ca += shift;
                	}else {
                		
                		ca+= shift - 26;
                	}
                }
                rotation[shift] += (char) ca;
            }
    	}
    	
    	return rotation;
    }
    
    public static int smol(int[] arr) {
    	
    	int smallest = Integer.MAX_VALUE;
        int index=0;
        while(index<arr.length) {
            if(smallest>arr[index]) {
                smallest=arr[index];
            }
            index++; 
        }
        return smallest;
    }
    
    public static char FrequencyAnalysis(String[] rotations) { //analyze and return the best key
    	int shift = 0;
    	return (char) (shift + 65);
    }
}