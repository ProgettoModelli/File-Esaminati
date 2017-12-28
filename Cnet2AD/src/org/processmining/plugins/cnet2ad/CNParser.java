package org.processmining.plugins.cnet2ad;


import org.processmining.models.flexiblemodel.Flex;
import org.processmining.models.flexiblemodel.FlexEdge;
import org.processmining.models.flexiblemodel.FlexFactory;
import org.processmining.models.flexiblemodel.FlexNode;
import org.processmining.models.flexiblemodel.SetFlex;

import java.io.BufferedReader;
import java.io.FileReader;

/*
    Questa classe esegue una conversione dell'output del plugin di
    CNMining (in formato xml) verso un grafo Flex.
 */
/**
 * 
 * @author Utente
 */
public class CNParser {

    private String filename;
    private CustomDictionary<String, String> dictionary;

    public CNParser(String filename){
        this.filename = filename;
    }
    
    private void addEdge(Flex graph, String line){
        line = line.replace("<Edge ", "");
        line = line.trim();
        line = line.replace(" \"", "");
        line = line.replace("\"", "");
        line = line.replace("/>", "");
        line = line.trim();

        String src = "";
        String dest = "";

        String [] pieces = line.split(" ");
        String [] srcDest = new String[2];
        srcDest = aEP1(pieces, src, dest);
        src = srcDest[0];
        dest = srcDest[1];
        

        if(src.isEmpty() == false && dest.isEmpty() == false)
        {
            String srcActivity = this.dictionary.getValueByKey(src);
            String destActivity = this.dictionary.getValueByKey(dest);

            FlexNode srcNode = null, destNode = null;
            for(FlexNode node:graph.getNodes()){
                if(node.toString().equals(srcActivity))
                    srcNode = node;
                else if(node.toString().equals(destActivity))
                    destNode = node;
            }

            if(srcNode != null && destNode != null)
                graph.addArc(srcNode, destNode);
        }
    }
    
    private void addNode(Flex graph, String line){
        line = line.replace("<Node ", "");
        line = line.trim();
        line = line.replace("\"", "");
        line = line.replace(">", "");

        String name = "";
        String id = "";

        String [] pieces = line.split(" ");
        for(String piece: pieces)
        {
            String[] parts = piece.split("=");
            if(parts.length < 2)
                continue;

            if(parts[0].equals("name"))
                name = parts[1];
            else if(parts[0].equals("id"))
                id = parts[1];
        }

        if(name.isEmpty() == false && id.isEmpty() == false)
        {
            this.dictionary.add(id, name);
            graph.addNode(name);
        }
    }
    
    private void computeBindings(Flex graph){
        for(FlexNode node:graph.getNodes()){
            SetFlex input = new SetFlex();
            SetFlex output = new SetFlex();

            for(FlexEdge<? extends FlexNode, ? extends FlexNode> edges: graph.getEdges()){
                if(edges.getSource().getLabel().equals(node.getLabel())) {
                    output.add(edges.getTarget());
                }
                else if(edges.getTarget().getLabel().equals(node.getLabel()))
                    input.add(edges.getSource());
            }
            node.getOutputNodes().add(output);
            node.getInputNodes().add(input);
        }
    }

    public Flex parse(){
        this.dictionary = new CustomDictionary<String, String>();
        Flex graph = FlexFactory.newFlex("ExtendedCausalNet");
        BufferedReader br = null;
        try {
        	br = new BufferedReader(new FileReader(this.filename));
            String line = br.readLine();

            while (line != null) {
                if(line.startsWith("<Node "))
                    this.addNode(graph, line);
                else if(line.startsWith("<Edge "))
                    this.addEdge(graph, line);

                line = br.readLine();
            }
        }
        catch(Exception e){
            System.out.println("Cannot parser file:");
            System.out.println("error");
            return null;
        } finally {
            if(br != null){
                try {
                    br.close();
                } catch (Exception e) {
                    System.out.println("errore");
                }
            }
        }
        
        this.computeBindings(graph);

        return graph;
    }
    
    

    
    
    private static String [] aEP1(String [] pieces, String src, String dest){
        for(String piece: pieces)
        {
            String[] parts = piece.split("=");
            if(parts.length < 2)
                continue;

            if(parts[0].equals("src"))
                src = parts[1];
            else if(parts[0].equals("dest"))
                dest = parts[1];
        }
        String [] output = new String [2];
        output[0] = src;
        output[1] = dest;
        return output;
    }

    

}
