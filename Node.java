package org.processmining.plugins.cnmining;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.ObjectIntOpenHashMap;

public class Node
{
	ObjectIntOpenHashMap<IntOpenHashSet> output = new ObjectIntOpenHashMap<IntOpenHashSet>();
	ObjectIntOpenHashMap<IntOpenHashSet> input = new ObjectIntOpenHashMap<IntOpenHashSet>();
	ObjectIntOpenHashMap<IntArrayList> extendedOutput = new ObjectIntOpenHashMap<IntArrayList>();
	ObjectIntOpenHashMap<IntArrayList> extendedInput = new ObjectIntOpenHashMap<IntArrayList>();
  
	public ObjectIntOpenHashMap<IntArrayList> getExtendedOutput()
	{
		return this.extendedOutput;
	}
  
	public void setExtendedOutput(ObjectIntOpenHashMap<IntArrayList> extendedOutput)
	{
		this.extendedOutput = extendedOutput;
	}
  
	public ObjectIntOpenHashMap<IntArrayList> getExtendedInput()
	{
		return this.extendedInput;
	}
	
	public void setExtendedInput(ObjectIntOpenHashMap<IntArrayList> extendedInput)
	{
		this.extendedInput = extendedInput;
	}
  
	public ObjectIntOpenHashMap<IntOpenHashSet> getOutput()
	{
		return this.output;
	}
  
	public void setOutput(ObjectIntOpenHashMap<IntOpenHashSet> output)
	{
		this.output = output;
	}
  
	public ObjectIntOpenHashMap<IntOpenHashSet> getInput()
	{
		return this.input;
	}
  
	public void setJoin(ObjectIntOpenHashMap<IntOpenHashSet> input)
	{
		this.input = input;
	}
  
	private int inner_degree = 0;
	private int outer_degree = 0;
	private final int id_attivita;
	private final String nome_attivita;
	private boolean mark = false;
  
	public Node(String nome_attivita, int id_attivita)
	{
		this.nome_attivita = nome_attivita;
		this.id_attivita = id_attivita;
	}
  
	public void decr_Inner_degree()
	{
		this.inner_degree -= 1;
	}
  
	public void decr_Outer_degree()
	{
		this.outer_degree -= 1;
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
		Node other = (Node)obj;
		if (this.id_attivita != other.id_attivita) {
			return false;
		}
		if (this.nome_attivita == null)
		{
			if (other.nome_attivita != null) {
				return false;
			}
		}
		else if (!this.nome_attivita.equals(other.nome_attivita)) {
			return false;
		}
		return true;
	}
  
	public int getID_attivita()
	{
		return this.id_attivita;
	}
  
	public int getInner_degree()
	{
		return this.inner_degree;
	}
  
	public String getNomeAttivita()
	{
		return this.nome_attivita;
	}
  
	public int getOuter_degree()
	{
		return this.outer_degree;
	}
  
	public int hashCode()
	{
		int result = 1;
		result = 31 * result + this.id_attivita;
		result = 31 * result + (this.nome_attivita == null ? 0 : this.nome_attivita.hashCode());
		return result;
	}
  
	public void incr_Inner_degree()
	{
		this.inner_degree += 1;
	}
  
	public void incr_Outer_degree()
	{
		this.outer_degree += 1;
	}
  
	public boolean isMarked()
	{	
		return this.mark;
	}
  
	public void setInner_degree(int inner_degree)
	{
		this.inner_degree = inner_degree;
	}
  
	public void setOuter_degree(int outer_degree)
	{
		this.outer_degree = outer_degree;
	}
  
	public void setMark(boolean mark)
	{
		this.mark = mark;
	}
  
	public String toString()
	{
		return this.nome_attivita;
	}
}
