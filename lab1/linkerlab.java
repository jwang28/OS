import java.io.*;
import java.util.*;

public class linkerlab {
    public static void main(String[] args){
    	int MACHINE_SIZE = 200;
    	int numAddresses=0;
    	int counter;
		Map <String, Integer> var = new LinkedHashMap<String, Integer>();
		Map <String, Integer> varModule = new LinkedHashMap<String, Integer>();
		ArrayList<String> uses = new ArrayList<String>();
		Map <Integer, HashMap<Integer, String>> useList = new LinkedHashMap<Integer, HashMap<Integer, String>>();
		Map <Integer, ArrayList<String>> types = new LinkedHashMap<Integer, ArrayList<String>>();
		Map <Integer, ArrayList<Integer>> addresses = new LinkedHashMap<Integer, ArrayList<Integer>>();

		Scanner input = new Scanner(System.in);
		int numModules = input.nextInt();
		int[] baseMod = new int[numModules];
		
		for (int modNum=0; modNum < numModules; modNum++) {
			counter = input.nextInt();
			for (int i = 0; i < counter; i++){
				String variable = input.next();
				var.put(variable, input.nextInt()+numAddresses);
				varModule.put(variable, modNum);
			}	
			counter = input.nextInt();
			HashMap<Integer, String> vars = new HashMap<Integer, String>();
			for (int j = 0; j < counter; j++){
				String use = input.next();
				vars.put(j, use);
				uses.add(use);
			}
			useList.put(modNum, vars);
			counter = input.nextInt();
			ArrayList<String> typeList = new ArrayList<String>();
			ArrayList<Integer> addressList = new ArrayList<Integer>();
			for (int k = 0; k < counter; k++){
				typeList.add(input.next());
				addressList.add(input.nextInt());
				numAddresses++;
			}
			baseMod[modNum] = numAddresses;
			types.put(modNum, typeList);
			addresses.put(modNum, addressList);
		}

		for (int modNum=0; modNum < numModules; modNum++) {
			ArrayList<String> typeList = types.get(modNum);
			ArrayList<Integer> addressList = addresses.get(modNum);
			for (int i = 0; i < typeList.size(); i++){
				if (typeList.get(i).equals("R") && (modNum > 0)){
					addressList.set(i, addressList.get(i) + baseMod[modNum-1]);
				}
				if (typeList.get(i).equals("E")){
					int curAddress = addressList.get(i);
					int remainder = curAddress % MACHINE_SIZE;
					String letter = useList.get(modNum).get(remainder);
					int newAddress = (curAddress - remainder) + var.get(letter);
					addressList.set(i,newAddress);
				}
			}
		}

		System.out.println("Symbol Table");
		var.forEach((key, value) -> {
			System.out.println(key + "=" + value);
		});
		System.out.println("\nMemory Map");
		int memory = 0;
		for (int i = 0; i < numModules; i++){
			ArrayList<Integer> arr = addresses.get(i);
			for (int j = 0; j < arr.size(); j++){
				System.out.printf("%3d: %5d\n", memory, arr.get(j));
				memory++;
			}
		}
		//print warning for defined but not used
		var.forEach((key, value) -> {
			if(!uses.contains(key)){
				System.out.println("Warning: " + key + " was defined in module " + varModule.get(key) + " but never used.");
			}
		});
	}
}