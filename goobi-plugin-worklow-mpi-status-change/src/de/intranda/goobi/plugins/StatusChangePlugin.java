package de.intranda.goobi.plugins;

import org.apache.commons.lang.StringUtils;
import org.goobi.beans.Process;
import org.goobi.beans.Step;
import org.goobi.production.enums.PluginType;
import org.goobi.production.plugin.interfaces.IWorkflowPlugin;

import de.sub.goobi.config.ConfigPlugins;
import de.sub.goobi.helper.Helper;
import de.sub.goobi.helper.HelperSchritte;
import de.sub.goobi.helper.enums.StepStatus;
import de.sub.goobi.persistence.managers.ProcessManager;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
@Log4j
@Data
public class StatusChangePlugin implements IWorkflowPlugin {

    private String title = "StatusChangePlugin";
    private String gui = "/uii/mpi_workflow_StatusChange.xhtml";
    private PluginType type = PluginType.Workflow;
    private String usage;

    private String displayImputField;
    private String barcode;

    private String taskNameReceipt;
    private String taskNameReturn;

    public StatusChangePlugin() {
        taskNameReceipt = ConfigPlugins.getPluginConfig(this).getString("receiptTaskName");
        taskNameReturn = ConfigPlugins.getPluginConfig(this).getString("returnTaskName");
    }

    public void updateStatus() {

        if (StringUtils.isBlank(barcode)) {
            Helper.setFehlerMeldung("TODO: field is empty");
            return;
        }

        Process process = ProcessManager.getProcessByTitle(barcode);
        if (process == null) {
            Helper.setFehlerMeldung("TODO: cannot find barcode");
            return;
        }

        Step receiptStep = null;
        Step returnStep = null;

        for (Step step : process.getSchritte()) {
            if (step.getTitel().equalsIgnoreCase(taskNameReceipt)) {
                receiptStep = step;
            } else if (step.getTitel().equalsIgnoreCase(taskNameReturn)) {
                returnStep = step;
            }
        }

        if (usage.equals("mpi_receipt")) {
            if (receiptStep == null) {
                Helper.setFehlerMeldung("TODO: " + taskNameReceipt + " not found");
                return;
            } else if (receiptStep.getBearbeitungsstatusEnum().equals(StepStatus.DONE)) {
                Helper.setMeldung("TODO: " + taskNameReceipt + " already processed");
                return;
            }
            new HelperSchritte().CloseStepObjectAutomatic(receiptStep);
            Helper.setMeldung("TODO: erfolgreich registriert");

        } else if (usage.equals("mpi_return")) {
            if (returnStep == null) {
                Helper.setFehlerMeldung("TODO: " + taskNameReturn + " not found");
                return;
            } else if (returnStep.getBearbeitungsstatusEnum().equals(StepStatus.DONE)) {
                Helper.setMeldung("TODO: " + taskNameReceipt + " already processed");
                return;
            }
            new HelperSchritte().CloseStepObjectAutomatic(returnStep);
            Helper.setMeldung("TODO: erfolgreich registriert");
        }
    }

}
