import { IProfile, NewProfile } from './profile.model';

export const sampleWithRequiredData: IProfile = {
  id: 18889,
};

export const sampleWithPartialData: IProfile = {
  id: 23204,
  status: 'skateboard below quietly',
  picture: '../fake-data/blob/hipster.png',
  pictureContentType: 'unknown',
};

export const sampleWithFullData: IProfile = {
  id: 29727,
  status: 'shoulder',
  picture: '../fake-data/blob/hipster.png',
  pictureContentType: 'unknown',
};

export const sampleWithNewData: NewProfile = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
