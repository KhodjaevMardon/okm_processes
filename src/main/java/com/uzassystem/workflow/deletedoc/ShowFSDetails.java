package com.uzassystem.workflow.deletedoc;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.openkm.bean.form.TextArea;

public class ShowFSDetails implements ActionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		// TODO Auto-generated method stub
		String docFound = (String) executionContext.getContextInstance().getVariable("docFound");

		TextArea fsInfo = new TextArea();
		StringBuilder label = new StringBuilder();

		if (docFound.equals("true")) {

			label.append("Document was found.\n");
			String docPath = (String) executionContext.getContextInstance().getVariable("docPath");
			label.append(docPath);

		} else {
			label.append("Document was not found.");
		}

		fsInfo.setLabel(String.valueOf(label));
		fsInfo.setData(String.valueOf(label));
		fsInfo.setName(String.valueOf(label));
		fsInfo.setValue(String.valueOf(label));

		executionContext.getContextInstance().setVariable("fsInfo", fsInfo);

	}
}
