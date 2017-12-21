package org.processmining.plugins.cnmining;

import com.carrotsearch.hppc.ObjectArrayList;

/*
 * Questa classe funge da contenitore dei vincoli 
 */

/**
 * 
 * @author Utente
 */
public class ConstraintsManager {

    public ObjectArrayList<Forbidden> 
    	forbidden, 
    	forbiddenUnfolded;
    public ObjectArrayList<Constraint>	
    	positivi, 
    	negati, 
    	positiviUnfolded, 
    	negatiUnfolded;
    
    public ConstraintsManager(){
    	forbidden = new ObjectArrayList<Forbidden>();
    	forbiddenUnfolded = new ObjectArrayList<Forbidden>();
        positivi = new ObjectArrayList<Constraint>();	    
        negati = new ObjectArrayList<Constraint>();
        positiviUnfolded = new ObjectArrayList<Constraint>();	    
        negatiUnfolded = new ObjectArrayList<Constraint>();
    }
    
}
