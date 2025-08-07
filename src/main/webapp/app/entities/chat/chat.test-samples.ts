import { IChat, NewChat } from './chat.model';

export const sampleWithRequiredData: IChat = {
  id: 14793,
};

export const sampleWithPartialData: IChat = {
  id: 26853,
};

export const sampleWithFullData: IChat = {
  id: 27153,
  initiatorName: 'substitution',
  accepted: false,
};

export const sampleWithNewData: NewChat = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
