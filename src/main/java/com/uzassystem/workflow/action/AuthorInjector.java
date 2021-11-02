package com.uzassystem.workflow.action;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

public class AuthorInjector implements ActionHandler {
	private static final long serialVersionUID = 1L;

	public String author_name;

	public void execute(ExecutionContext executionContext) throws Exception {
		this.author_name = (String) executionContext.getJbpmContext().getActorId();

		executionContext.setVariable("author_name", this.author_name);

	}
}