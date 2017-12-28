package org.processmining.contexts.uitopia.model;

import java.lang.reflect.Array;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.deckfour.uitopia.api.model.Action;
import org.deckfour.uitopia.api.model.ActionStatus;
import org.deckfour.uitopia.api.model.ActionType;
import org.deckfour.uitopia.api.model.Author;
import org.deckfour.uitopia.api.model.Category;
import org.deckfour.uitopia.api.model.Parameter;
import org.deckfour.uitopia.api.model.Resource;
import org.deckfour.uitopia.api.model.ResourceType;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.hub.ProMResourceManager;
import org.processmining.framework.plugin.PluginDescriptor;
import org.processmining.framework.plugin.PluginManager;
import org.processmining.framework.plugin.PluginParameterBinding;


/**
 * 
 * @author Utente
 */
public class ProMAction implements Action, Comparable<ProMAction> {

	private final PluginDescriptor plugin;
	private final int methodIndex;
	private final List<Parameter> inputs;
	private String name;
	private final List<Parameter> outputs;
	private final boolean[] isResource;
	private final int[] resourceIndex;
	private final Author author;
	private final PluginManager pluginManager;
	private final String pack;

	private String help;
	private String[] keywords;
	private String[] catergories;
        
        private void pMAP1(int parameterNamesSize, PluginDescriptor plugDes, int metIn, ProMResourceManager resourceManager, int resIndex){
            for (int i = 0; i < parameterNamesSize; i++) {
			Class<?> type = plugDes.getParameterTypes(metIn).get(i);

			String name = plugDes.getParameterNames(metIn).get(i);

			boolean isArray = type.isArray();
			if (isArray) {
				type = type.getComponentType();
			}
			ResourceType resType = resourceManager.getResourceTypeFor(type);

			if (resType != null) {
				inputs.add(new ProMParameter(name, i, resType, isArray));
				isResource[i] = true;
				resourceIndex[i] = resIndex++;
			} else {
				isResource[i] = false;
				resourceIndex[i] = -1;
			}
		}
        }
        
        private void pMAP2(int returnNamesSize, PluginDescriptor plugDes, ProMResourceManager resourceManager){
            for (int i = 0; i < returnNamesSize; i++) {
			Class<?> type = plugDes.getReturnTypes().get(i);
			String name = plugDes.getReturnNames().get(i);

			boolean isArray = type.isArray();
			if (type.isArray()) {
				type = type.getComponentType();
			}
			ResourceType resType = resourceManager.getResourceTypeFor(type);
			if (resType != null) {
				outputs.add(new ProMParameter(name, i, resType, isArray));
			}
		}
        }

	public ProMAction(ProMResourceManager resourceManager, PluginManager pluginManager, final PluginDescriptor plugin,
			final int methodIndex) {
		this.pluginManager = pluginManager;
		this.plugin = plugin;
		this.methodIndex = methodIndex;

		name = plugin.getAnnotation(UITopiaVariant.class, methodIndex).uiLabel();
		if (name.equals(UITopiaVariant.USEPLUGIN)) {
			name = plugin.getName();
		} else if (name.equals(UITopiaVariant.USEVARIANT)) {
			name = plugin.getMethodLabel(methodIndex);
		}

		help = plugin.getAnnotation(UITopiaVariant.class, methodIndex).uiHelp();
		if (help.equals(UITopiaVariant.USEPLUGIN)) {
			help = plugin.getHelp();
		} else if (help.equals(UITopiaVariant.USEVARIANT)) {
			help = plugin.getMethodHelp(methodIndex);
		}

		catergories = plugin.getCategories();
		keywords = plugin.getKeywords();

		pack = plugin.getAnnotation(UITopiaVariant.class, methodIndex).pack();

		author = new Author() {

			public String getAffiliation() {
				return plugin.getAnnotation(UITopiaVariant.class, methodIndex).affiliation();
			}

			public String getEmail() {
				return plugin.getAnnotation(UITopiaVariant.class, methodIndex).email();
			}

			public String getName() {
				return plugin.getAnnotation(UITopiaVariant.class, methodIndex).author();
			}

			public URI getWebsite() {
				URI uri = null;
				try {
					uri = new URL(plugin.getAnnotation(UITopiaVariant.class, methodIndex).website()).toURI();
				} catch (Exception e) {
					try {
						uri = new URL("http://www.processmining.org").toURI();
					} catch (Exception e2) {
                                            System.out.println("errore");    
					}
				}
				return uri;
			}

		};

		isResource = new boolean[plugin.getParameterNames(methodIndex).size()];
		resourceIndex = new int[plugin.getParameterNames(methodIndex).size()];

		inputs = new ArrayList<Parameter>();
		int resIndex = 0;
		int parameterNamesSize = plugin.getParameterNames(methodIndex).size();
                pMAP1(parameterNamesSize, plugin, methodIndex, resourceManager, resIndex);
		

		outputs = new ArrayList<Parameter>();
		int returnNamesSize = plugin.getReturnNames().size();
                pMAP2(returnNamesSize, plugin, resourceManager);
		

	}

	public boolean handlesCancel() {
		return plugin.handlesCancel();
	}

	public List<Parameter> getInput() {
		return inputs;
	}

	public String getName() {
		return name;
	}

	public String getPackage() {
		return pack;
	}

	public List<Parameter> getOutput() {
		return outputs;
	}

	public <R extends Resource> List<PluginParameterBinding> getBindings(List<Collection<R>> parameterValues,
			boolean executable) {
		Class<?>[] types = new Class<?>[parameterValues.size()];
		int paramValuesSize = parameterValues.size();
		for (int i = 0; i < paramValuesSize; i++) {
			Collection<? extends Resource> resources = parameterValues.get(i);
			if (resources.isEmpty()) {
				types[i] = null;
				continue;
			}
			types[i] = resources.iterator().next().getType().getTypeClass();
			if (resources.size() > 1) {
				types[i] = Array.newInstance(types[i], 0).getClass();
			}
		}

		return PluginParameterBinding.Factory.tryToBind(pluginManager, plugin, methodIndex, executable, false, types);

	}

	public ActionStatus getStatus(List<Collection<? extends Resource>> parameterValues) {

		// This cast is safe, since ProMResource is a final class, i.e. the given collection
		// cannot be based on subtypes. 
		List<Collection<ProMResource<?>>> promParameterValues = Cast
				.<java.util.List<Collection<ProMResource<?>>>>cast(parameterValues);

		List<PluginParameterBinding> bindings = getBindings(promParameterValues, true);

		if (!bindings.isEmpty()) {
			return ActionStatus.EXECUTABLE;
		}

		bindings = getBindings(promParameterValues, false);

		if (!bindings.isEmpty()) {
			return ActionStatus.INCOMPLETE;
		}

		return ActionStatus.INVALID;

	}

	public ActionType getType() {
		if (UIPluginContext.class.isAssignableFrom(plugin.getContextType(methodIndex))) {
			return ActionType.INTERACTIVE;
		} else {
			return ActionType.HEADLESS;
		}
	}

	public PluginDescriptor getPlugin() {
		return plugin;
	}

	public int getMethodIndex() {
		return methodIndex;
	}

	public int compareTo(ProMAction action) {
		int c = getName().compareTo(action.getName());
		if (c == 0) {
			return methodIndex - action.methodIndex;
		}
		return c;
	}

	public int getMostSignificantResultIndex() {
		return plugin.getMostSignificantResult();
	}

	public boolean equals(Object o) {
		return (o instanceof ProMAction ? plugin.equals(((ProMAction) o).plugin)
				&& (methodIndex == ((ProMAction) o).methodIndex) : false);
	}

	public int hashCode() {
		return 37 * plugin.hashCode() + methodIndex;
	}

	public String toString() {
		return getName();
	}

	public Author getAuthor() {
		return author;
	}

	public String getHelp() {
		return help;
	}

	public String[] getCategories() {
		return catergories;
	}

	public String[] getKeywords() {
		return keywords;
	}

	public boolean isOfCategory(Category category) {
		for (String c : catergories) {
			if (category.getName().equals(c)) {
				return true;
			}
		}
		return false;
	}
}
