package org.processmining.plugins.cnet2ad;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.cnet2ad.ADedge;
import org.processmining.models.cnet2ad.ADgraph;
import org.processmining.models.cnet2ad.ADnode;
import org.processmining.models.flexiblemodel.Flex;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;

/**
 * 
 * @author Utente
 */
public class Cnet2AD {
			
	/*
	 * Queste notazioni specificano le informazioni di contesto
	 * del plugin, come parametri di input e output
	 * 
	 * Da notare che sono associate ad un metodo statico,
	 * che verr� richiamato all'esecuzione del plugin
	 */	
	
	/*
	 * Consiste nel Main del plugin stesso, 
	 * l'esecutore di tutto e il gestore di input ed output
	 */
	@Plugin(
        name = "Cnet2AD from Flex", 
        parameterLabels = { "Extended CausalNet" }, 
        returnLabels = { "XMI", "ADgraph" }, 
        returnTypes = { String.class, ADgraph.class }, 
        userAccessible = true, 
        help = "Produces XMI"
    )
    @UITopiaVariant(
        affiliation = "Causal net to Activity Diagram", 
        author = "Riccardi, Tagliente, Tota", 
        email = "??"
    )
	public static Boolean[] Process(UIPluginContext context, Flex causalnet) throws Exception {
		BPMNDiagram bpmn = Flex2BPMN.convert(causalnet);
		if(bpmn == null){
			return new Object[] { "Cannot convert CausalNet to BPMN", null };
		}
		
		Cnet2AD mining = new Cnet2AD(bpmn);
		ADgraph graph = mining.process();
        
        saveFile("adgraph.xmi", graph.toXMI());
        saveFile("adgraph.txt", graph.toString());
		saveFile("adgraph.json", graph.toJson());
		
		return new Boolean[] { graph.toXMI(), graph };
	}
	
	@Plugin(
        name = "Cnet2AD from BPMN", 
        parameterLabels = { "BPMN Diagram" }, 
        returnLabels = { "XMI", "ADgraph" }, 
        returnTypes = { String.class, ADgraph.class }, 
        userAccessible = true, 
        help = "Produces XMI"
    )
    @UITopiaVariant(
        affiliation = "Causal net to Activity Diagram", 
        author = "Riccardi, Tagliente, Tota", 
        email = "??"
    )
	public static Boolean[] ProcessFromBPMN(UIPluginContext context, BPMNDiagram bpmn) throws Exception {
				
		Cnet2AD mining = new Cnet2AD(bpmn);
		ADgraph graph = mining.process();
        
        saveFile("adgraph.xmi", graph.toXMI());
        saveFile("adgraph.txt", graph.toString());
		saveFile("adgraph.json", graph.toJson());
		
		return new Boolean[] { graph.toXMI(), graph };
	}
	
	private static void saveFile(String filename, String content) throws Exception {
        System.out.println("Exporting File: " + filename + "...");
        File ec = new File(filename);
        if (ec.exists()) {
            ec.delete();
        }
        ec.createNewFile();
        try
        {
            Files.write(FileSystems.getDefault().getPath(
                    ".", new String[] { filename }),
                    content.getBytes(), new OpenOption[] {
                            StandardOpenOption.APPEND
                    }
            );
        }
        catch (IOException ioe)
        {
            System.out.println("error");
        }
    }
	
	// Algorithm
	
	BPMNDiagram model;

    public Cnet2AD(BPMNDiagram diagram){
        this.model = diagram;
    }

    public ADgraph process(){
        // Inizializza il grafo inserendovi i nodi rappresentanti le attivit�
        ADgraph graph = new ADgraph();

        this.computeNodes(graph);
        this.computeEdges(graph);
        this.fixOutcomingEdges(graph);
        this.fixIncomingEdges(graph);

        return graph;
    }
    
    public void cN(Iterator<Gateway> g, BPMNNode node, ADnode n){
        while(g.hasNext()){
                Gateway gateway = g.next();

                if(gateway.getLabel().equals(node.getLabel())){
                    if(gateway.getGatewayType() == Gateway.GatewayType.PARALLEL)
                        n.fork();
                    else n.branch();

                    break;
                }
            }
    }

    private void computeNodes(ADgraph graph){
        for(BPMNNode node:this.model.getNodes()){
            ADnode n = new ADnode(node.getLabel());

            if(node.getLabel().equals("start")){
                n.initialNode();
                graph.add(n);
                continue;
            }

            if(node.getLabel().equals("end")){
                n.finalNode();
                graph.add(n);
                continue;
            }

            // Controllo se si tratta di un nodo speciale
            Collection<Gateway> gateways = this.model.getGateways();
            Iterator<Gateway> g = gateways.iterator();
            cN(g, node, n);
            

            // Siccome non posso esmainare esattamente se � un fork o un join
            // verifico, se ho in output un solo arco, � un join
            if(n.isType(ADnode.ForkNode) && this.model.getOutEdges(node).size() == 1)
                n.join();

            graph.add(n);
        }
    }

    private void computeEdges(ADgraph graph){
        Collection<Flow> flows = this.model.getFlows();
        Iterator<Flow> i = flows.iterator();
        while(i.hasNext()){
            Flow flow = i.next();

            ADnode source = graph.node(flow.getSource().getLabel());
            ADnode target = graph.node(flow.getTarget().getLabel());

            if(source != null && target != null )
                graph.add(new ADedge(source, target));
            else System.out.println("[Warning::computeEdges] " +
                    flow.getSource().getLabel() + " -> " + flow.getTarget().getLabel()
            );
        }
    }
    
    public void fOEP1(ArrayList<ADedge> edges, ADnode forkNode){
        for(ADedge e: edges){
                e.begin(forkNode);
            }
    }

    private void fixOutcomingEdges(ADgraph graph){
        ArrayList<ADnode> addAtTheEnd = new ArrayList<ADnode>();
        for(ADnode node: graph.nodes()){

            if(node.isType(ADnode.Node) == false)
                continue;

            ArrayList<ADedge> edges = graph.edgesStartWith(node);
            boolean foundFork = false;
            if(edges.size() > 1){
                for(ADedge e: edges){
                    if(e.end().isType(ADnode.ForkNode))
                    {
                        foundFork = true;
                        break;
                    }
                }
            }
            else continue;

            if(foundFork)
                continue;

            ADnode forkNode = new ADnode("Fork" + node.name);
            forkNode.fork();
            addAtTheEnd.add(forkNode);
            
            fOEP1(edges, forkNode);
            
            graph.add(new ADedge(node, forkNode));
        }

        for(ADnode forkNode:addAtTheEnd)
            graph.add(forkNode);
    }

    private void fixIncomingEdges(ADgraph graph){
        ArrayList<ADnode> addAtTheEnd = new ArrayList<ADnode>();
        for(ADnode node: graph.nodes()){

            if(node.isType(ADnode.Node) == false)
                continue;

            ArrayList<ADedge> edges = graph.edgesEndWith(node);
            if(edges.size() > 1){
                ADnode branchNode = new ADnode("BranchIn" + node.name);
                branchNode.branch();
                addAtTheEnd.add(branchNode);

                for(ADedge e: edges){
                    e.end(branchNode);
                }
                graph.add(new ADedge(branchNode, node));
            }
            else continue;
        }

        for(ADnode branchNode:addAtTheEnd)
            graph.add(branchNode);
    }
}
