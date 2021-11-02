package com.uzassystem.workflow.invoiceRegistration;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.openkm.api.OKMDocument;
import com.openkm.api.OKMPropertyGroup;
import com.openkm.bean.form.Input;
import com.openkm.bean.form.Select;
import com.openkm.bean.form.TextArea;
import com.openkm.util.ISO8601;

public class SetInvoiceMetadata implements ActionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		// here we assign md groups to doc and fill them. After that we lock the
		// doc.
		String uuid;
		uuid = (String) executionContext.getContextInstance().getVariable("invoiceDocumentUUID");
		OKMPropertyGroup.getInstance().addGroup(null, uuid, "okg:invoice");

		Map<String, String> invoiceMap = new HashMap<>();

		String proTrigram = ((Select) executionContext.getContextInstance().getVariable("okp:invoice.pro_tiagram"))
				.getValue();
		invoiceMap.put("okp:invoice.pro_tiagram", proTrigram);

		String associationType = ((Select) executionContext.getContextInstance().getVariable("okp:invoice.association"))
				.getValue();
		invoiceMap.put("okp:invoice.association.type", associationType);

		String associationName = ((Input) executionContext.getContextInstance()
				.getVariable("okp:invoice.document.name")).getValue();
		invoiceMap.put("okp:invoice.association.name", associationName);

		String invoiceClassification = ((TextArea) executionContext.getContextInstance()
				.getVariable("okp:invoice.classification")).getValue();
		invoiceMap.put("okp:invoice.classification", invoiceClassification);

		String oldName = (String) executionContext.getContextInstance().getVariable("oldName");
		invoiceMap.put("okp:invoice.oldName", oldName);

		String paymentDueDate = ((Input) executionContext.getContextInstance().getVariable("okp:invoice.payment.date"))
				.getValue();
		invoiceMap.put("okp:invoice.payment.date", paymentDueDate);

		String currentDate = ISO8601.formatBasic(Calendar.getInstance());
		invoiceMap.put("okp:invoice.date", currentDate);

		String invoiceNumber = (String) executionContext.getContextInstance().getVariable("invoiceSequenceNumber");
		invoiceMap.put("okp:invoice.number", invoiceNumber);

		String invoiceStatus = ((Select) executionContext.getContextInstance().getVariable("okp:invoice.status"))
				.getValue();
		invoiceMap.put("okp:invoice.status", invoiceStatus);

		String invoiceStatusDescription = ((TextArea) executionContext.getContextInstance()
				.getVariable("okp:invoice.status.comment")).getValue();
		invoiceMap.put("okp:invoice.status.comment", invoiceStatusDescription);

		String invoiceCurrency = ((Select) executionContext.getContextInstance().getVariable("okp:invoice.currency"))
				.getValue();
		invoiceMap.put("okp:invoice.currency", invoiceCurrency);

		String invoiceBasicAmount = ((Input) executionContext.getContextInstance().getVariable("okp:invoice.amount"))
				.getValue();
		invoiceMap.put("okp:invoice.amount", invoiceBasicAmount);

		String invoiceVATAmount = ((Input) executionContext.getContextInstance().getVariable("okp:invoice.vat"))
				.getValue();
		invoiceMap.put("okp:invoice.vat", invoiceVATAmount);

		String invoiceTotalAmount = ((Input) executionContext.getContextInstance()
				.getVariable("okp:invoice.amount.total")).getValue();
		invoiceMap.put("okp:invoice.amount.total", invoiceTotalAmount);

		OKMPropertyGroup.getInstance().setPropertiesSimple(null, uuid, "okg:invoice", invoiceMap);

		OKMPropertyGroup.getInstance().addGroup(null, uuid, "okg:invoice.paymentInfo");

		OKMPropertyGroup.getInstance().addGroup(null, uuid, "okg:invoice.kostil");

		Map<String, String> invoiceKostilMap = new HashMap<>();
		// invoiceKostilMap.put("okp:invoice.kostil.oldStatus", "Released");
		invoiceKostilMap.put("okp:invoice.kostil.toCopyMD", "true");
		OKMPropertyGroup.getInstance().setPropertiesSimple(null, uuid, "okg:invoice.kostil", invoiceKostilMap);

		// lock the doc
		try {
			OKMDocument.getInstance().lock(null, uuid);
		} catch (Exception e) {
			e.printStackTrace();
		}

		executionContext.getContextInstance().setVariable("invoiceMap", invoiceMap);
		executionContext.getContextInstance().setVariable("invoiceKostilMap", invoiceKostilMap);

		executionContext.getToken().signal();

	}

}