package com.uzassystem.workflow.action;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

public class SetVariableForRepeation implements ActionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {

		executionContext.getContextInstance().setVariable("repeatOrNot", "yes");

	}
}
