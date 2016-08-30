package lab.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class NodeProcess extends Thread{
	private Integer uid;
	private Set<Integer> neighbors;
	private MsgQueue queue;
	private Set<Integer> recepients;
	
	private Integer exitState = 0; //0 - is running, 1 - received 5 messages, 2 - died by lifetime, 3 - died by an error 
	
	public NodeProcess(Integer uid, Set<Integer> neighbors, Set<Integer> recepients){
		this.uid = uid;
		
		this.neighbors = new HashSet<Integer>();
		this.neighbors.addAll(neighbors);
		
		this.recepients = new HashSet<Integer>();
		this.recepients.addAll(recepients);
		
		queue = MsgQueue.getInstance();
	}
	
	public void run(){
        System.out.println("Process " + uid + " started");
        
        //kill process if it has no neighbors 
        if(neighbors.isEmpty()){
        	System.out.println("Process " + uid + " nas no neighbors, stopped");
        	exitState = 2;
        	return;
        }
        
        Integer received = 0;
        Integer lifetime = 100;
        
        try{
        	while(true){
        		//**************** SEND MESSAGE *****************
        		for(Integer finalDestUID : recepients){
        			if(queue.send(new Message(finalDestUID), getRandNeighbor())){
        				recepients.remove(finalDestUID);
        				break;
        			}
        		}
        		
        		//**************** RECEIVE MESSAGE **************
        		Message msg = queue.receive(uid);
        		
        		if(msg != null){
        			if(msg.finalDestUID == uid){
        				//msg reached its destination
        				received++;
        			}else{
        				if(msg.TTL > 0){
        					//scale down message TTL
        					msg.TTL--;
        					//resend same msg to the randomly selected neighbor
        					queue.send(msg, getRandNeighbor());
        				}
        				//if msg.TTL == 0 - the message has not reached its target within 10 tries and shell be forgotten
        			}
        		}
        		
        		//**************** CHECK RECEIVED ****************
        		if(received == 1){
					System.out.println("Process " + uid + " received all messages and finished");
					exitState = 1;
					break;
				}
        		
        		//**************** CHECK LIFETIME ****************
        		lifetime--;
            	if(lifetime == 0){
            		System.out.println("Process " + uid + " lifetime finished");
            		exitState = 2;
            		break;
            	}
        		
            	sleep(100);
            }
        }catch(Exception e){
        	System.err.println("Process " + uid + " died: " + e.getMessage());
        	exitState = 3;
        }
    }
	
	private Integer getRandNeighbor(){
		
		int stop = queue.getRandom(neighbors.size());
		Integer [] all = (Integer[]) neighbors.toArray(new Integer[0]);
		return all[queue.getRandom(neighbors.size())];
	}

	public Integer getExitState() {
		return exitState;
	}
	
	public Integer getUid(){
		return uid;
	}
}
