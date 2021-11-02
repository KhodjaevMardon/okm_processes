package com.uzassystem.workflow.unitRegistration;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

public class SetRepetition implements ActionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		// its simple, we set the repetition to true so that we could get this info later in other classes
		executionContext.getContextInstance().setVariable("to repeat", "true");
		
	}

}