package org.processmining.plugins.cnmining;

/**
 * 
 * @author Utente
 */
public class Settings
{
	/**
         * public 
         */
        public String constraintsFilename = "";
        /**
     * public 
     */
	public boolean constraintsEnabled;
        /**
     * public 
     */
	public double sigmaLogNoise;
        /**
     * public 
     */
	public double fallFactor;
        /**
     * public 
     */
	public String logName;
        /**
     * public 
     */
	public double relativeToBest;
        /**
     * public 
     */
	public double sigmaUpCsDiff;
        /**
     * public 
     */
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