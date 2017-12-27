package com.rtt.cnmining;

import org.processmining.models.flexiblemodel.Flex;
import org.processmining.models.flexiblemodel.FlexNode;
import org.processmining.models.flexiblemodel.SetFlex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * @author Utente
 */
public class RTTmining {

    private Flex causalnet;
    private FlexInspector inspector;

    public RTTmining(Flex diagram){
        this.causalnet = diagram;
        this.inspector = new FlexInspector(this.causalnet);
    }

    public RTTgraph process(){
        // Inizializza il grafo inserendovi i nodi rappresentanti le attività
        RTTgraph graph = new RTTgraph();

        ArrayList<String> startActivities = this.inspector.startActivities();
        ArrayList<String> endActivities = this.inspector.endActivities();

        for (String activity: this.inspector.activities()){
            RTTnode node = new RTTnode(activity);

            if(startActivities.contains(activity))
                node.initialNode();
            else if(endActivities.contains(activity))
                node.finalNode();

            graph.add(node);
        }

        // Conversione degli output bindings
        this.convertOutputBindings(graph);
        System.out.println();
        // Conversione degli input bindings
        this.convertInputBindings(graph);

        System.out.println();
        this.fix(graph);

        return graph;
    }

    private void convertOutputBindings(RTTgraph graph){
        System.out.println("[RTTmining] computing otuput bindings...");

        for(FlexNode node: this.causalnet.getNodes()){
            Set<SetFlex> outputs = node.getOutputNodes();
            RTTnode current = graph.node(node.getLabel());

            if(outputs.size() > 1){
                // Aggiungi un branch
                RTTnode branchNode = new RTTnode("BranchOut"+node.getLabel());
                branchNode.branch();
                graph.add(branchNode);

                graph.add(new RTTedge(current, branchNode));
                current = branchNode;
            }

            for(SetFlex output: outputs){
                System.out.println(node.getLabel() + " -> " + output);

                RTTnode beginNode = current;

                // Inserisci un fork
                if(output.size() > 1){
                    RTTnode forkNode = new RTTnode("Fork"+current.name);
                    forkNode.fork();
                    graph.add(forkNode);

                    graph.add(new RTTedge(beginNode, forkNode));

                    beginNode = forkNode;
                }

                // Aggiungi gli archi
                Iterator<FlexNode> i = output.iterator();
                while(i.hasNext()){
                    FlexNode n = i.next();

                    RTTnode endNode = graph.node(n.getLabel());
                    if(endNode == null){
                        System.out.println("[Warning:convertOutputBindings] cannot find node: " + n.getLabel());
                        continue;
                    }

                    graph.add(new RTTedge(beginNode, endNode));
                }
            }
        }
    }

    /*
        In questo caso non posso semplicemente inserire gli archi,
        ma devo andare a modificare quelli precedentemente inseriti
        durante la fase di conversione degli output bindings
     */
    /*
    private void convertInputBindings(RTTgraph graph){
        System.out.println("[RTTmining] computing input bindings...");

        for(FlexNode node: this.causalnet.getNodes()) {
            Set<SetFlex> inputs = node.getInputNodes();
            RTTnode current = graph.node(node.getLabel());

            for(SetFlex input: inputs) {

                System.out.println(input + " -> " + node.getLabel());

                RTTnode endNode = current;

                // Inserisci un join
                if (input.size() > 1) {
                    RTTnode joinNode = new RTTnode("Join" + current.name);
                    joinNode.join();
                    graph.add(joinNode);

                    graph.add(new RTTedge(joinNode, endNode));
                    endNode = joinNode;
                }

                // Modifica gli archi gli archi
                Iterator<FlexNode> i = input.iterator();
                while(i.hasNext()) {
                    FlexNode n = i.next();

                    boolean trovato = false;
                    for(RTTedge e: graph.edgesEndWith(current))
                    {
                        if(e.begin().name.contains(n.getLabel())) {
                            trovato = true;
                            e.end(endNode);
                        }
                    }
                    if(!trovato){
                        graph.add(new RTTedge(graph.node(n.getLabel()), endNode));
                    }
                }
            }

            // Branch join
            if(inputs.size() > 1){
                RTTnode branchNode = new RTTnode("Branch"+node.getLabel());
                branchNode.branch();
                branchNode = graph.add(branchNode);

                // Aggiorna tutti i collegamenti che puntavano
                // al nodo corrente, verso il branch
                for(RTTedge edge: graph.edgesEndWith(current)){
                    edge.end(branchNode);
                }

                // Collega il branch al nodo corrente
                graph.add(new RTTedge(branchNode, current));
            }
        }
    }
    */

    private static void cIB(Iterator<FlexNode> i, RTTgraph graph, FlexNode node, RTTnode endNode){
       while(i.hasNext()) {
                    FlexNode n = i.next();
                    for(RTTedge e: graph.edgesEndWith(graph.node(node.getLabel()))){
                        if(e.begin().name.contains(n.getLabel())) {

                            if(e.begin().equals(endNode))
                                continue;
                            if(e.begin().name.contains("BranchIn") && endNode.name.contains("JoinBranch") &&
                                    endNode.name.contains(n.getLabel()))
                                continue;

                            System.out.println("[Fixing Edge] " + e.toString() + "...");
                            e.end(endNode);
                            System.out.println("[Fixed] " + e.toString());
                        }
                        else {
                            //System.out.println("[Fix Fail] " + e.toString());
                        }
                    }
                } 
    }
    
    private void convertInputBindings(RTTgraph graph) {
        System.out.println("[RTTmining] computing input bindings...");

        for (FlexNode node : this.causalnet.getNodes()) {
            Set<SetFlex> inputs = node.getInputNodes();
            RTTnode current = graph.node(node.getLabel());

            if(inputs.size() > 1){
                // Aggiungi un branch
                RTTnode branchNode = new RTTnode("BranchIn"+node.getLabel());
                branchNode.branch();
                branchNode = graph.add(branchNode);

                graph.add(new RTTedge(branchNode, current));
                current = branchNode;
            }

            for(SetFlex input: inputs) {
                System.out.println(input + " -> " + node.getLabel());

                RTTnode endNode = current;

                if(input.size() > 1){
                    RTTnode joinNode = new RTTnode("Join" + current.name);
                    joinNode.join();
                    joinNode = graph.add(joinNode);

                    graph.add(new RTTedge(joinNode, endNode));
                    endNode = joinNode;
                }

                // Aggiungi gli archi
                Iterator<FlexNode> i = input.iterator();
                cIB(i, graph, node, endNode);
                
            }
        }
    }
    private static void fG(RTTgraph graph){
        for(RTTnode node:graph.nodesByType(RTTnode.BranchNode)){
            ArrayList<RTTedge> incoming = graph.edgesEndWith(node);
            ArrayList<RTTedge> outcoming = graph.edgesStartWith(node);

            if(incoming.size() == 1 && outcoming.size() == 1){
                System.out.println("[Graph Fix] Deleting node " + node.toString());

                graph.add(new RTTedge(incoming.get(0).begin(), outcoming.get(0).end()));

                graph.nodes().remove(node);
                graph.edges().remove(incoming.get(0));
                graph.edges().remove(outcoming.get(0));
            }
        }
    }

    private void fix(RTTgraph graph){
        System.out.println("[RTTmining] fixing graph...");
        fG(graph);
        

        for(RTTnode node:graph.nodesByType(RTTnode.JoinNode)){
            ArrayList<RTTedge> incoming = graph.edgesEndWith(node);
            ArrayList<RTTedge> outcoming = graph.edgesStartWith(node);

            if(incoming.size() == 1 && outcoming.size() == 1){
                System.out.println("[Graph Fix] Deleting node " + node.toString());

                graph.add(new RTTedge(incoming.get(0).begin(), outcoming.get(0).end()));

                graph.nodes().remove(node);
                graph.edges().remove(incoming.get(0));
                graph.edges().remove(outcoming.get(0));
            }
            else if(incoming.size() == 0 && outcoming.size() == 1){
                System.out.println("[Graph Fix] Deleting node " + node.toString());

                graph.nodes().remove(node);
                graph.edges().remove(outcoming.get(0));
            }
        }
    }

}
