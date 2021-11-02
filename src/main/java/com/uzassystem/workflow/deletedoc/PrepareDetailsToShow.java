/**
 * 
 */
package com.uzassystem.workflow.deletedoc;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.openkm.bean.form.TextArea;
import com.openkm.bean.form.Input;

/**
 * @author openkm
 *
 */
public class PrepareDetailsToShow implements ActionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		// TODO Auto-generated method stub

		// prepare TextArea for task
		TextArea fsInfo = new TextArea();
		StringBuilder label = new StringBuilder();

		// prepare records info and (if found some) db query
		String dbRecordsCountString = (String) executionContext.getContextInstance().getVariable("dbRecordsCount");
		int dbRecordsCount = Integer.parseInt(dbRecordsCountString);
		String docUUID = ((Input) executionContext.getContextInstance().getVariable("Doc_UUID")).getValue();

		label.append(String.format("Found %d records in database table 'DOCUMENTS_DATA' matching given uuid.\n",
				dbRecordsCount));

		if (dbRecordsCount > 0) {
			label.append("Database query:\nDELETE FROM DOCUMENTS_DATA WHERE DOCUMENT_UUID = '" + docUUID + "';\n");
		}

		label.append("\n");

		// prepare process instances ids list
		int processInstancesCount = Integer
				.parseInt((String) executionContext.getContextInstance().getVariable("processInstancesCount"));
		if (processInstancesCount > 0) {
			label.append(String.format(
					"Found %d process instances with following IDs\n(Format:Process Definition ID _"
							+ " Process Definition Name : Process Instance ID1 _ Process Instance ID2 _ ...):\n",
					processInstancesCount));
			String processInstanceIDs = (String) executionContext.getContextInstance()
					.getVariable("processInstancesIds");
			label.append(processInstanceIDs);
			label.append("\n");
		} else {
			label.append("Found 0 process instances.\n\n");
		}

		// prepare process instances ids list
		int badProcessInstancesCount = Integer
				.parseInt((String) executionContext.getContextInstance().getVariable("badProcessInstancesCount"));
		if (badProcessInstancesCount > 0) {
			label.append(String.format(
					"Found %d bad(with uuid equal to your document's UUID) process instances with following IDs"
							+ "\n(Format:Process Definition ID _ Process Definition Name : Process Instance ID1 _ Process Instance ID2 _ ...):\n",
					badProcessInstancesCount));
			String badProcessInstanceIDs = (String) executionContext.getContextInstance()
					.getVariable("badProcessInstancesIds");
			label.append(badProcessInstanceIDs);
			label.append("\n");
		} else {
			label.append("Found 0 bad process instances.\n\n");
		}

		// prepare docPath
		String docFound = (String) executionContext.getContextInstance().getVariable("docFound");
		if (docFound.equals("true")) {
			label.append("Document path found:\n");
			String docPath = (String) executionContext.getContextInstance().getVariable("docPath");
			label.append(docPath);
		} else {
			label.append("Document path not found.");
		}

		executionContext.getContextInstance().setVariable("label", label);

		fsInfo.setLabel(String.valueOf(label));
		fsInfo.setData(String.valueOf(label));
		fsInfo.setName(String.valueOf(label));
		fsInfo.setValue(String.valueOf(label));

		executionContext.getContextInstance().setVariable("docInfo", fsInfo);

		executionContext.getToken().signal();

	}

}
