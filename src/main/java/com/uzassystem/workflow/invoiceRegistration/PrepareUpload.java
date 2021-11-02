package com.uzassystem.workflow.invoiceRegistration;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.openkm.api.OKMFolder;
import com.openkm.bean.form.Input;
import com.openkm.bean.form.Select;
import com.openkm.bean.form.Upload;

public class PrepareUpload implements ActionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		String folderPath;
		String proTrigram;
		String baseFolder;
		String associationName;
		String associationFolder;
		String documentName;
		String subcontractorName = "";
		String invoiceFolderName;
		// StringBuilder docPathBuilder = new StringBuilder();

		proTrigram = ((Select) executionContext.getContextInstance().getVariable("okp:invoice.pro_tiagram")).getValue();
		executionContext.getContextInstance().setVariable("proTrigram", proTrigram);

		baseFolder = "/okm:root/";
		executionContext.getContextInstance().setVariable("baseFolder", baseFolder);

		documentName = ((Input) executionContext.getContextInstance().getVariable("okp:invoice.document.name"))
				.getValue();
		executionContext.getContextInstance().setVariable("documentName", documentName);

		associationName = ((Select) executionContext.getContextInstance().getVariable("okp:invoice.association"))
				.getValue();
		executionContext.getContextInstance().setVariable("association", associationName);

		// associationName is either Contract or Subcontract, so we add 's/' to
		// make it a folderName (wonderful, innit?)
		associationFolder = associationName + "s/";
		executionContext.getContextInstance().setVariable("associationFolder", associationFolder);

		if (associationName.equals("Subcontract")) {
			subcontractorName = ((Select) executionContext.getContextInstance()
					.getVariable("okp:invoice.subcontractor.name")).getValue() + "/";
		}

		invoiceFolderName = "INV_" + documentName.substring(0, documentName.lastIndexOf("."));

		// in the line below, subcontractorName is either empty(if user chose
		// 'Contract' as association), or equal to the chosen Subcontractor name

		// folderPath = baseFolder + associationFolder + proTrigram + "/" +
		// subcontractorName + invoiceFolderName;

		String newFolderPath = baseFolder + "Contractual Documents/" + proTrigram + "/" + associationFolder
				+ subcontractorName + documentName.substring(0, documentName.lastIndexOf(".")) + "/"
				+ invoiceFolderName;
		folderPath = newFolderPath;

		Map<String, String> debugInfoMap = new HashMap<>();
		debugInfoMap.put("folderPath", folderPath);
		debugInfoMap.put("associationFolder", associationFolder);
		debugInfoMap.put("invoiceFolderName", invoiceFolderName);
		debugInfoMap.put("documentName", documentName);
		executionContext.getContextInstance().setVariable("debugMap", debugInfoMap);

		OKMFolder.getInstance().createMissingFolders(null, folderPath);

		Upload upd = new Upload();
		upd.setFolderPath(folderPath);
		upd.setName("dynamic");
		executionContext.getContextInstance().setVariable("updData", upd);

		executionContext.getToken().signal();
	}

}