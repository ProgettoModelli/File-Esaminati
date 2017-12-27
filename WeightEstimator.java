package org.processmining.plugins.cnmining;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntOpenHashSet;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * @author Utente
 */
public class WeightEstimator
{
	public static boolean NORMALIZE_BY_ROW_MAX = false;
	public static boolean NON_NEGATIVE_WEIGHTS = false;
	public static boolean CLOSEST_OCCURRENCE_ONLY = false;
	public static final int STRATEGY__TASK_PAIRS = 0;
	public static final int STRATEGY__TASK_PAIRS_NORMALIZED_BY_COUNTS = 1;
	public static final int STRATEGY__PER_TRACE = 2;
	public static final int DEFAULT_MAX_GAP = -1;
	public static final double DEFAULT_FALL_FACTOR = 0.2D;
	public static final int DEFAULT_ESTIMATION_STRATEGY = 2;
  
	public static void printMatrix(double[][] matrix)
	{
		for (int i = 0; i < matrix.length; i++)
		{
			for (int j = 0; j < matrix[0].length; j++) {
				System.out.print(matrix[i][j] + "\t");
			}
			System.out.println();
		}
	}
  
	private boolean computationStarted = false;
	private final int taskNr;
	private int estimationStrategy = 2;
	private int maxGap = -1;
	private double fallFactor = 1.0D;
	private double[][] unnormDepMatrix = null;
	private double[][] countMatrix = null;
	private double[][] depMatrix = null;
	private int[] traceFreq = null;
  
	public WeightEstimator(int taskNr) throws Exception
	{
		this.taskNr = taskNr;
		this.fallFactor = 0.2D;
		this.maxGap = -1;
		this.estimationStrategy = 2;
    
		this.unnormDepMatrix = new double[taskNr][taskNr];
		this.depMatrix = new double[taskNr][taskNr];
		switch (this.estimationStrategy)
		{
			case 1: 
				this.countMatrix = new double[taskNr][taskNr];
				break;
			case 0: 
				break;
			case 2: 
				this.traceFreq = new int[taskNr];
				break;
			default: 
				throw new Exception("Unknown Estimation Strategy !!");
		}
	}
  
	public WeightEstimator(int taskNr, int maxGap, double fallFactor, int estimationStrategy) throws Exception
	{
		this.taskNr = taskNr;
   		this.fallFactor = fallFactor;
   		this.maxGap = maxGap;
   		this.estimationStrategy = estimationStrategy;
    
   		this.unnormDepMatrix = new double[taskNr][taskNr];
   		this.depMatrix = new double[taskNr][taskNr];
   		switch (estimationStrategy)
   		{
   			case 1: 
   				this.countMatrix = new double[taskNr][taskNr];
   				break;
   			case 0: 
   				break;
   			case 2: 
   				this.traceFreq = new int[taskNr];
   				break;
   			default: 
   				throw new Exception("Unknown Estimation Strategy !!");
   		}
	}
        
        public boolean aTCP1(IntOpenHashSet visitedTasks, int task1, boolean horizonReached){
            if (this.estimationStrategy == 2) {
				if (!visitedTasks.contains(task1))
				{
					visitedTasks.add(task1);
					this.traceFreq[task1] += 1;
				}
				else
				{
					horizonReached = true;
				}
			}
            return horizonReached;
        }
        
        public void aTCP2_1(double power, boolean nonOverlappingPairs, IntOpenHashSet visitedFollowers, int task2, int task1){
            if ((!nonOverlappingPairs) || (!visitedFollowers.contains(task2)))
					{
						visitedFollowers.add(task2);
                                                
						this.unnormDepMatrix[task1][task2] += power;
						if (this.countMatrix != null) {
							this.countMatrix[task1][task2] += 1.0D;
						}
					}
        }
        
        public void aTCP2(int i, double power, boolean horizonReached, int gap, IntArrayList trace, int traceSize, int task1, IntOpenHashSet visitedFollowers){
            for (int j = i + 1; (!horizonReached) && ((this.maxGap < 0) || (gap <= this.maxGap)) && (j < traceSize); j++)
			{
				int task2 = trace.get(j);
        
				horizonReached = (CLOSEST_OCCURRENCE_ONLY) && (task2 == task1) && (gap > 0);
				if (!horizonReached)
				{
					boolean nonOverlappingPairs = (CLOSEST_OCCURRENCE_ONLY) || (this.estimationStrategy == 2);
					aTCP2_1(power, nonOverlappingPairs, visitedFollowers, task2, task1);
                                        
					power *= this.fallFactor;
				}
				gap++;
			}
        }
  
	public void addTraceContribution(IntArrayList trace)
	{
		this.computationStarted = true;
    
		IntOpenHashSet visitedTasks = new IntOpenHashSet();

		int traceSize = trace.size();
                IntOpenHashSet visitedFollowers = new IntOpenHashSet();
		for (int i = 0; i < traceSize; i++)
		{
			int gap = 0;
			double power = 1.0D;
			int task1 = trace.get(i);
			boolean horizonReached = false;
                        horizonReached = aTCP1(visitedTasks, task1, horizonReached);
			
                        aTCP2(i, power, horizonReached, gap, trace, traceSize, task1, visitedFollowers);    
			
		}
	}
        
        public void nBRM(){
            if (NORMALIZE_BY_ROW_MAX) {
			normalizeByRowMax();
		}
        }
  
	public void computeWeigths()
	{
		for (int i = 0; i < this.taskNr; i++) {
			for (int j = 0; j < this.taskNr; j++)
			{
				double numerator;
				double denominator;
				if (i == j)
				{
					numerator = this.unnormDepMatrix[i][i];
					switch (this.estimationStrategy)
					{
						case 1: 
							denominator = this.countMatrix[i][i] + 1.0D;
							break;
						case 0: 
							denominator = this.unnormDepMatrix[i][i] + 1.0D;
							break;
						default: 
							denominator = this.traceFreq[i];
            
							break;
					}
				}
				else
				{
					numerator = this.unnormDepMatrix[i][j] - this.unnormDepMatrix[j][i];
					switch (this.estimationStrategy)
					{
						case 1: 
							denominator = this.countMatrix[i][j] + this.countMatrix[j][i] + 1.0D;
							break;
						case 0: 
							denominator = this.unnormDepMatrix[i][j] + this.unnormDepMatrix[j][i] + 1.0D;
							break;
						default: 
							denominator = this.traceFreq[i];
					}
				}
				this.depMatrix[i][j] = (numerator / denominator);
			}
		}
                nBRM();
	}
  
	public double[][] getDependencyMatrix()
	{
		if (this.depMatrix == null) {
			computeWeigths();
		}
		return this.depMatrix;
	}
  
	private void normalizeByRowMax()
	{
		for (int i = 0; i < this.taskNr; i++)
		{
			double rowMax = this.depMatrix[i][0];
			for (int j = 1; j < this.taskNr; j++) {
				if (this.depMatrix[i][j] > rowMax) {
					rowMax = this.depMatrix[i][j];
				}
			}
			for (int j = 0; j < this.taskNr; j++) {
				if (rowMax != 0.0D) {
					this.depMatrix[i][j] /= rowMax;
				}
			}
		}
	}
  
	public void saveDependencyMatrix(String fileName)
	{
		if (this.depMatrix == null) {
			computeWeigths();
		}
		try
		{
			BufferedWriter br = new BufferedWriter(new FileWriter(fileName));
			for (int i = 0; i < this.depMatrix.length; i++)
			{
				for (int j = 0; j < this.depMatrix[0].length; j++) {
					br.append(this.depMatrix[i][j] + "\t");
				}
				br.newLine();
			}
			br.close();
		}
		catch (IOException e)
		{
			System.out.println("errore");
		}
	}
  
	public void saveDependencyMatrix(String fileName, String[] taskLabels)
	{
		if (this.depMatrix == null) {
			computeWeigths();
		}
		try
		{
			BufferedWriter br = new BufferedWriter(new FileWriter(fileName));
			br.append("\t");
			for (int i = 0; i < this.depMatrix.length; i++) {
				br.append(taskLabels[i] + "\t");
			}
			br.newLine();
			for (int i = 0; i < this.depMatrix.length; i++)
			{
				br.append(taskLabels[i] + "\t");
				for (int j = 0; j < this.depMatrix[0].length; j++) {
					br.append(this.depMatrix[i][j] + "\t");
				}
				br.newLine();
			}	
			br.close();
		}
		catch (IOException e)
		{
			System.out.println("error");
		}
	}
        
        public void sESC1(){
            if ((this.countMatrix == null) || (this.countMatrix.length != this.taskNr)) {
    				this.countMatrix = new double[this.taskNr][this.taskNr];
    			}
        }
  
        public void sESC2(){
            if ((this.traceFreq == null) || (this.traceFreq.length != this.taskNr)) {
    				this.traceFreq = new int[this.taskNr];
    			}
        }
        
	public void setEstimationStrategy(int estimationStrategy)
		throws Exception
	{
            
		if (this.computationStarted) {
			throw new Exception("Weigth Evaluation already started!!");
		}
		switch (estimationStrategy)
    	{
    		case 1: 
                        sESC1();
    			break;
    		case 0: 
    			break;
    		case 2: 
                        sESC2();
    			break;
    		default: 
    			throw new Exception("Unknown Estimation Strategy !!");
    	}
	}
  
	public void setFallFactor(double fallFactor)
		throws Exception
	{
		if (this.computationStarted) {
			throw new Exception("Weigth Evaluation already started!!");
		}
		this.fallFactor = fallFactor;
	}
  
	public void setMaxGap(int maxGap)
		throws Exception
	{
		if (this.computationStarted) {
			throw new Exception("Weigth Evaluation already started!!");
		}
		this.maxGap = maxGap;
	}
}
