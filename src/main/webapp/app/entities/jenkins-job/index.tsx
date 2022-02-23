import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import JenkinsJob from './jenkins-job';
import JenkinsJobDetail from './jenkins-job-detail';
import JenkinsJobUpdate from './jenkins-job-update';
import JenkinsJobDeleteDialog from './jenkins-job-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={JenkinsJobUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={JenkinsJobUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={JenkinsJobDetail} />
      <ErrorBoundaryRoute path={match.url} component={JenkinsJob} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={JenkinsJobDeleteDialog} />
  </>
);

export default Routes;
