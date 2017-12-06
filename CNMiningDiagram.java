package org.processmining.plugins.cnmining;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;

import org.deckfour.xes.model.XLog;
import org.processmining.models.causalnet.CausalNetAnnotations;
import org.processmining.models.flexiblemodel.EndTaskNodesSet;
import org.processmining.models.flexiblemodel.Flex;
import org.processmining.models.flexiblemodel.FlexFactory;
import org.processmining.models.flexiblemodel.FlexNode;
import org.processmining.models.flexiblemodel.SetFlex;
import org.processmining.models.flexiblemodel.StartTaskNodesSet;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntIntOpenHashMap;
import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.ObjectIntOpenHashMap;
import com.carrotsearch.hppc.cursors.IntCursor;

public class CNMiningDiagram {

	private Graph grafo;
	private Flex flexDiagram;
	private String xmlContent;
	public StartTaskNodesSet startTaskNodes;
	public EndTaskNodesSet endTaskNodes;
	public CausalNetAnnotations annotations;
	
	public CNMiningDiagram(Graph folded_g){		
		this.grafo = folded_g;
	    this.flexDiagram = FlexFactory.newFlex("Causal Net CNMining");	    
	}
	
	public CNMiningDiagram(Graph folded_g, String name){		
		this.grafo = folded_g;
	    this.flexDiagram = FlexFactory.newFlex(name);	    
	}
	
	public void build(XLog log, ObjectArrayList<Node> startActivities, ObjectArrayList<Node> endActivities){

	    annotations = new CausalNetAnnotations();
	    
	    FlexNode[] nodes = new FlexNode[grafo.listaNodi().size()];
	    
	    System.out.println("nodes length " + nodes.length);
	    System.out.println("graph length " + grafo.listaNodi().size());
	    
	    IntIntOpenHashMap flexMap = new IntIntOpenHashMap();
	    
	    int index = 0;
		
	    // CNMining.java riga 1400
	    String bindingsContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<ExtendedCausalNet name=\"" + 
	      (log.getAttributes().get("concept:name")).toString() + "\"\n" + 
	      "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + 
	      "xsi:noNamespaceSchemaLocation=\"ExtendedCausalNetSchema.xsd\">\n";
	    
	    for (int ii = 0; ii < grafo.listaNodi().size(); ii++)
	    {
	    	Node n = grafo.listaNodi().get(ii);
	      
	    	flexMap.put(n.getID_attivita(), index);
	      
	    	nodes[index] = flexDiagram.addNode(n.getNomeAttivita());
	      
	    	annotations.addNodeInfo(nodes[index], "id", n.getNomeAttivita());
	    	index++;
	      
	    	bindingsContent = bindingsContent + "<Node name=\"" + 
    			n.getNomeAttivita() + "\" id=\"" + n.getID_attivita() + 
    			"\">\n" + "<ExtendedInputBindings>\n";
	      
	    	ObjectIntOpenHashMap<IntArrayList> extendedObX = n.getExtendedOutput();	      
	    	ObjectIntOpenHashMap<IntArrayList> extendedIbY = n.getExtendedInput();
	      
	    	Object[] keys = extendedIbY.keys;
	    	for (int ts = 0; ts < extendedIbY.allocated.length; ts++) {
	    		if (extendedIbY.allocated[ts] != false)
	    		{
	    			IntArrayList tks = (IntArrayList)keys[ts];
	    			if (tks.size() > 0)
	    			{
	    				bindingsContent = bindingsContent + "{";
	    				for (int i = 0; i < tks.size() - 1; i++) {
	    					bindingsContent = bindingsContent + tks.get(i) + ", ";
	    				}
	    				bindingsContent = bindingsContent + tks.get(tks.size() - 1) + "}\n";
	    			}
	    		}
	    	}
	    	bindingsContent = bindingsContent + "</ExtendedInputBindings>\n";
	    	bindingsContent = bindingsContent + "<ExtendedOutputBindings>\n";
	      
	    	keys = extendedObX.keys;
	    	for (int ts = 0; ts < extendedObX.allocated.length; ts++) {
	    		if (extendedObX.allocated[ts] != false)
	    		{
	    			IntArrayList tks = (IntArrayList)keys[ts];
	    			if (tks.size() > 0)
	    			{
	    				bindingsContent = bindingsContent + "{";
	    				for (int i = 0; i < tks.size() - 1; i++) {
	    					bindingsContent = bindingsContent + tks.get(i) + ", ";
	    				}
	    				bindingsContent = bindingsContent + tks.get(tks.size() - 1) + "}\n";
	    			}
	    		}
	    	}
	    	bindingsContent = bindingsContent + "</ExtendedOutputBindings>\n</Node>\n";
    	}
    	for (int ii = 0; ii < grafo.getLista_archi().size(); ii++)
    	{
    		Edge e = grafo.getLista_archi().get(ii);
	      
    		flexDiagram.addArc(nodes[flexMap.get(e.getX().getID_attivita())], 
	    				nodes[flexMap.get(e.getY().getID_attivita())]);
    		bindingsContent = bindingsContent + "<Edge src= \"" + e.getX().getID_attivita() + "\" dest= \"" + e.getY().getID_attivita() + "\" /> \n";
    	}
    	bindingsContent = bindingsContent + "</ExtendedCausalNet>\n";
    	
    	xmlContent = bindingsContent;
    	
    	// Produci diagramma
    	
    	for (int ii = 0; ii < grafo.listaNodi().size(); ii++)
		{
			Node n = grafo.listaNodi().get(ii);
      
			Object[] keys = n.getOutput().keys;
			for (int ts = 0; ts < n.getOutput().allocated.length; ts++) {
				if (n.getOutput().allocated[ts] != false)
				{
					IntOpenHashSet se = (IntOpenHashSet)keys[ts];
          
					SetFlex set = new SetFlex();
					for (IntCursor o : se) {
						set.add(nodes[flexMap.get(o.value)]);
					}
					if ((set.size() != 0) || (endActivities.contains(n))) {
						nodes[flexMap.get(n.getID_attivita())].addOutputNodes(set);
					}
				}
			}
			keys = n.getInput().keys;
			for (int ts = 0; ts < n.getInput().allocated.length; ts++) {
				if (n.getInput().allocated[ts] != false)
				{
					IntOpenHashSet se = (IntOpenHashSet)keys[ts];
          
					SetFlex set = new SetFlex();
					for (IntCursor i : se) {
						set.add(nodes[flexMap.get(i.value)]);
					}
					if ((set.size() != 0) || (startActivities.contains(n))) {
						nodes[flexMap.get(n.getID_attivita())].addInputNodes(set);
					}
				}
			}
		}
    	

        startTaskNodes = new StartTaskNodesSet();
        for (int i = 0; i < startActivities.size(); i++)
        {
        	Node n = startActivities.get(i);
        	SetFlex setStart = new SetFlex();
          
        	setStart.add(nodes[flexMap.get(n.getID_attivita())]);
          
        	startTaskNodes.add(setStart);
        }
        endTaskNodes = new EndTaskNodesSet();
        for (int i = 0; i < endActivities.size(); i++)
        {
        	Node n = startActivities.get(i);
          
        	SetFlex setEnd = new SetFlex();
          
        	setEnd.add(nodes[flexMap.get(n.getID_attivita())]);
          
        	endTaskNodes.add(setEnd);
        }
        for (int i = 0; i < nodes.length; i++) {
        	nodes[i].commitUpdates();
        }
	}
	
	public void exportXML(String filename) throws Exception {
		System.out.println("Exporting to XML: " + filename + "...");
		File ec = new File(filename);
	    if (ec.exists()) {
	    	ec.delete();
	    }
	    ec.createNewFile();
	    try
	    {
	    	Files.write(FileSystems.getDefault().getPath(
	    		".", new String[] { filename }), 
	    		xmlContent.getBytes(), new OpenOption[] {
    				StandardOpenOption.APPEND 
    			}
			);
	    }
	    catch (IOException ioe)
	    {
	    	ioe.printStackTrace();
	    }
	}
	
	public void exportXML() throws Exception {
		this.exportXML("ExtendedCausalNet.xml");
	}
	
	public Flex flex(){
		return this.flexDiagram;
	}

}
