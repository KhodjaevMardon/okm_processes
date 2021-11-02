package com.uzassystem.workflow.deletedoc;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.openkm.bean.form.Input;
import com.openkm.dao.LegacyDAO;

public class DeleteFromDB implements ActionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(ExecutionContext arg0) throws Exception {
		// TODO Auto-generated method stub

		String docUUID = (((Input) arg0.getContextInstance().getVariable("Doc_UUID")).getValue());

		// db remove
		try {
			String removeRecordsQuery = "DELETE FROM DOCUMENTS_DATA WHERE DOCUMENT_UUID = '" + docUUID + "';";
			LegacyDAO.executeSQL(removeRecordsQuery);
		} catch (Exception e) {
			throw new RuntimeException("Error in delete part:\n" + e.getStackTrace().toString(), e);
		}

		arg0.getToken().signal();
	}

}