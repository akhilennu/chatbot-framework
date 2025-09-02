import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './followup.reducer';

export const FollowupDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const followupEntity = useAppSelector(state => state.followup.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="followupDetailsHeading">Followup</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{followupEntity.id}</dd>
          <dt>
            <span id="question">Question</span>
          </dt>
          <dd>{followupEntity.question}</dd>
          <dt>
            <span id="targetEntity">Target Entity</span>
          </dt>
          <dd>{followupEntity.targetEntity}</dd>
          <dt>
            <span id="order">Order</span>
          </dt>
          <dd>{followupEntity.order}</dd>
          <dt>Intent</dt>
          <dd>{followupEntity.intent ? followupEntity.intent.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/followup" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/followup/${followupEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default FollowupDetail;
