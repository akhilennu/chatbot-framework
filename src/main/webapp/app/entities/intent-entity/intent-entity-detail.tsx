import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './intent-entity.reducer';

export const IntentEntityDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const intentEntityEntity = useAppSelector(state => state.intentEntity.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="intentEntityDetailsHeading">Intent Entity</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{intentEntityEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{intentEntityEntity.name}</dd>
          <dt>
            <span id="optional">Optional</span>
          </dt>
          <dd>{intentEntityEntity.optional ? 'true' : 'false'}</dd>
          <dt>Intents</dt>
          <dd>
            {intentEntityEntity.intents
              ? intentEntityEntity.intents.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {intentEntityEntity.intents && i === intentEntityEntity.intents.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/intent-entity" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/intent-entity/${intentEntityEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default IntentEntityDetail;
