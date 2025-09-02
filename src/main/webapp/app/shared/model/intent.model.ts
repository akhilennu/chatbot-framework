import { IIntentResponse } from 'app/shared/model/intent-response.model';
import { IBot } from 'app/shared/model/bot.model';
import { IIntentEntity } from 'app/shared/model/intent-entity.model';

export interface IIntent {
  id?: number;
  name?: string;
  description?: string | null;
  response?: IIntentResponse | null;
  bot?: IBot | null;
  entities?: IIntentEntity[] | null;
}

export const defaultValue: Readonly<IIntent> = {};
