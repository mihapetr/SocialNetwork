import { IMessage } from 'app/entities/message/message.model';
import { IUser } from 'app/entities/user/user.model';
import { IPost } from 'app/entities/post/post.model';
import { IProfile } from 'app/entities/profile/profile.model';

export interface IComment {
  id: number;
  parent?: IMessage | null;
  user?: Pick<IUser, 'id'> | null;
  post?: IPost | null;
  profile?: IProfile | null;
}

export type NewComment = Omit<IComment, 'id'> & { id: null };
