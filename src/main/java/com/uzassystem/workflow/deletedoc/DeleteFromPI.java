///**
// * 
// */
//package com.openkm.workflow.deletedoc;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.jbpm.graph.def.ActionHandler;
//import org.jbpm.graph.exe.ExecutionContext;
//
//import com.openkm.api.OKMWorkflow;
//import com.openkm.bean.form.Input;
//import com.openkm.bean.workflow.ProcessInstance;
//import com.openkm.bean.workflow.TaskInstance;
//
///**
// * @author openkm
// *
// */
//public class DeleteFromPI implements ActionHandler {
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.jbpm.graph.def.ActionHandler#execute(org.jbpm.graph.exe.
//	 * ExecutionContext)
//	 */
//	@Override
//	public void execute(ExecutionContext executionContext) throws Exception {
//		// TODO Auto-generated method stub
//
//		String docUUID = ((Input) executionContext.getContextInstance().getVariable("Doc_UUID")).getValue();
//
//		executionContext.getContextInstance().setVariable("weGotToCheckpoint", "1");
//		// process instance deletion
//		Map<String, String> transitionNamesForNodes = new HashMap<>(16, 1.0f);
//
//		transitionNamesForNodes.put("verifyDocument", "to End");
//		transitionNamesForNodes.put("InitialDownload", "to uploadForReview");
//		transitionNamesForNodes.put("uploadForReview", "to check checkout");
//		transitionNamesForNodes.put("reviewerDownload", "to reviewer decision");
//		transitionNamesForNodes.put("reviewerDecision", "to approver download");
//		transitionNamesForNodes.put("approverDownload", "to approver decision");
//		transitionNamesForNodes.put("downloadWithNotes", "to upload for review");
//		transitionNamesForNodes.put("ApproverDecision", "to signAndLock");
//		transitionNamesForNodes.put("signAndLock", "to readyForIssuing");
//		transitionNamesForNodes.put("TranslationPart", "goToTranslate");
//		transitionNamesForNodes.put("TranslatedVersion", "SecondDocument");
//
//		try {
//			//List<ProcessInstance> processInstancesList = CheckPI
//			//		.findProcessInstancesByVariableValue("docUUID", docUUID);
//			//executionContext.getContextInstance().setVariable("processInstancesFound", processInstancesList.toString());
//
//			executionContext.getContextInstance().setVariable("weGotToCheckpoint", "2");
//
//			for (ProcessInstance currentProcessInstance : processInstancesList) {
//				// get id and delete processInstance through
//				// OKMWorkflow.deleteProcessInstance().
//				long currentProcessInstanceId = currentProcessInstance.getId();
//				List<TaskInstance> taskInstancesList = OKMWorkflow.getInstance().findTaskInstances(null,
//						currentProcessInstanceId);
//
//				executionContext.getContextInstance().setVariable("weGotToCheckpoint", "3");
//
//				if (taskInstancesList.size() == 0) {
//					break;
//				}
//
//				for (TaskInstance currentTaskInstance : taskInstancesList) {
//
//					if (currentTaskInstance.isOpen()) {
//
//						String currentTaskInstanceName = currentTaskInstance.getName();
//						executionContext.getContextInstance().setVariable("currentTIName", currentTaskInstanceName);
//
//						executionContext.getContextInstance().setVariable("weGotToCheckpoint", "4");
//
//						OKMWorkflow.getInstance().endTaskInstance(null, currentTaskInstance.getId(),
//								transitionNamesForNodes.get(currentTaskInstanceName));
//
//					}
//				}
//
//				executionContext.getContextInstance().setVariable("weGotToCheckpoint", "5");
//
//				OKMWorkflow.getInstance().endProcessInstance(null, currentProcessInstanceId);
//				OKMWorkflow.getInstance().deleteProcessInstance(null, currentProcessInstanceId);
//
//			}
//		} catch (Exception e) {
//			throw new RuntimeException("Error in ProcessInstance removal part. Please fix.", e);
//		}
//
//		// vicarious == stranger
//		try {
//			List<ProcessInstance> vicariousProcessInstancesList = CheckPI.findProcessInstancesByVariableValue("uuid",
//					docUUID);
//			executionContext.getContextInstance().setVariable("vicariousProcessInstances",
//					vicariousProcessInstancesList.toString());
//
//			if (vicariousProcessInstancesList.size() != 0) {
//
//				final String staticUUID = "0fa009ef-dcb2-4f79-be80-f7e1dadb9c94";
//				for (ProcessInstance currentVicariousProcessInstance : vicariousProcessInstancesList) {
//
//					Map<String, Object> cVPIVMap = currentVicariousProcessInstance.getVariables();
//					cVPIVMap.put("uuid", staticUUID);
//					currentVicariousProcessInstance.setVariables(cVPIVMap);
//
//				}
//			}
//		} catch (Exception e) {
//			throw new RuntimeException("Error in vicariousProcesses", e);
//		}
//
//		executionContext.getToken().signal();
//	}
//
//}
