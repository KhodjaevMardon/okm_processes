package com.uzassystem.workflow.documentRemoval;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.openkm.api.OKMRepository;
import com.openkm.bean.form.Input;

public class FileSystemCheck implements ActionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(ExecutionContext execCtxt) throws Exception {
		// ready to use, i guess
		String docUUID = ((Input) execCtxt.getContextInstance().getVariable("Doc_UUID")).getValue();
		boolean docDoesExist = true;
		String path = "";
		try {
			path = OKMRepository.getInstance().getNodePath(null, docUUID);
		} catch (Exception e) {
			docDoesExist = false;
		}

		execCtxt.getContextInstance().setVariable("docPath", path);
		execCtxt.getContextInstance().setVariable("docFound", String.valueOf(docDoesExist));

		execCtxt.getToken().signal();

	}

}