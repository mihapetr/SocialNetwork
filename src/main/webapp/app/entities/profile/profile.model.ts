import { IUser } from 'app/entities/user/user.model';
import { IChat } from 'app/entities/chat/chat.model';

export interface IProfile {
  id: number;
  status?: string | null;
  picture?: string | null;
  pictureContentType?: string | null;
  user?: Pick<IUser, 'id'> | null;
  others?: IProfile[] | null;
  chats?: IChat[] | null;
  profiles?: IProfile[] | null;
}

export type NewProfile = Omit<IProfile, 'id'> & { id: null };
