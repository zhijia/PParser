import java.io.*;
import java.util.*;

public class Parser extends Thread{

	int pos;
	String input;
	Node root;
	Remain remain;
	Stack<Token> stack;
	Stack<Node> treeStack;
	
	Parser(String name, String input, Remain remain)
	{
		super(name);
		this.input = input;
		this.remain = remain;
		this.remain.input = input;
		this.root = new Node(null, null, true);
		this.pos = 0;
	}
	
	void buildtree()
	{
		stack = new Stack<Token>();
		treeStack = new Stack<Node>();
		Node currentNode = root;
		
		while(true)
		{
			Token token = tokenize();
			
			if(token.type.equals("start"))
			{
				stack.push(token);
				Node node = new Node(currentNode, token, false);
				treeStack.push(node);
				currentNode.children.add(node);
				currentNode = node;
				continue;
			}
			
			if(token.type.equals("end"))
			{
				if(stack.size()>0 && stack.peek().value.equals(token.value))  // paired end tag
				{
					currentNode.complete = true;
					stack.pop();
					treeStack.pop();
					
					if(treeStack.size() > 0)
						currentNode = treeStack.peek();
					else
					{
						remain.forest.add(root);
						root = new Node(null, null, true);
						currentNode = root;
					}
				}
				else	// unpaired end tag
				{
					if(root.children.size() >0)  // if root is not empty, add it to forest, create a new root
					{
						remain.forest.add(root);
						root = new Node(null, null, true);
						currentNode = root;
						// the treeStack and stack could be nonempty caused by unpaired tags in broken comments
						//if(treeStack.size() > 0)
							//System.out.println(getName()+" tree stack is supposed to be empty. size: "+treeStack.size());
						//if(stack.size() > 0)
							//System.out.println(getName()+" token stack is supposed to be empty. size: "+stack.size());
					}
					// add unpaired end tag as a single node to the forest
					remain.forest.add(new Node(null, token, false));
				}
				continue;
			}
			
			if(token.type.equals("comment"))
			{
				currentNode.children.add(new Node(currentNode, token, true));
				continue;
			}
			
			if(token.type.equals("comment_start"))
			{
				currentNode.children.add(new Node(currentNode, token, false));
				continue;
			}
			
			if(token.type.equals("comment_end"))
			{
				System.out.println(getName()+" rollbacks from "+pos+" to 0, reprocesses its segment.");
				// rollback
				pos = 0;
				root.children = new ArrayList<Node>();
				currentNode = root;
				stack = new Stack<Token>();
				treeStack = new Stack<Node>();
				remain.forest = new ArrayList<Node>();
				
				// append comment_start
				input = "<!--"+input;
				continue;
			}
			
			if(token.type.equals("EOF"))
			{
				if(root.children.size() > 0)
					remain.forest.add(root);
				break;
			}
			
		}
	}
	
	
	Token tokenize()
	{
		String tokenStr = read();
		
		// comment token 
		// comment start token
		if(tokenStr.startsWith("<!--"))
		{
			String value = tokenStr;
			while(tokenStr.endsWith("-->") == false && tokenStr.equals("EOF") == false)
			{
				tokenStr = read();
				value += tokenStr;
			}
			if(tokenStr.endsWith("-->"))
				return new Token("comment", value.substring(4, value.length()-3));
			else
				return new Token("comment_start", value.substring(4, value.length()));
		}
				
		// EOF token
		if(tokenStr.equals("EOF"))      
			return new Token("EOF", "");
		
		// end tag token
		if(tokenStr.startsWith("</"))   
		{
			String value = tokenStr.substring(2, tokenStr.length()-1);
			return new Token("end", value);
		}
		
		// start tag token
		if(tokenStr.startsWith("<") && tokenStr.charAt(1) != '/')							
		{
			String value = tokenStr.substring(1, tokenStr.length()-1);
			return new Token("start", value);
		}
		
		// comment end token
		if(tokenStr.endsWith("-->"))
			return new Token("comment_end", "");
		
		// unknown token
		System.out.println("unknown token: "+tokenStr);
		return null;
		
	}
	
	String read()
	{
		if(pos >= input.length()-1)
			return "EOF";
		String tokenStr = "";
		while(input.charAt(pos) != '>')
		{
			tokenStr += input.charAt(pos);
			pos++;
		}
		tokenStr += '>';
		pos++;			
		return tokenStr;
	}
	
    static void treePrintToDot(Node root, String fileName)                                                    
    {   
        try{
            FileWriter file = new FileWriter(fileName);
            PrintWriter out = new PrintWriter(file);                                                   
            out.println("digraph Tree{");                                                              
            root.print(out, "0");                                                                      
            out.println("}");                                                                          
            out.close();
        }catch(IOException e){
            e.printStackTrace();                                                                       
        }                                                                                              
    } 
	
	public void run()
	{
		buildtree();
		
		// print the trees to dot files
//		if(getName().equals("parser[0]"))
//		{
//			treePrintToDot(remain.forest.get(0), "dot/"+getName()+"tree.dot");
//		}
//		else
//		{
//			for(int i=0; i<remain.forest.size(); i++)
//				treePrintToDot(remain.forest.get(i), "dot/"+getName()+"tree"+i+".dot");
//		}
	}

}
