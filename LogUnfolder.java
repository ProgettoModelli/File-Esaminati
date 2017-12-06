package org.processmining.plugins.cnmining;

import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.ObjectIntOpenHashMap;
import com.carrotsearch.hppc.ObjectObjectOpenHashMap;
import java.util.Date;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

public class LogUnfolder
{
	public static void aggiungiAttivitaFittizia(XLog xlog)
	{
		XFactory factory = (XFactory)XFactoryRegistry.instance().currentDefault();
		for (int i = 0; i < xlog.size(); i++)
		{
			XTrace trace = (XTrace)xlog.get(i);
			XEvent activity_first = (XEvent)trace.get(0);
			XEvent activity_last = (XEvent)trace.get(trace.size() - 1);
			
			XAttribute concept_name = activity_first.getAttributes().get("concept:name");
			if (concept_name.equals("_START_")) {
				break;
			}
			Date first_activity_ts = XTimeExtension.instance().extractTimestamp(activity_first);
      
			XEvent event_first = factory.createEvent();
      
			XConceptExtension.instance().assignName(event_first, "_START_");
			XLifecycleExtension.instance().assignTransition(event_first, "complete");
			if (first_activity_ts != null) {
				XTimeExtension.instance().assignTimestamp(event_first, new Date(first_activity_ts.getTime() - 10L));
			}
			trace.add(0, event_first);
      
			Date last_activity_ts = XTimeExtension.instance().extractTimestamp(activity_last);
      
			XEvent event_last = factory.createEvent();
      
			XConceptExtension.instance().assignName(event_last, "_END_");
			XLifecycleExtension.instance().assignTransition(event_last, "complete");
			if (last_activity_ts != null) {
				XTimeExtension.instance().assignTimestamp(event_last, new Date(last_activity_ts.getTime() + 10L));
			}
			trace.add(event_last);
		}
	}
  
	public static UnfoldResult unfold(XLog log) throws Exception
	{
		UnfoldResult result = new UnfoldResult();
    
		int count = 0;
		
    	for (int i = 0; i < log.size(); i++)
		{
			XTrace trace = (XTrace)log.get(i);
			String traccia = trace.getAttributes().get("concept:name") + " # " + i;
			if (!result.traccia_attivita.containsKey(traccia))
			{
				ObjectArrayList<String> lista = new ObjectArrayList<String>();
				lista.trimToSize();
				result.traccia_attivita.put(traccia, lista);
			}
			for (XEvent activity : trace)
			{
				String nome_attivita = activity.getAttributes().get("concept:name") + "#" + String.format("%04d", new Object[] { Integer.valueOf(0) });
				if (!(result.traccia_attivita.get(traccia)).contains(nome_attivita))
				{
					(result.traccia_attivita.get(traccia)).add(nome_attivita);
				}
				else
				{
					int counter = -1;
					for (int ii = (result.traccia_attivita.get(traccia)).size() - 1; ii >= 0; ii--)
					{
						String nome_attiv = (result.traccia_attivita.get(traccia)).get(ii);
            
						String[] split = nome_attiv.split("#");
						if (split[0].equals(nome_attivita.split("#")[0]))
						{
							counter = Integer.parseInt(split[1]) + 1;
							break;
						}
					}
					nome_attivita = nome_attivita.split("#")[0] + "#" + String.format("%04d", new Object[] { Integer.valueOf(counter) });
					(result.traccia_attivita.get(traccia)).add(nome_attivita);
				}
				if (!result.attivita_tracce.containsKey(nome_attivita))
				{
					ObjectArrayList<String> lista_tracce = new ObjectArrayList<String>();
          
					lista_tracce.add(traccia);
					result.attivita_tracce.put(nome_attivita, lista_tracce);
				}
				else
				{
					(result.attivita_tracce.get(nome_attivita)).add(traccia);
				}
			}
		}
		Object[] keys = result.attivita_tracce.keys;
    
		boolean[] states = result.attivita_tracce.allocated;
		for (int i = 0; i < states.length; i++) {
			if (states[i] != false) {
				if (!result.map.containsKey((String)keys[i]))
				{
					result.map.put((String)keys[i], count);
					count++;
				}
				else
				{
					System.out.println("PROBLEMA!");
				}
			}
		}
    
		return result;
	}
}
