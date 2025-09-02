import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Bot from './bot';
import BotDetail from './bot-detail';
import BotUpdate from './bot-update';
import BotDeleteDialog from './bot-delete-dialog';

const BotRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Bot />} />
    <Route path="new" element={<BotUpdate />} />
    <Route path=":id">
      <Route index element={<BotDetail />} />
      <Route path="edit" element={<BotUpdate />} />
      <Route path="delete" element={<BotDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default BotRoutes;
