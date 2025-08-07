import dayjs from 'dayjs/esm';

import { IPost, NewPost } from './post.model';

export const sampleWithRequiredData: IPost = {
  id: 8730,
};

export const sampleWithPartialData: IPost = {
  id: 28493,
  time: dayjs('2025-08-06T22:35'),
};

export const sampleWithFullData: IPost = {
  id: 9917,
  image: '../fake-data/blob/hipster.png',
  imageContentType: 'unknown',
  description: 'clearly darn icy',
  time: dayjs('2025-08-07T00:36'),
};

export const sampleWithNewData: NewPost = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
