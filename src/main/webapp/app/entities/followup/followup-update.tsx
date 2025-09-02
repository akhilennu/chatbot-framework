import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getIntents } from 'app/entities/intent/intent.reducer';
import { createEntity, getEntity, reset, updateEntity } from './followup.reducer';

export const FollowupUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const intents = useAppSelector(state => state.intent.entities);
  const followupEntity = useAppSelector(state => state.followup.entity);
  const loading = useAppSelector(state => state.followup.loading);
  const updating = useAppSelector(state => state.followup.updating);
  const updateSuccess = useAppSelector(state => state.followup.updateSuccess);

  const handleClose = () => {
    navigate('/followup');
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
    if (values.order !== undefined && typeof values.order !== 'number') {
      values.order = Number(values.order);
    }

    const entity = {
      ...followupEntity,
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
          ...followupEntity,
          intent: followupEntity?.intent?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="chatbotFrameworkApp.followup.home.createOrEditLabel" data-cy="FollowupCreateUpdateHeading">
            Create or edit a Followup
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="followup-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Question"
                id="followup-question"
                name="question"
                data-cy="question"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField
                label="Target Entity"
                id="followup-targetEntity"
                name="targetEntity"
                data-cy="targetEntity"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField label="Order" id="followup-order" name="order" data-cy="order" type="text" />
              <ValidatedField id="followup-intent" name="intent" data-cy="intent" label="Intent" type="select">
                <option value="" key="0" />
                {intents
                  ? intents.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/followup" replace color="info">
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

export default FollowupUpdate;
