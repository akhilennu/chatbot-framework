import React from 'react';
// eslint-disable-line

import MenuItem from 'app/shared/layout/menus/menu-item'; // eslint-disable-line

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/bot">
        Bot
      </MenuItem>
      <MenuItem icon="asterisk" to="/intent">
        Intent
      </MenuItem>
      <MenuItem icon="asterisk" to="/intent-entity">
        Intent Entity
      </MenuItem>
      <MenuItem icon="asterisk" to="/utterance">
        Utterance
      </MenuItem>
      <MenuItem icon="asterisk" to="/followup">
        Followup
      </MenuItem>
      <MenuItem icon="asterisk" to="/intent-response">
        Intent Response
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
