import { IUser } from 'app/entities/user/user.model';
import { IProfile } from 'app/entities/profile/profile.model';

export interface IChat {
  id: number;
  initiatorName?: string | null;
  accepted?: boolean | null;
  user?: Pick<IUser, 'id'> | null;
  profiles?: IProfile[] | null;
}

export type NewChat = Omit<IChat, 'id'> & { id: null };
