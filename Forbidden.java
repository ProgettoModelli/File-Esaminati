package org.processmining.plugins.cnmining;

public class Forbidden
{
	private String b;
	private String a;
	
	public Forbidden(String b, String a)
	{
		this.b = b;
		this.a = a;
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
		Forbidden other = (Forbidden)obj;
		if (this.a == null)
		{
			if (other.a != null) {
				return false;
			}
		}
		else if (!this.a.equals(other.a)) {
			return false;
		}
		if (this.b == null)
		{
			if (other.b != null) {
				return false;
			}
		}
		else if (!this.b.equals(other.b)) {
			return false;
		}
		return true;
	}
  
	public String getA()
	{
		return this.a;
	}
  
	public String getB()
	{
		return this.b;
	}
  
	public int hashCode()
	{
		int result = 1;
		result = 31 * result + (this.a == null ? 0 : this.a.hashCode());
		result = 31 * result + (this.b == null ? 0 : this.b.hashCode());
		return result;
	}
  
	public void setA(String a)
  	{
		this.a = a;
  	}
  
	public void setB(String b)
	{
		this.b = b;
	}
  
	public String toString()
	{
		return "[Forbidden] " + this.b + " => " + this.a;
	}
}
