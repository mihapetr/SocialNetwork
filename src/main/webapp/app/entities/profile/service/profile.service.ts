import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProfile, NewProfile } from '../profile.model';
import { IChat } from '../../chat/chat.model';

export type PartialUpdateProfile = Partial<IProfile> & Pick<IProfile, 'id'>;

export type EntityResponseType = HttpResponse<IProfile>;
export type ChatResponseType = HttpResponse<IChat>;
export type EntityArrayResponseType = HttpResponse<IProfile[]>;

@Injectable({ providedIn: 'root' })
export class ProfileService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/profiles');

  create(profile: NewProfile): Observable<EntityResponseType> {
    return this.http.post<IProfile>(this.resourceUrl, profile, { observe: 'response' });
  }

  update(profile: IProfile): Observable<EntityResponseType> {
    return this.http.put<IProfile>(`${this.resourceUrl}/${this.getProfileIdentifier(profile)}`, profile, { observe: 'response' });
  }

  partialUpdate(profile: PartialUpdateProfile): Observable<EntityResponseType> {
    return this.http.patch<IProfile>(`${this.resourceUrl}/${this.getProfileIdentifier(profile)}`, profile, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProfile>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  findCurrent(): Observable<EntityResponseType> {
    return this.http.get<IProfile>(`${this.resourceUrl}/current-user`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProfile[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  requestChat(id: number): Observable<ChatResponseType> {
    const url = this.applicationConfigService.getEndpointFor('api/chats');
    return this.http.post<IChat>(`${url}/request-chat-with-profile/${id}`, null, { observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getProfileIdentifier(profile: Pick<IProfile, 'id'>): number {
    return profile.id;
  }

  compareProfile(o1: Pick<IProfile, 'id'> | null, o2: Pick<IProfile, 'id'> | null): boolean {
    return o1 && o2 ? this.getProfileIdentifier(o1) === this.getProfileIdentifier(o2) : o1 === o2;
  }

  addProfileToCollectionIfMissing<Type extends Pick<IProfile, 'id'>>(
    profileCollection: Type[],
    ...profilesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const profiles: Type[] = profilesToCheck.filter(isPresent);
    if (profiles.length > 0) {
      const profileCollectionIdentifiers = profileCollection.map(profileItem => this.getProfileIdentifier(profileItem));
      const profilesToAdd = profiles.filter(profileItem => {
        const profileIdentifier = this.getProfileIdentifier(profileItem);
        if (profileCollectionIdentifiers.includes(profileIdentifier)) {
          return false;
        }
        profileCollectionIdentifiers.push(profileIdentifier);
        return true;
      });
      return [...profilesToAdd, ...profileCollection];
    }
    return profileCollection;
  }
}
