import java.io.*;
import java.util.*;

public class linkerlab {
    public static void main(String[] args){
    	int MACHINE_SIZE = 200;
    	int numAddresses=0;
    	int counter;
		Map <String, Integer> var = new LinkedHashMap<String, Integer>();
		Map <String, Integer> varModule = new LinkedHashMap<String, Integer>();
		Map <String, String> varError = new LinkedHashMap<String, String>();
		ArrayList<String> uses = new ArrayList<String>();
		Map <Integer, HashMap<Integer, String>> useList = new LinkedHashMap<Integer, HashMap<Integer, String>>();
		Map <Integer, ArrayList<String>> types = new LinkedHashMap<Integer, ArrayList<String>>();
		Map <Integer, ArrayList<Integer>> addresses = new LinkedHashMap<Integer, ArrayList<Integer>>();
		Map <Integer, Integer> absAddressError = new LinkedHashMap<Integer, Integer>();
		ArrayList<Integer> relAddressError = new ArrayList<Integer>();
		ArrayList<Integer> externalError = new ArrayList<Integer>();
		Map <Integer, String> undefinedError = new HashMap<Integer, String>();

		Scanner input = new Scanner(System.in);
		int numModules = input.nextInt();
		int[] baseMod = new int[numModules];
		
		for (int modNum=0; modNum < numModules; modNum++) {
			counter = input.nextInt();
			for (int i = 0; i < counter; i++){
				String variable = input.next();
				if (!var.containsKey(variable)){
					var.put(variable, input.nextInt()+numAddresses);
				}
				else{
					varError.put(variable, "Error: This variable is multiply defined; first value used.");
				}
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

		int count = 0;
		for (int modNum=0; modNum < numModules; modNum++) {
			ArrayList<String> typeList = types.get(modNum);
			ArrayList<Integer> addressList = addresses.get(modNum);
			for (int i = 0; i < typeList.size(); i++){
				if (typeList.get(i).equals("R") && (modNum > 0)){
					//check if relative address exceeds base module
					int curAddress = addressList.get(i);
					int remainder = curAddress % 1000;
					if (remainder > baseMod[modNum]){
						addressList.set(i, curAddress - remainder);
						relAddressError.add(count);
					}
					else{
						addressList.set(i, addressList.get(i) + baseMod[modNum-1]);
					}
				}
				if (typeList.get(i).equals("E")){
					int curAddress = addressList.get(i);
					int remainder = curAddress % 1000;
					if (remainder >= useList.size()){
						//add to warning list
						externalError.add(count);
					}
					else{
						String letter = useList.get(modNum).get(remainder);
						int newAddress;
						if (var.get(letter)!= null){
							newAddress = (curAddress - remainder) + var.get(letter);
						}
						else{
							newAddress = curAddress - remainder;
							//add error
							undefinedError.put(count, letter);
						}
						addressList.set(i,newAddress);
					}
					
				}
				if (typeList.get(i).equals("A")){
					int curAddress = addressList.get(i);
					int remainder = curAddress % 1000;
					if (remainder > MACHINE_SIZE){
						addressList.set(i, curAddress - remainder);
						absAddressError.put(count, 1);
					}
					else{
						absAddressError.put(count, 0);
					}
					
				}
				count++;
			}
		}
		System.out.println("Symbol Table");
		var.forEach((key, value) -> {
			System.out.print(key + "=" + value);
			if (varError.containsKey(key)){
				System.out.print(" " + varError.get(key));
			}
			System.out.println();
		});
		System.out.println("\nMemory Map");
		int memory = 0;
		for (int i = 0; i < numModules; i++){
			ArrayList<Integer> arr = addresses.get(i);
			for (int j = 0; j < arr.size(); j++){
				System.out.printf("%3d: %5d", memory, arr.get(j));
				if (absAddressError.getOrDefault(memory,0) == 1){
					System.out.print(" " + "Error: Absolute address exceeds machine size; zero used.");
				}
				else if (undefinedError.get(memory)!=null){
					System.out.print(" " + undefinedError.get(memory) + " is not defined; zero used.");
				}
				else if (relAddressError.contains(memory)){
					System.out.print(" Error: Relative address exceeds module size; zero used.");
				}
				else if(externalError.contains(memory)){
					System.out.print(" Error: External address exceeds length of use list; treated as immediate");
				}
				System.out.println();
				memory++;
			}
		}
		System.out.println();
		//print warning for defined but not used
		var.forEach((key, value) -> {
			if(!uses.contains(key)){
				System.out.println("Warning: " + key + " was defined in module " + varModule.get(key) + " but never used.");
			}
			
		});
	}
}