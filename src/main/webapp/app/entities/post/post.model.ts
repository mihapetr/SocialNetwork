import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IProfile } from 'app/entities/profile/profile.model';
import { IComment } from '../comment/comment.model';

export interface IPost {
  id: number;
  image?: string | null;
  imageContentType?: string | null;
  description?: string | null;
  time?: dayjs.Dayjs | null;
  user?: IUser | null;
  profile?: IProfile | null;
  comments?: IComment[] | null;
}

export type NewPost = Omit<IPost, 'id'> & { id: null };
