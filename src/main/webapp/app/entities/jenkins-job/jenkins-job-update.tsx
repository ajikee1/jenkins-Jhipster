import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IJiraTicket } from 'app/shared/model/jira-ticket.model';
import { getEntities as getJiraTickets } from 'app/entities/jira-ticket/jira-ticket.reducer';
import { getEntity, updateEntity, createEntity, reset } from './jenkins-job.reducer';
import { IJenkinsJob } from 'app/shared/model/jenkins-job.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const JenkinsJobUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const jiraTickets = useAppSelector(state => state.jiraTicket.entities);
  const jenkinsJobEntity = useAppSelector(state => state.jenkinsJob.entity);
  const loading = useAppSelector(state => state.jenkinsJob.loading);
  const updating = useAppSelector(state => state.jenkinsJob.updating);
  const updateSuccess = useAppSelector(state => state.jenkinsJob.updateSuccess);
  const handleClose = () => {
    props.history.push('/jenkins-job');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getJiraTickets({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...jenkinsJobEntity,
      ...values,
      jiraTicket: jiraTickets.find(it => it.id.toString() === values.jiraTicket.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...jenkinsJobEntity,
          jiraTicket: jenkinsJobEntity?.jiraTicket?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="jenkinsJhipsterApp.jenkinsJob.home.createOrEditLabel" data-cy="JenkinsJobCreateUpdateHeading">
            Create or edit a JenkinsJob
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="jenkins-job-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Job Name"
                id="jenkins-job-jobName"
                name="jobName"
                data-cy="jobName"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField
                label="Build Number"
                id="jenkins-job-buildNumber"
                name="buildNumber"
                data-cy="buildNumber"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <ValidatedField id="jenkins-job-jiraTicket" name="jiraTicket" data-cy="jiraTicket" label="Jira Ticket" type="select">
                <option value="" key="0" />
                {jiraTickets
                  ? jiraTickets.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.jiraTicketName}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/jenkins-job" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default JenkinsJobUpdate;
