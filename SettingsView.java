package org.processmining.plugins.cnmining;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.deckfour.uitopia.api.event.TaskListener;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.widgets.ProMPropertiesPanel;

import com.fluxicon.slickerbox.components.NiceSlider;
import com.fluxicon.slickerbox.factory.SlickerFactory;

/*
 * Questa classe implementa l'interfaccia grafica
 * utilizzata per la configurazione del plugin
 */

public class SettingsView {

	private UIPluginContext context;
	private XLog log;
	
	private static double sigmaLogNoise = 0.0D, fallFactor = 0.0D, relativeToBest = 0.0D;
	
	public SettingsView(UIPluginContext context, XLog log){
		this.context = context;
		this.log = log;
	}
	
	/*
	 * Questo metodo espone una intefaccia grafica
	 * dalla quale si inizializza l'oggetto settings
	 */
	public Settings show(){
		XConceptExtension conceptExtension = XConceptExtension.instance();
	    String logName = conceptExtension.extractName(log);
	    
	    ProMPropertiesPanel viewContainer = new ProMPropertiesPanel("");
	    ConstraintsViewPanel pannelloVincoli = new ConstraintsViewPanel();
	    		
	    // Crea lo slider per valorizzare la variabile value
	    final NiceSlider slider = SlickerFactory.instance().createNiceIntegerSlider("SigmaLogNoise percentage", 0, 
	    	      100, 5, NiceSlider.Orientation.HORIZONTAL);
	    ChangeListener listener = new ChangeListener()
	    {
	    	public void stateChanged(ChangeEvent e)
	    	{
	    		int percentage = slider.getSlider().getValue();
	        
	    		SettingsView.sigmaLogNoise = (percentage / 100.0D);
	    	}
	    };
	    
	    slider.addChangeListener(listener);
	    listener.stateChanged(null); // invocalo una volta
	    
	    // delta
	    final NiceSlider slider1 = SlickerFactory.instance().createNiceIntegerSlider("FallFactor percentage", 0, 100, 
	      90, NiceSlider.Orientation.HORIZONTAL);
	    ChangeListener listener1 = new ChangeListener()
	    {
	    	public void stateChanged(ChangeEvent e)
	    	{
	    		int percentage = slider1.getSlider().getValue();
	    		SettingsView.fallFactor = (percentage / 100.0D);
	    	}
	    };
	    slider1.addChangeListener(listener1);
	    listener1.stateChanged(null);
	    
	    // RelativeToBest
	    final NiceSlider slider2 = SlickerFactory.instance().createNiceIntegerSlider("RelativeToBest percentage", 0, 
	      100, 75, NiceSlider.Orientation.HORIZONTAL);
	    ChangeListener listener2 = new ChangeListener()
	    {
	    	public void stateChanged(ChangeEvent e)
	    	{
	    		int percentage = slider2.getSlider().getValue();
	        
	    		SettingsView.relativeToBest = (percentage / 100.0D);
	    	}
	    };
	    slider2.addChangeListener(listener2);
	    listener2.stateChanged(null);
	    
	    // determina una stile grafico per i vari slider
	    slider.setBorder(BorderFactory.createEtchedBorder(Color.white, Color.gray));
	    slider1.setBorder(BorderFactory.createEtchedBorder(Color.white, Color.gray));
	    slider2.setBorder(BorderFactory.createEtchedBorder(Color.white, Color.gray));
	    
	    // posiziona i controlli grafici
	    viewContainer.add(pannelloVincoli);
	    viewContainer.add(slider);
	    viewContainer.add(slider1);
	    viewContainer.add(slider2);
	    
	    // mostra la schermata di configurazione
	    TaskListener.InteractionResult result = context.showConfiguration("Settings", viewContainer);
	    if (result.equals(TaskListener.InteractionResult.CANCEL)) {
	      context.getFutureResult(0).cancel(true);
	    }
	    
	    // Raggruppe le confiruazioni del plugin
	    
	    Settings s = new Settings();
	    
	    s.constraintsEnabled = pannelloVincoli.areConstraintsEnabled();
	    s.constraintsFilename = pannelloVincoli.getFilename();
	    s.sigmaLogNoise = SettingsView.sigmaLogNoise;
	    s.logName = logName;
	    s.fallFactor = SettingsView.fallFactor;
	    s.relativeToBest = SettingsView.relativeToBest;
	    
	    return s;
	}
	
}
