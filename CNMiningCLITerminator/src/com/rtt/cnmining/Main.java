package com.rtt.cnmining;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.in.XMxmlParser;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.flexiblemodel.Flex;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.processmining.plugins.cnmining.CNMining;
import org.processmining.plugins.cnmining.Settings;
import org.processmining.plugins.converters.FlexToBPMNConversionPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * 
 * @author Utente
 */
public class Main {

    public static void main(String[] args){

        XLog log = parse("logs/all_10_0_CON_RISORSE.mxml");


        try {
            OntologyManager ontologyManager=new OntologyManager("ontology.owl", log);
            ontologyManager.readData();
            ArrayList<String> cacca=ontologyManager.resourceQuery("checkticket");
            ArrayList<String> cacca2=ontologyManager.roleQuery("Sara");
            ArrayList<String> cacca3=ontologyManager.roleQuery("Ellen");
            int provaDioMerda=0;
            for(String role:cacca3){

                if(ontologyManager.subClassQuery(role)==null)
                    provaDioMerda=69;
            }

            if(true)
                return;
            Settings settings = new Settings();
            settings.sigmaLogNoise = 0.05;
            settings.fallFactor = 0.9;
            settings.relativeToBest = 0.75;

            Object[] data = CNMining.startCNMining(null, log, settings, false);
            Flex cnminningGraph = (Flex)data[0];
            //CNParser parser = new CNParser("ExtendedCausalNet.xml");
            //Flex cnminningGraph = parser.parse();

            BPMNDiagram bpmn = Flex2BPMN.convert(cnminningGraph);
            RTTminingBPMN m = new RTTminingBPMN(bpmn);
            RTTgraph graph = m.process();

            System.out.println();
            //System.out.println(graph.toString());

            ArrayList<String> resources = new ArrayList<>();
            resources.add("pippo");
            resources.add("pluto");
            explodeNode(graph, graph.node("Keep_records"), resources);

            saveFile("rttgraph.js", "var data = [" + graph.toJson() + "]");
        }
        catch(Exception e){
            System.out.println("Exception " + e.toString());
        }

    }

    static void explodeNode(RTTgraph graph, RTTnode node, ArrayList<String> resources){
        if(graph.nodes().contains(node) == false)
            return;

        ArrayList<RTTnode> nodes = new ArrayList<>();
        for(String res:resources){
            nodes.add(new RTTnode(node.name + " | " + res));
        }

        // Rimpiazza il nodo corrente con i nuovi
        // tenendo presente una cosa
        // se ci sono piu risorse, bisogna mettere un fork prima e
        // un join dopo la lista di nodi rimpiazzati
        if(resources.size() > 1){
            // add fork
            RTTnode fork = new RTTnode("ForkResources" + node.name);
            fork.fork();
            // add join
            RTTnode join = new RTTnode("JoinResources" + node.name);
            join.join();

            graph.add(fork);
            graph.add(join);

            for(RTTedge edge:graph.edgesEndWith(node))
                edge.end(fork);
            for(RTTedge edge:graph.edgesStartWith(node))
                edge.begin(join);

            // aggiungi i nodi e per ognuno definisci gli archi
            for(RTTnode n:nodes){
                graph.add(n);

                graph.add(new RTTedge(fork, n));
                graph.add(new RTTedge(n, join));
            }

            // rimuovi il nodo rimpiazzato
            graph.nodes().remove(node);
        }
        else node.name += " | " + resources.get(0);
    }

    static void printLog(XLog log){
        int logSize = log.size();
        for (int i = 0; i < logSize; i++) {
            XTrace trace = log.get(i);
            System.out.println("trace: " + XConceptExtension.instance().extractName(trace));
            for (XEvent activity : trace)
            {
                String nome_attivita = activity.getAttributes().get("concept:name").toString();
                System.out.println(nome_attivita);
                Set<String> a = activity.getAttributes().keySet();
                for(String b:a)
                {
                    System.out.println(b+"-"+activity.getAttributes().get(b).toString());
                }
            }
            System.out.println(trace);
        }
    }

    static XLog parse(String name){
        try {
            XMxmlParser mxmlParser = new XMxmlParser();
            XesXmlParser xesParser= new XesXmlParser();
            List<XLog> logs=null;
            File file = new File(name);
            if(mxmlParser.canParse(file))
                logs = mxmlParser.parse(file);
            else if(xesParser.canParse(file))
                logs= xesParser.parse(file);
            else
                System.out.println("Error, cannot parse input file.");
            System.out.println(logs.size());

            return logs.iterator().next();
        }
        catch(Exception e){
            System.out.println("exception" + e.toString());
            return null;
        }
    }

    public static void saveFile(String filename, String content) throws Exception {
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
        catch (IOException e)
        {
            System.out.println("errore");
        }
    }

    static void printFlex(Flex graph){

    }
}
