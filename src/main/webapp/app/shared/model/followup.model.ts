import { IIntent } from 'app/shared/model/intent.model';

export interface IFollowup {
  id?: number;
  question?: string;
  targetEntity?: string;
  order?: number | null;
  intent?: IIntent | null;
}

export const defaultValue: Readonly<IFollowup> = {};
