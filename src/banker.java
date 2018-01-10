//Programmer: Ikjae Jung

import java.util.ArrayList;
import java.util.LinkedList;

public class banker {
	int tasknum;
	int tasktemp;
	int resourcenum;
	int[] units;
	
	int[] finishing;
	int[] waiting;
	
	
	
	LinkedList<Integer> blockQueue = new LinkedList();
	ArrayList<ArrayList<String>> terminated = new ArrayList<ArrayList<String>>();
	
	public banker(ArrayList<ArrayList<String>> inst, int tasks, int resources, int[] resource_units){
		tasknum = tasks;
		tasktemp = 0;
		resourcenum = resources;
		units = resource_units;
		
		
		
		int[][] need = new int[tasknum][resourcenum];
		int[][] claims = new int[tasknum][resourcenum];
		int[][] alloc = new int[tasknum][resourcenum];
		int[] avail = new int[resourcenum];
		int[] availNext = new int[resourcenum];
		int[] delay = new int[tasknum];
		boolean[] delayed = new boolean[tasknum];
		
		boolean[] forNext = new boolean[tasknum];
		
		
		finishing  = new int[tasknum];
		waiting  = new int[tasknum];
		
		for(int i = 0; i < resourcenum; i++){
			waiting[i] = 0;
		}
		
		for(int i = 0; i < resourcenum; i++){
			avail[i] = units[i];
		}
		
		int cycle = 0;
		while(terminated.size() != inst.size()){
			cycle++;
			availNext = avail.clone();
			int blocksize = blockQueue.size();
			int index = 0;
			for(int j = 0; j < blocksize; j++){
				int temp = blockQueue.get(index);
				//System.out.println("Blocked: " + (temp+1));
				if (!checkSafe(temp,claims,avail,alloc) && (blockQueue.size() + terminated.size()) == inst.size()){
					waiting[temp]++;
					//System.out.println("Aborted: " + (temp+1));
					for(int i = 0; i < resourcenum; i++){
						int toReturn = alloc[temp][i];
						avail[i] += toReturn;
						availNext[i] += toReturn;
					}terminated.add(inst.get(temp));
					blockQueue.remove(index);
				}else if(!checkSafe(temp,claims,avail,alloc)){
					//System.out.println((temp+1) + " is waiting");
					waiting[temp]++;
					index++;
				}else{
					waiting[temp]++;
					String[] command = inst.get(temp).get(0).split(" ");
					//System.out.println("avail: " + avail[0] + " need: " + Integer.parseInt(command[4]) + " availNext: " + availNext[0]);
					avail[Integer.parseInt(command[3])-1] -= Integer.parseInt(command[4]);
					availNext[Integer.parseInt(command[3])-1] -= Integer.parseInt(command[4]);
					alloc[Integer.parseInt(command[1])-1][Integer.parseInt(command[3])-1] += Integer.parseInt(command[4]);
					inst.get(temp).remove(0);
					//System.out.println("avail: " + avail[0]);
					//System.out.println("Exiting through block");
					blockQueue.remove(index);
					forNext[temp] = true;
				}
			}
			for(int i = 0; i < inst.size(); i++){
				//System.out.println("Available: " + avail[0]);
				String[] command = inst.get(i).get(0).split(" ");
				if(!delayed[i]){
					delay[i] = Integer.parseInt(command[2]);
					delayed[i] = true;
				}
				if(delay[i] == 0){
					if(blockQueue.contains(i)){
						continue;
					}else if(terminated.contains(inst.get(i))){
						continue;
					}else if(forNext[i]){
						forNext[i] = false;
					}else if(inst.get(i).get(0).contains("initiate")){
						//System.out.println("Initiate");
						claims[Integer.parseInt(command[1])-1][Integer.parseInt(command[3])-1] = Integer.parseInt(command[4]);
						//System.out.println(claims[Integer.parseInt(command[1])-1][Integer.parseInt(command[3])-1]);
						inst.get(i).remove(0);
						delayed[i] = false;
					}else if(inst.get(i).get(0).contains("request")){
						//System.out.println("request");
						need[Integer.parseInt(command[1])-1][Integer.parseInt(command[3])-1] = Integer.parseInt(command[4]);
						//System.out.println((i+1) + ": ");
						//System.out.println("Avail: " + avail[Integer.parseInt(command[3])-1]);
						//System.out.println("Request: " + Integer.parseInt(command[4]));
						if(need[Integer.parseInt(command[1])-1][Integer.parseInt(command[3])-1] > claims[Integer.parseInt(command[1])-1][Integer.parseInt(command[3])-1]){
							terminated.add(inst.get(i));
						}else{
							if(!checkSafe(i,claims,avail,alloc)){
								blockQueue.add(i);
								//System.out.println("sent to block: " + i);
							}else{
								avail[Integer.parseInt(command[3])-1] -= Integer.parseInt(command[4]);
								availNext[Integer.parseInt(command[3])-1] -= Integer.parseInt(command[4]);
								alloc[Integer.parseInt(command[1])-1][Integer.parseInt(command[3])-1] += Integer.parseInt(command[4]);
								inst.get(i).remove(0);
							}
						}//System.out.println("Avail: " + avail[0]);
						//System.out.println("AvailNext: " + availNext[0]);
						delayed[i] = false;
						//System.out.println(alloc[Integer.parseInt(command[1])-1][Integer.parseInt(command[3])-1]);
					}else if(inst.get(i).get(0).contains("release")){
						//System.out.println("release");
						alloc[Integer.parseInt(command[1])-1][Integer.parseInt(command[3])-1] -= Integer.parseInt(command[4]);
						availNext[Integer.parseInt(command[3])-1] += Integer.parseInt(command[4]);
						//System.out.println("Release " + (i+1) + ":");
						//System.out.println("alloc: "+ alloc[Integer.parseInt(command[1])-1][Integer.parseInt(command[3])-1] + " availn: " + availNext[Integer.parseInt(command[3])-1] + " avail: "  + avail[Integer.parseInt(command[3])-1]);
						inst.get(i).remove(0);
						delayed[i] = false;
					}else if(inst.get(i).get(0).contains("terminate")){
						//System.out.println("terminate");
						//System.out.println("this: " + command[1]);
						//System.out.println(tasktemp);
						terminated.add(inst.get(Integer.parseInt(command[1])-1));
						tasktemp++;
						finishing[Integer.parseInt(command[1])-1] = cycle-1;
						delayed[i] = false;
					}
				}else{
					delay[i]--;
				}
			}//System.out.println("avail: " + avail[0] + " availNext: " + availNext[0]);
			avail = availNext.clone();
			//System.out.println("\nCycle:" + cycle);
		}
	}
	
	public int getCycle(int task){
		return finishing[task];
	}
	
	public int getWaiting(int task){
		return waiting[task];
	}
	
	boolean checkSafe(int process, int[][]claim, int[] avail, int[][] alloc) {
		int[]need = new int[resourcenum];
		for(int i = 0; i < resourcenum; i++){
			need[i] = claim[process][i] - alloc[process][i];
		}
		
		for(int i = 0; i < resourcenum; i++) {
        	if(avail[i] < need[i]){
            	return false;
            }
        }
		return true;
	}
}
