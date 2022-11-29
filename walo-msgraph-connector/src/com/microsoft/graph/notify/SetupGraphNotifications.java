package com.microsoft.graph.notify;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.process.eventstart.AbstractProcessStartEventBean;
import ch.ivyteam.ivy.service.ServiceException;
import ch.ivyteam.ivy.workflow.IWorkflowManager;

public class SetupGraphNotifications extends AbstractProcessStartEventBean {

  private PlannerNotifier listener;

  public SetupGraphNotifications() {
    super("GraphNotifier", "Setup Graph Notifications");
  }

  @Override
  public void poll() {
    getEventBeanRuntime().setPollTimeInterval(-1); // no-polling!
  }

  @Override
  public void start(IProgressMonitor monitor) throws ServiceException {
    super.start(monitor);
    IWorkflowManager wfManager = IWorkflowManager.instance();
    this.listener = new PlannerNotifier();
    wfManager.addWorkflowListener(listener);
    Ivy.log().info("graph-notification installed");
  }

  @Override
  public void stop(IProgressMonitor monitor) throws ServiceException {
    if (this.listener != null) {
      IWorkflowManager.instance().removeWorkflowListener(listener);
      Ivy.log().info("graph-notification stopped");
    }
    super.stop(monitor);
  }

}
