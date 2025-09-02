import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './intent.reducer';

export const IntentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const intentEntity = useAppSelector(state => state.intent.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="intentDetailsHeading">Intent</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{intentEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{intentEntity.name}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{intentEntity.description}</dd>
          <dt>Response</dt>
          <dd>{intentEntity.response ? intentEntity.response.id : ''}</dd>
          <dt>Bot</dt>
          <dd>{intentEntity.bot ? intentEntity.bot.name : ''}</dd>
          <dt>Entities</dt>
          <dd>
            {intentEntity.entities
              ? intentEntity.entities.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.name}</a>
                    {intentEntity.entities && i === intentEntity.entities.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/intent" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/intent/${intentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default IntentDetail;
