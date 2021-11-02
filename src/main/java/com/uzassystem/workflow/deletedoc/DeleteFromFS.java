/**
 * 
 */
package com.uzassystem.workflow.deletedoc;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.openkm.api.OKMDocument;
import com.openkm.bean.form.Input;

/**
 * @author openkm
 *
 */
public class DeleteFromFS implements ActionHandler {

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
	public void execute(ExecutionContext execCtxt) throws Exception {
		// TODO Auto-generated method stub

		String docUUID = ((Input) execCtxt.getContextInstance().getVariable("Doc_UUID")).getValue();
		String docFound = (String) execCtxt.getContextInstance().getVariable("docFound");
		// file system remove
		if (docFound.equals("true")) {
			try {
				OKMDocument.getInstance().delete(null, docUUID);
			} catch (Exception e) {
				throw new RuntimeException(e.getStackTrace().toString(), e);
			}
		}

		execCtxt.getToken().signal();

	}

}
