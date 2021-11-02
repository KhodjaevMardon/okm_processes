package com.uzassystem.workflow.subcontractRegistration;

import com.openkm.api.OKMFolder;
import com.openkm.api.OKMRepository;
import com.openkm.bean.Folder;
import com.openkm.bean.form.Select;
import com.openkm.bean.form.TextArea;
import com.openkm.bean.form.Upload;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

public class RegisterSubcontract implements ActionHandler {

	private static final long serialVersionUID = 1L;
	private String projectCode;
	private String subcontractType;
	private String subcontractorName;
	private String folderPath = "/okm:root/Subcontracts/";

	public void execute(final ExecutionContext executionContext) throws Exception {

		this.subcontractType = ((Select) executionContext.getContextInstance().getVariable("okp:subcontract.type"))
				.getValue();

		Select proTiagram = (Select) executionContext.getContextInstance().getVariable("okp:subcontract.pro_tiagram");
		if (proTiagram != null) {
			this.projectCode = proTiagram.getValue();
			executionContext.getContextInstance().deleteVariable("okp:subcontract.pro_tiagram");
			executionContext.getContextInstance().setVariable("pro_tiagram", this.projectCode);
		} else {
			this.projectCode = (String) executionContext.getContextInstance().getVariable("pro_tiagram");
		}

		Select subcontractorField = (Select) executionContext.getContextInstance()
				.getVariable("okp:subcontract.subcontractor.name");
		if (subcontractorField != null) {
			this.subcontractorName = subcontractorField.getValue();
			executionContext.getContextInstance().setVariable("subcontractorNameForFolder", this.subcontractorName);
		} else {
			throw new RuntimeException("subcontractorField (okp:subcontract.subcontractor.name) is null, please fix");
		}

		// this.folderPath = this.folderPath + this.projectCode + "/" +
		// this.subcontractorName;

		String baseFolder = "/okm:root/Contractual Documents/";
		String folderPathNew = baseFolder + this.projectCode + "/Subcontracts/" + this.subcontractorName;
		this.folderPath = folderPathNew;
		OKMFolder.getInstance().createMissingFolders(null, this.folderPath);

		StringBuilder stringBuilder = new StringBuilder();

		for (Folder fld : OKMFolder.getInstance().getChildren(null, this.folderPath)) {
			String path = OKMRepository.getInstance().getNodePath(null, fld.getUuid());
			stringBuilder.append(path.substring(path.lastIndexOf('/') + 1).split("\\.")[0]).append("\n");
		}

		if (this.subcontractType.equals("CNT")) {
			String newCntName = getMaxSequence(this.folderPath);
			executionContext.getContextInstance().setVariable("newCntName", newCntName);
			this.folderPath = this.folderPath + "/" + newCntName;
			OKMFolder.getInstance().createMissingFolders(null, this.folderPath);
		}
		Upload upd = new Upload();
		upd.setFolderPath(this.folderPath);
		upd.setName("dynamic");
		executionContext.setVariable("updData", upd);

		TextArea subcontractList = new TextArea();
		subcontractList.setValue(stringBuilder.toString());
		executionContext.getContextInstance().setVariable("subcontractList", subcontractList);

		executionContext.getToken().signal();

	}

	public String getMaxSequence(String projPath) throws Exception {
		int max = 0;
		for (Folder fld : OKMFolder.getInstance().getChildren(null, projPath)) {
			int curr = getSequence(fld.getPath());
			if (curr > max) {
				max = curr;
			}
		}

		String cntName = this.projectCode + "-CNT-AGR-" + String.format("%03d", max + 1);

		return cntName;
	}

	public int getSequence(String name) {
		return Integer.parseInt(name.substring(name.length() - 3, name.length()));
	}
}
