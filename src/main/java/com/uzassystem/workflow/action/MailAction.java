package com.uzassystem.workflow.action;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.openkm.util.MailUtils;

public class MailAction implements ActionHandler {
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		MailUtils.sendMessage("josep@openkm.com", "subject", "content");
	}
}