package org.processmining.plugins.cnmining;

public class Settings
{
	public String constraintsFilename = "";
	public boolean constraintsEnabled;
	public double sigmaLogNoise;
	public double fallFactor;
	public String logName;
	public double relativeToBest;
	public double sigmaUpCsDiff;
	public double sigmaLowCsConstrEdges;
	
	
	public Settings(){
		this.constraintsFilename = this.logName = "";
		this.constraintsEnabled = false;
		this.sigmaLogNoise = this.fallFactor = this.relativeToBest = 0.0D;
		this.sigmaUpCsDiff = 0.2D;
		this.sigmaLowCsConstrEdges = 0.0D;
	}
	
	public boolean areConstraintsAvailable(){
		return this.constraintsEnabled && this.constraintsFilename.equals("") == false;
	}
}