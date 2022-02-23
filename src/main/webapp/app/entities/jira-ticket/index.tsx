import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import JiraTicket from './jira-ticket';
import JiraTicketDetail from './jira-ticket-detail';
import JiraTicketUpdate from './jira-ticket-update';
import JiraTicketDeleteDialog from './jira-ticket-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={JiraTicketUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={JiraTicketUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={JiraTicketDetail} />
      <ErrorBoundaryRoute path={match.url} component={JiraTicket} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={JiraTicketDeleteDialog} />
  </>
);

export default Routes;
