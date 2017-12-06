package org.processmining.models.cnet2ad;

public class ADedge {

    private static int idCounter = 0;

    private ADnode beginNode, endNode;
    public String id;
    public String name;

    public ADedge(ADnode start, ADnode end){
        this.beginNode = start;
        this.endNode = end;
        this.id = "Edge-" + idCounter;
        idCounter++;
    }

    public ADedge(String id, ADnode start, ADnode end){
        this.beginNode = start;
        this.endNode = end;
        this.id = id;
    }
    public ADnode begin(){
        return this.beginNode;
    }

    public void begin(ADnode value){
        this.beginNode = value;
    }

    public ADnode end(){
        return this.endNode;
    }

    public void end(ADnode value){
        this.endNode = value;
    }

    public String toXMI(){
        StringBuilder xmi = new StringBuilder();

        xmi.append("<edge ");
        xmi.append("xmi:type=\"uml:ControlFlow\" ");

        xmi.append("xmi:id=\"");
        xmi.append(this.id);
        xmi.append("\" ");

        xmi.append("target=\"");
        xmi.append(this.end().id);
        xmi.append("\" ");

        xmi.append("source=\"");
        xmi.append(this.begin().id);
        xmi.append("\" ");

        xmi.append(">\n");

        xmi.append("\t");
        xmi.append("<guard xmi:type=\"uml:LiteralBoolean\" xmi:id=\"");
        xmi.append(this.id + "guard");
        xmi.append("\" name=\"");
        xmi.append(this.begin().name + "_To_" + this.end().name + "_guard");
        xmi.append("\" value=\"true\"/>\n");

        xmi.append("\t");
        xmi.append("<weight xmi:type=\"uml:LiteralInteger\" xmi:id=\"");
        xmi.append(this.id + "weight");
        xmi.append("\" name=\"");
        xmi.append(this.begin().name + "_To_" + this.end().name + "_weight");
        xmi.append("\" />\n");

        xmi.append("</edge>");

        return xmi.toString();
    }

    public String toJson(){
        StringBuilder json = new StringBuilder("{");

        json.append("from: \"");
        json.append(this.begin().name);
        json.append("\"");

        json.append(", ");

        json.append("to: \"");
        json.append(this.end().name);
        json.append("\"");

        json.append("}");

        return json.toString();
    }

    public String toString(){
        StringBuilder str = new StringBuilder("{ ");

        str.append(this.begin().toString());
        str.append(" -> ");
        str.append(this.end().toString());

        str.append(" }");

        return str.toString();
    }

}

