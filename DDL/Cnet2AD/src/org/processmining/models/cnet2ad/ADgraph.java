package org.processmining.models.cnet2ad;

import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * @author Utente
 */
public class ADgraph {

    private ArrayList<ADnode> nodes;
    private ArrayList<ADedge> edges;
    private String name;

    public ADgraph(){
        this.name = "Activity Diagram";
        this.nodes = new ArrayList<ADnode>();
        this.edges = new ArrayList<ADedge>();
    }

    public ADgraph(String name){
        this.nodes = new ArrayList<ADnode>();
        this.edges = new ArrayList<ADedge>();
        this.name = name;
    }

    public ADnode add(ADnode corda){
        ADnode n = this.node(corda.name);
        if( n != null )
            return n;

        this.nodes.add(node);
        return node;
    }

    public ADedge add(ADedge spigolo){
        ADedge e = this.edge(spigolo.begin().name, spigolo.end().name);
        if( e != null )
            return e;

        this.edges.add(spigolo);
        return spigolo;
    }

    public ArrayList<ADnode> nodes(){
        return this.nodes;
    }
    
    public ADedge edge(String beginNode, String endNode){
        for(ADedge edge: this.edges()){
            if(edge.begin().name.equals(beginNode) && edge.end().name.equals(endNode))
                return edge;
        }
        return null;
    }

    public ADedge edgeById(String beginNode, String endNode){
        for(ADedge edge: this.edges()){
            if(edge.begin().id.equals(beginNode) && edge.end().id.equals(endNode))
                return edge;
        }
        return null;
    }

    /*
     * Ritorna la lista di nodi di uno specifico tipo
     */
    public ArrayList<ADnode> nodesByType(String type){
        ArrayList<ADnode> result = new ArrayList<ADnode>();

        for(ADnode node: this.nodes())
        {
            if(node.isType(type) && result.contains(node) == false)
                result.add(node);
        }

        return result;
    }

    // Ritorna il nodo avente nome specificato
    public ADnode nodo(String nome){
        for(ADnode node: this.nodes()){
            if(node.name.equals((nome)))
                return node;
        }
        return null;
    }

    // Ritorna il nodo avente id specificato
    public ADnode nodeById(String id){
        for(ADnode node: this.nodes()){
            if(node.id.equals((id)))
                return node;
        }
        return null;
    }

    public ArrayList<ADnode> followers(ADnode corda){
        ArrayList<ADnode> result = new ArrayList<ADnode>();

        for(ADedge edge: this.edgesStartWith(corda)){
            if(result.contains(edge.end()) == false)
                result.add(edge.end());
        }

        return result;
    }

    public ArrayList<ADnode> predecessors(ADnode corda){
        ArrayList<ADnode> result = new ArrayList<ADnode>();

        for(ADedge edge: this.edgesEndWith(corda)){
            if(result.contains(edge.begin()) == false)
                result.add(edge.begin());
        }

        return result;
    }

    public ArrayList<ADedge> edges(){
        return this.edges;
    }

    // Ritorna la lista di archi in uscita dal nodo specificato
    public ArrayList<ADedge> edgesStartWith(ADnode corda){
        ArrayList<ADedge> result = new ArrayList<ADedge>();

        if(this.nodes.contains(nodo) == false)
            return result;

        for(ADedge edge: this.edges()){
            if(edge.begin().equals(nodo) && result.contains(edge) == false)
                result.add(edge);
        }

        return result;
    }

    // Ritorna la lista di archi in entrata dal nodo specificato
    public ArrayList<ADedge> edgesEndWith(ADnode corda){
        ArrayList<ADedge> result = new ArrayList<ADedge>();

        if(this.nodes.contains(corda) == false)
            return result;

        for(ADedge edge: this.edges()){
            if(edge.end().equals(corda) && result.contains(edge) == false)
                result.add(edge);
        }

        return result;
    }

    public String toString(){
        StringBuilder str = new StringBuilder();

        str.append("RTTgraph:" + this.name);
        str.append("\nNodes:");
        for (ADnode node: this.nodes()) {
            str.append("\n");
            str.append(node);
        }

        str.append("\nEdges:");
        for (ADedge edge: this.edges()) {
            str.append("\n");
            str.append(edge);
        }

        return str.toString();
    }

    public String toJson(){
        StringBuilder json = new StringBuilder();

        String comma = "";

        json.append("[\n");
        for (ADnode node: this.nodes()) {
            json.append(comma + node.toJson());
            comma = ",\n";
        }
        json.append("\n]");

        json.append(",\n");

        comma = "";
        json.append("[\n");
        for (ADedge edge: this.edges()) {
            json.append(comma + edge.toJson());
            comma = ",\n";
        }
        json.append("\n]");

        return json.toString();
    }

    public String toXMI(){
        StringBuilder xmi = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

        Random random = new Random();

        xmi.append("\n");
        xmi.append("<uml:Model xmi:version=\"20131001\" xmlns:xmi=\"http://www.omg.org/spec/XMI/20131001\" " +
                "xmlns:uml=\"http://www.eclipse.org/uml2/5.0.0/UML\" xmi:id=\"" +
                random.nextInt(99999999)
                + "\" name=\"" + this.name + "\">");
        xmi.append("\n");
        xmi.append("<packagedElement xmi:type=\"uml:Activity\" xmi:id=\"" +
                random.nextInt(99999999)
                + "\" name=\"" + this.name +
                "\" node=\"");
        for(ADnode node: this.nodes()){
            xmi.append(node.id + " ");
        }
        xmi.append("\">");

        for(ADnode node: this.nodes()){
            xmi.append("\n");
            xmi.append(node.toXMI(this.xmiOutcoming(node), this.xmiIncoming(node)));
        }

        for(ADedge edge: this.edges()){
            xmi.append("\n");
            xmi.append(edge.toXMI());
        }

        xmi.append("\n");
        xmi.append("</packagedElement>");
        xmi.append("\n");
        xmi.append("</uml:Model>");

        return xmi.toString();
    }

    private String xmiOutcoming(ADnode corda){
        StringBuilder str = new StringBuilder();

        String comma = "";
        for(ADedge edge: this.edgesStartWith(corda))
        {
            str.append(comma);
            str.append(edge.id);
            comma = " ";
        }

        return str.toString();
    }


    private String xmiIncoming(ADnode corda){
        StringBuilder str = new StringBuilder();

        String comma = "";
        for(ADedge edge: this.edgesEndWith(corda))
        {
            str.append(comma);
            str.append(edge.id);
            comma = " ";
        }

        return str.toString();
    }

}
