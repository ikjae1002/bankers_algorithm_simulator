//Programmer: Ikjae Jung

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class bankers {

	public static void main(String[] args) {
		// Variable for input file
		File fileName;
		if (args.length < 1){
			System.err.println("Missing arguments");
		}
		fileName = new File(args[0]);
		
		if (!fileName.canRead()) {
			System.err.printf("Error: cannot read from file %s\n.",
					fileName.getAbsolutePath());
			System.exit(0);
		}
		
		Scanner dataInput = null;
		try {
			dataInput = new Scanner(fileName);
		}
		catch (FileNotFoundException e) {
			System.err.printf("Error: cannot open file %s for reading\n.",
					fileName.getAbsolutePath());
			System.exit(0);
		}
		
		ArrayList<String> inputList = new ArrayList<String>();

		while(dataInput.hasNext()){
			inputList.add(dataInput.next());
		}
		
		int tasks;
		int resources;
		ArrayList<ArrayList<String>> inst = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> copy = new ArrayList<ArrayList<String>>();
		
		int count = 0;
		tasks = Integer.parseInt(inputList.get(count));
		count++;
		resources = Integer.parseInt(inputList.get(count));
		int[] resource_units = new int[resources]; 
		count++;
		for(int i = 0; i < resources; i++){
			resource_units[i] = Integer.parseInt(inputList.get(count));
			count++;
		}
		for(int i = 0; i < tasks; i++){
			inst.add(new ArrayList<String>());
			copy.add(new ArrayList<String>());
		}
		ArrayList<String> insts = null;
		while(count < inputList.size()){
			String instruction = "";
			instruction = inputList.get(count) + " ";count++;
			int processnum = Integer.parseInt(inputList.get(count));count++;
			instruction = instruction + processnum + " ";
			instruction = instruction + inputList.get(count) + " ";count++;
			instruction = instruction +inputList.get(count) + " ";count++;
			instruction += inputList.get(count);count++;
			inst.get(processnum-1).add(instruction);
			copy.get(processnum-1).add(instruction);
		}
		fifo fi = new fifo(inst, tasks, resources, resource_units);
		banker ba = new banker(copy,tasks,resources,resource_units);
		
		//FIFO output
		System.out.println("              FIFO");
		float totalwait = 0;
		float totalcycle = 0;
		for(int i = 0; i <tasks; i++){
			if(fi.getCycle(i) != 0){
				totalcycle += fi.getCycle(i);
				totalwait += fi.getWaiting(i);
			}
			float waiting = fi.getWaiting(i);
			if(fi.getCycle(i) == 0){
				String percentage = "aborted   ";
				System.out.println("     Task " + (i+1) + "      " + percentage + "        ");
			}else{
				float percentage = 100*(waiting / fi.getCycle(i));
				System.out.println("     Task " + (i+1) + "      " + fi.getCycle(i) + 
								   "   " + fi.getWaiting(i) + "   " + 
								   String.format("%.0f", percentage) + "%");
			}
		}float percentage = 100*(totalwait/totalcycle);
		System.out.println("     total       " + String.format("%.0f",totalcycle)
						   + "   " + String.format("%.0f",totalwait) + "   "
						   + String.format("%.0f", percentage) + "%");
		//Banker's Output
		System.out.println("\n            BANKER'S");
		float totalwaits = 0;
		float totalcycles = 0;
		for(int i = 0; i <tasks; i++){
			if(ba.getCycle(i) != 0){
				totalcycles += ba.getCycle(i);
				totalwaits += ba.getWaiting(i);
			}
			float waiting = ba.getWaiting(i);
			if(ba.getCycle(i) == 0){
				String percentages = "aborted   ";
				System.out.println("     Task " + (i+1) + "      " + percentages + "        ");
			}else{
				float percentages = 100*(waiting / ba.getCycle(i));
				System.out.println("     Task " + (i+1) + "      " + ba.getCycle(i) + 
								   "   " + ba.getWaiting(i) + "   " + 
								   String.format("%.0f", percentages) + "%");
			}
		}float percentages = 100*(totalwaits/totalcycles);
		System.out.println("     total       " + String.format("%.0f",totalcycle)
						   + "   " + String.format("%.0f",totalwait) + "   "
						   + String.format("%.0f", percentages) + "%");
	}

}
