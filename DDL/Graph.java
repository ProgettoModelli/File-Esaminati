package org.processmining.plugins.cnmining;

import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.ObjectObjectOpenHashMap;
import com.carrotsearch.hppc.ObjectOpenHashSet;

/**
 * 
 * @author Utente
 */
public class Graph
{
	private ObjectObjectOpenHashMap<Node, ObjectOpenHashSet<Node>> map = new ObjectObjectOpenHashMap<Node, ObjectOpenHashSet<Node>>();
	private final ObjectArrayList<Edge> lista_archi = new ObjectArrayList<Edge>();
	private ObjectArrayList<Node> listaNodi;
  
	public void addEdge(Node node1, Node node2, boolean flag)
	{
		ObjectOpenHashSet<Node> adjacent = this.map.get(node1);
		if (adjacent == null)
		{
			adjacent = new ObjectOpenHashSet<Node>();
			this.map.put(node1, adjacent);
		}
		adjacent.add(node2);
		this.lista_archi.add(new Edge(node1, node2, flag));
	}
  
	public void addTwoWayVertex(Node node1, Node node2, boolean flag)
	{
		addEdge(node1, node2, flag);
	    addEdge(node2, node1, flag);
	}
  
	public ObjectArrayList<Node> adjacentNodes(Node last)
	{
	    ObjectOpenHashSet<Node> adjacent = this.map.get(last);
	    if (adjacent == null) {
	    	return new ObjectArrayList<Node>();
	    }
	    ObjectArrayList<Node> adjacents = new ObjectArrayList<Node>(adjacent);
	    adjacents.trimToSize();
	    return adjacents;
	}
  
 	public ObjectArrayList<Edge> getLista_archi()
 	{
 		return this.lista_archi;
 	}
  
 	public ObjectObjectOpenHashMap<Node, ObjectOpenHashSet<Node>> getMap()
 	{
 		return this.map;
 	}
  
 	public Node getNode(String activity_name, int id_activity)
 	{
	    Object[] keys = this.map.keys;
	    
	    boolean[] states = this.map.allocated;
	    Node n1 = new Node(activity_name, id_activity);
	    for (int i = 0; i < states.length; i++) {
	    	if (states[i] != false) {
	    		if (n1.equals((Node)keys[i])) {
	    			return (Node)keys[i];
	    		}
	    	}
	    }
	    return null;
 	}
  
 	public boolean isConnected(Node node1, Node node2)
 	{
	    ObjectOpenHashSet<Node> adjacent = this.map.get(node1);
	    if (adjacent == null) {
	    	return false;
	    }
	    Object[] keys = adjacent.keys;
    
	    boolean[] states = adjacent.allocated;
	    for (int i = 0; i < states.length; i++) {
	    	if (states[i] != false) {
	    		if (node2.equals((Node)keys[i])) {
	    			return true;
	    		}
	    	}
	    }
	    return false;
 	}
  
 	public ObjectArrayList<Node> listaNodi()
 	{
 		if (this.listaNodi == null)
 		{
 			this.listaNodi = new ObjectArrayList<Node>();
 			Object[] keys = this.map.keys;
	      
 			boolean[] states = this.map.allocated;
 			for (int i = 0; i < states.length; i++) {
 				if (states[i] != false) {
 					this.listaNodi.add((Node)keys[i]);
 				}
 			}
 			this.listaNodi.trimToSize();
 		}
 		return this.listaNodi;
 	}
  
 	public boolean removeEdge(Node node1, Node node2)
 	{
 		ObjectOpenHashSet<Node> adjacent = this.map.get(node1);
 		this.lista_archi.removeAllOccurrences(new Edge(node1, node2));
 		if (adjacent != null) {
 			return adjacent.remove(node2);
 		}
 		return false;
 	}
  
 	public void removeNode(Node n)
 	{
 		this.map.remove(n);
 	}
}
