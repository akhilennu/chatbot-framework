import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getIntentResponses } from 'app/entities/intent-response/intent-response.reducer';
import { getEntities as getBots } from 'app/entities/bot/bot.reducer';
import { getEntities as getIntentEntities } from 'app/entities/intent-entity/intent-entity.reducer';
import { createEntity, getEntity, reset, updateEntity } from './intent.reducer';

export const IntentUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const intentResponses = useAppSelector(state => state.intentResponse.entities);
  const bots = useAppSelector(state => state.bot.entities);
  const intentEntities = useAppSelector(state => state.intentEntity.entities);
  const intentEntity = useAppSelector(state => state.intent.entity);
  const loading = useAppSelector(state => state.intent.loading);
  const updating = useAppSelector(state => state.intent.updating);
  const updateSuccess = useAppSelector(state => state.intent.updateSuccess);

  const handleClose = () => {
    navigate(`/intent${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getIntentResponses({}));
    dispatch(getBots({}));
    dispatch(getIntentEntities({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }

    const entity = {
      ...intentEntity,
      ...values,
      response: intentResponses.find(it => it.id.toString() === values.response?.toString()),
      bot: bots.find(it => it.id.toString() === values.bot?.toString()),
      entities: mapIdList(values.entities),
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
          ...intentEntity,
          response: intentEntity?.response?.id,
          bot: intentEntity?.bot?.id,
          entities: intentEntity?.entities?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="chatbotFrameworkApp.intent.home.createOrEditLabel" data-cy="IntentCreateUpdateHeading">
            Create or edit a Intent
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="intent-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Name"
                id="intent-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField label="Description" id="intent-description" name="description" data-cy="description" type="text" />
              <ValidatedField id="intent-response" name="response" data-cy="response" label="Response" type="select">
                <option value="" key="0" />
                {intentResponses
                  ? intentResponses.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="intent-bot" name="bot" data-cy="bot" label="Bot" type="select">
                <option value="" key="0" />
                {bots
                  ? bots.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField label="Entities" id="intent-entities" data-cy="entities" type="select" multiple name="entities">
                <option value="" key="0" />
                {intentEntities
                  ? intentEntities.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/intent" replace color="info">
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

export default IntentUpdate;
