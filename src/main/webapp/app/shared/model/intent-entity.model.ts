import { IIntent } from 'app/shared/model/intent.model';

export interface IIntentEntity {
  id?: number;
  name?: string;
  optional?: boolean | null;
  intents?: IIntent[] | null;
}

export const defaultValue: Readonly<IIntentEntity> = {
  optional: false,
};
