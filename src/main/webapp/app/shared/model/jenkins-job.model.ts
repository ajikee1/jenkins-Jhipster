import { IJiraTicket } from 'app/shared/model/jira-ticket.model';

export interface IJenkinsJob {
  id?: number;
  jobName?: string;
  buildNumber?: number;
  jiraTicket?: IJiraTicket | null;
}

export const defaultValue: Readonly<IJenkinsJob> = {};
