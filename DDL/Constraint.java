package org.processmining.plugins.cnmining;

import java.util.Collections;
import java.util.LinkedList;

/**
 * 
 * @author Utente
 */
public class Constraint
{
	private final LinkedList<String> head_list = new LinkedList<String>();
	private final LinkedList<String> body_list = new LinkedList<String>();
	private boolean positive_constraint;
	private boolean pathConstraint = false;
  
	public Constraint() {}
  
	public Constraint(boolean type, boolean pathConstraint)
	{
		this.positive_constraint = type;
		this.pathConstraint = pathConstraint;
	}
  
	public void addBody(String body)
	{
		this.body_list.add(body);
	}
  
	public void addHead(String head)
	{
		this.head_list.add(head);
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
		Constraint other = (Constraint)obj;
		Collections.sort(this.body_list);
		Collections.sort(other.body_list);
		if (!this.body_list.equals(other.body_list)) {
			return false;
		}
		if (this.head_list == null)
		{
			if (other.head_list != null) {
				return false;
			}
		}
		else if (!this.head_list.equals(other.head_list)) {
			return false;
		}
		return true;
	}
  
	public LinkedList<String> getBodyList()
	{
		return this.body_list;
	}
  
	public LinkedList<String> getHeadList()
	{
		return this.head_list;
	}
  
	public int hashCode()
	{
		int result = 1;
		result = 31 * result + (this.head_list == null ? 0 : this.head_list.hashCode());
		return result;
	}
  
	public boolean isPathConstraint()
	{
		return this.pathConstraint;
	}
  
	public boolean isPositiveConstraint()
	{
		return this.positive_constraint;
	}
  
	public void setConstraintType(boolean flag)
	{
		this.positive_constraint = flag;
	}
  
	public void setPathConstraint(boolean pathConstraint)
	{
		this.pathConstraint = pathConstraint;
	}
  
	public void setType(boolean type)
	{
		this.positive_constraint = type;
	}
  
	public String toString()
	{
		String result = "[CONSTRAINT]= ";
		for (String body : this.body_list) {
			result = result + body + " ; ";
		}
		result = result.substring(0, result.length() - 2) + "=> " + this.head_list;
		result = result + (this.pathConstraint ? " PATH " : " EDGE ");
    
		result = result + (this.positive_constraint ? "POSITIVO" : "NEGATO");
    
		return result;
	}
}
