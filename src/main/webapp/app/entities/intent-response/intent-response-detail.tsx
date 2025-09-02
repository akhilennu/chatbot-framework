import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './intent-response.reducer';

export const IntentResponseDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const intentResponseEntity = useAppSelector(state => state.intentResponse.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="intentResponseDetailsHeading">Intent Response</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{intentResponseEntity.id}</dd>
          <dt>
            <span id="message">Message</span>
          </dt>
          <dd>{intentResponseEntity.message}</dd>
        </dl>
        <Button tag={Link} to="/intent-response" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/intent-response/${intentResponseEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default IntentResponseDetail;
