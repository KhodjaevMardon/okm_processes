package com.uzassystem.workflow.action;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

public class AuthorInjection implements ActionHandler {
	private static final long serialVersionUID = 1L;

	private String authorName;

	public void execute(ExecutionContext executionContext) throws Exception {
		this.authorName = (String) executionContext.getJbpmContext().getActorId();

		executionContext.getContextInstance().setVariable("author_name", this.authorName);
	}
}