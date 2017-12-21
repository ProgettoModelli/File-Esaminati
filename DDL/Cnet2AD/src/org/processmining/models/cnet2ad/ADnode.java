package org.processmining.models.cnet2ad;

/**
 * 
 * @author Utente
 */
public class ADnode {

    public static final String JoinNode = "JoinNode";
    public static final String ForkNode = "ForkNode";
    public static final String BranchNode = "BranchNode";
    public static final String Node = "Node";
    public static final String InitialNode = "InitialNode";
    public static final String FinalNode = "FinalNode";

    public String name;
    public String id;
    private String type;

    private static int idCounter = 0;

    public ADnode(String name){
        this.name = name;
        this.id = "Node-" + idCounter;
        idCounter++;
        this.node();
    }

    public ADnode(String name, String id){
        this.name = name;
        this.id = id;
        this.node();
    }

    private void node(){
        this.type = Node;
    }

    public void fork(){
        this.type = ForkNode;
    }

    public void join(){
        this.type = JoinNode;
    }

    public void branch(){
        this.type = BranchNode;
    }

    public boolean isType(String value){
        return this.type.equals(value);
    }

    public void initialNode(){
        this.type = InitialNode;
    }

    public void finalNode(){
        this.type = FinalNode;
    }

    public String toXMI(){
        return this.toXMI("", "");
    }
    
    public StringBuilder startToXMI(StringBuilder str){
        str.append("<node xmi:type=\"uml:");
        if(this.isType(FinalNode))
            str.append("ActivityFinalNode");
        else if(this.isType(InitialNode))
            str.append("InitialNode");
        else if(this.isType(ForkNode))
            str.append("ForkNode");
        else if(this.isType(JoinNode))
            str.append("JoinNode");
        else if(this.isType(BranchNode))
            str.append("DecisionNode");
        else str.append("OpaqueAction");
        return str;
    }

    public String toXMI(String outcoming, String incoming){
        StringBuilder str = new StringBuilder();
        str = startToXMI(str);
        

        str.append("\" xmi:id=\"");
        str.append(this.id);

        str.append("\" name=\"");
        str.append(this.name);
        str.append("\"");

        if(outcoming.isEmpty() == false) {
            str.append(" outgoing=\"");
            str.append(outcoming);
            str.append("\"");
        }

        if(incoming.isEmpty() == false) {
            str.append(" incoming=\"");
            str.append(incoming);
            str.append("\"");
        }

        if(this.isType(JoinNode)){
            str.append(">\n");

            str.append("<joinSpec xmi:type=\"uml:LiteralBoolean\" xmi:id=\"");
            str.append(this.id + "_spec");
            str.append("\" name=\"");
            str.append(this.name);
            str.append("\" value=\"true\"/>\n");

            str.append("</node>");
        }
        else str.append("/>");

        return str.toString();
    }

    public String toJson(){
        StringBuilder json = new StringBuilder("{");

        json.append("key: \"");
        json.append(this.name);
        json.append("\"");

        json.append(", ");

        json.append("category: \"");
        json.append(this.type);
        json.append("\"");

        json.append(", ");

        json.append("color: \"");
        if(isType(ForkNode))
            json.append("red");
        else if(isType(JoinNode))
            json.append("pink");
        else if(isType(BranchNode))
            json.append("cyan");
        else if(isType(InitialNode) || isType(FinalNode))
            json.append("black");
        else json.append("orange");
        json.append("\"");

        json.append("}");

        return json.toString();
    }

    public String toString(){
        StringBuilder str = new StringBuilder();

        str.append(this.type + ": " + this.name + ", id: " + this.id);

        return str.toString();
    }

}
