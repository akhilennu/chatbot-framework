import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import IntentEntity from './intent-entity';
import IntentEntityDetail from './intent-entity-detail';
import IntentEntityUpdate from './intent-entity-update';
import IntentEntityDeleteDialog from './intent-entity-delete-dialog';

const IntentEntityRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<IntentEntity />} />
    <Route path="new" element={<IntentEntityUpdate />} />
    <Route path=":id">
      <Route index element={<IntentEntityDetail />} />
      <Route path="edit" element={<IntentEntityUpdate />} />
      <Route path="delete" element={<IntentEntityDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default IntentEntityRoutes;
