package org.processmining.plugins.cnmining;

import java.util.LinkedList;

public class FakeDependency
{
	private final int id_attivita_x;
	private final int id_attivita_y;
	private LinkedList<Node> nodi_del_path;
  
	public FakeDependency(int attivita_x, int attivita_y)
	{
		this.id_attivita_x = attivita_x;
		this.id_attivita_y = attivita_y;
	}
  
	public FakeDependency(int attivita_x, int attivita_y, LinkedList<Node> lista_nodi)
	{
		this.id_attivita_x = attivita_x;
		this.id_attivita_y = attivita_y;
    
		this.nodi_del_path = lista_nodi;
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
		FakeDependency other = (FakeDependency)obj;
		if (this.id_attivita_x != other.id_attivita_x) {
			return false;
		}
		if (this.id_attivita_y != other.id_attivita_y) {
			return false;
		}
		return true;
  	}
  
	public int getAttivita_x()
	{
		return this.id_attivita_x;
	}
  
	public int getAttivita_y()
	{
		return this.id_attivita_y;
	}
  
	public LinkedList<Node> getNodi_del_path()
	{
		return this.nodi_del_path;
	}
  
	public int hashCode()
	{
		int result = 1;
		result = 31 * result + this.id_attivita_x;
		result = 31 * result + this.id_attivita_y;
		return result;
	}
  
	public void setNodi_del_path(LinkedList<Node> nodi_del_path)
	{
		this.nodi_del_path = nodi_del_path;
	}
  
	public String toString()
	{
		return "Attivita_Parallela [id_attivita_x=" + 
			this.id_attivita_x + ", id_attivita_y=" + this.id_attivita_y + "]";
	}
}
