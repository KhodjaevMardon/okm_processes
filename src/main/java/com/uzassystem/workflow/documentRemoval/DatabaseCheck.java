package com.uzassystem.workflow.documentRemoval;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.openkm.bean.form.Input;
import com.openkm.dao.LegacyDAO;

public class DatabaseCheck implements ActionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(ExecutionContext execCtxt) throws Exception {
		// Kinda ready

		String docUUID = ((Input) execCtxt.getContextInstance().getVariable("Doc_UUID")).getValue();

		String dbDocumentRecordCheck = "SELECT * FROM documents_data WHERE DOCUMENT_UUID = '" + docUUID + "';";

		List<List<String>> dbRecordsList = new ArrayList<>();
		dbRecordsList = LegacyDAO.executeSQL(dbDocumentRecordCheck);

		execCtxt.getContextInstance().setVariable("dbRecordsCount", String.valueOf(dbRecordsList.size()));
		execCtxt.getToken().signal();

	}

}