package org.processmining.plugins.cnmining;

import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.ObjectIntOpenHashMap;
import com.carrotsearch.hppc.ObjectObjectOpenHashMap;

public class UnfoldResult {
	public ObjectIntOpenHashMap<String> map;
	public ObjectObjectOpenHashMap<String, ObjectArrayList<String>> attivita_tracce;	    
	public ObjectObjectOpenHashMap<String, ObjectArrayList<String>> traccia_attivita;		
	
	public UnfoldResult(){
		map = new ObjectIntOpenHashMap<String>();	    
		attivita_tracce = new ObjectObjectOpenHashMap<String, ObjectArrayList<String>>();
		traccia_attivita = new ObjectObjectOpenHashMap<String, ObjectArrayList<String>>();
	}
}

/*
 * Specificando che ogni traccia del log viene identificata 
 * con un id del tipo 1#0, 2#1
 * dove il primo numero rappresenta l'id della traccia, specificato nel log stesso
 * il secondo numero indica il contatore di elaborazione 
 *
 * 
 * L'oggetto attivita_tracce: rappresenta, per ogni attività, le tracce in cui questo occorre
 * attivita_tracce = [
 * 		'attivita1#0000' => [ '1#0', '4#3', '5#4' ],
 * 		'attivita2#0000' => [ '2#1 ]	
 * 		.....
 * 		'attivitaN#0000' => [ '7#6' ] 
 * ]
 * 
 * L'oggetto traccia_attivita: rappresenta la lista di attività che costituisce una traccia
 * traccia_attivita = [
 * 		'1#0' => [ 'attivita1#0000', 'attivita2#0000', ..., 'attivitaN#0000' ],
 * 		....
 * ] 
 * 
 * E' da considerare una cosa, nel caso in cui il log non è linerare,
 * ovvero si presentano tracce con attività che occorrono più volte,
 * le occorrenze successive vengono rinominate, definendo così attività virtuali.
 * Es. A, B, B, C, A, D => A#0000, B#0000, B#0001, C#0000, A#0001, D#0000
 * 
 * 
 * Il senso dell'oggetto map mi è ancora ignoto.
 * Si presenta nella forma:
 * [
 * 		'attivita1#0000' => valore1,
 * 		'attivita2#0000' => valore2,
 * 		...
 * ]
 * Dove valore è un intero definito da un contatore
 * 
 * 
 */
