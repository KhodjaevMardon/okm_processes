package com.uzassystem.workflow.action;

import com.openkm.bean.form.Select;
import java.util.List;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import com.openkm.bean.form.Input;

public class ShowCodeToControl implements ActionHandler {
	private static final long serialVersionUID = 1L;
	public String docCode;
	public String docName;
	public List<List<String>> docSequenceFlag;
	public String docSequence = "000";
	public String tiagram_label;
	public String tiagram_value;
	public String phase_label;
	public String phase_value;
	public String discip_label;
	public String discip_value;
	public String docType_label;
	public String docType_value;
	public String loc_label;
	public String loc_value;
	public String author_name;
	public String docExtension;
	public String templatePath = "/okm:root/templates/";
	public String templateName = "template";
	public String docUUID;
	public String folderUUID;

	public void execute(ExecutionContext executionContext) throws Exception {
		Select pro_tiagram = (Select) executionContext.getContextInstance().getVariable("okp:codification.pro_tiagram");
		if (pro_tiagram != null) {
			this.tiagram_label = pro_tiagram.getLabel();
			this.tiagram_value = pro_tiagram.getValue();
			System.out.println(this.tiagram_label + "    " + this.tiagram_value);
		}
		Select pro_phase = (Select) executionContext.getContextInstance().getVariable("okp:codification.pro_phase");
		if (pro_phase != null) {
			this.phase_label = pro_phase.getLabel();
			this.phase_value = pro_phase.getValue();
		}

		Select discipline = (Select) executionContext.getContextInstance().getVariable("okp:codification.discipline");
		if (discipline != null) {
			this.discip_label = discipline.getLabel();
			this.discip_value = discipline.getValue();
		}

		Select doc_type = (Select) executionContext.getContextInstance().getVariable("okp:codification.doc_type");
		if (doc_type != null) {
			this.docType_label = doc_type.getLabel();
			this.docType_value = doc_type.getValue();
		}

		Select location = (Select) executionContext.getContextInstance().getVariable("okp:codification.location");
		if (location != null) {
			this.loc_label = location.getLabel();
			this.loc_value = location.getValue();
		}

		Select doc_extension = (Select) executionContext.getContextInstance().getVariable("fileExtension");
		if (doc_extension != null) {
			this.docExtension = doc_extension.getValue();
		}

		this.docCode = this.tiagram_value + "-" + this.phase_value + "-" + this.discip_value + "-" + this.docType_value
				+ "-" + this.loc_value;
		Input codeTVerify = new Input();
		codeTVerify.setName("codeToVerify");
		codeTVerify.setLabel("Code to be Verified");
		codeTVerify.setValue(this.docCode);
		codeTVerify.setWidth("200px");
		executionContext.getContextInstance().setVariable("codeToVerify", codeTVerify);
		// executionContext.getToken().signal();

	}
}