import java.io.*;
import java.util.*;

public class linkerlab {
    public static void main(String[] args){
    	int numAddresses=0;
    	int counter;
		Map <String, Integer> var = new LinkedHashMap<String, Integer> ();
		Scanner input = new Scanner(System.in);
		int numModules = input.nextInt();
		int[] baseMod = new int[numModules];
		baseMod[0] = 0;
		
		for (int modNum=0; modNum < numModules; modNum++) {
			//will need to check if key set already exists
			counter = input.nextInt();

			for (int i = 0; i < counter; i++){
				var.put(input.next(), input.nextInt()+numAddresses);
			}
			//skip use list, will prob come in handy for error checking
			counter = input.nextInt();
			for (int j = 0; j < counter; j++){
				input.next();
			}
	
			counter = input.nextInt();
			baseMod[modNum] = counter;
			for (int k = 0; k < counter; k++){
				input.next();
				input.nextInt();
				numAddresses++;
			}
		}
		System.out.println("done");
		System.out.println(var.keySet() + " " + var.values());
	}
}