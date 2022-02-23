import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './jira-ticket.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const JiraTicketDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const jiraTicketEntity = useAppSelector(state => state.jiraTicket.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="jiraTicketDetailsHeading">JiraTicket</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{jiraTicketEntity.id}</dd>
          <dt>
            <span id="jiraTicketName">Jira Ticket Name</span>
          </dt>
          <dd>{jiraTicketEntity.jiraTicketName}</dd>
        </dl>
        <Button tag={Link} to="/jira-ticket" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/jira-ticket/${jiraTicketEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default JiraTicketDetail;
