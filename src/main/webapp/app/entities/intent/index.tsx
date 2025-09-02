import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Intent from './intent';
import IntentDetail from './intent-detail';
import IntentUpdate from './intent-update';
import IntentDeleteDialog from './intent-delete-dialog';

const IntentRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Intent />} />
    <Route path="new" element={<IntentUpdate />} />
    <Route path=":id">
      <Route index element={<IntentDetail />} />
      <Route path="edit" element={<IntentUpdate />} />
      <Route path="delete" element={<IntentDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default IntentRoutes;
