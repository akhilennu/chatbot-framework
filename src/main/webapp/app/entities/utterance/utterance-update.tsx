import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getIntents } from 'app/entities/intent/intent.reducer';
import { createEntity, getEntity, reset, updateEntity } from './utterance.reducer';

export const UtteranceUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const intents = useAppSelector(state => state.intent.entities);
  const utteranceEntity = useAppSelector(state => state.utterance.entity);
  const loading = useAppSelector(state => state.utterance.loading);
  const updating = useAppSelector(state => state.utterance.updating);
  const updateSuccess = useAppSelector(state => state.utterance.updateSuccess);

  const handleClose = () => {
    navigate('/utterance');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getIntents({}));
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
      ...utteranceEntity,
      ...values,
      intent: intents.find(it => it.id.toString() === values.intent?.toString()),
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
          ...utteranceEntity,
          intent: utteranceEntity?.intent?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="chatbotFrameworkApp.utterance.home.createOrEditLabel" data-cy="UtteranceCreateUpdateHeading">
            Create or edit a Utterance
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="utterance-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Text"
                id="utterance-text"
                name="text"
                data-cy="text"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField label="Language" id="utterance-language" name="language" data-cy="language" type="text" />
              <ValidatedField id="utterance-intent" name="intent" data-cy="intent" label="Intent" type="select">
                <option value="" key="0" />
                {intents
                  ? intents.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/utterance" replace color="info">
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

export default UtteranceUpdate;
