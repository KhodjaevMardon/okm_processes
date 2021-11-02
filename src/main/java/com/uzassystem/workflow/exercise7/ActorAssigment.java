package com.uzassystem.workflow.exercise7;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;

import com.openkm.bean.form.Input;

public class ActorAssigment implements AssignmentHandler {
	private static final long serialVersionUID = 1L;

	@Override
	public void assign(Assignable assignable, ExecutionContext executionContext) throws Exception {
		Input username = (Input) executionContext.getContextInstance().getVariable("username");

		if (username != null) {
			assignable.setActorId(username.getValue());
		} else {
			assignable.setActorId("okmAdmin");
		}
	}
}