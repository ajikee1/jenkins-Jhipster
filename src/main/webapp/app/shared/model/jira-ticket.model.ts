import { IJenkinsJob } from 'app/shared/model/jenkins-job.model';

export interface IJiraTicket {
  id?: number;
  jiraTicketName?: string;
  jiraTicketToJobRels?: IJenkinsJob[] | null;
}

export const defaultValue: Readonly<IJiraTicket> = {};
