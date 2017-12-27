package org.processmining.plugins.cnmining;

/**
 * 
 * @author Utente
 */
public class Edge
{
	private Node x;
	private Node y;
	private boolean flag;
  
	public Edge(Node x, Node y)
	{
		this.x = x;
		this.y = y;
	}
  
	public Edge(Node x, Node y, boolean flag)
	{
		this.x = x;
		this.y = y;
		this.flag = flag;
	}
  
        public boolean eP1(Edge other){
            if (this.x == null)
		{
			if (other.x != null) {
				return false;
			}
		}
		else if (!this.x.equals(other.x)) {
			return false;
		}
            return true;
        }
        
        public boolean ep2(Edge other){
            if (this.y == null)
		{
			if (other.y != null) {
				return false;
			}
		}
		else if (!this.y.equals(other.y)) {
			return false;
		}
            return true;
        }
        
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Edge other = (Edge)obj;
                
                if(!eP1(other)) return false;
		
                if(!ep2(other)) return false;
		
		return true;
	}
  
	public Node getX()
	{
		return this.x;
	}
  
	public Node getY()
	{
		return this.y;
	}
  
	public int hashCode()
	{
		int result = 1;
		result = 31 * result + (this.flag ? 1231 : 1237);
		result = 31 * result + (this.x == null ? 0 : this.x.hashCode());
		result = 31 * result + (this.y == null ? 0 : this.y.hashCode());
		return result;
	}
  
	public boolean isFlag()
	{
		return this.flag;
	}
  
	public void setFlag(boolean flag)
	{
		this.flag = flag;
	}
  
	public void setX(Node x)
	{
		this.x = x;
	}
  
	public void setY(Node y)
	{
		this.y = y;
	}
  
	public String toString()
	{
		return "Edge [x=" + this.x + ", y=" + this.y + ", flag=" + this.flag + "]";
	}
}
