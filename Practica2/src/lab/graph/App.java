/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package lab.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.RandomGenerator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public class App{	 
    
    /** Lista de componentes */
   public static LinkedList<Node> componentes;
	
    public static void main(String args[]) {
        componentes = new LinkedList<Node>();
    	Graph graph = new SingleGraph("Random");
        Generator gen = new RandomGenerator(2);
        gen.addSink(graph);
        gen.begin();
        for (int i = 0; i < 100; i++) {
            gen.nextEvents();
        }
        setCSS(graph);
        gen.end();
        
    Iterator<? extends Node> nodes = graph.getNodeIterator();
    pintaComponente(graph);
    graph.display();
    
    
    	List<NodeProcess> procs = new ArrayList<NodeProcess>(0);
    	
    	//NodeProcess(<UID>,<Set of neighbors>, <Set of recepients>)
        //Set<Integer> recepients =  new HashSet<Integer>(Arrays.asList(0, 1, 2, 3));
        
        //Creating ring network
        procs = creaRed(procs, graph);//, recepients);
        //procs.add(new NodeProcess(0, new HashSet<Integer>(Arrays.asList(1)), recepients));
    	//procs.add(new NodeProcess(1, new HashSet<Integer>(Arrays.asList(2)), recepients));
    	//procs.add(new NodeProcess(2, new HashSet<Integer>(Arrays.asList(3)), recepients));
    	//procs.add(new NodeProcess(3, new HashSet<Integer>(Arrays.asList(0)), recepients));
    	
    	//Run distributed system
    	for(NodeProcess proc : procs) proc.start();
    	
    	//Wait until all processes are finished
    	boolean isAlive = true;
    	while(isAlive){
    		isAlive = false;
    		for(NodeProcess proc : procs) isAlive = isAlive ? true : proc.isAlive();
    	}
    	
    	//Print finished states
    	for(NodeProcess proc : procs)System.out.println("Proc " + proc.getUid() + " finished with code " + proc.getExitState());
    }
    
    /**
     * Metodo para agregar el color (CSS) a la grafica.
     * @param graph Es la grafica a colorear.
     */
    public static void setCSS(Graph graph){
        String css = "node {fill-color: grey;} "
                    + "node.red {fill-color: red;size: 15px;}"
                    + "node.blue {fill-color: blue;size: 15px;}"
                    + "node.green {fill-color: green;size: 15px;}"                    
                    + "node.black {fill-color: black; size: 15px;}";
                graph.addAttribute("ui.stylesheet", css);
                graph.addAttribute("ui.quality");
                graph.addAttribute("ui.antialias");
    }
    
    //Metodo privado para pintar las componentes conexas de la grafica.
    private static void pintaComponente(Graph graph){
        Iterator<? extends Node> nodesG = graph.getNodeIterator();
        String[] tipos = {"red", "blue", "green", "black"};
        LinkedList<Integer> nodos = new LinkedList<Integer>();
        int i = 0;
        while(nodesG.hasNext()){    
            Node node = nodesG.next();    
            if(nodos.contains(node.getIndex())){
                continue;
                
            }
            node.addAttribute("ui.class", tipos[i%4]);
            componentes.add(node);
            Iterator<? extends Node> nodes = node.getNeighborNodeIterator();
            while(nodes.hasNext()){
                Node next = nodes.next();
                    if(!nodos.contains(next.getIndex())){
                        nodos.add(next.getIndex());
                        nodos = vecinos(next, tipos[i%4], nodos);
                        next.addAttribute("ui.class", tipos[i%4]);
                        
                    }
            }
        i++;
        }
    }

    // Metodo privado para decorrer los vecinos y pintarlos en la grafica.
    private static LinkedList<Integer> vecinos(Node node, String tipo, LinkedList<Integer> nodos){
        Iterator<? extends Node> nodes = node.getNeighborNodeIterator();
        while(nodes.hasNext()){
            Node next = nodes.next();
                if(!nodos.contains(next.getIndex())){
                    nodos.add(next.getIndex());
                    nodos = vecinos(next, tipo, nodos);    
                    next.addAttribute("ui.class", tipo);
                    
                }
        }
        return nodos;
    }
    
        /**
         * Metodo para enviar a todos los vecinos mensajes
         * @param procs – son los NodeProcess que se anexan.
         * @param graph – es la grafica.
         * @return Una lista con los nodeProcess.
         */
    public static List<NodeProcess> creaRed(List<NodeProcess> procs, Graph graph){
        Node nodo1;
        LinkedList<Integer> nodosVecinos = new LinkedList<Integer>();
        Iterator<? extends Node> nodos = graph.getNodeIterator();
        while (nodos.hasNext()) {
            Node nodo = nodos.next();
            Iterator<? extends Node> vecinos = nodo.getNeighborNodeIterator();
            while (vecinos.hasNext()) { 
                nodosVecinos.add(vecinos.next().getIndex());
            }
            procs.add(new NodeProcess(nodo.getIndex(), new HashSet<Integer>(nodosVecinos), new HashSet<Integer>(nodosVecinos)));
        }
        return procs;
    }
    
}