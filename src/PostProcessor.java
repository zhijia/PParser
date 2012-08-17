import java.util.*;

public class PostProcessor{
	
	Remain[] remains;
	
	PostProcessor(Remain[] remains)
	{
		this.remains = remains;
	}
	
	public Node process()
	{
		for(int i=1; i< remains.length; i++)
		{
			remains[0] = merge(remains[0], remains[i]);
		}
		return remains[0].forest.get(0);
	}
	
	Remain merge(Remain remain0, Remain remain)
	{
		ArrayList<Node> rightEdge = getRightEdge(remain0.forest.get(0));

		// handle broken comments
		Node rightMost = rightEdge.get(rightEdge.size()-1);
		Node leftMost = remain.forest.get(0);
		while(leftMost.children.size() != 0)
			leftMost = leftMost.children.get(0);
		
		if(rightMost.token.type.equals("comment_start")
		   && rightMost.complete == false)
		{
			if(leftMost.token.type.equals("comment"))
			{
				rightMost.token.value += leftMost.token.value;  // add leftMost's comment to rightMost's comment
				
				leftMost.parent.children.remove(0);				// remove leftMost
				if(leftMost.parent.children.size() == 0)
					remain.forest.remove(0);
				
				rightMost.complete = true;
				rightMost.token.type = "comment";
			}
			else	// the whole input segement is part of comment
			{
				rightMost.token.value += remain.input;
	            return remain0;
			}
		}

		// merge remain.forest to remain0.tree
		for(int i=0; i<remain.forest.size(); i++)
		{
			Node node = remain.forest.get(i);

			for(int j=rightEdge.size()-1; j>=0; j--)
			{
				if(rightEdge.get(j).complete == true)
					continue;

				if(node.children.size() == 0)  // single end tag
				{
					if(node.token.value.equals(rightEdge.get(j).token.value))
						rightEdge.get(j).complete = true;
					else
						System.out.println("tree merge err: "
					    +rightEdge.get(j).token.value+" not match "+node.token.value);
					break;
				}
				else							// a complete subtree
				{
					for(int k=0; k<node.children.size(); k++)
							rightEdge.get(j).children.add(node.children.get(k));
					break;
				}
			}
		}
		return remain0;
	}
	
	ArrayList<Node> getRightEdge(Node root)
	{
		ArrayList<Node> rightEdge = new ArrayList<Node>();
		Node node = root;
		
		do{
			rightEdge.add(node);
			node = node.children.get(node.children.size()-1);
		}while(node.children.size() > 0);
		rightEdge.add(node);
		
		return rightEdge;
	}
}
