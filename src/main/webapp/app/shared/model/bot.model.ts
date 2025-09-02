export interface IBot {
  id?: number;
  name?: string;
  description?: string | null;
  active?: boolean | null;
}

export const defaultValue: Readonly<IBot> = {
  active: false,
};
