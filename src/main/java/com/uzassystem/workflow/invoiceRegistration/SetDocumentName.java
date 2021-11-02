package com.uzassystem.workflow.invoiceRegistration;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.openkm.api.OKMDocument;
import com.openkm.api.OKMRepository;
import com.openkm.automation.AutomationException;
import com.openkm.bean.Document;
import com.openkm.bean.form.Select;
import com.openkm.bean.form.Input;
import com.openkm.bean.form.Upload;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.ItemExistsException;
import com.openkm.core.LockException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.extension.core.ExtensionException;

public class SetDocumentName implements ActionHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String docName;

	public void execute(ExecutionContext executionContext) {
		// we set invoice document's name and then set metadata to it.
		// most of the time, 'doc' means the invoice document.
		String uuid, docPath = "", proTrigram, associationDocumentName, associationSequenceNumber, docSequenceNumber;

		Upload upd = new Upload();
		upd = (Upload) executionContext.getContextInstance().getVariable("okp:invoice.uploadInvoice");
		uuid = upd.getDocumentUuid();

		executionContext.getContextInstance().setVariable("invoiceDocumentUUID", uuid);

		try {
			docPath = OKMRepository.getInstance().getNodePath(null, uuid);
		} catch (AccessDeniedException | PathNotFoundException | RepositoryException | DatabaseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		this.docName = docPath.substring(docPath.lastIndexOf("/"));

		executionContext.getContextInstance().setVariable("oldName", this.docName.substring(1));

		docSequenceNumber = String.format("%03d", getGreatestSequence(docPath) + 1);
		executionContext.getContextInstance().setVariable("invoiceSequenceNumber", docSequenceNumber);
		associationDocumentName = ((Input) executionContext.getContextInstance()
				.getVariable("okp:invoice.document.name")).getValue();
		associationSequenceNumber = extractSequenceNumber(associationDocumentName);

		proTrigram = ((Select) executionContext.getContextInstance().getVariable("okp:invoice.pro_tiagram")).getValue();

		String docExtension = docPath.substring(docPath.lastIndexOf("."));// example:
		// '.doc',
		// '.pdf'
		// and so on

		try {
			OKMDocument.getInstance().rename(null, uuid,
					proTrigram + "-INV-" + associationSequenceNumber + "-" + docSequenceNumber + docExtension);
			// Example: P001-INV-001-001.pdf
		} catch (AutomationException | PathNotFoundException | ItemExistsException | AccessDeniedException
				| LockException | RepositoryException | DatabaseException | ExtensionException e) {
			e.printStackTrace();
		}

		executionContext.getToken().signal();

	}

	public int getGreatestSequence(String docPath) {
		List<Document> docs = new ArrayList<>();
		try {
			docs = OKMDocument.getInstance().getChildren(null, docPath.substring(0, docPath.lastIndexOf("/")));
		} catch (Exception e) {
			e.printStackTrace();
		}

		int maxSequence = 0;
		for (Document doc : docs) {
			String currentDocumentPath = doc.getPath(), currentDocumentName;
			currentDocumentName = currentDocumentPath.substring(currentDocumentPath.lastIndexOf("/"));
			if (currentDocumentName.equals(this.docName)) {
				continue;
			}
			int currentSequence;
			currentSequence = Integer.parseInt(extractSequenceNumber(doc.getPath()));
			if (currentSequence > maxSequence) {
				maxSequence = currentSequence;
			}
		}

		return maxSequence;
	}

	public String extractSequenceNumber(String docName) {
		int lastIndexOfDot = docName.lastIndexOf(".");
		if (lastIndexOfDot <= 3) {
			throw new RuntimeException(
					"LastIndexOfDot in extractSequenceNumber is less than 3. Document name: " + docName);
		}
		return docName.substring(lastIndexOfDot - 3, lastIndexOfDot);
	}
}