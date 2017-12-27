package org.processmining.plugins.cnet2ad;

import org.processmining.models.flexiblemodel.Flex;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.plugins.converters.BPMNUtils;
import org.processmining.plugins.converters.FlexToBPMNConverter;

/**
 * 
 * @author Utente
 */
public class Flex2BPMN {

    public static BPMNDiagram convert(Flex model){
        FlexToBPMNConverter converter = new FlexToBPMNConverter(model);
        BPMNDiagram diagram = converter.convert();
        BPMNUtils.simplifyBPMNDiagram(converter.getConversionMap(), diagram);
        return diagram;
    }

}

