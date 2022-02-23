import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './jenkins-job.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const JenkinsJobDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const jenkinsJobEntity = useAppSelector(state => state.jenkinsJob.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="jenkinsJobDetailsHeading">JenkinsJob</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{jenkinsJobEntity.id}</dd>
          <dt>
            <span id="jobName">Job Name</span>
          </dt>
          <dd>{jenkinsJobEntity.jobName}</dd>
          <dt>
            <span id="buildNumber">Build Number</span>
          </dt>
          <dd>{jenkinsJobEntity.buildNumber}</dd>
          <dt>Jira Ticket</dt>
          <dd>{jenkinsJobEntity.jiraTicket ? jenkinsJobEntity.jiraTicket.jiraTicketName : ''}</dd>
        </dl>
        <Button tag={Link} to="/jenkins-job" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/jenkins-job/${jenkinsJobEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default JenkinsJobDetail;
