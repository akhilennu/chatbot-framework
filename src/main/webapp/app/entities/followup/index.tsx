import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Followup from './followup';
import FollowupDetail from './followup-detail';
import FollowupUpdate from './followup-update';
import FollowupDeleteDialog from './followup-delete-dialog';

const FollowupRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Followup />} />
    <Route path="new" element={<FollowupUpdate />} />
    <Route path=":id">
      <Route index element={<FollowupDetail />} />
      <Route path="edit" element={<FollowupUpdate />} />
      <Route path="delete" element={<FollowupDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default FollowupRoutes;
