import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import IntentResponse from './intent-response';
import IntentResponseDetail from './intent-response-detail';
import IntentResponseUpdate from './intent-response-update';
import IntentResponseDeleteDialog from './intent-response-delete-dialog';

const IntentResponseRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<IntentResponse />} />
    <Route path="new" element={<IntentResponseUpdate />} />
    <Route path=":id">
      <Route index element={<IntentResponseDetail />} />
      <Route path="edit" element={<IntentResponseUpdate />} />
      <Route path="delete" element={<IntentResponseDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default IntentResponseRoutes;
