package org.processmining.plugins.cnmining;

import com.carrotsearch.hppc.ObjectArrayList;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author Utente
 */
public class ConstraintParser extends DefaultHandler
{
	private int nrP;
	private int nrE;
	private int nrNP;
	private int nrNE;
	private final ObjectArrayList<Constraint> constraints;
	private String tempVal;
	private Constraint tempConstraint;
	private final String pathToConstraints;
  
	public int getNrP()
	{
		return this.nrP;
 	}
  
	public int getNrE()
	{
		return this.nrE;
	}
  
	public int getNrNP()
	{
		return this.nrNP;
	}
  
	public int getNrNE()
	{
		return this.nrNE;
	}
  
	public ConstraintParser(String pathToConstraints)
	{
		this.pathToConstraints = pathToConstraints;
		this.constraints = new ObjectArrayList<Constraint>();
	}
  
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		this.tempVal = new String(ch, start, length);
	}
  
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (qName.equalsIgnoreCase("Constraint")) {
			this.constraints.add(this.tempConstraint);
		} else if (qName.equalsIgnoreCase("Head")) {
			this.tempConstraint.addHead(this.tempVal);
		} else if (qName.equalsIgnoreCase("Body")) {
			this.tempConstraint.addBody(this.tempVal);
		}
	}
  
	public ObjectArrayList<Constraint> getConstraints()
	{
		return this.constraints;
	}
  
	private boolean parseDocument()
	{
		boolean validDocument = true;
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try
		{
			SAXParser sp = spf.newSAXParser();
      
			sp.parse(this.pathToConstraints, this);
		}
		catch (SAXException se)
		{
			System.out.println("errore");
			validDocument = false;
		}
		catch (ParserConfigurationException pce)
		{
			System.out.println("errore");
			validDocument = false;
		}
		catch (IOException ie)
		{
			System.out.println("errore");
			validDocument = false;
		}
		return validDocument;
	}
  
	public boolean run()
	{
		return parseDocument();
	}
  
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		this.tempVal = "";
		if (qName.equalsIgnoreCase("Constraint"))
		{
			this.tempConstraint = new Constraint();
			String type = attributes.getValue("type");
			if (type.equalsIgnoreCase("edge"))
			{
				this.tempConstraint.setPathConstraint(false);
				this.tempConstraint.setConstraintType(true);
				this.nrE += 1;
			}
			else if (type.equalsIgnoreCase("notEdge"))
			{
				this.tempConstraint.setPathConstraint(false);
				this.tempConstraint.setConstraintType(false);
				this.nrNE += 1;
			}
			else if (type.equalsIgnoreCase("path"))
			{
				this.tempConstraint.setPathConstraint(true);
				this.tempConstraint.setConstraintType(true);
				this.nrP += 1;
			}
			else if (type.equalsIgnoreCase("notPath"))
			{
				this.nrNP += 1;
				this.tempConstraint.setPathConstraint(true);
				this.tempConstraint.setConstraintType(false);
			}
		}
	}
}
