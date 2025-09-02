import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './utterance.reducer';

export const UtteranceDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const utteranceEntity = useAppSelector(state => state.utterance.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="utteranceDetailsHeading">Utterance</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{utteranceEntity.id}</dd>
          <dt>
            <span id="text">Text</span>
          </dt>
          <dd>{utteranceEntity.text}</dd>
          <dt>
            <span id="language">Language</span>
          </dt>
          <dd>{utteranceEntity.language}</dd>
          <dt>Intent</dt>
          <dd>{utteranceEntity.intent ? utteranceEntity.intent.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/utterance" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/utterance/${utteranceEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default UtteranceDetail;
