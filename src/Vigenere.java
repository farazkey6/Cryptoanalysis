import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Vigenere{

    public static void main(String[] args) {
        
        String cipher = "UYWOVTRBANTPIJQFIGPNOUOXYFVLWZQCSPBJCZQFUIOPRWZUAAFISWAEKCOUPNVKJUFGQPDVGOSVCZUPSPDPNORZUFFKVAPJGVAE";
        System.out.println("Possible key sizes along with their frequency: ");
        System.out.println(Kasiski(cipher, 2, 6));
        //System.out.print(Kasiski(cipher, 2, 6));
    }

    public static int Friedman(String cipher, int keysize){

        String[] container = new String[cipher.length()];
        for (int i = 2; i < cipher.length(); i++){ //length of key

            for (int j = 0; j < container.length; j++){ //empty the string array for a new key size

                container[j] = "";
            }
            for (int k = 0; k < cipher.length(); k++){

                container[k%i] += cipher.charAt(k);
            }

            //System.out.println(container[0]);
        }
        return 0;
    }
    
    public static HashMap Kasiski(String cipher, int minKeyLength, int maxKeyLength) {
    	
    	List<Integer> occurances = new ArrayList<Integer>();
    	List<Integer> divisors = new ArrayList<Integer>();
    	HashMap<Integer, Integer> frequency = new HashMap<Integer, Integer>();
    	
    	for (int key = minKeyLength; key < maxKeyLength; key++) { //find all repeated pattern with lengths between min and max
    		
    		for (int i = 0; i + key < cipher.length(); i++) {
    			
    			String temp = cipher.substring(i, i + key);
    			for (int j = i + key; j + key< cipher.length(); j++) {
    				
    				if(temp.equals(cipher.substring(j, j + key))){
    					occurances.add(j - i);
    				}
    			}
    		}
    	}
    	
    	for (Integer number : occurances) {
    		
    		for (int i = 2; i < number; i++) {
    			
    			if(number%i == 0) {
    				divisors.add(i);
    			}
    		}
    		//System.out.print(divisors);
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
    	LinkedHashMap<Integer, Integer> mapSortedBasedOnValues = hm.entrySet().stream().sorted((e1,e2)-> //using lambda
		e2.getValue().compareTo(e1.getValue())).
		collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1,e2)->e1, LinkedHashMap::new));
		
		//mapSortedBasedOnValues.forEach((k,v)->System.out.println(k+"\t"+v));
    	return mapSortedBasedOnValues;
    }
}