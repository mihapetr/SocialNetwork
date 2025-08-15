import { IUser } from 'app/entities/user/user.model';
import { IChat } from 'app/entities/chat/chat.model';
import { IPost } from '../post/post.model';

export interface IProfile {
  id: number;
  status?: string | null;
  picture?: string | null;
  pictureContentType?: string | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
  others?: IProfile[] | null;
  chats?: IChat[] | null;
  profiles?: IProfile[] | null;
  posts?: IPost[] | null;
}

export type NewProfile = Omit<IProfile, 'id'> & { id: null };
