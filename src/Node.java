import java.io.PrintWriter;
import java.util.ArrayList;


public class Node {
	
	Token token;
	boolean complete;	// if a normal tag is closed, then true.
	Node parent;
	ArrayList<Node> children;
	
	Node(Node parent, Token token, boolean complete)
	{
		this.parent = parent;
		this.token = token;
		this.complete = complete;
		children = new ArrayList<Node>();
	}
	
    void print(PrintWriter out, String label)
    {   
    	String str = "";
    	if(token == null) str = "root";
    	else
    	{
	    	if(token.type.equals("comment")) str = "comment";
	    	if(token.type.equals("EOF")) str = "EOF";
	    	if(token.type.equals("start")) str = "tag "+token.value;
	    	if(token.type.equals("end")) str = "tag "+token.value;
	    	
	    	if(complete == false)
	    		str += " :"+token.type;

    	}
    	out.println(label+"[label=\""+str+"\"];");


        if(children != null)
        {   
            for(int i=0; i<children.size(); i++)
            {   
                    out.println(label+"->"+label+i);
                    children.get(i).print(out, label+i);
            }   
        }   
        else
            out.println("");
    } 
}
