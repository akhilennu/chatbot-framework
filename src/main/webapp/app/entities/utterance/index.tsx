import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Utterance from './utterance';
import UtteranceDetail from './utterance-detail';
import UtteranceUpdate from './utterance-update';
import UtteranceDeleteDialog from './utterance-delete-dialog';

const UtteranceRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Utterance />} />
    <Route path="new" element={<UtteranceUpdate />} />
    <Route path=":id">
      <Route index element={<UtteranceDetail />} />
      <Route path="edit" element={<UtteranceUpdate />} />
      <Route path="delete" element={<UtteranceDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default UtteranceRoutes;
