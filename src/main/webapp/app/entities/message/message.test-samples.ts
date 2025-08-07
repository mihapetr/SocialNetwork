import dayjs from 'dayjs/esm';

import { IMessage, NewMessage } from './message.model';

export const sampleWithRequiredData: IMessage = {
  id: 10168,
};

export const sampleWithPartialData: IMessage = {
  id: 21809,
  senderName: 'per after splurge',
  time: dayjs('2025-08-07T06:14'),
};

export const sampleWithFullData: IMessage = {
  id: 2775,
  senderName: 'exalt besmirch scent',
  content: 'chatter label hexagon',
  time: dayjs('2025-08-07T16:12'),
};

export const sampleWithNewData: NewMessage = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
