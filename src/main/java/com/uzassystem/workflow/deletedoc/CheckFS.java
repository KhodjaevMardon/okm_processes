/**
 * 
 */
package com.uzassystem.workflow.deletedoc;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.openkm.api.OKMRepository;
import com.openkm.bean.form.Input;

/**
 * @author openkm
 *
 */
public class CheckFS implements ActionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jbpm.graph.def.ActionHandler#execute(org.jbpm.graph.exe.
	 * ExecutionContext)
	 */
	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		// TODO Auto-generated method stub
		String docUUID = ((Input) executionContext.getContextInstance().getVariable("Doc_UUID")).getValue();
		boolean docDoesExist = true;
		String path = "";
		try {
			path = OKMRepository.getInstance().getNodePath(null, docUUID);
		} catch (Exception e) {
			docDoesExist = false;
		}

		executionContext.getContextInstance().setVariable("docPath", path);
		executionContext.getContextInstance().setVariable("docFound", String.valueOf(docDoesExist));

		executionContext.getToken().signal();
	}

}
