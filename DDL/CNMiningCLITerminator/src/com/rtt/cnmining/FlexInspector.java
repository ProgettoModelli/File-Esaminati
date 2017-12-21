package com.rtt.cnmining;

import org.processmining.models.flexiblemodel.Flex;
import org.processmining.models.flexiblemodel.FlexEdge;
import org.processmining.models.flexiblemodel.FlexNode;

import java.util.ArrayList;

/**
 * 
 * @author Utente
 */
public class FlexInspector {

    private Flex graph;

    public FlexInspector(Flex graph){
        this.graph = graph;
    }

    public ArrayList<String> activities(){
        ArrayList<String> result = new ArrayList<>();

        for(FlexNode node: this.graph.getNodes()){
            if(result.contains(node.toString()) == false)
                result.add(node.getLabel());
        }

        return result;
    }

    // Ritorna la lista delle attività iniziali
    public ArrayList<String> startActivities(){
        ArrayList<String> result = new ArrayList<>();

        for(String activity: this.activities()){
            if(this.predecessors(activity).size() == 0 && result.contains(activity) == false)
                result.add(activity);
        }

        return result;
    }

    // Ritorna la lista delle attività finali
    public ArrayList<String> endActivities(){
        ArrayList<String> result = new ArrayList<>();

        for(String activity: this.activities()){
            if(this.followers(activity).size() == 0 && result.contains(activity) == false)
                result.add(activity);
        }

        return result;
    }

    // Ritorna la lista delle attività t(i), tali che a > t(i)
    public ArrayList<String> followers(String activity){
        ArrayList<String> result = new ArrayList<>();

        for(FlexEdge edge: this.graph.getEdges()){
            String[] pieces = edge.toString().split("->");

            if(pieces.length <= 1)
                continue;

            String start = pieces[0].trim();
            String end = pieces[1].trim();

            if(start.equals(activity) && result.contains(end) == false)
                result.add(end);
        }

        return result;
    }

    // Ritorna la lista delle attività t(i), tali che t(i) > a
    public ArrayList<String> predecessors(String activity){
        ArrayList<String> result = new ArrayList<>();

        for(FlexEdge edge: this.graph.getEdges()){
            String[] pieces = edge.toString().split("->");

            if(pieces.length <= 1)
                continue;

            String start = pieces[0].trim();
            String end = pieces[1].trim();

            if(end.equals(activity) && result.contains(start) == false)
                result.add(start);
        }

        return result;
    }

}