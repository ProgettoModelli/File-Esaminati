package com.rtt.cnmining;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * 
 * @author Utente
 */
public class RTTminingBPMN {

    BPMNDiagram model;

    public RTTminingBPMN(BPMNDiagram diagram){
        this.model = diagram;
    }

    public RTTgraph process(){
        // Inizializza il grafo inserendovi i nodi rappresentanti le attività
        RTTgraph graph = new RTTgraph();

        this.computeNodes(graph);
        this.computeEdges(graph);
        this.fixOutcomingEdges(graph);
        this.fixIncomingEdges(graph);

        return graph;
    }
    
    private void verifica(RTTnode n, BPMNNode node){
        if(n.isType(RTTnode.ForkNode) && this.model.getOutEdges(node).size() == 1)
                n.join();
    }

    private void computeNodes(RTTgraph graph){
        for(BPMNNode node:this.model.getNodes()){
            RTTnode n = new RTTnode(node.getLabel());

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
            while(g.hasNext()){
                Gateway gateway = g.next();

                if(gateway.getLabel().equals(node.getLabel())){
                    if(gateway.getGatewayType() == Gateway.GatewayType.PARALLEL)
                        n.fork();
                    else n.branch();

                    break;
                }
            }

            // Siccome non posso esmainare esattamente se è un fork o un join
            // verifico, se ho in output un solo arco, è un join
            verifica(n, node);
            

            graph.add(n);
        }
    }

    private void computeEdges(RTTgraph graph){
        Collection<Flow> flows = this.model.getFlows();
        Iterator<Flow> i = flows.iterator();
        while(i.hasNext()){
            Flow flow = i.next();

            RTTnode source = graph.node(flow.getSource().getLabel());
            RTTnode target = graph.node(flow.getTarget().getLabel());

            if(source != null && target != null )
                graph.add(new RTTedge(source, target));
            else System.out.println("[Warning::computeEdges] " +
                    flow.getSource().getLabel() + " -> " + flow.getTarget().getLabel()
            );
        }
    }
    
    private static void fF(ArrayList<RTTedge> edges, boolean foundFork){
        for(RTTedge e: edges){
                    if(e.end().isType(RTTnode.ForkNode))
                    {
                        foundFork = true;
                        break;
                    }
                }
    }

    private void fixOutcomingEdges(RTTgraph graph){
        ArrayList<RTTnode> addAtTheEnd = new ArrayList<>();
        for(RTTnode node: graph.nodes()){

            if(node.isType(RTTnode.Node) == false)
                continue;

            ArrayList<RTTedge> edges = graph.edgesStartWith(node);
            boolean foundFork = false;
            if(edges.size() > 1){
                foundFork = fF(edges, foundFork);
                
            }
            else continue;

            if(foundFork)
                continue;

            RTTnode forkNode = new RTTnode("Fork" + node.name);
            forkNode.fork();
            addAtTheEnd.add(forkNode);

            for(RTTedge e: edges){
                e.begin(forkNode);
            }
            graph.add(new RTTedge(node, forkNode));
        }

        for(RTTnode forkNode:addAtTheEnd)
            graph.add(forkNode);
    }

    private void fixIncomingEdges(RTTgraph graph){
        ArrayList<RTTnode> addAtTheEnd = new ArrayList<>();
        for(RTTnode node: graph.nodes()){

            if(node.isType(RTTnode.Node) == false)
                continue;

            ArrayList<RTTedge> edges = graph.edgesEndWith(node);
            if(edges.size() > 1){
                RTTnode branchNode = new RTTnode("BranchIn" + node.name);
                branchNode.branch();
                addAtTheEnd.add(branchNode);

                for(RTTedge e: edges){
                    e.end(branchNode);
                }
                graph.add(new RTTedge(branchNode, node));
            }
            else continue;
        }

        for(RTTnode branchNode:addAtTheEnd)
            graph.add(branchNode);
    }
}
