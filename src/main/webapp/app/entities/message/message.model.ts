import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IChat } from 'app/entities/chat/chat.model';

export interface IMessage {
  id: number;
  senderName?: string | null;
  content?: string | null;
  time?: dayjs.Dayjs | null;
  user?: Pick<IUser, 'id'> | null;
  chat?: IChat | null;
}

export type NewMessage = Omit<IMessage, 'id'> & { id: null };
