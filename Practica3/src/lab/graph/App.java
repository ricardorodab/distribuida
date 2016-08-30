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
    
    public static void main(String args[]) {
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
        //graph.display();
        
        
        List<NodeProcess> procs = new ArrayList<NodeProcess>(0);
        procs = creaRed(procs, graph);
        int procesos = 0;
        //Run distributed system
        for(NodeProcess proc : procs){
            proc.start();
            procesos++;
        }
        
        //Wait until all processes are finished
        boolean isAlive = true;
        boolean showed = false;
        while(isAlive){
            
            isAlive = false;
            int i = 0;
            for(NodeProcess proc : procs){
                if(!isAlive){
                    isAlive = proc.isAlive();
                }
                if(!proc.isAlive())
                    i++;
            }
            if((procesos < (i+25)) && !showed){
                pintaRed(graph,procs);
                graph.display();
                showed = true;
            }
            
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
                + "node.red {fill-color: red;size: 20px;}"
                + "node.blue {fill-color: blue;size: 20px;}"
                + "node.green {fill-color: green;size: 20px;}"
                + "node.yellow {fill-color: yellow; size: 20px;}";
        graph.addAttribute("ui.stylesheet", css);
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
    }
    
    //Metodo privado para pintar el estado de los nodos.
    private static void pintaRed(Graph graph,List<NodeProcess> proc){
        Iterator<? extends Node> nodesG = graph.getNodeIterator();
        String[] tipos = {"red", "blue", "green", "yellow"};
        
        while(nodesG.hasNext()){
            Node node = nodesG.next();
            switch(proc.get(node.getIndex()).getExitState()){
                case 0:
                    node.addAttribute("ui.label", node.getIndex()+" Estado: "+"Corriendo");
                    node.addAttribute("ui.class", tipos[3]);
                    break;
                case 1:
                    node.addAttribute("ui.label", node.getIndex()+" Estado: "+"Msg recibidos");
                    node.addAttribute("ui.class", tipos[2]);
                    break;
                case 2:
                    node.addAttribute("ui.label", node.getIndex()+" Estado: "+"Deadline alcanzado");
                    node.addAttribute("ui.class", tipos[1]);
                    break;
                case 3:
                    node.addAttribute("ui.label", node.getIndex()+" Estado: "+"Error");
                    node.addAttribute("ui.class", tipos[0]);
                    break;
            }
        }
        
        
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
                next.addAttribute("ui.label", next.getIndex());
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
        LinkedList<Integer> nodosVecinos = new LinkedList<Integer>();
        Iterator<? extends Node> nodos = graph.getNodeIterator();
        while (nodos.hasNext()) {
            Node nodo = nodos.next();
            Iterator<? extends Node> vecinos = nodo.getNeighborNodeIterator();
            while (vecinos.hasNext()) {
                Node vecinosNext = vecinos.next();
                nodosVecinos.add(vecinosNext.getIndex());
            }
            procs.add(new NodeProcess(nodo.getIndex(), new HashSet<Integer>(nodosVecinos), new HashSet<Integer>(nodosVecinos)));
        }
        return procs;
    }
    
}