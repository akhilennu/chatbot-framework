import bot from 'app/entities/bot/bot.reducer';
import intent from 'app/entities/intent/intent.reducer';
import intentEntity from 'app/entities/intent-entity/intent-entity.reducer';
import utterance from 'app/entities/utterance/utterance.reducer';
import followup from 'app/entities/followup/followup.reducer';
import intentResponse from 'app/entities/intent-response/intent-response.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  bot,
  intent,
  intentEntity,
  utterance,
  followup,
  intentResponse,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
