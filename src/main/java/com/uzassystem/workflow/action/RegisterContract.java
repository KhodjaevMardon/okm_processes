package com.uzassystem.workflow.action;

import com.openkm.api.OKMFolder;
import com.openkm.api.OKMRepository;
import com.openkm.bean.Folder;
import com.openkm.bean.form.*;
import com.openkm.bean.form.Upload;

// by Mardon 
// last changes: 15.10.2021

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

public class RegisterContract implements ActionHandler {

	private static final long serialVersionUID = 1L;
	private String projectCode;
	private String contractType;
	private String folderPath = "/okm:root/Contracts/";

	public void execute(final ExecutionContext executionContext) throws Exception {

		this.contractType = ((Select) executionContext.getContextInstance().getVariable("okp:contract.type"))
				.getValue();

		Select proTiagram = (Select) executionContext.getContextInstance().getVariable("okp:contract.pro_tiagram");
		if (proTiagram != null) {
			this.projectCode = proTiagram.getValue();
			executionContext.getContextInstance().deleteVariable("okp:contract.pro_tiagram");
			executionContext.getContextInstance().setVariable("pro_tiagram", this.projectCode);
		} else {
			this.projectCode = (String) executionContext.getContextInstance().getVariable("pro_tiagram");
		}

		String basePath = "/okm:root/Contractual Documents/";
		this.folderPath = basePath + this.projectCode + "/Contracts";
		OKMFolder.getInstance().createMissingFolders(null, this.folderPath);

		StringBuilder stringBuilder = new StringBuilder();

		for (Folder fld : OKMFolder.getInstance().getChildren(null, this.folderPath)) {
			String path = OKMRepository.getInstance().getNodePath(null, fld.getUuid());
			stringBuilder.append(path.substring(path.lastIndexOf('/') + 1).split("\\.")[0]).append("\n");
		}

		TextArea contractList = new TextArea();
		contractList.setValue(stringBuilder.toString());
		executionContext.getContextInstance().setVariable("contractList", contractList);

		if (this.contractType.equals("CNT")) {
			String newCntName = getMaxSequence(this.folderPath);
			executionContext.getContextInstance().setVariable("newCntName", newCntName);
			this.folderPath = this.folderPath + "/" + newCntName;
			OKMFolder.getInstance().createMissingFolders(null, this.folderPath);
		}

		Upload upd = new Upload();
		upd.setFolderPath(this.folderPath);
		upd.setName("dynamic");
		executionContext.setVariable("updData", upd);

		executionContext.getContextInstance().deleteVariable("repeatOrNot");
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
