import React from 'react';
import { Route } from 'react-router'; // eslint-disable-line

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Bot from './bot';
import Intent from './intent';
import IntentEntity from './intent-entity';
import Utterance from './utterance';
import Followup from './followup';
import IntentResponse from './intent-response';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="bot/*" element={<Bot />} />
        <Route path="intent/*" element={<Intent />} />
        <Route path="intent-entity/*" element={<IntentEntity />} />
        <Route path="utterance/*" element={<Utterance />} />
        <Route path="followup/*" element={<Followup />} />
        <Route path="intent-response/*" element={<IntentResponse />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
